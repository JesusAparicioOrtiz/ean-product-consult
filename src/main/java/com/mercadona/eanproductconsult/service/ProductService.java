package com.mercadona.eanproductconsult.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mercadona.eanproductconsult.exception.InvalidEanException;
import com.mercadona.eanproductconsult.model.Product;
import com.mercadona.eanproductconsult.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    public Optional<Product> getProduct(String ean) {
        if (!ean.matches("^\\d{13}$")) {
            throw new InvalidEanException("El código EAN no cumple con el formato esperado");
        } else {
            return productRepository.findById(ean);
        }
    }

    public void save(Product product) {
        productRepository.save(product);
    }

    @Transactional
    public void delete(String ean) {
        productRepository.deleteById(ean);
    }
}
