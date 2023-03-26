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
import com.mercadona.eanproductconsult.model.Destination;
import com.mercadona.eanproductconsult.service.DestinationService;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DestinationControllerTests {

    private static final String DESTINATION_URL = "/api/v1/destination/";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DestinationService destinationService;

    private static Destination destination;

    @BeforeAll
    static void setUp() {
        destination = new Destination();
        destination.setId("1");
    }

    @Test
    public void getDestination_whenDestinationExists_returnsDestination() throws Exception {

        // Given
        String existentId = "1";
        
        // When and Then
        when(destinationService.getDestination(existentId)).thenReturn(Optional.of(destination));

        mockMvc.perform(MockMvcRequestBuilders.get(DESTINATION_URL + existentId))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(destination.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(destination.getName()));
    }

    @Test
    public void getDestination_whenDestinationDoesNotExist_returns404NotFound() throws Exception {

        // Given
        String nonExistentId = "5";

        // When and Then
        when(destinationService.getDestination(nonExistentId)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get(DESTINATION_URL + nonExistentId))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Destination with ID " + nonExistentId + " not found"));
    }

    @Test
    public void getDestination_whitInvalidIdFormat_returns400BadRequest() throws Exception {

        // Given
        String invalidId = "1";

        // When and Then
        when(destinationService.getDestination(invalidId)).thenThrow(new InvalidIdException("ID must be a number between 0 and 9 excluding 7"));

        mockMvc.perform(MockMvcRequestBuilders.get(DESTINATION_URL + invalidId))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("ID must be a number between 0 and 9 excluding 7"));
    }

    @Test
    public void createDestination_withValidId_returns201Created() throws Exception {
        
        // Given
        Destination valiDestination = new Destination();
        valiDestination.setId("2");


        mockMvc.perform(MockMvcRequestBuilders.post(DESTINATION_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(valiDestination)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Destination with ID " + valiDestination.getId() + " created successfully"));
    }

    @Test
    public void createDestination_withUnvalidId_returns400BadRequest() throws Exception {
        
        // Given
        Destination invalidDestination = new Destination();
        invalidDestination.setId("123");


        mockMvc.perform(MockMvcRequestBuilders.post(DESTINATION_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDestination)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("[ID must be a number between 0 and 9 excluding 7]"));
    }

    @Test
    public void updateDestination_withValidId_returns200Ok() throws Exception {

        // When and Then
        when(destinationService.getDestination(destination.getId())).thenReturn(Optional.of(destination));
        doNothing().when(destinationService).save(destination);


        mockMvc.perform(MockMvcRequestBuilders.put(DESTINATION_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(destination)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Destination with ID " + destination.getId() + " updated successfully"));
    }

    @Test
    public void deleteDestination_whenDestinationExists_returns200Ok() throws Exception {

        // Given
        String existentId = "1";
        
        // When and Then
        when(destinationService.getDestination(existentId)).thenReturn(Optional.of(destination));
        doNothing().when(destinationService).delete(existentId);

        mockMvc.perform(MockMvcRequestBuilders.delete(DESTINATION_URL + existentId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Destination with ID " + existentId + " deleted successfully"));
    }

    @Test
    public void deleteDestination_whenDestinationDoesNotExist_returns404NotFound() throws Exception {

        // Given
        String nonExistentId = "5";
        
        // When and Then
        when(destinationService.getDestination(nonExistentId)).thenReturn(Optional.empty());
    
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/destination/{id}", nonExistentId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void deleteDestination_whenIdIsInvalid_returns400BadRequest() throws Exception {
        
        // Given
        String invalidId = "1890";

        // When and Then
        when(destinationService.getDestination(invalidId)).thenThrow(new InvalidIdException("ID must be a number between 0 and 9 excluding 7"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/destination/{id}", invalidId))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}