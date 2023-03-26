package com.mercadona.eanproductconsult.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Entity
@Table(name = "products")
public class Product {

    @Id
    @JsonProperty("ean")
    @NotNull(message = "EAN is required")
    @Pattern(regexp = "^\\d{13}$", message = "EAN must be a 13-digit numeric string")
    @Column(name = "ean")
    private String ean;
    
    @JsonProperty("name")
    @NotNull(message = "Name is required")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
	@Column(name = "name")
	private String name;

    @JsonProperty("price")
    @NotNull(message = "Price is required")
    @PositiveOrZero(message = "Price must be positive or zero")
    @Digits(integer = 4, fraction = 2, message = "Price must have up to 4 digits and 2 decimal places")
    @Column(name = "price")
    private BigDecimal price;

    @JsonProperty("description")
    @Size(min = 5, max = 200, message = "Description must be between 5 and 200 characters")
    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "id_provider")
    private Provider provider;

    @ManyToOne
    @JoinColumn(name = "id_destination")
    private Destination destination;

    public String getProductCode() {
        return ean.substring(7, 12);
    }
}
