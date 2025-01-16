package com.realtors.springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String register(String username, String password, String role) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(User.Role.valueOf(role.toUpperCase())); // Convert String to Role enum

        // Builders require admin approval
        if (User.Role.BUILDER.equals(user.getRole())) {
            user.setApproved(false); // Default to not approved
            userRepository.save(user);
            return "Registration request sent for approval.";
        }

        // Customers and Admins are directly approved
        user.setApproved(true);
        userRepository.save(user);

        return "User registered successfully.";
    }

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!user.isApproved()) {
            throw new RuntimeException("User is not approved by Admin.");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        return user;
    }

    public List<User> getPendingApprovals() {
        return userRepository.findByRoleAndIsApproved(User.Role.BUILDER, false); // Use Role enum
    }

    public String approveBuilder(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!User.Role.BUILDER.equals(user.getRole())) { // Compare enum values directly
            throw new RuntimeException("Only Builders can be approved.");
        }

        user.setApproved(true);
        userRepository.save(user);

        return "Builder approved successfully.";
    }
}
