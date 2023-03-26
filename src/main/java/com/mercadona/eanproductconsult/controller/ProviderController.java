package com.mercadona.eanproductconsult.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mercadona.eanproductconsult.model.Provider;
import com.mercadona.eanproductconsult.service.ProviderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/provider")
public class ProviderController {

    @Autowired
    private ProviderService providerService;

    @Cacheable( value = "providers")
    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> getProvider(@PathVariable String id) {
        Optional<Provider> provider = providerService.getProvider(id);

        if (!provider.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Provider with ID " + id + " not found"));
        }

        return ResponseEntity.ok(provider.get());
    }

    @PostMapping(path = "/", consumes = "application/json")
    public ResponseEntity<Object> createProvider(@Valid @RequestBody Provider provider) {

        Optional<Provider> existingProvider = providerService.getProvider(provider.getId());

        if (existingProvider.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MessageResponse("Provider with ID " + provider.getId() + " already exists"));
        }

        providerService.save(provider);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("Provider with ID " + provider.getId() + " created successfully"));
    }

    @PutMapping(path = "/", consumes = "application/json")
    public ResponseEntity<Object> updateProvider(@Valid @RequestBody Provider provider) {

        Optional<Provider> existingProvider = providerService.getProvider(provider.getId());

        if (!existingProvider.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Provider with ID " + provider.getId() + " not found"));
        }

        providerService.save(provider);
        return ResponseEntity.ok(new MessageResponse("Provider with ID " + provider.getId() + " updated successfully"));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> deleteProvider(@PathVariable String id) {

        Optional<Provider> provider = providerService.getProvider(id);

        if (!provider.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Provider with ID " + id + " not found"));
        }

        providerService.delete(id);
        return ResponseEntity.ok(new MessageResponse("Provider with ID " + id + " deleted successfully"));
    }
}
