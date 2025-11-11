package com.example.projeto.controller;

import com.example.projeto.dto.CupomDTO;
import com.example.projeto.exception.BusinessException;
import com.example.projeto.service.CupomService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cupons")
@RequiredArgsConstructor
public class CupomController {

    private final CupomService cupomService;

    @Operation(summary = "Lista todos os cupons ativos")
    @GetMapping
    public ResponseEntity<List<CupomDTO>> listarAtivos() {
        return ResponseEntity.ok(cupomService.findAllAtivos());
    }

    @Operation(summary = "Busca cupom por ID")
    @GetMapping("/{id}")
    public ResponseEntity<CupomDTO> buscarPorId(@PathVariable Integer id) {
        return cupomService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new BusinessException("CUP001", "Cupom não encontrado."));
    }

    @Operation(summary = "Cria um novo cupom")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CupomDTO> criar(@RequestBody CupomDTO dto) {
        return ResponseEntity.ok(cupomService.create(dto));
    }

    @Operation(summary = "Atualiza um cupom existente")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CupomDTO> atualizar(@PathVariable Integer id, @RequestBody CupomDTO dto) {
        return cupomService.update(id, dto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new BusinessException("CUP002", "Erro ao atualizar cupom."));
    }

    @Operation(summary = "Deleta um cupom existente")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletar(@PathVariable Integer id) {
        boolean removido = cupomService.delete(id);
        if (!removido) {
            throw new BusinessException("CUP003", "Cupom ainda válido, não pode ser removido.");
        }
        return ResponseEntity.ok("Cupom removido com sucesso.");
    }
}
