package com.example.projeto.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class CategoriaDTO {
    private Integer id;

    @NotBlank(message = "O nome da categoria é obrigatório")
    private String nome;

    private String descricao; // opcional
}
