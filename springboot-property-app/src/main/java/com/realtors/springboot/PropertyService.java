package com.realtors.springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    // Add Property
    public Property addProperty(Property property) {
        // Optional: Add validation or transformation logic before saving
        if (property.getName() == null || property.getName().trim().length() <= 2) {
            throw new IllegalArgumentException("Property name must be at least 3 characters long.");
        }
        return propertyRepository.save(property);
    }

    // Get All Properties (Unpaginated)
    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    // Detect the category and search accordingly
    /**
     * New method for categorized search.
     * Returns distinct search results grouped by city, state, locality, country, and pincode.
     */
    public Map<String, List<String>> searchPropertiesCategorized(String query) {
        Map<String, List<String>> categorizedResults = new HashMap<>();

        // Populate categories with distinct values
        categorizedResults.put("city", propertyRepository.findDistinctCities(query));
        categorizedResults.put("state", propertyRepository.findDistinctStates(query));
        categorizedResults.put("locality", propertyRepository.findDistinctLocalities(query));
        categorizedResults.put("country", propertyRepository.findDistinctCountries(query));
        categorizedResults.put("pincode", propertyRepository.findDistinctPincodes(query));

        return categorizedResults;
    }

//    // Automatically detect the category of the search term
//    private String detectCategory(String searchTerm) {
//        // Check if the search term is numeric (pincode detection)
//        if (searchTerm.matches("\\d{6}")) {
//            return "pincode";
//        }
//
//        // Check if the search term is a country (you can maintain a list of countries or use an API)
//        List<String> countries = Arrays.asList("India", "USA", "Canada", "UK"); // Sample list
//        if (countries.contains(searchTerm)) {
//            return "country";
//        }
//
//        // Check if the search term is a city (check if it's a known city, or you could use an API for detection)
//        List<String> cities = Arrays.asList("Jaipur", "New York", "Los Angeles"); // Sample list
//        if (cities.contains(searchTerm)) {
//            return "city";
//        }
//
//        // Check if the search term matches a known state (you can maintain a list or use a geo API)
//        List<String> states = Arrays.asList("Rajasthan", "California", "Texas"); // Sample list
//        if (states.contains(searchTerm)) {
//            return "state";
//        }
//
//        // If none of the above, consider it as a locality
//        return "locality";
//    }

    // Get Paginated Properties
    public Page<Property> getProperties(Pageable pageable) {
        return propertyRepository.findAll(pageable);
    }

    // Get Filtered Properties with Pagination
    public Page<Property> getFilteredProperties(
            String city, String state, String locality, String country, String pincode,
            Integer minCost, Integer maxCost, Integer minArea, Integer maxArea,
            String propertyType, String details, Pageable pageable) {

        return propertyRepository.findFilteredProperties(
                city, state, locality, country, pincode,
                minCost, maxCost, minArea, maxArea,
                propertyType, details, pageable);
    }

    // Delete Property by ID
    public boolean deletePropertyById(Long propertyId) {
        Optional<Property> property = propertyRepository.findById(propertyId);
        if (property.isPresent()) {
            propertyRepository.deleteById(propertyId);
            return true;
        } else {
            return false;
        }
    }

    // Update Property
    public Property editPropertyById(Long propertyId, Property updatedProperty) {
        Optional<Property> existingProperty = propertyRepository.findById(propertyId);
        if (existingProperty.isPresent()) {
            Property property = existingProperty.get();
            property.setName(updatedProperty.getName());
            property.setType(updatedProperty.getType());
            property.setCost(updatedProperty.getCost());
            property.setArea(updatedProperty.getArea());
            property.setDetails(updatedProperty.getDetails());
            property.setAddress(updatedProperty.getAddress());

            // Any other fields can be updated based on changes in Property.java
//            existingProperty.setCity(updatedProperty.getCity());
//            existingProperty.setState(updatedProperty.getState());
//            existingProperty.setLocality(updatedProperty.getLocality());
//            existingProperty.setCountry(updatedProperty.getCountry());
//            existingProperty.setPincode(updatedProperty.getPincode());
            return propertyRepository.save(property);
        } else {
            throw new IllegalArgumentException("Property not found with ID: " + propertyId);
        }
    }
}
