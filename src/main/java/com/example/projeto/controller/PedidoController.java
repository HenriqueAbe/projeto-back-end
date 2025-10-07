package com.example.projeto.controller;

import com.example.projeto.dto.PedidoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/pedido")
@Tag(name = "Pedidos", description = "APIs de gerenciamento de pedidos.")
public class PedidoController {

    private final List<PedidoDTO> pedidos = new ArrayList<>();
    private int nextId = 1;

    // CREATE
    @PostMapping
    @Operation(summary = "Cria um novo pedido", description = "Registra um novo pedido feito por um cliente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos.")
    })
    public ResponseEntity<?> create(@RequestBody PedidoDTO pedido) {
        if (pedido.getCliente() == null || pedido.getCliente().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", "O campo 'cliente' é obrigatório."));
        }

        if (pedido.getProdutos() == null || pedido.getProdutos().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", "O pedido deve conter pelo menos um produto."));
        }

        pedido.setId(nextId++);
        pedido.setData(LocalDate.now());
        pedido.setStatus("EM_ANDAMENTO");

        pedidos.add(pedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
    }

    // READ ALL (com filtros opcionais)
    @GetMapping
    @Operation(summary = "Lista pedidos", description = "Lista pedidos por cliente, data ou status.")
    public ResponseEntity<?> list(
            @RequestParam(required = false) String cliente,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String data
    ) {
        List<PedidoDTO> filtrados = new ArrayList<>(pedidos);

        if (cliente != null && !cliente.isBlank()) {
            filtrados = filtrados.stream()
                    .filter(p -> p.getCliente().equalsIgnoreCase(cliente))
                    .collect(Collectors.toList());
        }

        if (status != null && !status.isBlank()) {
            filtrados = filtrados.stream()
                    .filter(p -> p.getStatus().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
        }

        if (data != null && !data.isBlank()) {
            try {
                LocalDate filtroData = LocalDate.parse(data);
                filtrados = filtrados.stream()
                        .filter(p -> p.getData().equals(filtroData))
                        .collect(Collectors.toList());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("erro", "Formato de data inválido. Use o formato AAAA-MM-DD."));
            }
        }

        if (filtrados.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensagem", "Nenhum pedido encontrado com os filtros informados."));
        }

        return ResponseEntity.ok(filtrados);
    }

    // READ BY ID
    @GetMapping("/{id}")
    @Operation(summary = "Busca pedido por ID", description = "Retorna um pedido específico pelo ID.")
    public ResponseEntity<?> findById(@PathVariable Integer id) {
        Optional<PedidoDTO> pedido = pedidos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();

        return pedido.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("erro", "Pedido com ID " + id + " não encontrado.")));
    }

    // UPDATE STATUS
    @PutMapping("/{id}")
    @Operation(summary = "Atualiza status do pedido", description = "Permite alterar o status do pedido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado.")
    })
    public ResponseEntity<?> updateStatus(@PathVariable Integer id, @RequestBody PedidoDTO update) {
        Optional<PedidoDTO> pedidoOpt = pedidos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();

        if (pedidoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", "Pedido com ID " + id + " não encontrado."));
        }

        PedidoDTO pedido = pedidoOpt.get();
        String novoStatus = update.getStatus();

        if (novoStatus == null || novoStatus.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", "O campo 'status' é obrigatório."));
        }

        List<String> statusValidos = List.of("EM_ANDAMENTO", "ENTREGUE", "CANCELADO");
        if (!statusValidos.contains(novoStatus.toUpperCase())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", "Status inválido. Use: EM_ANDAMENTO, ENTREGUE ou CANCELADO."));
        }

        pedido.setStatus(novoStatus.toUpperCase());
        return ResponseEntity.ok(Map.of(
                "mensagem", "Status do pedido atualizado com sucesso.",
                "pedido", pedido
        ));
    }

    // DELETE
    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui pedido cancelado", description = "Remove o pedido apenas se estiver cancelado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido excluído com sucesso."),
            @ApiResponse(responseCode = "400", description = "Pedido não está cancelado."),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado.")
    })
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        Optional<PedidoDTO> pedidoOpt = pedidos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();

        if (pedidoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", "Pedido com ID " + id + " não encontrado."));
        }

        PedidoDTO pedido = pedidoOpt.get();

        if (!pedido.getStatus().equalsIgnoreCase("CANCELADO")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", "Somente pedidos CANCELADOS podem ser excluídos."));
        }

        pedidos.remove(pedido);
        return ResponseEntity.ok(Map.of("mensagem", "Pedido cancelado removido com sucesso."));
    }
}