package com.apple.jmet.purview.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.apple.jmet.purview.domain.Feature;
import com.apple.jmet.purview.dto.FeatureListDto;
import com.apple.jmet.purview.enums.RolloutPhase;
import com.apple.jmet.purview.repository.FeatureListDtoRepository;
import com.apple.jmet.purview.repository.FeatureRepository;

/**
 * REST controller for managing {@link com.apple.jmet.purview.domain.Feature}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class FeatureResource {

    @Autowired
    private FeatureRepository featureRepository;
    @Autowired
    private FeatureListDtoRepository featureListDtoRepository;

    //@PreAuthorize("hasAnyRole('ROLE_SRE','ROLE_EPM')")
    @PostMapping("/features")
    public Feature createFeature(@RequestBody Feature feature) {
        if(this.featureRepository.findByName(feature.getName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Feature with name " + feature.getName() + " already exists");
        }
        feature.setRolloutPhase(RolloutPhase.NEW);
        return featureRepository.save(feature);
    }

    //@PreAuthorize("hasAnyRole('ROLE_SRE','ROLE_EPM')")
    @PutMapping("/features/{id}")
    public Feature updateFeature(@PathVariable(value = "id", required = false) final Long id,
            @RequestBody Feature feature) {
        return featureRepository.save(feature);
    }

    @GetMapping("/features")
    public List<Feature> getAllFeatures() {
        return featureRepository.findAll();
    }

    @GetMapping("/features-list-dto")
    public List<FeatureListDto> getAllFeaturesListDto() {
        return featureListDtoRepository.findAllBy();
    }

    @GetMapping("/features/{id}")
    public Feature getFeature(@PathVariable Long id) {
        return featureRepository.findById(id).orElse(null);
    }

    @GetMapping("/features-by-name/{name}")
    public Feature getFeatureByName(@PathVariable String name) {
        return featureRepository.findByName(name).orElse(null);
    }

}
