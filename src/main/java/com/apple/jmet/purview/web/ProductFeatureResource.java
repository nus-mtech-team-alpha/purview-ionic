package com.apple.jmet.purview.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apple.jmet.purview.domain.Feature;
import com.apple.jmet.purview.domain.Product;
import com.apple.jmet.purview.domain.ProductFeature;
import com.apple.jmet.purview.repository.FeatureRepository;
import com.apple.jmet.purview.repository.ProductFeatureRepository;
import com.apple.jmet.purview.repository.ProductRepository;

/**
 * REST controller for managing
 * {@link com.apple.jmet.purview.domain.ProductFeature}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ProductFeatureResource {

    Logger logger = LoggerFactory.getLogger(ProductFeatureResource.class);

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductFeatureRepository productFeatureRepository;
    @Autowired
    private FeatureRepository featureRepository;
    @Autowired
    private ProductAppResource productAppResource;

    @GetMapping("/product-features/{productId}")
    public List<ProductFeature> getProductFeatures(@PathVariable Long productId) {
        Optional<Product> productOpt = this.productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            return this.productFeatureRepository.findByProductId(product.getId());
        }
        return List.of();
    }

    @GetMapping("/product-features-by-feature/{featureId}")
    public List<ProductFeature> getProductFeaturesByFeature(@PathVariable Long featureId) {
        Optional<Feature> featureOpt = this.featureRepository.findById(featureId);
        if (featureOpt.isPresent()) {
            Feature feature = featureOpt.get();
            return this.productFeatureRepository.findByFeatureId(feature.getId());
        }
        return List.of();
    }

    //@PreAuthorize("hasAnyRole('ROLE_SRE','ROLE_EPM')")
    @PostMapping("/product-features/{productId}")
    public List<Feature> createProductFeature(@PathVariable Long productId, @RequestBody List<Feature> features) {
        Optional<Product> productOpt = this.productRepository.findById(productId);
        List<Feature> affectedFeatures = new ArrayList<>();
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            features.forEach(feature -> {
                ProductFeature pf = new ProductFeature();
                pf.setProduct(product);
                Optional<Feature> featureOpt = this.featureRepository.findById(feature.getId());
                if (featureOpt.isPresent()) {
                    Feature featureToSave = featureOpt.get();
                    affectedFeatures.add(featureToSave);
                    pf.setFeature(featureToSave);
                    Optional<ProductFeature> optPf = this.productFeatureRepository.findByProductIdAndFeatureId(product.getId(), featureToSave.getId());
                    if(!optPf.isPresent()){
                        this.productFeatureRepository.save(pf);
                    }
                }
            });
            this.productAppResource.updateProductAppByFeatures(product, affectedFeatures);
        }
        return features;
    }

    //@PreAuthorize("hasAnyRole('ROLE_SRE','ROLE_EPM')")
    @DeleteMapping("/product-features/{productId}/{featureId}")
    public void deleteProductFeature(@PathVariable Long productId, @PathVariable Long featureId) {
        Optional<ProductFeature> optPf = this.productFeatureRepository.findByProductIdAndFeatureId(productId, featureId);
        if(optPf.isPresent()){
            this.productFeatureRepository.delete(optPf.get());
        }
    }

}
