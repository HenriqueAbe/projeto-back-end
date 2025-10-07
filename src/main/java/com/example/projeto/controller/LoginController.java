package com.example.projeto.controller;

import com.example.projeto.security.AuthRequest;
import com.example.projeto.security.AuthResponse;
import com.example.projeto.security.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
@AllArgsConstructor
public class LoginController {

    private final AuthService authService;

    @PostMapping
    public ResponseEntity<AuthResponse> authenticate(
            @RequestBody AuthRequest request
    ) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
}
