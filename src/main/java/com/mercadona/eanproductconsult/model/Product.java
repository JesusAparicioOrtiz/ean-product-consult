package com.mercadona.eanproductconsult.model;

import java.math.BigDecimal;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Entity
@Table(name = "products")
public class Product {

    @Id
    @NotNull(message = "EAN is required")
    @Pattern(regexp = "^[0-9]{13}$", message = "EAN must be a 13-digit numeric string")
    @Column(name = "ean")
    private String ean;
    
    @NotNull(message = "Name is required")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
	@Column(name = "name")
	private String name;

    @NotNull(message = "Price is required")
    @PositiveOrZero(message = "Price must be positive or zero")
    @Digits(integer = 4, fraction = 2, message = "Price must have up to 4 digits and 2 decimal places")
    @Column(name = "price")
    private BigDecimal price;

    @Size(min = 5, max = 200)
    @Column(name = "description")
    private String description;

    public String getDestination() {
        String lastDigit = ean.substring(ean.length() - 1);
        switch (lastDigit) {
            case "1":
            case "2":
            case "3":
            case "4":
            case "5":
                return "Mercadona Spain";
            case "6":
                return "Mercadona Portugal";
            case "8":
                return "Warehouse";
            case "9":
                return "Mercadona Offices";
            case "0":
                return "Hives";
            default:
                return "Unknown";
        }
    }

    public String getProvider() {
        String providerId = ean.substring(0, 7);
        switch (providerId) {
            case "8437008":
                return "Hacendado";
            default:
                return "Unknown provider";
        }
    }

}
