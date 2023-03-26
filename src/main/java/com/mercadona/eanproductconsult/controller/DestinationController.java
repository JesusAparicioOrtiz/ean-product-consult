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

import com.mercadona.eanproductconsult.model.Destination;
import com.mercadona.eanproductconsult.service.DestinationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/destination")
public class DestinationController {

    @Autowired
    private DestinationService destinationService;

    @Cacheable( value = "destinations")
    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> getDestination(@PathVariable String id) {
        Optional<Destination> destination = destinationService.getDestination(id);

        if (!destination.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Destination with ID " + id + " not found"));
        }

        return ResponseEntity.ok(destination.get());
    }

    @PostMapping(path = "/", consumes = "application/json")
    public ResponseEntity<Object> createDestination(@Valid @RequestBody Destination destination) {

        Optional<Destination> existingDestination = destinationService.getDestination(destination.getId());

        if (existingDestination.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MessageResponse("Destination with ID " + destination.getId() + " already exists"));
        }

        destinationService.save(destination);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("Destination with ID " + destination.getId() + " created successfully"));
    }

    @PutMapping(path = "/", consumes = "application/json")
    public ResponseEntity<Object> updateDestination(@Valid @RequestBody Destination destination) {

        Optional<Destination> existingDestination = destinationService.getDestination(destination.getId());

        if (!existingDestination.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Destination with ID " + destination.getId() + " not found"));
        }

        destinationService.save(destination);
        return ResponseEntity.ok(new MessageResponse("Destination with ID " + destination.getId() + " updated successfully"));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> deleteDestination(@PathVariable String id) {

        Optional<Destination> destination = destinationService.getDestination(id);

        if (!destination.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Destination with ID " + id + " not found"));
        }

        destinationService.delete(id);
        return ResponseEntity.ok(new MessageResponse("Destination with ID " + id + " deleted successfully"));
    }
}
