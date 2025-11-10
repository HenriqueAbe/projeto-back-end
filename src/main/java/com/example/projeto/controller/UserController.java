package com.example.projeto.controller;

import com.example.projeto.exception.BusinessException;
import com.example.projeto.model.User;
import com.example.projeto.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<User>> listar() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<User> buscarPorId(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) throw new BusinessException("USR001", "Usuário não encontrado.");
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<User> criar(@RequestBody User user) {
        if (user.getEmail() == null || user.getPassword() == null) {
            throw new BusinessException("USR002", "E-mail e senha são obrigatórios.");
        }
        return ResponseEntity.ok(userService.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<User> atualizar(@PathVariable Long id, @RequestBody User user) {
        return ResponseEntity.ok(userService.update(id, user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
