package com.mercadona.eanproductconsult.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Entity
@Table(name = "providers")
public class Provider {

    @Id
    @JsonProperty("id")
    @NotNull(message = "ID is required")
    @Pattern(regexp = "^\\d{7}$", message = "ID must be a 7-digit numeric string")
    @Column(name = "ean")
    private String id;
    
    @JsonProperty("name")
    @NotNull(message = "Name is required")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
	@Column(name = "name")
	private String name;

}
