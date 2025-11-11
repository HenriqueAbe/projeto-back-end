package com.example.projeto.service;

import com.example.projeto.model.Cupom;
import com.example.projeto.model.Pedido;
import com.example.projeto.repository.CupomRepository;
import com.example.projeto.repository.PedidoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    private final CupomRepository cupomRepository;

    public Cupom findCupomById(Integer id) {
        return cupomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cupom com ID " + id + " não encontrado."));
    }

    public List<Pedido> findAll() {
        return pedidoRepository.findAll();
    }

    public Pedido findById(Integer id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido com ID " + id + " não encontrado."));
    }

    @Transactional
    public Pedido save(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido update(Integer id, Pedido novoPedido) {
        Pedido existente = findById(id);
        existente.setCliente(novoPedido.getCliente());
        existente.setProdutos(novoPedido.getProdutos());
        existente.setData(novoPedido.getData());
        existente.setStatus(novoPedido.getStatus());
        return pedidoRepository.save(existente);
    }

    @Transactional
    public void delete(Integer id) {
        if (!pedidoRepository.existsById(id)) {
            throw new EntityNotFoundException("Pedido com ID " + id + " não encontrado.");
        }
        pedidoRepository.deleteById(id);
    }
}
