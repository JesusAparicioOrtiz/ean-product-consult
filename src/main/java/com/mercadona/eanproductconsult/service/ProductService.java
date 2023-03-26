package com.mercadona.eanproductconsult.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mercadona.eanproductconsult.exception.InvalidEanException;
import com.mercadona.eanproductconsult.exception.InvalidIdException;
import com.mercadona.eanproductconsult.model.Destination;
import com.mercadona.eanproductconsult.model.Product;
import com.mercadona.eanproductconsult.model.Provider;
import com.mercadona.eanproductconsult.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DestinationService destinationService;

    @Autowired
    private ProviderService providerService;
    
    public Optional<Product> getProduct(String ean, Boolean checkProviderExistence) {

        if (!ean.matches("^\\d{13}$")) {
            throw new InvalidIdException("EAN code must be 13 digits");
        } else {
            Optional<Product> productOptional = productRepository.findById(ean);
            if (productOptional.isPresent() && checkProviderExistence){
                Product product = productOptional.get();
                if(product.getProvider() == null) {
                    // Provider is assigned by the first 7 digits of the EAN code in case it exists
                    String idProvider = product.getEan().substring(0, 7);
                    Optional<Provider> providerOptional = providerService.getProvider(idProvider);
                    if(providerOptional.isPresent()) {
                        product.setProvider(providerOptional.get());
                    } else {
                        throw new InvalidEanException("Product with EAN " + ean + " does not have a provider assigned");
                    }
                }
                
                if(product.getDestination() == null) {
                    // Destination is created in case it does not exist and assigned by the last digit of the EAN code
                    String idDestination = product.getEan().substring(product.getEan().length() - 1);
                    Optional<Destination> destinationOptional = destinationService.getDestination(idDestination);
                    if(!destinationOptional.isPresent()) {
                        Destination destination = new Destination();
                        destination.setId(idDestination);
                        destinationService.save(destination);
                        product.setDestination(destination);
                    } else {
                        product.setDestination(destinationOptional.get());
                    }
                }
            }
            return productOptional;
        }
    }

    @Transactional
    public void save(Product product) {

        // Destination is created in case it does not exist and assigned by the last digit of the EAN code
        String idDestination = product.getEan().substring(product.getEan().length() - 1);
        Optional<Destination> destinationOptional = destinationService.getDestination(idDestination);
        if(!destinationOptional.isPresent()) {
            Destination destination = new Destination();
            destination.setId(idDestination);
            destinationService.save(destination);
            product.setDestination(destination);
        } else {
            product.setDestination(destinationOptional.get());
        }

        // Provider is assigned by the first 7 digits of the EAN code in case it exists
        String idProvider = product.getEan().substring(0, 7);
        Optional<Provider> providerOptional = providerService.getProvider(idProvider);
        if(providerOptional.isPresent()) {
            product.setProvider(providerOptional.get());
        }

        productRepository.save(product);
    }

    @Transactional
    public void delete(String ean) {
        productRepository.deleteById(ean);
    }
}
