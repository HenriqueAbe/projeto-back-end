package com.example.projeto.dto;

import lombok.Data;

@Data
public class ProductDTO {
    private Integer id;
    private String nome;
    private Double preco;
    private String descricao;
    private String imagem;
    private Integer estoque;
    private String categoria;
}
