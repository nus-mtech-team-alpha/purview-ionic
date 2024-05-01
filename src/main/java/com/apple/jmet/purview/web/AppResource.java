package com.apple.jmet.purview.web;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.apple.jmet.purview.domain.App;
import com.apple.jmet.purview.domain.AppConfig;
import com.apple.jmet.purview.domain.AppConfigValue;
import com.apple.jmet.purview.domain.ConfigType;
import com.apple.jmet.purview.domain.Person;
import com.apple.jmet.purview.dto.AppListDto;
import com.apple.jmet.purview.enums.RolloutPhase;
import com.apple.jmet.purview.repository.AppConfigRepository;
import com.apple.jmet.purview.repository.AppListDtoRepository;
import com.apple.jmet.purview.repository.AppRepository;
import com.apple.jmet.purview.repository.ConfigTypeRepository;
import com.apple.jmet.purview.repository.PersonRepository;
import com.apple.jmet.purview.utils.ComparisonUtils;
import com.apple.jmet.purview.utils.JmetAnsibleHelper;

import lombok.extern.slf4j.Slf4j;


/**
 * REST controller for managing {@link com.apple.jmet.purview.domain.App}.
 */
@Slf4j
@RestController
@RequestMapping("/api")
@Transactional
public class AppResource {

    Logger logger = LoggerFactory.getLogger(AppResource.class);

    @Autowired
    private AppRepository appRepository;
    @Autowired
    private ConfigTypeRepository configTypeRepository;
    @Autowired
    private AppListDtoRepository appListDtoRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private PersonResource personResource;
    @Autowired
    private JmetAnsibleHelper jmetAnsibleHelper;
    @Autowired
    private AppConfigRepository appConfigRepository;

    private ComparisonUtils comparisonUtils = ComparisonUtils.getInstance();

    //@PreAuthorize("hasAnyRole('ROLE_SRE')")
    @PostMapping("/apps")
    public App createApp(@RequestBody App app) {
        if(this.appRepository.findByInternalName(app.getInternalName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "App with name " + app.getInternalName() + " already exists");
        }
        return appRepository.save(app);
    }

