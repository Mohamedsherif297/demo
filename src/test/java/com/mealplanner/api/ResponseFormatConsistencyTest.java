package com.mealplanner.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class to verify response format consistency across all endpoints
 * Validates Requirements 13.1, 13.2, 13.3, 13.4
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@SuppressWarnings("null")
public class ResponseFormatConsistencyTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Test that paginated responses include all required metadata
     * Validates Requirement 13.3
     */
    @Test
    public void testPaginatedResponseStructure() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/meals")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        // Verify pagination metadata fields exist
        assertTrue(jsonNode.has("content"), "Response should have 'content' field");
        assertTrue(jsonNode.has("totalElements"), "Response should have 'totalElements' field");
        assertTrue(jsonNode.has("totalPages"), "Response should have 'totalPages' field");
        assertTrue(jsonNode.has("number") || jsonNode.has("currentPage"), 
                "Response should have 'number' or 'currentPage' field");
        assertTrue(jsonNode.has("size") || jsonNode.has("pageSize"), 
                "Response should have 'size' or 'pageSize' field");
    }

    /**
     * Test that error responses follow standardized format
     * Validates Requirement 13.2
     */
    @Test
    public void testErrorResponseFormat() throws Exception {
        // Request a non-existent meal to trigger 404 error
        MvcResult result = mockMvc.perform(get("/api/meals/99999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        // Verify all required error response fields exist
        assertTrue(jsonNode.has("timestamp"), "Error response should have 'timestamp' field");
        assertTrue(jsonNode.has("status"), "Error response should have 'status' field");
        assertTrue(jsonNode.has("error"), "Error response should have 'error' field");
        assertTrue(jsonNode.has("message"), "Error response should have 'message' field");
        assertTrue(jsonNode.has("path"), "Error response should have 'path' field");

        // Verify status code matches
        assertEquals(404, jsonNode.get("status").asInt(), "Status code should be 404");
    }

    /**
     * Test that date fields use ISO 8601 format
     * Validates Requirement 13.4
     */
    @Test
    public void testISO8601DateFormat() throws Exception {
        // This test would need actual subscription data to verify date format
        // For now, we verify the configuration is in place
        
        // Test that error response timestamp uses ISO 8601 format
        MvcResult result = mockMvc.perform(get("/api/meals/99999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        String timestamp = jsonNode.get("timestamp").asText();
        
        // Verify ISO 8601 format pattern (yyyy-MM-ddTHH:mm:ss)
        assertTrue(timestamp.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*"),
                "Timestamp should follow ISO 8601 format");
    }

    /**
     * Test that success responses use appropriate HTTP status codes
     * Validates Requirement 13.1
     */
    @Test
    public void testSuccessResponseStatusCodes() throws Exception {
        // Test GET request returns 200 OK
        mockMvc.perform(get("/api/meals")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Test GET request for categories returns 200 OK
        mockMvc.perform(get("/api/plans/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
