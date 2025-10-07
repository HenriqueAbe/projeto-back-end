package com.example.projeto.controller;

import com.example.projeto.dto.CupomDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/v1/cupom")
@Tag(name = "Cupons", description = "CRUD de cupons/descontos com regras e validações")
public class CupomController {

    private final List<CupomDTO> cupons = new ArrayList<>();
    private int nextId = 1;

    // CREATE
    @PostMapping
    @Operation(summary = "Cria um novo cupom", description = "Adiciona um cupom com valor, validade e mínimo de compra")
    public ResponseEntity<?> create(@RequestBody CupomDTO cupom) {
        if (cupom.getCodigo() == null || cupom.getCodigo().isBlank() ||
                cupom.getValor() == null || cupom.getValor() <= 0 ||
                cupom.getValidade() == null ||
                cupom.getMinimoCompra() == null || cupom.getMinimoCompra() < 0) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", "Todos os campos obrigatórios devem ser preenchidos corretamente."));
        }

        cupom.setId(nextId++);
        cupom.setAtivo(cupom.getValidade().isAfter(LocalDate.now()));
        cupons.add(cupom);

        return ResponseEntity.status(HttpStatus.CREATED).body(cupom);
    }

    // READ ALL - apenas cupons ativos
    @GetMapping
    @Operation(summary = "Lista cupons ativos", description = "Retorna apenas os cupons válidos")
    public ResponseEntity<?> findAll() {
        List<CupomDTO> ativos = cupons.stream()
                .filter(c -> c.isAtivo() && c.getValidade().isAfter(LocalDate.now()))
                .toList();

        if (ativos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensagem", "Nenhum cupom ativo encontrado."));
        }

        return ResponseEntity.ok(ativos);
    }

    // READ BY ID
    @GetMapping("/{id}")
    @Operation(summary = "Busca cupom por ID", description = "Retorna um cupom específico pelo ID")
    public ResponseEntity<?> findById(@PathVariable Integer id) {
        Optional<CupomDTO> cupom = cupons.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();

        return cupom.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("erro", "Cupom com ID " + id + " não encontrado.")));
    }

    // UPDATE
    @PutMapping("/{id}")
    @Operation(summary = "Atualiza cupom", description = "Edita regras ou datas do cupom")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody CupomDTO updated) {
        Optional<CupomDTO> cupomOpt = cupons.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();

        if (cupomOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", "Cupom com ID " + id + " não encontrado."));
        }

        CupomDTO cupom = cupomOpt.get();

        if (updated.getCodigo() != null && !updated.getCodigo().isBlank())
            cupom.setCodigo(updated.getCodigo());

        if (updated.getValor() != null && updated.getValor() > 0)
            cupom.setValor(updated.getValor());

        if (updated.getMinimoCompra() != null && updated.getMinimoCompra() >= 0)
            cupom.setMinimoCompra(updated.getMinimoCompra());

        if (updated.getValidade() != null)
            cupom.setValidade(updated.getValidade());

        // Atualiza status ativo
        cupom.setAtivo(cupom.getValidade().isAfter(LocalDate.now()));

        return ResponseEntity.ok(cupom);
    }

    // DELETE - remover cupons expirados ou inválidos
    @DeleteMapping("/{id}")
    @Operation(summary = "Remove cupom", description = "Exclui cupom expirado ou inválido")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        Optional<CupomDTO> cupomOpt = cupons.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();

        if (cupomOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", "Cupom com ID " + id + " não encontrado."));
        }

        CupomDTO cupom = cupomOpt.get();
        if (cupom.getValidade().isAfter(LocalDate.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", "Não é possível remover um cupom válido."));
        }

        cupons.remove(cupom);
        return ResponseEntity.ok(Map.of("mensagem", "Cupom removido com sucesso."));
    }
}