package com.realtors.springboot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);  // Find user by username
//    Optional<User> findByUserid(String username);

    List<User> findByRoleAndIsApproved(User.Role role, boolean isApproved);  // Find users by role and approval status

    // Additional methods for checking approval of specific roles
    Optional<User> findByIdAndIsApproved(Long id, boolean isApproved);  // Find user by ID and approval status

    // Optional: If you need to get users waiting for approval
    List<User> findByRoleAndIsApprovedFalse(User.Role role);  // Find users with a role who are not approved yet
}
