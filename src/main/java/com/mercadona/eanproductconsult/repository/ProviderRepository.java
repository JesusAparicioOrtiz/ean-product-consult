package com.mercadona.eanproductconsult.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mercadona.eanproductconsult.model.Provider;


public interface ProviderRepository extends JpaRepository<Provider, String> {

}