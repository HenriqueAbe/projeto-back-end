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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "Usuário", description = "APIs de gerenciamento de usuários")
public class UserController {

    private final List<UserDTO> usuarios = new ArrayList<>();
    private int nextId = 1;

    // CREATE
    @PostMapping
    @Operation(summary = "Salva um usuário", description = "Salva um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário salvo com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Os dados do usuário estão incorretos.")
    })
    public ResponseEntity<UserDTO> save(@Valid @RequestBody UserDTO usuarioDTO) {
        if (usuarioDTO.getNome() == null || usuarioDTO.getEmail() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        usuarioDTO.setId(nextId++);
        usuarios.add(usuarioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioDTO);
    }

    // READ ALL
    @GetMapping
    @Operation(summary = "Lista todos os usuários", description = "Retorna a lista de usuários")
    public List<UserDTO> findAll() {
        return usuarios;
    }

    // READ ONE
    @GetMapping("/{id}")
    @Operation(summary = "Busca usuário por ID", description = "Retorna um usuário específico pelo ID")
    public ResponseEntity<UserDTO> findById(@PathVariable Integer id) {
        Optional<UserDTO> user = usuarios.stream().filter(u -> u.getId().equals(id)).findFirst();
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // UPDATE
    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um usuário", description = "Atualiza os dados de um usuário existente")
    public ResponseEntity<UserDTO> update(@PathVariable Integer id, @RequestBody UserDTO updatedUser) {
        for (UserDTO user : usuarios) {
            if (user.getId().equals(id)) {
                user.setNome(updatedUser.getNome());
                user.setEmail(updatedUser.getEmail());
                return ResponseEntity.ok(user);
            }
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE
    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um usuário", description = "Remove um usuário pelo ID")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        boolean removed = usuarios.removeIf(u -> u.getId().equals(id));
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
