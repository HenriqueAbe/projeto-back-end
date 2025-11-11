package com.example.projeto.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "cupons")
public class Cupom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false)
    private Double valor;

    @Column(nullable = false)
    private LocalDate validade;

    @Column(nullable = false)
    private Double minimoCompra;

    private boolean ativo;

    @OneToMany(mappedBy = "cupom")
    @ToString.Exclude
    private List<Pedido> pedidos;

    public Cupom(String codigo, Double valor, LocalDate validade, Double minimoCompra) {
        this.codigo = codigo;
        this.valor = valor;
        this.validade = validade;
        this.minimoCompra = minimoCompra;
        this.ativo = validade.isAfter(LocalDate.now());
    }
}
