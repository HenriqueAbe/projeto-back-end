package com.example.projeto.controller;

import com.example.projeto.dto.ProductDTO;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/produto")
@Tag(name = "Produto", description = "APIs de gerenciamento de produtos")
public class ProductController {

    private final List<ProductDTO> produtos = new ArrayList<>();
    private int nextId = 1;

    // CREATE
    @PostMapping
    @Operation(summary = "Adiciona um novo produto", description = "Cria um novo produto no catálogo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Produto criado com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Os dados do produto estão incorretos.")
    })
    public ResponseEntity<ProductDTO> create(@Valid @RequestBody ProductDTO productDTO) {
        if (productDTO.getNome() == null || productDTO.getPreco() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        productDTO.setId(nextId++);
        produtos.add(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(productDTO);
    }

    // READ ALL
    @GetMapping
    @Operation(summary = "Lista todos os produtos", description = "Retorna a lista completa de produtos")
    public List<ProductDTO> findAll() {
        return produtos;
    }

    // READ ONE (por ID)
    @GetMapping("/{id}")
    @Operation(summary = "Busca produto por ID", description = "Retorna um produto específico pelo ID")
    public ResponseEntity<ProductDTO> findById(@PathVariable Integer id) {
        Optional<ProductDTO> produto = produtos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
        return produto.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // SEARCH (por nome)
    @GetMapping("/buscar/nome/{nome}")
    @Operation(summary = "Busca produto por nome", description = "Procura produtos que contenham o nome informado")
    public List<ProductDTO> findByNome(@PathVariable String nome) {
        return produtos.stream()
                .filter(p -> p.getNome().toLowerCase().contains(nome.toLowerCase()))
                .collect(Collectors.toList());
    }

    // SEARCH (por categoria)
    @GetMapping("/buscar/categoria/{categoria}")
    @Operation(summary = "Busca produtos por categoria", description = "Procura produtos de uma categoria específica")
    public List<ProductDTO> findByCategoria(@PathVariable String categoria) {
        return produtos.stream()
                .filter(p -> p.getCategoria() != null &&
                        p.getCategoria().toLowerCase().contains(categoria.toLowerCase()))
                .collect(Collectors.toList());
    }

    // UPDATE
    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um produto", description = "Atualiza os dados de um produto existente")
    public ResponseEntity<ProductDTO> update(@PathVariable Integer id, @RequestBody ProductDTO updatedProduct) {
        for (ProductDTO product : produtos) {
            if (product.getId().equals(id)) {
                product.setNome(updatedProduct.getNome());
                product.setPreco(updatedProduct.getPreco());
                product.setDescricao(updatedProduct.getDescricao());
                product.setImagem(updatedProduct.getImagem());
                product.setEstoque(updatedProduct.getEstoque());
                product.setCategoria(updatedProduct.getCategoria());
                return ResponseEntity.ok(product);
            }
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE
    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um produto", description = "Remove um produto pelo ID")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        boolean removed = produtos.removeIf(p -> p.getId().equals(id));
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
