package com.example.projeto.controller;

import com.example.projeto.exception.BusinessException;
import com.example.projeto.model.Categoria;
import com.example.projeto.model.Produto;
import com.example.projeto.service.CategoriaService;
import com.example.projeto.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;
    private final CategoriaService categoriaService;

    @Operation(summary = "Lista todos os produtos")
    @GetMapping
    public ResponseEntity<List<Produto>> listar() {
        return ResponseEntity.ok(produtoService.findAll());
    }

    @Operation(summary = "Busca produto por ID")
    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(produtoService.findById(id));
    }

    @Operation(summary = "Cria um novo produto")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Produto> criar(@RequestBody Produto produto) {
        if (produto.getCategoria() == null || produto.getCategoria().getId() == null) {
            throw new BusinessException("PROD001", "Produto deve possuir uma categoria válida.");
        }

        Categoria categoria = categoriaService.findById(produto.getCategoria().getId());
        produto.setCategoria(categoria);

        return ResponseEntity.ok(produtoService.save(produto));
    }

    @Operation(summary = "Atualiza um produto existente")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizar(@PathVariable Integer id, @RequestBody Produto produto) {
        return ResponseEntity.ok(produtoService.update(id, produto));
    }

    @Operation(summary = "Deleta um produto existente")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        produtoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
