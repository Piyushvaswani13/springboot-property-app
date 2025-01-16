package com.realtors.springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestParam String username,
                                           @RequestParam String password,
                                           @RequestParam String role) {
        return ResponseEntity.ok(authService.register(username, password, role));
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestParam String username,
                                      @RequestParam String password) {
        return ResponseEntity.ok(authService.login(username, password));
    }

    @GetMapping("/pending-approvals")
    public ResponseEntity<List<User>> getPendingApprovals() {
        return ResponseEntity.ok(authService.getPendingApprovals());
    }

    @PostMapping("/approve-builder")
    public ResponseEntity<String> approveBuilder(@RequestParam Long id) {
        return ResponseEntity.ok(authService.approveBuilder(id));
    }
}

