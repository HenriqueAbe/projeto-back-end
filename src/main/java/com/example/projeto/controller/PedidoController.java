package com.example.projeto.controller;

import com.example.projeto.exception.BusinessException;
import com.example.projeto.model.Pedido;
import com.example.projeto.model.Produto;
import com.example.projeto.model.User;
import com.example.projeto.service.PedidoService;
import com.example.projeto.service.ProdutoService;
import com.example.projeto.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;
    private final ProdutoService produtoService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<Pedido>> listar() {
        return ResponseEntity.ok(pedidoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(pedidoService.findById(id));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping
    public ResponseEntity<Pedido> criar(@RequestBody Pedido pedido) {
        if (pedido.getCliente() == null || pedido.getCliente().getId() == null) {
            throw new BusinessException("PED001", "Pedido deve conter um cliente válido.");
        }
        if (pedido.getProdutos() == null || pedido.getProdutos().isEmpty()) {
            throw new BusinessException("PED002", "Pedido deve conter ao menos um produto.");
        }

        User cliente = userService.findById(pedido.getCliente().getId());
        List<Produto> produtos = pedido.getProdutos().stream()
                .map(p -> produtoService.findById(p.getId()))
                .toList();

        pedido.setCliente(cliente);
        pedido.setProdutos(produtos);
        pedido.setData(LocalDate.now());
        pedido.setStatus("EM_ANDAMENTO");

        return ResponseEntity.ok(pedidoService.save(pedido));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Pedido> atualizar(@PathVariable Integer id, @RequestBody Pedido pedido) {
        return ResponseEntity.ok(pedidoService.update(id, pedido));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        pedidoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
