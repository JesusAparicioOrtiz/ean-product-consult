package com.mercadona.eanproductconsult.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mercadona.eanproductconsult.exception.InvalidIdException;
import com.mercadona.eanproductconsult.model.Destination;
import com.mercadona.eanproductconsult.repository.DestinationRepository;

import jakarta.transaction.Transactional;

@Service
public class DestinationService {
    
    @Autowired
    private DestinationRepository destinationRepository;
    
    public Optional<Destination> getDestination(String id) {
        if (!id.matches("^[0-68-9]$")) {
            throw new InvalidIdException("ID must be a number between 0 and 9 excluding 7");
        } else {
            return destinationRepository.findById(id);
        }
    }

    @Transactional
    public void save(Destination destination) {
        destinationRepository.save(destination);
    }

    @Transactional
    public void delete(String id) {
        destinationRepository.deleteById(id);
    }
}
