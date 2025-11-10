package com.example.projeto.service;

import com.example.projeto.model.Categoria;
import com.example.projeto.model.Produto;
import com.example.projeto.repository.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    private Produto produto;
    private Categoria categoria;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

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
    void deveListarProdutos() {
        when(produtoRepository.findAll()).thenReturn(List.of(produto));

        List<Produto> result = produtoService.findAll();
        assertEquals(1, result.size());
        verify(produtoRepository, times(1)).findAll();
    }

    @Test
    void deveBuscarProdutoPorId() {
        when(produtoRepository.findById(1)).thenReturn(Optional.of(produto));

        Produto result = produtoService.findById(1);
        assertEquals("Smartphone", result.getNome());
        verify(produtoRepository, times(1)).findById(1);
    }

    @Test
    void deveCriarProduto() {
        when(produtoRepository.save(produto)).thenReturn(produto);

        Produto result = produtoService.save(produto);
        assertEquals("Smartphone", result.getNome());
        verify(produtoRepository, times(1)).save(produto);
    }

    @Test
    void deveAtualizarProduto() {
        when(produtoRepository.findById(1)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(produto)).thenReturn(produto);

        Produto novoProduto = new Produto();
        novoProduto.setNome("Smartphone Atualizado");
        novoProduto.setPreco(1600.0);
        novoProduto.setDescricao("Nova descrição");
        novoProduto.setEstoque(8);
        novoProduto.setCategoria(categoria);

        Produto result = produtoService.update(1, novoProduto);
        assertEquals("Smartphone Atualizado", result.getNome());
        verify(produtoRepository, times(1)).save(produto);
    }

    @Test
    void deveDeletarProduto() {
        when(produtoRepository.existsById(1)).thenReturn(true);
        doNothing().when(produtoRepository).deleteById(1);

        produtoService.delete(1);
        verify(produtoRepository, times(1)).deleteById(1);
    }
}
