package com.mercadona.eanproductconsult.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mercadona.eanproductconsult.model.Product;

public interface ProductRepository extends JpaRepository<Product, String> {

}