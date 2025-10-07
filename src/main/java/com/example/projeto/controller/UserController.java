package com.example.projeto.controller;

import com.example.projeto.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "Usuário", description = "APIs de gerenciamento de usuários")
public class UserController {

    private final List<UserDTO> usuarios = new ArrayList<>();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private int nextId = 1;

    // CREATE
    @PostMapping
    @Operation(summary = "Salva um usuário", description = "Cria e salva um novo usuário com senha.")
    public ResponseEntity<?> save(@Valid @RequestBody UserDTO usuarioDTO) {
        if (usuarioDTO.getNome() == null || usuarioDTO.getNome().isBlank() ||
                usuarioDTO.getEmail() == null || usuarioDTO.getEmail().isBlank() ||
                usuarioDTO.getPassword() == null || usuarioDTO.getPassword().isBlank()) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", "Nome, e-mail e senha são obrigatórios."));
        }

        usuarioDTO.setId(nextId++);
        // Hash da senha
        usuarioDTO.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
        usuarios.add(usuarioDTO);

        // Não retorna a senha no response
        UserDTO response = new UserDTO();
        response.setId(usuarioDTO.getId());
        response.setNome(usuarioDTO.getNome());
        response.setEmail(usuarioDTO.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // READ ALL
    @GetMapping
    @Operation(summary = "Lista todos os usuários", description = "Retorna todos os usuários cadastrados sem a senha.")
    public ResponseEntity<?> findAll() {
        if (usuarios.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensagem", "Nenhum usuário encontrado."));
        }

        // Retorna lista sem senha
        List<Map<String, Object>> listaSemSenha = new ArrayList<>();
        for (UserDTO u : usuarios) {
            listaSemSenha.add(Map.of(
                    "id", u.getId(),
                    "nome", u.getNome(),
                    "email", u.getEmail()
            ));
        }

        return ResponseEntity.ok(listaSemSenha);
    }

    // READ ONE
    @GetMapping("/{id}")
    @Operation(summary = "Busca usuário por ID", description = "Retorna um usuário específico sem senha.")
    public ResponseEntity<?> findById(@PathVariable Integer id) {
        Optional<UserDTO> user = usuarios.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", "Usuário com ID " + id + " não encontrado."));
        }

        UserDTO u = user.get();
        Map<String, Object> response = Map.of(
                "id", u.getId(),
                "nome", u.getNome(),
                "email", u.getEmail()
        );

        return ResponseEntity.ok(response);
    }

    // UPDATE
    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um usuário", description = "Atualiza dados e senha do usuário.")
    public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody UserDTO updatedUser) {
        Optional<UserDTO> existingUserOpt = usuarios.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();

        if (existingUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", "Usuário com ID " + id + " não encontrado."));
        }

        UserDTO existingUser = existingUserOpt.get();

        if (updatedUser.getNome() != null && !updatedUser.getNome().isBlank())
            existingUser.setNome(updatedUser.getNome());

        if (updatedUser.getEmail() != null && !updatedUser.getEmail().isBlank())
            existingUser.setEmail(updatedUser.getEmail());

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank())
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));

        // Retorna sem senha
        Map<String, Object> response = Map.of(
                "id", existingUser.getId(),
                "nome", existingUser.getNome(),
                "email", existingUser.getEmail()
        );

        return ResponseEntity.ok(response);
    }

    // DELETE
    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um usuário", description = "Remove um usuário pelo ID.")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        boolean removed = usuarios.removeIf(u -> u.getId().equals(id));

        if (removed) {
            return ResponseEntity.ok(Map.of("mensagem", "Usuário com ID " + id + " foi excluído com sucesso."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", "Usuário com ID " + id + " não encontrado."));
        }
    }
}
