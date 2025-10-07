package com.example.projeto.controller;

import com.example.projeto.dto.CategoriaDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/categoria")
@Tag(name = "Categorias", description = "CRUD de categorias de produtos com validações.")
public class CategoriaController {

    private final List<CategoriaDTO> categorias = new ArrayList<>();
    private final Map<Integer, List<Integer>> dependenciasProdutos = new HashMap<>();
    // Simula produtos vinculados à categoria (chave: categoriaId, valor: lista de ids de produtos)
    private int nextId = 1;

    // CREATE
    @PostMapping
    @Operation(summary = "Cria uma nova categoria", description = "Cadastra uma categoria de produto")
    public ResponseEntity<?> create(@RequestBody CategoriaDTO categoria) {
        if (categoria.getNome() == null || categoria.getNome().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", "O campo 'nome' é obrigatório."));
        }

        categoria.setId(nextId++);
        categorias.add(categoria);
        dependenciasProdutos.put(categoria.getId(), new ArrayList<>());

        return ResponseEntity.status(HttpStatus.CREATED).body(categoria);
    }

    // READ ALL
    @GetMapping
    @Operation(summary = "Lista todas as categorias", description = "Retorna a lista de categorias cadastradas")
    public ResponseEntity<?> findAll() {
        if (categorias.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensagem", "Nenhuma categoria encontrada."));
        }
        return ResponseEntity.ok(categorias);
    }

    // READ BY ID
    @GetMapping("/{id}")
    @Operation(summary = "Busca categoria por ID", description = "Retorna uma categoria específica pelo ID")
    public ResponseEntity<?> findById(@PathVariable Integer id) {
        Optional<CategoriaDTO> categoria = categorias.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();

        return categoria.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("erro", "Categoria com ID " + id + " não encontrada.")));
    }

    // UPDATE
    @PutMapping("/{id}")
    @Operation(summary = "Atualiza categoria", description = "Edita nome ou descrição da categoria")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody CategoriaDTO updated) {
        Optional<CategoriaDTO> categoriaOpt = categorias.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();

        if (categoriaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", "Categoria com ID " + id + " não encontrada."));
        }

        CategoriaDTO categoria = categoriaOpt.get();
        if (updated.getNome() != null && !updated.getNome().isBlank())
            categoria.setNome(updated.getNome());

        if (updated.getDescricao() != null)
            categoria.setDescricao(updated.getDescricao());

        return ResponseEntity.ok(categoria);
    }

    // DELETE
    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui categoria", description = "Remove categoria apenas se não houver produtos vinculados")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        Optional<CategoriaDTO> categoriaOpt = categorias.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();

        if (categoriaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", "Categoria com ID " + id + " não encontrada."));
        }

        List<Integer> produtosVinculados = dependenciasProdutos.get(id);
        if (produtosVinculados != null && !produtosVinculados.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", "Não é possível excluir categoria que possui produtos vinculados."));
        }

        categorias.remove(categoriaOpt.get());
        dependenciasProdutos.remove(id);

        return ResponseEntity.ok(Map.of("mensagem", "Categoria removida com sucesso."));
    }

}