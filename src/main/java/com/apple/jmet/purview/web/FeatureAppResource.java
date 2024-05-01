package com.apple.jmet.purview.web;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apple.jmet.purview.domain.App;
import com.apple.jmet.purview.domain.Feature;
import com.apple.jmet.purview.domain.FeatureApp;
import com.apple.jmet.purview.domain.Product;
import com.apple.jmet.purview.domain.ProductFeature;
import com.apple.jmet.purview.repository.AppRepository;
import com.apple.jmet.purview.repository.FeatureAppRepository;
import com.apple.jmet.purview.repository.FeatureRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for managing
 * {@link com.apple.jmet.purview.domain.FeatureApp}.
 */
@RestController
@RequestMapping("/api")
@Transactional
@Slf4j
public class FeatureAppResource {

    Logger logger = LoggerFactory.getLogger(FeatureAppResource.class);

    @Autowired
    private FeatureRepository featureRepository;
    @Autowired
    private FeatureAppRepository featureAppRepository;
    @Autowired
    private AppRepository appRepository;
    @Autowired
    private AppResource appResource;
    @Autowired
    private ProductFeatureResource productFeatureResource;
    @Autowired
    private ProductAppResource productAppResource;
    @Autowired
    private RequestResource requestResource;
    
    @GetMapping("/feature-apps/{featureId}")
    public List<FeatureApp> getFeatureApps(@PathVariable Long featureId) {
        Optional<Feature> featureOpt = this.featureRepository.findById(featureId);
        if (featureOpt.isPresent()) {
            Feature feature = featureOpt.get();
            return this.featureAppRepository.findByFeatureId(feature.getId());
        }
        return List.of();
    }

    //@PreAuthorize("hasAnyRole('ROLE_SRE')")
    @PostMapping("/feature-apps/{featureId}")
    public List<FeatureApp> createFeatureApp(@PathVariable Long featureId, @RequestBody List<FeatureApp> featureApps) {
        featureApps.stream().filter(fas -> !CollectionUtils.isEmpty(fas.getFeatureAppConfigs())).forEach(fa -> {

            fa.getFeatureAppConfigs().forEach(fac -> fac.setDateAdded(new Date()));

            Optional<FeatureApp> faOpt = this.featureAppRepository.findByFeatureIdAndAppId(featureId,
                    fa.getAppIdToSave());
            if (faOpt.isPresent()) {
                FeatureApp faToUpdate = faOpt.get();
                faToUpdate.getFeatureAppConfigs().clear();
                if(!fa.isToRemove() && !CollectionUtils.isEmpty(fa.getFeatureAppConfigs())){
                    faToUpdate.getFeatureAppConfigs().addAll(fa.getFeatureAppConfigs());
                    faToUpdate.getFeatureAppConfigs().forEach(fac -> this.appResource.addAppConfigIfNotExists(faToUpdate.getApp(), fac.getName(), fac.getVal()));
                    this.featureAppRepository.save(faToUpdate);
                }else{
                    this.featureAppRepository.delete(faToUpdate);
                }
            } else if(!CollectionUtils.isEmpty(fa.getFeatureAppConfigs())) {
                Optional<Feature> featureOpt = this.featureRepository.findById(featureId);
                if (featureOpt.isPresent()) {
                    Feature feature = featureOpt.get();
                    fa.setFeature(feature);
                    Optional<App> appOpt = this.appRepository.findById(fa.getAppIdToSave());
                    if (appOpt.isPresent()) {
                        App app = appOpt.get();
                        fa.setApp(app);
                        fa.getFeatureAppConfigs().forEach(fac -> this.appResource.addAppConfigIfNotExists(app, fac.getName(), fac.getVal()));
                        this.featureAppRepository.save(fa);
                    }
                }
            }

        });
        
        List<ProductFeature> affectedPfs = this.productFeatureResource.getProductFeaturesByFeature(featureId);
        List<FeatureApp> affectedFas = this.featureAppRepository.findByFeatureId(featureId);
        this.requestResource.updateRequestActionsBasedOnFeatureChanges(featureId, affectedFas);

        affectedPfs.forEach(pf -> {
            Product product = pf.getProduct();
            this.productAppResource.updateProductAppByFeatures(product, List.of(pf.getFeature()));
        });

        return affectedFas;
    }

    //@PreAuthorize("hasAnyRole('ROLE_SRE','ROLE_EPM')")
    @PutMapping("/feature-apps-radars/{featureId}")
    public List<FeatureApp> updateFeatureAppRadars(@PathVariable Long featureId, @RequestBody List<FeatureApp> featureApps) {
        featureApps.forEach(fa -> {
            Optional<FeatureApp> faOpt = this.featureAppRepository.findByFeatureIdAndAppId(featureId,
                    fa.getAppIdToSave());
            if (faOpt.isPresent()) {
                FeatureApp faToUpdate = faOpt.get();
                faToUpdate.setRadars(fa.getRadars());
                this.featureAppRepository.save(faToUpdate);
            } else if(StringUtils.isNotBlank(fa.getRadars())) {
                Feature feature = this.featureRepository.findById(featureId).orElseThrow();
                fa.setFeature(feature);
                App app = this.appRepository.findById(fa.getAppIdToSave()).orElseThrow();
                fa.setApp(app);
                this.featureAppRepository.save(fa);
            }
        });
        return this.featureAppRepository.findByFeatureId(featureId);
    }

}
