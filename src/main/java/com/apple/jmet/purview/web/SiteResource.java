package com.apple.jmet.purview.web;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.apple.jmet.purview.domain.Request;
import com.apple.jmet.purview.domain.Site;
import com.apple.jmet.purview.dto.SiteListDto;
import com.apple.jmet.purview.enums.Environment;
import com.apple.jmet.purview.enums.RequestStatus;
import com.apple.jmet.purview.repository.RequestRepository;
import com.apple.jmet.purview.repository.SiteListDtoRepository;
import com.apple.jmet.purview.repository.SiteRepository;

/**
 * REST controller for managing {@link com.apple.jmet.purview.domain.Site}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class SiteResource {

    Logger logger = LoggerFactory.getLogger(SiteResource.class);

    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private SiteListDtoRepository siteListDtoRepository;

    //@PreAuthorize("hasAnyRole('ROLE_SRE','ROLE_OPS','ROLE_EPM')")
    @PostMapping("/sites")
    public Site createSite(@RequestBody Site site) {
        if(this.siteRepository.findByCode(site.getCode()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Site with code " + site.getCode() + " already exists");
        }
        return siteRepository.save(site);
    }

    //@PreAuthorize("hasAnyRole('ROLE_SRE','ROLE_OPS')")
    @PutMapping("/sites/{id}")
    public Site updateSite(@PathVariable(value = "id", required = false) final Long id, @RequestBody Site site) {
        return siteRepository.save(site);
    }

    @GetMapping("/sites")
    public List<Site> getAllSites() {
        return siteRepository.findAll();
    }

    @GetMapping("/sites-list-dto")
    public List<SiteListDto> getAllSitesListDto() {
        return siteListDtoRepository.findAllBy();
    }

    @GetMapping("/sites-by-category")
    public List<Site> getSitesByCategory(@RequestParam String category) {
        List<String> fatpCategories = List.of("IPHONE", "IPAD", "MAC", "WATCH", "AUDIO", "TV", "ACCESSORIES");
        if(fatpCategories.contains(category)){
            return siteRepository.findByProductCategoriesContaining(category);
        }
        return siteRepository.findAll();
    }

    @GetMapping("/sites/{id}")
    public Site getSite(@PathVariable Long id) {
        Optional<Site> siteOpt = siteRepository.findById(id);
        if (siteOpt.isPresent()) {
            Site site = siteOpt.get();
            // this.setLastActions(site);
            return site;
        } else {
            return null;
        }
    }

    @GetMapping("/sites-by-code/{code}")
    public Site getSiteByCode(@PathVariable String code) {
        Optional<Site> siteOpt = siteRepository.findByCode(code);
        if (siteOpt.isPresent()) {
            Site site = siteOpt.get();
            // this.setLastActions(site);
            return site;
        } else {
            return null;
        }
    }

    // private void setLastActions(Site site){
    //     Optional<Request> lastCompletedRequestStaging = this.requestRepository
    //             .findTop1BySiteAndStatusOrderByCompletedDateDesc(site, RequestStatus.COMPLETED.getValue(), Environment.STAGING.getValue());
    //     if (lastCompletedRequestStaging.isPresent()) {
    //         Request request = lastCompletedRequestStaging.get();
    //         site.setLastCompletedRequestRefIdStaging(request.getReferenceId());
    //         site.setLastCompletedActionsStaging(request.getActions());
    //     }
    //     Optional<Request> lastCompletedRequestProduction = this.requestRepository
    //             .findTop1BySiteAndStatusOrderByCompletedDateDesc(site, RequestStatus.COMPLETED.getValue(), Environment.PRODUCTION.getValue());
    //     if (lastCompletedRequestProduction.isPresent()) {
    //         Request request = lastCompletedRequestProduction.get();
    //         site.setLastCompletedRequestRefIdProduction(request.getReferenceId());
    //         site.setLastCompletedActionsProduction(request.getActions());
    //     }
    // }

    @GetMapping("/sites-active-count")
    public Integer getActiveSitesCount() {
        return this.siteRepository.countByStatus("ACTIVE");
    }
}
