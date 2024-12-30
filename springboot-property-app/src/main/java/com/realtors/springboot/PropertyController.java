package com.realtors.springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000") // Update with your frontend URL
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    // Add Property
    @PostMapping("/add-property")
    public ResponseEntity<?> addProperty(@RequestBody Property property) {
        try {
            if (property.getName() == null || property.getName().trim().length() <= 2) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", "failure");
                errorResponse.put("message", "Property name must be at least 3 characters long.");
                errorResponse.put("httpCode", HttpStatus.BAD_REQUEST.value());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            Property savedProperty = propertyService.addProperty(property);

            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("status", "success");
            successResponse.put("propertyId", savedProperty.getPropertyId());
            successResponse.put("httpCode", HttpStatus.OK.value());
            return ResponseEntity.ok(successResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "failure");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("httpCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Get Filtered Properties with Pagination
    @GetMapping("/properties")
    public ResponseEntity<?> getAllProperties(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String locality,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String pincode,
            @RequestParam(required = false) Integer minCost,
            @RequestParam(required = false) Integer maxCost,
            @RequestParam(required = false) Integer minArea,
            @RequestParam(required = false) Integer maxArea,
            @RequestParam(required = false) String propertyType,
            @RequestParam(required = false) String details,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int limit) {

        try {
            // Create Pageable object (Spring Data JPA requires 0-indexed pages)
            Pageable pageable = PageRequest.of(page - 1, limit);

            // Fetch filtered and paginated properties
            Page<Property> propertiesPage = propertyService.getFilteredProperties(
                    city, state, locality, country, pincode,
                    minCost, maxCost, minArea, maxArea,
                    propertyType, details, pageable);

            if (propertiesPage.isEmpty()) {
                Map<String, Object> emptyResponse = new HashMap<>();
                emptyResponse.put("status", "success");
                emptyResponse.put("message", "No properties found.");
                emptyResponse.put("data", propertiesPage.getContent());
                emptyResponse.put("httpCode", HttpStatus.OK.value());
                return ResponseEntity.ok(emptyResponse);
            }

            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("status", "success");
            successResponse.put("data", propertiesPage.getContent());
            successResponse.put("totalElements", propertiesPage.getTotalElements());
            successResponse.put("totalPages", propertiesPage.getTotalPages());
            successResponse.put("currentPage", propertiesPage.getNumber() + 1);
            successResponse.put("httpCode", HttpStatus.OK.value());

            return ResponseEntity.ok(successResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "failure");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("httpCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    // Search Properties by Category
    // Search properties by automatically detecting the category
//    @GetMapping("/search")
//    public ResponseEntity<?> searchProperties(@RequestParam("query") String query){
//        try {
//            List<Property> properties = propertyService.searchProperties(query);
//            return ResponseEntity.ok(properties);
//        } catch (Exception e) {
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("status", "failure");
//            errorResponse.put("message", e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//        }
//    }
    // New Categorized Search Endpoint
    @GetMapping("/search")
    public ResponseEntity<?> propertySearch(@RequestParam("query") String query) {
        try {
            if (query == null || query.trim().isEmpty()) {
                Map<String, List<String>> emptyResults = new HashMap<>();
                emptyResults.put("city", new ArrayList<>());
                emptyResults.put("state", new ArrayList<>());
                emptyResults.put("locality", new ArrayList<>());
                emptyResults.put("country", new ArrayList<>());
                emptyResults.put("pincode", new ArrayList<>());
                return ResponseEntity.ok(emptyResults);
            }

            Map<String, List<String>> searchResults = propertyService.searchPropertiesCategorized(query);
            return ResponseEntity.ok(searchResults);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "failure");
            errorResponse.put("message", "An error occurred while processing the search.");
            errorResponse.put("httpCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    // Delete Property
    @DeleteMapping("/delete-property/{id}")
    public ResponseEntity<?> deleteProperty(@PathVariable Long id) {
        try {
            boolean isDeleted = propertyService.deletePropertyById(id);
            if (isDeleted) {
                Map<String, Object> successResponse = new HashMap<>();
                successResponse.put("status", "success");
                successResponse.put("message", "Property deleted successfully.");
                successResponse.put("httpCode", HttpStatus.OK.value());
                return ResponseEntity.ok(successResponse);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", "failure");
                errorResponse.put("message", "Property not found.");
                errorResponse.put("httpCode", HttpStatus.NOT_FOUND.value());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "failure");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("httpCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Edit Property
    @PutMapping("/edit-property/{id}")
    public ResponseEntity<?> editProperty(@PathVariable Long id, @RequestBody Property updatedProperty) {
        try {
            Property editedProperty = propertyService.editPropertyById(id, updatedProperty);
            if (editedProperty != null) {
                Map<String, Object> successResponse = new HashMap<>();
                successResponse.put("status", "success");
                successResponse.put("data", editedProperty);
                successResponse.put("httpCode", HttpStatus.OK.value());
                return ResponseEntity.ok(successResponse);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", "failure");
                errorResponse.put("message", "Property not found.");
                errorResponse.put("httpCode", HttpStatus.NOT_FOUND.value());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "failure");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("httpCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}