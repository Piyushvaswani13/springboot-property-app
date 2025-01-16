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

    @Autowired
    private UserRepository userRepository;


    // Add Property
    public Property addProperty(Property property, Long userId) {
        // Find the user by userId
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Ensure that the user is either a builder or an admin
        if (!user.getRole().equals(User.Role.BUILDER) && !user.getRole().equals(User.Role.ADMIN)) {
            throw new IllegalArgumentException("Only builders and admins can add properties.");
        }

        // If user is a builder, ensure they are approved
        if (user.getRole().equals(User.Role.BUILDER) && !user.isApproved()) {
            throw new IllegalArgumentException("Builder is not approved yet. Please wait for admin approval.");
        }

        // Set the createdBy and modifiedBy fields to the user object
        property.setCreatedBy(user);
        property.setModifiedBy(user);  // Initially, the same user who added it

        // Save the property
        return propertyRepository.save(property);
    }


    // Get All Properties (Unpaginated)
    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    // Detect the category and search accordingly
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

    public Page<Property> getPropertiesByUserId(Long userId, String city, String state, String locality,
                                                String country, String pincode, Integer minCost, Integer maxCost,
                                                Integer minArea, Integer maxArea, String propertyType, String details, Pageable pageable) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Implement logic to filter properties based on userId and other criteria
        return propertyRepository.findFilteredPropertiesByUserId(
                userId,user.getRole().name(), city, state, locality, country, pincode,
                minCost, maxCost, minArea, maxArea, propertyType, details, pageable);
    }
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

    // Delete Property by ID (Admin can delete any, Builder can delete only their own)
    public boolean deletePropertyById(Long propertyId, Long userId) {
        Optional<Property> property = propertyRepository.findById(propertyId);
        if (property.isPresent()) {
            Property existingProperty = property.get();
            User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

            // Admin can delete any property
            if (user.getRole().equals(User.Role.ADMIN)) {
                propertyRepository.deleteById(propertyId);
                return true;
            }

            // Builder can delete only their own properties
            if (user.getRole().equals(User.Role.BUILDER) && existingProperty.getCreatedBy().getId().equals(userId)) {
                propertyRepository.deleteById(propertyId);
                return true;
            } else {
                throw new IllegalArgumentException("Builder can only delete their own properties.");
            }
        } else {
            return false;
        }
    }

    // Update Property (Admin can update any, Builder can update only their own)
    public Property editPropertyById(Long propertyId, Property updatedProperty, Long userId) {
        Optional<Property> existingProperty = propertyRepository.findById(propertyId);
        if (existingProperty.isPresent()) {
            Property property = existingProperty.get();
            User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

            // Admin can edit any property
            if (user.getRole().equals(User.Role.ADMIN)) {
                property.setName(updatedProperty.getName());
                property.setType(updatedProperty.getType());
                property.setCost(updatedProperty.getCost());
                property.setArea(updatedProperty.getArea());
                property.setDetails(updatedProperty.getDetails());
                property.setAddress(updatedProperty.getAddress());
                property.setModifiedBy(user);
                return propertyRepository.save(property);
            }

            // Builder can edit only their own properties
            if (user.getRole().equals(User.Role.BUILDER) && property.getCreatedBy().getId().equals(userId)) {
                property.setName(updatedProperty.getName());
                property.setType(updatedProperty.getType());
                property.setCost(updatedProperty.getCost());
                property.setArea(updatedProperty.getArea());
                property.setDetails(updatedProperty.getDetails());
                property.setAddress(updatedProperty.getAddress());
                property.setModifiedBy(user);
                return propertyRepository.save(property);
            } else {
                throw new IllegalArgumentException("Builder can only edit their own properties.");
            }
        } else {
            throw new IllegalArgumentException("Property not found with ID: " + propertyId);
        }
    }
}
