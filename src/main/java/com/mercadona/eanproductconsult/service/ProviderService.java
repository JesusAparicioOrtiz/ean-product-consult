package com.mercadona.eanproductconsult.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mercadona.eanproductconsult.exception.InvalidIdException;
import com.mercadona.eanproductconsult.model.Provider;
import com.mercadona.eanproductconsult.repository.ProviderRepository;

import jakarta.transaction.Transactional;

@Service
public class ProviderService {
    
    @Autowired
    private ProviderRepository providerRepository;
    
    public Optional<Provider> getProvider(String id) {
        if (!id.matches("^\\d{7}$")) {
            throw new InvalidIdException("ID must be 7 digits");
        } else {
            return providerRepository.findById(id);
        }
    }

    @Transactional
    public void save(Provider provider) {
        providerRepository.save(provider);
    }

    @Transactional
    public void delete(String id) {
        providerRepository.deleteById(id);
    }
}
