package com.example.projeto.controller;

import com.example.projeto.model.Pedido;
import com.example.projeto.model.Produto;
import com.example.projeto.model.User;
import com.example.projeto.service.PedidoService;
import com.example.projeto.service.ProdutoService;
import com.example.projeto.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PedidoService pedidoService;

    @MockitoBean
    private ProdutoService produtoService;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private Pedido pedido;
    private User cliente;
    private Produto produto;

    @BeforeEach
    void setup() {
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
    void deveListarPedidos() throws Exception {
        when(pedidoService.findAll()).thenReturn(List.of(pedido));

        mockMvc.perform(get("/api/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cliente.nome").value("João Silva"));

        verify(pedidoService, times(1)).findAll();
    }

    @Test
    void deveBuscarPedidoPorId() throws Exception {
        when(pedidoService.findById(1)).thenReturn(pedido);

        mockMvc.perform(get("/api/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cliente.nome").value("João Silva"));

        verify(pedidoService, times(1)).findById(1);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveCriarPedido() throws Exception {
        when(userService.findById(1L)).thenReturn(cliente);
        when(produtoService.findById(1)).thenReturn(produto);
        when(pedidoService.save(any(Pedido.class))).thenReturn(pedido);

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedido)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cliente.nome").value("João Silva"));

        verify(pedidoService, times(1)).save(any(Pedido.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveAtualizarPedido() throws Exception {
        when(pedidoService.update(eq(1), any(Pedido.class))).thenReturn(pedido);

        mockMvc.perform(put("/api/pedidos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedido)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cliente.nome").value("João Silva"));

        verify(pedidoService, times(1)).update(eq(1), any(Pedido.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveDeletarPedido() throws Exception {
        doNothing().when(pedidoService).delete(1);

        mockMvc.perform(delete("/api/pedidos/1"))
                .andExpect(status().isNoContent());

        verify(pedidoService, times(1)).delete(1);
    }
}
