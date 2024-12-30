package com.realtors.springboot;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    List<Property> findByAddedById(Long userId);

    @Query("SELECT p FROM Property p WHERE " +
            "(:city IS NULL OR p.city = :city) AND " +
            "(:state IS NULL OR p.state = :state) AND " +
            "(:locality IS NULL OR p.locality = :locality) AND " +
            "(:country IS NULL OR p.country = :country) AND " +
            "(:pincode IS NULL OR p.pincode = :pincode) AND " +
            "(:minCost IS NULL OR p.cost >= :minCost) AND " +
            "(:maxCost IS NULL OR p.cost <= :maxCost) AND " +
            "(:minArea IS NULL OR p.area >= :minArea) AND " +
            "(:maxArea IS NULL OR p.area <= :maxArea) AND " +
            "(:propertyType IS NULL OR p.type = :propertyType) AND " +
            "(:details IS NULL OR p.details = :details)")
    Page<Property> findFilteredProperties(
            @Param("city") String city,
            @Param("state") String state,
            @Param("locality") String locality,
            @Param("country") String country,
            @Param("pincode") String pincode,
            @Param("minCost") Integer minCost,
            @Param("maxCost") Integer maxCost,
            @Param("minArea") Integer minArea,
            @Param("maxArea") Integer maxArea,
            @Param("propertyType") String propertyType,
            @Param("details") String details,
            Pageable pageable);

    @Query("SELECT DISTINCT p.city FROM Property p WHERE LOWER(p.city) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<String> findDistinctCities(@Param("query") String query);

    @Query("SELECT DISTINCT p.state FROM Property p WHERE LOWER(p.state) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<String> findDistinctStates(@Param("query") String query);

    @Query("SELECT DISTINCT p.locality FROM Property p WHERE LOWER(p.locality) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<String> findDistinctLocalities(@Param("query") String query);

    @Query("SELECT DISTINCT p.country FROM Property p WHERE LOWER(p.country) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<String> findDistinctCountries(@Param("query") String query);

    @Query("SELECT DISTINCT p.pincode FROM Property p WHERE p.pincode LIKE CONCAT('%', :query, '%')")
    List<String> findDistinctPincodes(@Param("query") String query);

    @Query("SELECT p FROM Property p WHERE " +
            "(:category = 'city' AND LOWER(p.city) LIKE LOWER(CONCAT('%', :query, '%'))) OR " +
            "(:category = 'state' AND LOWER(p.state) LIKE LOWER(CONCAT('%', :query, '%'))) OR " +
            "(:category = 'locality' AND LOWER(p.locality) LIKE LOWER(CONCAT('%', :query, '%'))) OR " +
            "(:category = 'country' AND LOWER(p.country) LIKE LOWER(CONCAT('%', :query, '%'))) OR " +
            "(:category = 'pincode' AND p.pincode LIKE CONCAT('%', :query, '%'))")
    Page<Property> searchPropertiesByCategory(
            @Param("query") String query,
            @Param("category") String category,
            Pageable pageable);


    void deleteById(Long id);

    Property save(Property property);
}