    //@PreAuthorize("hasAnyRole('ROLE_SRE')")
    @PutMapping("/apps/{id}")
    public App updateApp(@PathVariable(value = "id", required = false) final Long id, @RequestBody App app) {
        Optional<App> savedAppOpt = this.appRepository.findById(id);
        if (savedAppOpt.isPresent()) {
            App savedApp = savedAppOpt.get();
            this.updateDris(savedApp, app);
            savedApp.setInternalName(app.getInternalName());
            savedApp.setExternalName(app.getExternalName());
            savedApp.setStatus(app.getStatus());
            if (null != app.getAppConfigs() && !app.getAppConfigs().isEmpty()) {
                // app.getAppConfigs().removeIf(ac -> ac.getValues().isEmpty()); // remove AppConfigs with no values
                app.getAppConfigs().forEach(ac -> { // set dateAdded for new AppConfigValues
                    ac.getValues().forEach(acv -> {
                        acv.setAppConfig(ac);
                        if (acv.getId() <= 0) {
                            acv.setDateAdded(new Date());
                        }
                    });
                });
                savedApp.setAppConfigs(app.getAppConfigs());
            }

            return appRepository.save(savedApp);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "App not found or not authorised");
        }
    }

    private void updateDris(App savedApp, App app){
        if(app.getDevIdToSave() > 0){
            savedApp.setDev(this.personRepository.findById(app.getDevIdToSave()).orElse(null));
        }
        if(app.getEpmIdToSave() > 0){
            savedApp.setEpm(this.personRepository.findById(app.getEpmIdToSave()).orElse(null));
        }
        if(app.getSreIdToSave() > 0){
            savedApp.setSre(this.personRepository.findById(app.getSreIdToSave()).orElse(null));
        }

        if(CollectionUtils.isNotEmpty(app.getBackupSres())){
            if(savedApp.getBackupSres() == null){
                savedApp.setBackupSres(new ArrayList<>());
            }
            app.getBackupSres().forEach(backupSre -> {
                if(backupSre.getId() > 0 && !savedApp.getBackupSres().stream().map(Person::getId).collect(Collectors.toList()).contains(backupSre.getId())){
                    savedApp.getBackupSres().add(this.personRepository.findById(backupSre.getId()).orElse(null));
                }
            });
            savedApp.getBackupSres().removeIf(backupSre -> {
                return !app.getBackupSres().stream().map(Person::getId).collect(Collectors.toList()).contains(backupSre.getId());
            });
        }
    }

    @GetMapping("/apps")
    public List<App> getAllApps() {
        return appRepository.findAll();
    }

    @GetMapping("/apps-list-dto")
    public List<AppListDto> getAllAppsDto() {
        return appListDtoRepository.findAllBy();
    }

    @GetMapping("/apps-list-dto-by-sre")
    public List<AppListDto> getAllAppsDtoBySre() {
        Person currentUser = this.personResource.getCurrentUserInternally();
        return appListDtoRepository.findAllBySreId(currentUser.getId());
    }

    @GetMapping("/apps/{id}")
    public App getApp(@PathVariable Long id) {
        return appRepository.findById(id).orElse(null);
    }

    @GetMapping("/apps-by-name/{name}")
    public App getAppByName(@PathVariable String name) {
        return appRepository.findByInternalName(name).orElse(null);
    }

    @GetMapping("/config-types")
    public List<ConfigType> getAllConfigTypes() {
        return configTypeRepository.findAll();
    }

    @GetMapping("/apps-active-count")
    public Integer getActiveAppsCount() {
        return this.appRepository.countByStatus("ACTIVE");
    }

    public void addAppConfigIfNotExists(App app, String configName, String configVal) {

        app.getAppConfigs().stream().filter(ac -> StringUtils.equalsIgnoreCase(ac.getName(), configName)).findFirst()
                .ifPresentOrElse(foundAc -> {
                    List<String> valsToCheck = List.of(configVal);
                    if(foundAc.isMulti()){
                        valsToCheck = List.of(configVal.split(","));
                    }
                    valsToCheck.forEach(valToCheck -> {
                        foundAc.getValues().stream().filter(acv -> StringUtils.equalsIgnoreCase(acv.getVal(), valToCheck))
                            .findFirst().ifPresentOrElse(foundAcv -> {
                                logger.debug("AppConfigValue already exists for AppConfig: {}", foundAc.getName());
                            }, () -> {
                                AppConfigValue acv = AppConfigValue.builder().val(valToCheck).build();
                                acv.setRolloutPhase(RolloutPhase.NEW);
                                acv.setDateAdded(new Date());
                                acv.setAppConfig(foundAc);
                                foundAc.getValues().add(acv);
                                this.appRepository.save(app);
                            });
                    });
                }, () -> {
                    AppConfig appConfig = AppConfig.builder().name(configName).build();
                    appConfig.setName(configName);
                    appConfig.setDateAdded(new Date());
                    ArrayList<AppConfigValue> values = new ArrayList<>();
                    values.add(
                        AppConfigValue.builder()
                            .val(configVal)
                            .appConfig(appConfig)
                            .build()
                    );
                    appConfig.setValues(values);
                    app.getAppConfigs().add(appConfig);
                    this.appRepository.save(app);
                });
    }

    @GetMapping("/apps/latest-jmet-ansible-main-publish-version")
    public String getLatestJmetAnsibleMainPublish() {
        return this.jmetAnsibleHelper.getLatestJmetAnsibleMainPublishVersion();
    }

    @GetMapping("/apps/get-config/latest-jmet-ansible-main-publish-version")
    public AppConfig getLatestJmetAnsibleMainPublishVersion(@RequestParam long appConfigId) {
        AppConfig appConfig = this.appConfigRepository.findById(appConfigId).orElse(null);
        if (appConfig != null) {
            List<AppConfigValue> values = appConfig.getValues().stream()
                .sorted(Comparator.comparing(AppConfigValue::getVal).reversed()).toList();
            String latestActual = this.jmetAnsibleHelper.getLatestJmetAnsibleMainPublishVersion();
            if(StringUtils.isNotBlank(latestActual) 
                && comparisonUtils.singleValueCompare(latestActual, values.get(0).getVal()) > 0 
                && appConfig.getValues().stream().noneMatch(acv -> StringUtils.equalsIgnoreCase(acv.getVal(), latestActual))){
                    AppConfigValue acv = AppConfigValue.builder().val(latestActual).build();
                    acv.setRolloutPhase(RolloutPhase.NEW);
                    acv.setDateAdded(new Date());
                    acv.setAppConfig(appConfig);
                    appConfig.getValues().add(acv);
                    this.appConfigRepository.save(appConfig);
            }
            return appConfig;
        }
        return null;
    }
    
}
