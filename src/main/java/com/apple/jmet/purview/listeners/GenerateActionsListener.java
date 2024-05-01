package com.apple.jmet.purview.listeners;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apple.jmet.purview.domain.Action;
import com.apple.jmet.purview.domain.App;
import com.apple.jmet.purview.domain.AppConfig;
import com.apple.jmet.purview.domain.AppConfigValue;
import com.apple.jmet.purview.domain.Feature;
import com.apple.jmet.purview.domain.FeatureApp;
import com.apple.jmet.purview.domain.GenericConfig;
import com.apple.jmet.purview.domain.Product;
import com.apple.jmet.purview.domain.ProductApp;
import com.apple.jmet.purview.domain.Request;
import com.apple.jmet.purview.domain.Site;
import com.apple.jmet.purview.domain.SiteApp;
import com.apple.jmet.purview.domain.SiteAppConfig;
import com.apple.jmet.purview.domain.SiteFeature;
import com.apple.jmet.purview.domain.SiteProduct;
import com.apple.jmet.purview.domain.Task;
import com.apple.jmet.purview.enums.ActionCategory;
import com.apple.jmet.purview.enums.ActionStatus;
import com.apple.jmet.purview.enums.Environment;
import com.apple.jmet.purview.enums.RequestStatus;
import com.apple.jmet.purview.enums.RolloutPhase;
import com.apple.jmet.purview.repository.AppConfigRepository;
import com.apple.jmet.purview.repository.FeatureAppRepository;
import com.apple.jmet.purview.repository.ProductAppRepository;
import com.apple.jmet.purview.repository.SiteAppRepository;
import com.apple.jmet.purview.repository.SiteFeatureRepository;
import com.apple.jmet.purview.repository.SiteProductRepository;
import com.apple.jmet.purview.utils.ComparisonUtils;
import com.apple.jmet.purview.utils.JmetAnsibleHelper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GenerateActionsListener implements RequestEventListener {

    Logger logger = LoggerFactory.getLogger(GenerateActionsListener.class);

    @Autowired
    private SiteAppRepository siteAppRepository;
    @Autowired
    private SiteProductRepository siteProductRepository;
    @Autowired
    private SiteFeatureRepository siteFeatureRepository;
    @Autowired
    private ProductAppRepository productAppRepository;
    @Autowired
    private FeatureAppRepository featureAppRepository;
    @Autowired
    private JmetAnsibleHelper jmetAnsibleHelper;
    @Autowired
    private AppConfigRepository appConfigRepository;

    private ComparisonUtils comparisonUtils = ComparisonUtils.getInstance();

    @Override
    public void update(RequestStatus requestStatus, Request request) {
        if (RequestStatus.valueOf(request.getStatus()).equals(RequestStatus.OPEN)) {
            logger.info("Generating actions for Request: {}...", request.getReferenceId());

            List<Action> actions = new ArrayList<>();
            Product targetProduct = request.getProduct();
            Feature targetFeature = request.getFeature();

            if(targetProduct != null){
                List<ProductApp> productAppsToSupport = this.productAppRepository.findByProductId(targetProduct.getId());
                productAppsToSupport.stream().forEach(productApp -> {
                    App app = productApp.getApp();
                    Action action = this.buildAction(request, app, productApp.getProductAppConfigs());
                    if(action != null){
                        actions.add(action);
                    }
                });
            }else if(targetFeature != null){
                List<FeatureApp> featureAppsToSupport = this.featureAppRepository.findByFeatureId(targetFeature.getId());
                featureAppsToSupport.stream().forEach(featureApp -> {
                    App app = featureApp.getApp();
                    Action action = this.buildAction(request, app, featureApp.getFeatureAppConfigs());
                    if(action != null){
                        actions.add(action);
                    }
                });
            }
            
            request.setActions(actions);
            
            Optional<Action> anyPendingAction = actions.stream().filter(action -> !action.getStatus().equals(ActionStatus.COMPLETED.getValue())).findFirst();
            if(!actions.isEmpty() && anyPendingAction.isEmpty()){
                request.setRequestStatus(RequestStatus.COMPLETED);
            }
        }
    }

    public Action buildAction(Request request, App app, List<? extends GenericConfig> genericConfigs){

        if((request.getSite().getCategory().equalsIgnoreCase("DC") && !app.getCategory().equalsIgnoreCase("DC"))
            || (!request.getSite().getCategory().equalsIgnoreCase("DC") && !app.getCategory().equalsIgnoreCase("FACTORY"))){
            return null;
        }

        String actionEnv = Environment.PRODUCTION.getValue();
        Optional<SiteApp> siteAppStaging = this.siteAppRepository.findBySiteIdAndAppIdAndEnvironment(request.getSite().getId(), app.getId(), Environment.STAGING.getValue());
        Optional<SiteApp> siteAppMp = this.siteAppRepository.findBySiteIdAndAppIdAndEnvironment(request.getSite().getId(), app.getId(), Environment.PRODUCTION.getValue());
        if(this.toConsiderStaging(request) 
                && siteAppStaging.isPresent() 
                && !this.isSupported(request, Environment.STAGING.getValue())) {
            actionEnv = Environment.STAGING.getValue();
        }else if(siteAppMp.isEmpty()){
            log.debug("Ignoring Action, target site has no {}", app.getInternalName());
            return null;
        }
        Action action = Action.builder()
                .environment(actionEnv)
                .request(request)
                .app(app)
                .category(ActionCategory.DEPLOYMENT.getValue())
                .status(ActionStatus.OPEN.getValue())
                .build();
        List<Task> tasks = new ArrayList<>();
        if (!CollectionUtils.isEmpty(genericConfigs)) {
            final String actionEnvString = actionEnv;
            genericConfigs.stream().filter(g -> StringUtils.isNotBlank(g.getVal())).forEach(gc -> {
                SiteApp siteApp = this.siteAppRepository.findBySiteIdAndAppIdAndEnvironment(request.getSite().getId(), app.getId(), actionEnvString).orElse(null);
                AppConfig appConfig = app.getAppConfigs().stream().filter(ac -> StringUtils.endsWithIgnoreCase(ac.getName(), gc.getName())).findFirst().orElse(null);
                String taskStatus = ActionStatus.OPEN.getValue();
                String preferredValue = gc.getVal();
                if(siteApp != null){
                    SiteAppConfig siteAppConfig = siteApp.getSiteAppConfigs().stream().filter(sac -> StringUtils.endsWithIgnoreCase(sac.getName(), gc.getName())).findFirst().orElse(null);
                    if(siteAppConfig != null && ((appConfig.isMulti() && CollectionUtils.containsAll(
                                                    List.of(siteAppConfig.getVal().split(",")), 
                                                    List.of(gc.getVal().split(","))))
                        || (!appConfig.isMulti() && comparisonUtils.singleValueCompare(siteAppConfig.getVal(), gc.getVal()) >= 0))){
                        taskStatus = ActionStatus.COMPLETED.getValue();
                    }else if (appConfig.isRecommend()){
                        //Only recommend when current version is less than minimum required
                        preferredValue = this.getRecommendedVersion(request, action, appConfig);
                    }
                }
                Task task = Task.builder()
                        .action(action)
                        .appConfig(appConfig)
                        .appConfigSetValue(preferredValue)
                        .status(taskStatus)
                        .assignee(app.getSre())
                        .build();
                tasks.add(task);
            });
            action.setTasks(tasks);
            Optional<Task> anyPendingTaskInAction = tasks.stream().filter(task -> !task.getStatus().equals(ActionStatus.COMPLETED.getValue())).findFirst();
            if(anyPendingTaskInAction.isEmpty()){
                action.setActionStatus(ActionStatus.COMPLETED);
            }
        }  
        return action;
    }

    public String getRecommendedVersion(Request request, Action action, AppConfig appConfig){
        String preferredValue = null;
        AppConfigValue latestConfigValue = this.getLatestConfigValuePrecedenceBased(request.getSite(), action, appConfig.getValues());
        if(latestConfigValue != null){
            preferredValue = latestConfigValue.getVal();
        }
        if(StringUtils.equalsIgnoreCase("CONFIG-VERSION", appConfig.getName())){
            log.debug("Getting latest actual from rio...");
            String latestActual = this.jmetAnsibleHelper.getLatestJmetAnsibleMainPublishVersion();
            if(StringUtils.isNotBlank(latestActual) 
                && (latestConfigValue == null || comparisonUtils.singleValueCompare(latestActual, latestConfigValue.getVal()) > 0)){
                if(appConfig.getValues().stream().noneMatch(acv -> StringUtils.equalsIgnoreCase(acv.getVal(), latestActual))){
                    AppConfigValue acv = AppConfigValue.builder().val(latestActual).build();
                    acv.setRolloutPhase(RolloutPhase.NEW);
                    acv.setDateAdded(new Date());
                    acv.setAppConfig(appConfig);
                    appConfig.getValues().add(acv);
                    this.appConfigRepository.save(appConfig);
                }
                preferredValue = latestActual;
            }
        }
        log.debug("Resulting value: {}", preferredValue);
        return preferredValue;
    }

    private boolean toConsiderStaging(Request request){
        return (request.getProduct() != null && "NPI".equalsIgnoreCase(request.getProduct().getStatus()))
            || (request.getFeature() != null && !"PRODUCTION".equalsIgnoreCase(request.getFeature().getRolloutPhase()));
    }

    private boolean isSupported(Request request, String environment){
        log.debug("Checking {} support in {}", request.getProduct() != null ? request.getProduct().getCode() : request.getFeature().getName(), request.getSite().getCode());
        if(request.getProduct() != null){
            Optional<SiteProduct> siteProduct = this.siteProductRepository.findBySiteIdAndProductIdAndEnvironmentAndDateSupportedNotNull(request.getSite().getId(), request.getProduct().getId(), environment);
            log.debug("Result: {}", siteProduct.isPresent());
            return siteProduct.isPresent();
        }else if(request.getFeature() != null){
            Optional<SiteFeature> siteFeature = this.siteFeatureRepository.findBySiteIdAndFeatureIdAndEnvironmentAndDateSupportedNotNull(request.getSite().getId(), request.getFeature().getId(), environment);
            log.debug("Result: {}", siteFeature.isPresent());
            return siteFeature.isPresent();
        }
        return false;
    }

    /**
     * Precedence:
     * 1. if site=DECK, then latest NEW
     * 2. if actionEnv=PRODUCTION, then latest PRODUCTION
     * 3. else latest STAGING > DECK > NEW > PRODUCTION
     */
    private AppConfigValue getLatestConfigValuePrecedenceBased(Site site, Action action,
            List<AppConfigValue> appConfigValues) {
        List<AppConfigValue> reverseSortedValues = appConfigValues.stream()
                .sorted(Comparator.comparing(AppConfigValue::getVal).reversed()).toList();
        
        Optional<AppConfigValue> latestOptProduction = reverseSortedValues.stream().filter(AppConfigValue::isProduction)
                .findFirst();
        Optional<AppConfigValue> latestOptStaging = reverseSortedValues.stream().filter(AppConfigValue::isStaging)
                .findFirst();
        Optional<AppConfigValue> latestOptDeck = reverseSortedValues.stream().filter(AppConfigValue::isDeck)
                .findFirst();
        Optional<AppConfigValue> latestOptNew = reverseSortedValues.stream().filter(AppConfigValue::isNew).findFirst();

        if ("DECK".equals(site.getCode()) && latestOptNew.isPresent()) {
            return latestOptNew.get();
        } else if (action.isForProduction() && latestOptProduction.isPresent()) {
            return latestOptProduction.get();
        } else if (latestOptStaging.isPresent()) {
            return latestOptStaging.get();
        } else if (latestOptDeck.isPresent()) {
            return latestOptDeck.get();
        } else if (latestOptNew.isPresent()) {
            return latestOptNew.get();
        } else if (latestOptProduction.isPresent()) {
            return latestOptProduction.get();
        }
        return null;
    }
}
