package com.mercadona.eanproductconsult;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

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
import com.mercadona.eanproductconsult.exception.InvalidIdException;
import com.mercadona.eanproductconsult.model.Provider;
import com.mercadona.eanproductconsult.service.ProviderService;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProviderControllerTests {

    private static final String PROVIDER_URL = "/api/v1/provider/";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProviderService providerService;

    private static Provider provider;

    @BeforeAll
    static void setUp() {
        provider = new Provider();
        provider.setId("8437008");
        provider.setName("Hacendado");
    }

    @Test
    public void getProvider_whenProviderExists_returns200Provider() throws Exception {

        // Given
        String existentId = "8437008";
        
        // When and Then
        when(providerService.getProvider(existentId)).thenReturn(Optional.of(provider));

        mockMvc.perform(MockMvcRequestBuilders.get(PROVIDER_URL + existentId))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(provider.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(provider.getName()));
    }

    @Test
    public void getProvider_whenProviderDoesNotExist_returns404NotFound() throws Exception {

        // Given
        String nonExistentId = "8437008899999";

        // When and Then
        when(providerService.getProvider(nonExistentId)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get(PROVIDER_URL + nonExistentId))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Provider with ID " + nonExistentId + " not found"));
    }

    @Test
    public void getProvider_whitInvalidIdFormat_returns400BadRequest() throws Exception {

        // Given
        String invalidId = "8437008";

        // When and Then
        when(providerService.getProvider(invalidId)).thenThrow(new InvalidIdException("ID must be a 7-digit numeric string"));

        mockMvc.perform(MockMvcRequestBuilders.get(PROVIDER_URL + invalidId))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("ID must be a 7-digit numeric string"));
    }

    @Test
    public void createProvider_withMissingId_returns400BadRequest() throws Exception {
        
        // Given
        Provider invalidProvider = new Provider();
        invalidProvider.setId(null);
        invalidProvider.setName("Provider Test");


        mockMvc.perform(MockMvcRequestBuilders.post(PROVIDER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProvider)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("[ID is required]"));
    }

    @Test
    public void createProvider_withUnvalidId_returns400BadRequest() throws Exception {
        
        // Given
        Provider invalidProvider = new Provider();
        invalidProvider.setId("123");
        invalidProvider.setName("Provider Test");


        mockMvc.perform(MockMvcRequestBuilders.post(PROVIDER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProvider)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("[ID must be a 7-digit numeric string]"));
    }

    @Test
    public void createProvider_withTooLongName_returns400BadRequest() throws Exception {

        // Given
        Provider invalidProvider = new Provider();
        invalidProvider.setId("8437008");
        invalidProvider.setName("A".repeat( 51 ) );

        mockMvc.perform(MockMvcRequestBuilders.post(PROVIDER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProvider)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("[Name must be between 3 and 50 characters]"));
    }

    @Test
    public void createProvider_withTooShortName_returns400BadRequest() throws Exception {

        // Given
        Provider invalidProvider = new Provider();
        invalidProvider.setId("8437008");
        invalidProvider.setName("Pr");

        mockMvc.perform(MockMvcRequestBuilders.post(PROVIDER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProvider)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("[Name must be between 3 and 50 characters]"));
    }

    @Test
    public void updateProvider_withMissingId_returns400BadRequest() throws Exception {

        // When and Then
        when(providerService.getProvider(provider.getId())).thenReturn(Optional.of(provider));
        doNothing().when(providerService).save(provider);


        mockMvc.perform(MockMvcRequestBuilders.put(PROVIDER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(provider)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Provider with ID " + provider.getId() + " updated successfully"));
    }

    @Test
    public void updateProvider_withUnvalidId_returns400BadRequest() throws Exception {
        
        // Given
        Provider invalidProvider = new Provider();
        invalidProvider.setId("123");
        invalidProvider.setName("Provider Test");


        mockMvc.perform(MockMvcRequestBuilders.put(PROVIDER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProvider)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("[ID must be a 7-digit numeric string]"));
    }

    @Test
    public void updateProvider_withTooLongName_returns400BadRequest() throws Exception {

        // Given
        Provider invalidProvider = new Provider();
        invalidProvider.setId("8437008");
        invalidProvider.setName("A".repeat( 51 ) );

        mockMvc.perform(MockMvcRequestBuilders.put(PROVIDER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProvider)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("[Name must be between 3 and 50 characters]"));
    }

    @Test
    public void updateProvider_withTooShortName_returns400BadRequest() throws Exception {

        // Given
        Provider invalidProvider = new Provider();
        invalidProvider.setId("8437008");
        invalidProvider.setName("Pr");

        mockMvc.perform(MockMvcRequestBuilders.put(PROVIDER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProvider)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("[Name must be between 3 and 50 characters]"));
    }

    @Test
    public void deleteProvider_whenProviderExists_returns200Ok() throws Exception {

        // Given
        String existentId = "8437008";
        
        // When and Then
        when(providerService.getProvider(existentId)).thenReturn(Optional.of(provider));
        doNothing().when(providerService).delete(existentId);

        mockMvc.perform(MockMvcRequestBuilders.delete(PROVIDER_URL + existentId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Provider with ID " + existentId + " deleted successfully"));
    }

    @Test
    public void deleteProvider_whenProviderDoesNotExist_returns404NotFound() throws Exception {

        // Given
        String nonExistentId = "8437008899999";
        
        // When and Then
        when(providerService.getProvider(nonExistentId)).thenReturn(Optional.empty());
    
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/provider/{id}", nonExistentId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void deleteProvider_whenIdIsInvalid_returns400BadRequest() throws Exception {
        
        // Given
        String invalidId = "8437008890";

        // When and Then
        when(providerService.getProvider(invalidId)).thenThrow(new InvalidIdException("ID must be a 7-digit numeric string"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/provider/{id}", invalidId))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}