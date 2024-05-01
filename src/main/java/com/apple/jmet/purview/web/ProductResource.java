package com.apple.jmet.purview.web;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
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

import com.apple.jmet.purview.domain.Product;
import com.apple.jmet.purview.domain.ProductApp;
import com.apple.jmet.purview.domain.Site;
import com.apple.jmet.purview.dto.ProductListDto;
import com.apple.jmet.purview.repository.ProductAppRepository;
import com.apple.jmet.purview.repository.ProductListDtoRepository;
import com.apple.jmet.purview.repository.ProductRepository;
import com.apple.jmet.purview.repository.SiteRepository;

/**
 * REST controller for managing {@link com.apple.jmet.purview.domain.Product}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ProductResource {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private ProductListDtoRepository productListDtoRepository;
    @Autowired
    private ProductAppRepository productAppRepository;
    @Autowired
    private RequestResource requestResource;

    //@PreAuthorize("hasAnyRole('ROLE_SRE','ROLE_EPM')")
    @PostMapping("/products")
    public Product createProduct(@RequestBody Product product) {
        if(this.productRepository.findByCode(product.getCode()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product with code " + product.getCode() + " already exists");
        }
        return productRepository.save(product);
    }

    //@PreAuthorize("hasAnyRole('ROLE_SRE','ROLE_EPM')")
    @PutMapping("/products/{id}")
    public Product updateProduct(@PathVariable(value = "id", required = false) final Long id,
            @RequestBody Product product) {
        return productRepository.save(product);
    }

    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/products-list-dto")
    public List<ProductListDto> getAllProductsListDto() {
        return productListDtoRepository.findAllBy();
    }

    @GetMapping("/products-by-site-category/{siteId}")
    public List<Product> getAllProductsBySiteCategory(@PathVariable Long siteId) {
        Optional<Site> siteOpt = this.siteRepository.findById(siteId);
        if (siteOpt.isPresent()) {
            Site site = siteOpt.get();
            List<String> categories = Arrays.asList(StringUtils.split(site.getProductCategories(), ','));
            return this.productRepository.findByCategoryIn(categories);
        }
        return List.of();
    }

    @GetMapping("/products/{id}")
    public Product getProduct(@PathVariable Long id) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            return productOpt.get();
        }
        return null;
    }

    @GetMapping("/products-by-code/{code}")
    public Product getProduct(@PathVariable String code) {
        Optional<Product> productOpt = productRepository.findByCode(code);
        if (productOpt.isPresent()) {
            return productOpt.get();
        }
        return null;
    }

    @GetMapping("/products-active-count")
    public Integer getActiveProductsCount(@RequestParam(name = "status", required = true) String status) {
        return this.productRepository.countByStatus(status);
    }

    //@PreAuthorize("hasAnyRole('ROLE_SRE','ROLE_EPM')")
    @PutMapping("/products-update-phase/{id}")
    public Product updateProductPhase(@PathVariable(value = "id", required = false) final Long id,
            @RequestBody Product product) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            Product productToEdit = productOpt.get();
            productToEdit.setStatus(product.getStatus());
            if(product.isMp()){
                List<ProductApp> productApps = this.productAppRepository.findByProductId(productToEdit.getId());
                this.requestResource.addRequestActionsForMp(id, productApps);
            }
            return this.productRepository.save(productToEdit);
        }
        return null;
    }
}
