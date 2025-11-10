package com.example.projeto.controller;

import com.example.projeto.model.Categoria;
import com.example.projeto.model.Produto;
import com.example.projeto.service.CategoriaService;
import com.example.projeto.service.ProdutoService;
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

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Desativa Spring Security
class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProdutoService produtoService;

    @MockitoBean
    private CategoriaService categoriaService;

    @Autowired
    private ObjectMapper objectMapper;

    private Produto produto;
    private Categoria categoria;

    @BeforeEach
    void setup() {
        categoria = new Categoria();
        categoria.setId(1);
        categoria.setNome("Eletrônicos");

        produto = new Produto();
        produto.setId(1);
        produto.setNome("Smartphone");
        produto.setDescricao("Descrição do smartphone");
        produto.setPreco(1500.0);
        produto.setEstoque(10);
        produto.setCategoria(categoria);
    }

    @Test
    void deveListarProdutos() throws Exception {
        when(produtoService.findAll()).thenReturn(List.of(produto));

        mockMvc.perform(get("/api/produtos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Smartphone"));

        verify(produtoService, times(1)).findAll();
    }

    @Test
    void deveBuscarProdutoPorId() throws Exception {
        when(produtoService.findById(1)).thenReturn(produto);

        mockMvc.perform(get("/api/produtos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Smartphone"));

        verify(produtoService, times(1)).findById(1);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveCriarProduto() throws Exception {
        when(categoriaService.findById(1)).thenReturn(categoria);
        when(produtoService.save(any(Produto.class))).thenReturn(produto);

        mockMvc.perform(post("/api/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Smartphone"));

        verify(produtoService, times(1)).save(any(Produto.class));
        verify(categoriaService, times(1)).findById(1);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveAtualizarProduto() throws Exception {
        when(produtoService.update(eq(1), any(Produto.class))).thenReturn(produto);

        mockMvc.perform(put("/api/produtos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Smartphone"));

        verify(produtoService, times(1)).update(eq(1), any(Produto.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveDeletarProduto() throws Exception {
        doNothing().when(produtoService).delete(1);

        mockMvc.perform(delete("/api/produtos/1"))
                .andExpect(status().isNoContent());

        verify(produtoService, times(1)).delete(1);
    }
}
