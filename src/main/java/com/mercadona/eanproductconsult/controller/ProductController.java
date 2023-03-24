package com.mercadona.eanproductconsult.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mercadona.eanproductconsult.model.Product;

@RestController
@RequestMapping("/api/v1/product")
public class ProductController{

    @GetMapping("/{ean}")
    public String getProduct(@PathVariable String ean) {
        //return new Product();
        return "test";
    }
    
}
