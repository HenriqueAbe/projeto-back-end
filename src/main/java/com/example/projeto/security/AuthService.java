package com.example.projeto.security;

import com.example.projeto.model.User;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class AuthService {
    //private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthResponse authenticate(AuthRequest request) {
        // Busca usuário na lista de usuários em memória
        UserAuthentication user = new UserAuthentication();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // só para teste

        String jwtToken = jwtService.generateToken(user);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(jwtToken);
        authResponse.setEmail(user.getEmail());
        authResponse.setExpires(new Date(System.currentTimeMillis() + 1000 * 60 * 24));

        return authResponse;
    }

}
