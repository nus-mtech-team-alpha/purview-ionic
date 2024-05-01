package com.apple.jmet.purview.web;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apple.jmet.purview.domain.Feature;
import com.apple.jmet.purview.domain.Site;
import com.apple.jmet.purview.domain.SiteFeature;
import com.apple.jmet.purview.repository.FeatureRepository;
import com.apple.jmet.purview.repository.SiteFeatureRepository;
import com.apple.jmet.purview.repository.SiteRepository;

/**
 * REST controller for managing
 * {@link com.apple.jmet.purview.domain.SiteFeature}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class SiteFeatureResource {

    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private FeatureRepository featureRepository;
    @Autowired
    private SiteFeatureRepository siteFeatureRepository;

    //@PreAuthorize("hasAnyRole('ROLE_SRE','ROLE_EPM')")
    @PostMapping("/site-feature-add")
    public SiteFeature addSiteFeature(@RequestBody SiteFeature siteFeature) {
        Optional<Site> siteOpt = this.siteRepository.findById(siteFeature.getSiteIdToAdd());
        if (siteOpt.isPresent()) {
            Site site = siteOpt.get();
            Optional<Feature> featureOpt = this.featureRepository.findById(siteFeature.getFeatureIdToAdd());
            if(featureOpt.isPresent()){
                Feature feature = featureOpt.get();
                SiteFeature siteFeatureToAdd = new SiteFeature();
                siteFeatureToAdd.setSite(site);
                siteFeatureToAdd.setFeature(feature);
                siteFeatureToAdd.setRequestReferenceId(siteFeature.getRequestReferenceId());
                return this.siteFeatureRepository.save(siteFeatureToAdd);
            }
        }
        return null;
    }

    @GetMapping("/site-features/{siteId}")
    public List<SiteFeature> getSiteFeatures(@PathVariable Long siteId) {
        Optional<Site> siteOpt = this.siteRepository.findById(siteId);
        if (siteOpt.isPresent()) {
            Site site = siteOpt.get();
            return this.siteFeatureRepository.findBySiteId(site.getId());
        }
        return List.of();
    }

    @GetMapping("/site-features-all")
    public List<SiteFeature> getAllSiteFeatures() {
        return this.siteFeatureRepository.findAll();
    }

    @GetMapping("/sites-by-feature/{featureId}")
    public List<SiteFeature> getSitesByFeature(@PathVariable Long featureId) {
        Optional<Feature> featureOpt = this.featureRepository.findById(featureId);
        if (featureOpt.isPresent()) {
            Feature feature = featureOpt.get();
            return this.siteFeatureRepository.findByFeatureId(feature.getId());
        }
        return List.of();
    }

    @GetMapping("/site-feature-by-request/{requestReferenceId}")
    public SiteFeature getSiteFeatureByRequestReferenceId(@PathVariable String requestReferenceId) {
        return this.siteFeatureRepository.findByRequestReferenceId(requestReferenceId).orElseThrow();
    }

}
