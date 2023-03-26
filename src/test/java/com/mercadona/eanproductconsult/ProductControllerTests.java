package com.mercadona.eanproductconsult;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadona.eanproductconsult.exception.InvalidEanException;
import com.mercadona.eanproductconsult.exception.InvalidIdException;
import com.mercadona.eanproductconsult.model.Destination;
import com.mercadona.eanproductconsult.model.Product;
import com.mercadona.eanproductconsult.model.Provider;
import com.mercadona.eanproductconsult.service.ProductService;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProductControllerTests {

    private static final String PRODUCT_URL = "/api/v1/product/";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private static Product product;

    private static Provider provider;

    private static Destination destination;

    @BeforeAll
    static void setUp() {
        product = new Product();
        product.setEan("8437008890123");
        product.setName("Product Test");
        product.setPrice(new BigDecimal("10.50"));
        product.setDescription("This is a test product");
    }

    @Test
    public void getProduct_whenProductExists_returnsProduct() throws Exception {

        // Given
        provider = new Provider();
        provider.setName("Hacendado");

        product.setProvider(provider);

        destination = new Destination();
        destination.setId(product.getEan().substring(product.getEan().length() - 1));

        product.setDestination(destination);

        String existentEan = "8437008890123";
        
        // When and Then
        when(productService.getProduct(existentEan, true)).thenReturn(Optional.of(product));

        mockMvc.perform(MockMvcRequestBuilders.get(PRODUCT_URL + existentEan))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.ean").value(product.getEan()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(product.getName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(product.getPrice().doubleValue()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(product.getDescription()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.destination.name").value("Mercadona Spain"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.provider.name").value("Hacendado"));
    }

    @Test
    public void getProduct_whenProductDoesNotExist_returnsNotFound() throws Exception {

        // Given
        String nonExistentEan = "8437008899999";

        // When and Then
        when(productService.getProduct(nonExistentEan, true)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get(PRODUCT_URL + nonExistentEan))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Product with EAN " + nonExistentEan + " not found"));
    }

    @Test
    public void getProduct_whenProviderDoesNotExist_returnsNotFound() throws Exception {

        // Given
        String nonExistentEan = "8437008890123";

        // When and Then
        when(productService.getProduct(nonExistentEan, true)).thenThrow(new InvalidEanException("Product with EAN " + nonExistentEan + " does not have a provider assigned"));

        mockMvc.perform(MockMvcRequestBuilders.get(PRODUCT_URL + nonExistentEan))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Product with EAN " + nonExistentEan + " does not have a provider assigned"));
    }

    @Test
    public void getProduct_whitInvalidEanFormat_returns400BadRequest() throws Exception {

        // Given
        String invalidEan = "8437008";

        // When and Then
        when(productService.getProduct(invalidEan, true)).thenThrow(new InvalidIdException("EAN must be a 13-digit numeric string"));

        mockMvc.perform(MockMvcRequestBuilders.get(PRODUCT_URL + invalidEan))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("EAN must be a 13-digit numeric string"));
    }

    @Test
    public void createProduct_whitValidRequestAndNonExistingEan_returns201Created() throws Exception {

        //Given
        product.setEan("8437008890123");

        // When and Then
        when(productService.getProduct(product.getEan(),false)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.post(PRODUCT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Product with EAN " + product.getEan() + " created successfully"));
    }

    @Test
    public void createProduct_whitValidRequestAndExistingEan_returns409Conflict() throws Exception {

        // When and Then
        when(productService.getProduct(product.getEan(),false)).thenReturn(Optional.of(product));
        doNothing().when(productService).save(product);

        mockMvc.perform(MockMvcRequestBuilders.post(PRODUCT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Product with EAN " + product.getEan() + " already exists"));
    }

    @Test
    public void createProduct_withInvalidEan_returns400BadRequest() throws Exception {

        // Given
        Product invalidProduct = new Product();
        invalidProduct.setEan("12345678901234");
        invalidProduct.setName("Product Test");
        invalidProduct.setPrice(new BigDecimal("10.50"));
        invalidProduct.setDescription("This is a test product");

        mockMvc.perform(MockMvcRequestBuilders.post(PRODUCT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("[EAN must be a 13-digit numeric string]"));
    }

    

    @Test
    public void createProduct_withTooLongName_returns400BadRequest() throws Exception {

        // Given
        Product invalidProduct = new Product();
        invalidProduct.setEan("8437008890123");
        invalidProduct.setName("A".repeat( 51 ) );
        invalidProduct.setPrice(new BigDecimal("10.50"));
        invalidProduct.setDescription("This is a test product");

        mockMvc.perform(MockMvcRequestBuilders.post(PRODUCT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("[Name must be between 3 and 50 characters]"));
    }

    @Test
    public void createProduct_withTooShortName_returns400BadRequest() throws Exception {

        // Given
        Product invalidProduct = new Product();
        invalidProduct.setEan("8437008890123");
        invalidProduct.setName("Pr");
        invalidProduct.setPrice(new BigDecimal("10.50"));
        invalidProduct.setDescription("This is a test product");

        mockMvc.perform(MockMvcRequestBuilders.post(PRODUCT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("[Name must be between 3 and 50 characters]"));
    }

    @Test
    public void createProduct_withNegativePrice_returnsBadRequest() throws Exception {

        // Given
        Product invalidProduct = new Product();
        invalidProduct.setEan("8437008890123");
        invalidProduct.setName("Product Test");
        invalidProduct.setPrice(new BigDecimal("-10.50"));
        invalidProduct.setDescription("This is a test product");

        mockMvc.perform(MockMvcRequestBuilders.post(PRODUCT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("[Price must be positive or zero]"));
    }

    @Test
    public void createProduct_withInvalidPriceFormat_returnsBadRequest() throws Exception {

        // Given
        Product invalidProduct = new Product();
        invalidProduct.setEan("8437008890123");
        invalidProduct.setName("Product Test");
        invalidProduct.setPrice(new BigDecimal("10000.50"));
        invalidProduct.setDescription("This is a test product");

        mockMvc.perform(MockMvcRequestBuilders.post(PRODUCT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("[Price must have up to 4 digits and 2 decimal places]"));
    }

    @Test
    public void createProduct_withTooLongDescription_returns400BadRequest() throws Exception {

        // Given
        Product invalidProduct = new Product();
        invalidProduct.setEan("8437008890123");
        invalidProduct.setName("Product Test");
        invalidProduct.setPrice(new BigDecimal("10.50"));
        invalidProduct.setDescription("A".repeat( 501 ) );

        mockMvc.perform(MockMvcRequestBuilders.post(PRODUCT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("[Description must be between 5 and 200 characters]"));
    }

    @Test
    public void createProduct_withTooShortDescription_returns400BadRequest() throws Exception {

        // Given
        Product invalidProduct = new Product();
        invalidProduct.setEan("8437008890123");
        invalidProduct.setName("Product Test");
        invalidProduct.setPrice(new BigDecimal("10.50"));
        invalidProduct.setDescription("Foo");

        mockMvc.perform(MockMvcRequestBuilders.post(PRODUCT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("[Description must be between 5 and 200 characters]"));
    }

    @Test
    public void updateProduct_whitValidRequestAndNonExistingEan_returns200Ok() throws Exception {

        // When and Then
        when(productService.getProduct(product.getEan(),false)).thenReturn(Optional.empty());
        doNothing().when(productService).save(product);

        mockMvc.perform(MockMvcRequestBuilders.put(PRODUCT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Product with EAN " + product.getEan() + " not found"));
    }

    @Test
    public void updateProduct_whitValidRequestAndExistingEan_returns409Conflict() throws Exception {

        // When and Then
        when(productService.getProduct(product.getEan(),false)).thenReturn(Optional.of(product));
        doNothing().when(productService).save(product);

        mockMvc.perform(MockMvcRequestBuilders.put(PRODUCT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Product with EAN " + product.getEan() + " updated successfully"));
    }

    @Test
    public void updateProduct_withInvalidEan_returns400BadRequest() throws Exception {

        // Given
        Product invalidProduct = new Product();
        invalidProduct.setEan("12345678901234");
        invalidProduct.setName("Product Test");
        invalidProduct.setPrice(new BigDecimal("10.50"));
        invalidProduct.setDescription("This is a test product");

        mockMvc.perform(MockMvcRequestBuilders.put(PRODUCT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("[EAN must be a 13-digit numeric string]"));
    }

    

    @Test
    public void updateProduct_withTooLongName_returns400BadRequest() throws Exception {

        // Given
        Product invalidProduct = new Product();
        invalidProduct.setEan("8437008890123");
        invalidProduct.setName("A".repeat( 51 ) );
        invalidProduct.setPrice(new BigDecimal("10.50"));
        invalidProduct.setDescription("This is a test product");

        mockMvc.perform(MockMvcRequestBuilders.put(PRODUCT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("[Name must be between 3 and 50 characters]"));
    }

    @Test
    public void updateProduct_withTooShortName_returns400BadRequest() throws Exception {

        // Given
        Product invalidProduct = new Product();
        invalidProduct.setEan("8437008890123");
        invalidProduct.setName("Pr");
        invalidProduct.setPrice(new BigDecimal("10.50"));
        invalidProduct.setDescription("This is a test product");

        mockMvc.perform(MockMvcRequestBuilders.put(PRODUCT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("[Name must be between 3 and 50 characters]"));
    }

    @Test
    public void updateProduct_withNegativePrice_returnsBadRequest() throws Exception {

        // Given
        Product invalidProduct = new Product();
        invalidProduct.setEan("8437008890123");
        invalidProduct.setName("Product Test");
        invalidProduct.setPrice(new BigDecimal("-10.50"));
        invalidProduct.setDescription("This is a test product");

        mockMvc.perform(MockMvcRequestBuilders.put(PRODUCT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("[Price must be positive or zero]"));
    }

    @Test
    public void updateProduct_withInvalidPriceFormat_returnsBadRequest() throws Exception {

        // Given
        Product invalidProduct = new Product();
        invalidProduct.setEan("8437008890123");
        invalidProduct.setName("Product Test");
        invalidProduct.setPrice(new BigDecimal("10000.50"));
        invalidProduct.setDescription("This is a test product");

        mockMvc.perform(MockMvcRequestBuilders.put(PRODUCT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("[Price must have up to 4 digits and 2 decimal places]"));
    }

    @Test
    public void updateProduct_withTooLongDescription_returns400BadRequest() throws Exception {

        // Given
        Product invalidProduct = new Product();
        invalidProduct.setEan("8437008890123");
        invalidProduct.setName("Product Test");
        invalidProduct.setPrice(new BigDecimal("10.50"));
        invalidProduct.setDescription("A".repeat( 501 ) );

        mockMvc.perform(MockMvcRequestBuilders.put(PRODUCT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("[Description must be between 5 and 200 characters]"));
    }

    @Test
    public void updateProduct_withTooShortDescription_returns400BadRequest() throws Exception {

        // Given
        Product invalidProduct = new Product();
        invalidProduct.setEan("8437008890123");
        invalidProduct.setName("Product Test");
        invalidProduct.setPrice(new BigDecimal("10.50"));
        invalidProduct.setDescription("Foo");

        mockMvc.perform(MockMvcRequestBuilders.put(PRODUCT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("[Description must be between 5 and 200 characters]"));
    }


    @Test
    public void deleteProduct_whenProductExists_returns200Ok() throws Exception {

        // Given
        String existentEan = "8437008890123";
        
        // When and Then
        when(productService.getProduct(existentEan,false)).thenReturn(Optional.of(product));
        doNothing().when(productService).delete(existentEan);

        mockMvc.perform(MockMvcRequestBuilders.delete(PRODUCT_URL + existentEan))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Product with EAN " + existentEan + " deleted successfully"));
    }

    @Test
    public void deleteProduct_whenProductDoesNotExist_returns404NotFound() throws Exception {

        // Given
        String nonExistentEan = "8437008899999";
        
        // When and Then
        when(productService.getProduct(nonExistentEan,false)).thenReturn(Optional.empty());
    
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/product/{ean}", nonExistentEan))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void deleteProduct_whenEanIsInvalid_returns400BadRequest() throws Exception {
        
        // Given
        String invalidEan = "8437008890";

        // When and Then
        when(productService.getProduct(invalidEan,false)).thenThrow(new InvalidIdException("EAN must be a 13-digit numeric string"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/product/{ean}", invalidEan))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}