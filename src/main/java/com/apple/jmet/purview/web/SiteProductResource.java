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

import com.apple.jmet.purview.domain.Product;
import com.apple.jmet.purview.domain.Site;
import com.apple.jmet.purview.domain.SiteProduct;
import com.apple.jmet.purview.repository.ProductRepository;
import com.apple.jmet.purview.repository.SiteProductRepository;
import com.apple.jmet.purview.repository.SiteRepository;

/**
 * REST controller for managing
 * {@link com.apple.jmet.purview.domain.SiteProduct}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class SiteProductResource {

    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private SiteProductRepository siteProductRepository;

    //@PreAuthorize("hasAnyRole('ROLE_SRE','ROLE_EPM')")
    @PostMapping("/site-product-add")
    public SiteProduct addSiteProduct(@RequestBody SiteProduct siteProduct) {
        Optional<Site> siteOpt = this.siteRepository.findById(siteProduct.getSiteIdToAdd());
        if (siteOpt.isPresent()) {
            Site site = siteOpt.get();
            Optional<Product> productOpt = this.productRepository.findById(siteProduct.getProductIdToAdd());
            if(productOpt.isPresent()){
                Product product = productOpt.get();
                SiteProduct siteProductToAdd = new SiteProduct();
                siteProductToAdd.setSite(site);
                siteProductToAdd.setProduct(product);
                siteProductToAdd.setRequestReferenceId(siteProduct.getRequestReferenceId());
                return this.siteProductRepository.save(siteProductToAdd);
            }
        }
        return null;
    }

    @GetMapping("/site-products/{siteId}")
    public List<SiteProduct> getSiteProducts(@PathVariable Long siteId) {
        Optional<Site> siteOpt = this.siteRepository.findById(siteId);
        if (siteOpt.isPresent()) {
            Site site = siteOpt.get();
            return this.siteProductRepository.findBySiteId(site.getId());
        }
        return List.of();
    }

    @GetMapping("/site-products-all")
    public List<SiteProduct> getAllSiteProducts() {
        return this.siteProductRepository.findAll();
    }

    @GetMapping("/sites-by-product/{productId}")
    public List<SiteProduct> getSitesByProduct(@PathVariable Long productId) {
        Optional<Product> productOpt = this.productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            return this.siteProductRepository.findByProductId(product.getId());
        }
        return List.of();
    }

    @GetMapping("/site-product-by-request/{requestReferenceId}")
    public SiteProduct getSiteProductByRequestReferenceId(@PathVariable String requestReferenceId) {
        return this.siteProductRepository.findByRequestReferenceId(requestReferenceId).orElseThrow();
    }

}
