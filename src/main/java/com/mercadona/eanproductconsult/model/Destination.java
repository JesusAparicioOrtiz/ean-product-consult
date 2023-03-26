package com.mercadona.eanproductconsult.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Entity
@Table(name = "destination")
public class Destination {

    @Id
    @JsonProperty("id")
    @NotNull(message = "ID is required")
    @Pattern(regexp = "^[0-68-9]$", message = "ID must be a number between 0 and 9 excluding 7")
    @Column(name = "id")
    private String id;
    
    public String getName() {
        switch (id) {
            case "6":
                return "Mercadona Portugal";
            case "8":
                return "Warehouse";
            case "9":
                return "Mercadona Offices";
            case "0":
                return "Hives";
            default:
                return "Mercadona Spain";
        }
    }
}
