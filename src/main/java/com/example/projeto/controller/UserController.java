package com.example.projeto.controller;

import com.example.projeto.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "Usuário", description = "APIs de gerenciamento de usuários")
public class UserController {

    private final List<UserDTO> usuarios = new ArrayList<>();
    private int nextId = 1;

    // CREATE
    @PostMapping
    @Operation(summary = "Salva um usuário", description = "Cria e salva um novo usuário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário salvo com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Os dados do usuário estão incorretos.")
    })
    public ResponseEntity<?> save(@Valid @RequestBody UserDTO usuarioDTO) {
        if (usuarioDTO.getNome() == null || usuarioDTO.getEmail() == null ||
                usuarioDTO.getNome().isBlank() || usuarioDTO.getEmail().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", "Nome e e-mail são obrigatórios."));
        }

        usuarioDTO.setId(nextId++);
        usuarios.add(usuarioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioDTO);
    }

    // READ ALL
    @GetMapping
    @Operation(summary = "Lista todos os usuários", description = "Retorna todos os usuários cadastrados.")
    public ResponseEntity<?> findAll() {
        if (usuarios.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensagem", "Nenhum usuário encontrado."));
        }
        return ResponseEntity.ok(usuarios);
    }

    // READ ONE
    @GetMapping("/{id}")
    @Operation(summary = "Busca usuário por ID", description = "Retorna um usuário específico pelo ID.")
    public ResponseEntity<?> findById(@PathVariable Integer id) {
        Optional<UserDTO> user = usuarios.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();

        return user.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("erro", "Usuário com ID " + id + " não encontrado.")));
    }

    // UPDATE
    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um usuário", description = "Atualiza os dados de um usuário existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso!"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })
    public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody UserDTO updatedUser) {
        Optional<UserDTO> existingUserOpt = usuarios.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();

        if (existingUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", "Usuário com ID " + id + " não encontrado."));
        }

        if (updatedUser.getNome() == null || updatedUser.getNome().isBlank() ||
                updatedUser.getEmail() == null || updatedUser.getEmail().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", "Nome e e-mail são obrigatórios para atualização."));
        }

        UserDTO existingUser = existingUserOpt.get();
        existingUser.setNome(updatedUser.getNome());
        existingUser.setEmail(updatedUser.getEmail());

        return ResponseEntity.ok(existingUser);
    }

    // DELETE
    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um usuário", description = "Remove um usuário pelo ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso!"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        boolean removed = usuarios.removeIf(u -> u.getId().equals(id));

        if (removed) {
            return ResponseEntity.ok(Map.of("mensagem", "Usuário com ID " + id + " foi excluído com sucesso."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", "Usuário com ID " + id + " não encontrado para exclusão."));
        }
    }
}

