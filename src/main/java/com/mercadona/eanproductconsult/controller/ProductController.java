package com.mercadona.eanproductconsult.controller;

import java.util.Optional;

import org.apache.logging.log4j.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mercadona.eanproductconsult.model.Product;
import com.mercadona.eanproductconsult.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/product")
public class ProductController{

    @Autowired
    private ProductService productService;

    @GetMapping(path = "/{ean}")
    public ResponseEntity<Object> getProduct(@PathVariable String ean) {
        Optional<Product> product =  productService.getProduct(ean);
    
        if (!product.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Product with EAN " + ean + " not found"));
        }

        return ResponseEntity.ok(product.get());
    }

    @PostMapping(path = "/",consumes = "application/json")
    public ResponseEntity<Object> createProduct(@Valid @RequestBody Product product) {
        Optional<Product> existingProduct =  productService.getProduct(product.getEan());
    
        if (existingProduct.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponse("Product with EAN " + product.getEan() + " already exists"));
        }

        productService.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("Product created successfully"));
    }

    @PutMapping(path = "/",consumes = "application/json")
    public ResponseEntity<Object> updateProduct(@Valid @RequestBody Product product) {
        Optional<Product> existingProduct =  productService.getProduct(product.getEan());
    
        if (!existingProduct.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Product with EAN " + product.getEan() + " not found"));
        }

        productService.save(product);
        return ResponseEntity.ok(new MessageResponse("Product updated successfully"));
    }

    @DeleteMapping(path = "/{ean}", produces = "application/json")
    public ResponseEntity<Object> deleteProduct(@PathVariable String ean) {
        Optional<Product> product =  productService.getProduct(ean);
    
        if (!product.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Product with EAN " + ean + " not found"));
        }

        productService.delete(ean);
        return ResponseEntity.ok(new MessageResponse("Product deleted successfully"));
    }
    
}
