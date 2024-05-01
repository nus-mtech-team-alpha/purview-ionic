package com.apple.jmet.purview.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.apple.jmet.purview.domain.FeatureAppConfig;
import com.apple.jmet.purview.domain.Product;
import com.apple.jmet.purview.domain.ProductApp;
import com.apple.jmet.purview.domain.ProductAppConfig;
import com.apple.jmet.purview.repository.AppRepository;
import com.apple.jmet.purview.repository.FeatureAppRepository;
import com.apple.jmet.purview.repository.ProductAppRepository;
import com.apple.jmet.purview.repository.ProductRepository;
import com.apple.jmet.purview.utils.ComparisonUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for managing
 * {@link com.apple.jmet.purview.domain.ProductApp}.
 */
@RestController
@RequestMapping("/api")
@Transactional
@Slf4j
public class ProductAppResource {

    Logger logger = LoggerFactory.getLogger(ProductAppResource.class);

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductAppRepository productAppRepository;
    @Autowired
    private AppRepository appRepository;
    @Autowired
    private AppResource appResource;
    @Autowired
    private RequestResource requestResource;
    @Autowired
    private FeatureAppRepository featureAppRepository;

    private ComparisonUtils comparisonUtils = ComparisonUtils.getInstance();

    @GetMapping("/product-apps/{productId}")
    public List<ProductApp> getProductApps(@PathVariable Long productId) {
        Optional<Product> productOpt = this.productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            return this.productAppRepository.findByProductId(product.getId());
        }
        return List.of();
    }

    //@PreAuthorize("hasAnyRole('ROLE_SRE','ROLE_EPM')")
    @PostMapping("/product-apps/{productId}")
    public List<ProductApp> manageProductApp(@PathVariable long productId, @RequestBody List<ProductApp> productApps) {
        productApps.stream().forEach(pa -> {
            pa.getProductAppConfigs().stream().filter(pac -> pac.getDateAdded() == null).forEach(pac -> pac.setDateAdded(new Date()));
            Optional<ProductApp> paOpt = this.productAppRepository.findByProductIdAndAppId(productId,
                    pa.getAppIdToSave());
            if (paOpt.isPresent()) {
                this.updateExistingProductApp(paOpt.get(), pa);
            } else if(!CollectionUtils.isEmpty(pa.getProductAppConfigs())) {
                Product product = this.productRepository.findById(productId).get();
                this.saveNewProductApp(product, pa);
            }
        });
        List<ProductApp> affectedPas = this.productAppRepository.findByProductId(productId);
        ProductApp pa = affectedPas.get(0);
        log.debug("Product App Configs in Product App Process: {}", pa.getProductAppConfigs().stream().map(pac -> pac.getName()).collect(Collectors.toSet()));
        this.requestResource.updateRequestActionsBasedOnProductChanges(productId, affectedPas);
        return affectedPas;
    }

    private void updateExistingProductApp(ProductApp paToUpdate, ProductApp pa){
        paToUpdate.getProductAppConfigs().clear();
        if(!pa.isToRemove() && !CollectionUtils.isEmpty(pa.getProductAppConfigs())){
            List<ProductAppConfig> pacsToDelete = pa.getProductAppConfigs().stream().filter(ProductAppConfig::isToDelete).toList();
            paToUpdate.getProductAppConfigs().addAll(pa.getProductAppConfigs());
            pacsToDelete.forEach(pacToDelete -> {
                log.debug("Deleting product app config: {}", pacToDelete.getName());
                paToUpdate.getProductAppConfigs().removeIf(pac -> pac.getName().equals(pacToDelete.getName()));
            });
            paToUpdate.getProductAppConfigs().forEach(pac -> this.appResource.addAppConfigIfNotExists(paToUpdate.getApp(), pac.getName(), pac.getVal()));
            log.debug("Remaining product app config: {}", paToUpdate.getProductAppConfigs().stream().map(ProductAppConfig::getName).toList());
            this.productAppRepository.save(paToUpdate);
        }else{
            this.productAppRepository.delete(paToUpdate);
        }
    }

    private void saveNewProductApp(Product product, ProductApp pa){
        pa.setProduct(product);
        Optional<App> appOpt = this.appRepository.findById(pa.getAppIdToSave());
        if (appOpt.isPresent()) {
            App app = appOpt.get();
            pa.setApp(app);
            pa.getProductAppConfigs().forEach(pac -> this.appResource.addAppConfigIfNotExists(app, pac.getName(), pac.getVal()));
            this.productAppRepository.save(pa);
        }
    }

    public void updateProductAppByFeatures(Product product, List<Feature> features) {

        features.forEach(feature -> {
            List<FeatureApp> fApps = this.featureAppRepository.findByFeatureId(feature.getId());
            fApps.forEach(fa -> {
                Optional<ProductApp> pAppOpt = this.productAppRepository.findByProductIdAndAppId(product.getId(), fa.getApp().getId());
                if(pAppOpt.isPresent()){
                    ProductApp paToUpdate = pAppOpt.get();
                    fa.getFeatureAppConfigs().stream().filter(fac -> StringUtils.isNotBlank(fac.getVal())).forEach(fac -> {
                        Optional<ProductAppConfig> pacOpt = paToUpdate.getProductAppConfigs().stream().filter(pac -> pac.getName().equals(fac.getName())).findFirst();
                        if(pacOpt.isPresent()){
                            ProductAppConfig pac = pacOpt.get();
                            this.updateConfigAppVal(pac, fac);
                        }else{
                            ProductAppConfig pac = ProductAppConfig.builder()
                                .dateAdded(new Date())
                                .multi(fac.isMulti())
                                .name(fac.getName())
                                .val(fac.getVal())
                                .build();
                            paToUpdate.getProductAppConfigs().add(pac);
                        }
                    });
                    this.productAppRepository.save(paToUpdate);
                }else{
                    List<ProductAppConfig> pacs = new ArrayList<>();
                    fa.getFeatureAppConfigs().stream().filter(fac -> StringUtils.isNotBlank(fac.getVal())).forEach(fac -> {
                        ProductAppConfig pac = ProductAppConfig.builder()
                            .name(fac.getName())
                            .dateAdded(fac.getDateAdded())
                            .multi(fac.isMulti())
                            .val(fac.getVal())
                            .build();
                        pacs.add(pac);
                    });
                    ProductApp pa = ProductApp.builder()
                        .appIdToSave(fa.getApp().getId())
                        .app(fa.getApp())
                        .productIdToSave(product.getId())
                        .product(product)
                        .productAppConfigs(pacs)
                        .build();           
                    this.saveNewProductApp(product, pa);
                }
            });
        });

        List<ProductApp> affectedPas = this.productAppRepository.findByProductId(product.getId());
        this.requestResource.updateRequestActionsBasedOnProductChanges(product.getId(), affectedPas);
    }

    private ProductAppConfig updateConfigAppVal(ProductAppConfig pac, FeatureAppConfig fac){
        if(pac.isMulti()){
            Set<String> pacVals = new HashSet<>(Arrays.asList(pac.getVal().split(",")));
            pacVals.addAll(Arrays.asList(fac.getVal().split(",")));
            pac.setVal(String.join(",", pacVals));
        }else if(comparisonUtils.singleValueCompare(pac.getVal(), fac.getVal()) < 0){
            pac.setVal(fac.getVal());
        }
        return pac;
    }

    //@PreAuthorize("hasAnyRole('ROLE_SRE','ROLE_EPM')")
    @PutMapping("/product-apps-radars/{productId}")
    public List<ProductApp> updateProductAppRadars(@PathVariable Long productId, @RequestBody List<ProductApp> productApps) {
        productApps.forEach(pa -> {
            Optional<ProductApp> paOpt = this.productAppRepository.findByProductIdAndAppId(productId,
                    pa.getAppIdToSave());
            if (paOpt.isPresent()) {
                ProductApp paToUpdate = paOpt.get();
                paToUpdate.setRadars(pa.getRadars());
                this.productAppRepository.save(paToUpdate);
            } else if(StringUtils.isNotBlank(pa.getRadars())) {
                Product product = this.productRepository.findById(productId).orElseThrow();
                pa.setProduct(product);
                App app = this.appRepository.findById(pa.getAppIdToSave()).orElseThrow();
                pa.setApp(app);
                this.productAppRepository.save(pa);
            }
        });
        return this.productAppRepository.findByProductId(productId);
    }

}
