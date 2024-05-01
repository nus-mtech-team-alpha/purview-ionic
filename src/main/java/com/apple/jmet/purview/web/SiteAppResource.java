package com.apple.jmet.purview.web;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.apple.jmet.purview.domain.App;
import com.apple.jmet.purview.domain.Site;
import com.apple.jmet.purview.domain.SiteApp;
import com.apple.jmet.purview.enums.Environment;
import com.apple.jmet.purview.repository.AppRepository;
import com.apple.jmet.purview.repository.SiteAppRepository;
import com.apple.jmet.purview.repository.SiteRepository;

/**
 * REST controller for managing {@link com.apple.jmet.purview.domain.SiteApp}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class SiteAppResource {

    Logger logger = LoggerFactory.getLogger(SiteAppResource.class);

    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private SiteAppRepository siteAppRepository;
    @Autowired
    private AppRepository appRepository;

    @GetMapping("/site-apps/{siteId}")
    public List<SiteApp> getSiteApps(@PathVariable Long siteId) {
        Optional<Site> siteOpt = this.siteRepository.findById(siteId);
        if (siteOpt.isPresent()) {
            Site site = siteOpt.get();
            return this.siteAppRepository.findBySiteId(site.getId());
        }
        return List.of();
    }

    @GetMapping("/site-apps-env/{siteId}")
    public List<SiteApp> getSiteAppsPerEnv(@PathVariable Long siteId,
            @RequestParam(name = "env", required = true) String env) {
        Optional<Site> siteOpt = this.siteRepository.findById(siteId);
        if (siteOpt.isPresent()) {
            Site site = siteOpt.get();
            return this.siteAppRepository.findBySiteIdAndEnvironment(site.getId(), env);
        }
        return List.of();
    }

    @GetMapping("/site-apps-for-npi/{siteId}")
    public List<SiteApp> getSiteAppsForNpi(@PathVariable Long siteId) {
        Optional<Site> siteOpt = this.siteRepository.findById(siteId);
        if (siteOpt.isPresent()) {
            List<SiteApp> siteApps = this.siteAppRepository.findBySiteId(siteId);
            List<SiteApp> siteAppsToSupport = new ArrayList<>();
            List<SiteApp> siteAppsStaging = siteApps.stream()
                    .filter(siteApp -> StringUtils.equalsIgnoreCase(Environment.STAGING.getValue(), siteApp.getEnvironment())).toList();
            final List<App> appsInStaging = new ArrayList<>();
            if (!CollectionUtils.isEmpty(siteAppsStaging)) {
                appsInStaging.addAll(siteAppsStaging.stream().map(SiteApp::getApp).toList());
                siteAppsToSupport.addAll(siteAppsStaging);
            }
            List<SiteApp> siteAppsProduction = siteApps.stream()
                    .filter(siteApp -> StringUtils.equalsIgnoreCase(Environment.PRODUCTION.getValue(), siteApp.getEnvironment())
                            && !appsInStaging.contains(siteApp.getApp()))
                    .toList();
            if (!CollectionUtils.isEmpty(siteAppsProduction)) {
                siteAppsToSupport.addAll(siteAppsProduction);
            }
            return siteAppsToSupport;
        }
        return List.of();
    }

    //@PreAuthorize("hasAnyRole('ROLE_SRE')")
    @PostMapping("/site-apps/{siteId}")
    public List<SiteApp> createSiteApp(@PathVariable Long siteId, @RequestBody List<SiteApp> siteApps) {
        siteApps.forEach(sa -> {
            Optional<SiteApp> saOpt = this.siteAppRepository.findBySiteIdAndAppIdAndEnvironment(siteId, sa.getAppIdToSave(), sa.getEnvironment());
            Site site = this.siteRepository.findById(siteId).orElse(null);
            App app = this.appRepository.findById(sa.getAppIdToSave()).orElse(null);
            if (saOpt.isPresent()) {
                SiteApp existingSa = saOpt.get();
                existingSa.setVip(sa.getVip());
                this.siteAppRepository.save(existingSa);
            } else {
                SiteApp siteApp = SiteApp.builder()
                                        .site(site)
                                        .app(app)
                                        .vip(sa.getVip())
                                        .environment(sa.getEnvironment())
                                        .build();
                this.siteAppRepository.save(siteApp);
            }
        });
        return this.siteAppRepository.findBySiteId(siteId);
    }

}
