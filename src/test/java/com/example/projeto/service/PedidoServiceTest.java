package com.example.projeto.service;

import com.example.projeto.model.Pedido;
import com.example.projeto.model.Produto;
import com.example.projeto.model.User;
import com.example.projeto.repository.PedidoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private PedidoService pedidoService;

    private Pedido pedido;
    private User cliente;
    private Produto produto;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        cliente = new User();
        cliente.setId(1L);
        cliente.setNome("João Silva");

        produto = new Produto();
        produto.setId(1);
        produto.setNome("Produto 1");

        pedido = new Pedido();
        pedido.setId(1);
        pedido.setCliente(cliente);
        pedido.setProdutos(List.of(produto));
        pedido.setData(LocalDate.now());
        pedido.setStatus("EM_ANDAMENTO");
    }

    @Test
    void deveListarPedidos() {
        when(pedidoRepository.findAll()).thenReturn(List.of(pedido));

        List<Pedido> result = pedidoService.findAll();
        assertEquals(1, result.size());
        verify(pedidoRepository, times(1)).findAll();
    }

    @Test
    void deveBuscarPedidoPorId() {
        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido));

        Pedido result = pedidoService.findById(1);
        assertEquals("João Silva", result.getCliente().getNome());
        verify(pedidoRepository, times(1)).findById(1);
    }

    @Test
    void deveCriarPedido() {
        when(pedidoRepository.save(pedido)).thenReturn(pedido);

        Pedido result = pedidoService.save(pedido);
        assertEquals("João Silva", result.getCliente().getNome());
        verify(pedidoRepository, times(1)).save(pedido);
    }

    @Test
    void deveAtualizarPedido() {
        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(pedido)).thenReturn(pedido);

        Pedido novoPedido = new Pedido();
        novoPedido.setCliente(cliente);
        novoPedido.setProdutos(List.of(produto));
        novoPedido.setData(LocalDate.now());
        novoPedido.setStatus("CONCLUIDO");

        Pedido result = pedidoService.update(1, novoPedido);
        assertEquals("CONCLUIDO", result.getStatus());
        verify(pedidoRepository, times(1)).save(pedido);
    }

    @Test
    void deveDeletarPedido() {
        when(pedidoRepository.existsById(1)).thenReturn(true);
        doNothing().when(pedidoRepository).deleteById(1);

        pedidoService.delete(1);
        verify(pedidoRepository, times(1)).deleteById(1);
    }

    @Test
    void deveLancarExcecaoAoBuscarPedidoInexistente() {
        when(pedidoRepository.findById(2)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> pedidoService.findById(2));
    }
}
