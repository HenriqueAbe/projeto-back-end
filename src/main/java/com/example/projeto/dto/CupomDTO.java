package com.example.projeto.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class CupomDTO {
    private Integer id;

    @NotBlank(message = "Código do cupom é obrigatório")
    private String codigo;

    @NotNull(message = "Valor do desconto é obrigatório")
    private Double valor;

    @NotNull(message = "Data de validade é obrigatória")
    private LocalDate validade;

    @NotNull(message = "Valor mínimo de compra é obrigatório")
    private Double minimoCompra;

    private boolean ativo = true; // indica se o cupom está válido
}
