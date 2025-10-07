package com.example.projeto.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class PedidoDTO {
    private Integer id;
    private String cliente;
    private List<String> produtos;
    private LocalDate data;
    private String status; // EM_ANDAMENTO, ENTREGUE, CANCELADO
}

