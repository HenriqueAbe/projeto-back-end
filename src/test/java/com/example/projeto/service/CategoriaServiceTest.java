package com.example.projeto.service;

import com.example.projeto.model.Categoria;
import com.example.projeto.repository.CategoriaRepository;
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

class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        categoria = new Categoria();
        categoria.setId(1);
        categoria.setNome("Eletrônicos");
        categoria.setDescricao("Categoria de eletrônicos");
    }

    @Test
    void deveListarCategorias() {
        when(categoriaRepository.findAll()).thenReturn(List.of(categoria));

        List<Categoria> result = categoriaService.findAll();
        assertEquals(1, result.size());
        verify(categoriaRepository, times(1)).findAll();
    }

    @Test
    void deveBuscarCategoriaPorId() {
        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoria));

        Categoria result = categoriaService.findById(1);
        assertEquals("Eletrônicos", result.getNome());
        verify(categoriaRepository, times(1)).findById(1);
    }

    @Test
    void deveCriarCategoria() {
        when(categoriaRepository.save(categoria)).thenReturn(categoria);

        Categoria result = categoriaService.save(categoria);
        assertEquals("Eletrônicos", result.getNome());
        verify(categoriaRepository, times(1)).save(categoria);
    }

    @Test
    void deveAtualizarCategoria() {
        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(categoria)).thenReturn(categoria);

        Categoria novaCategoria = new Categoria();
        novaCategoria.setNome("Eletrodomésticos");
        novaCategoria.setDescricao("Nova descrição");

        Categoria result = categoriaService.update(1, novaCategoria);
        assertEquals("Eletrodomésticos", result.getNome());
        verify(categoriaRepository, times(1)).save(categoria);
    }

    @Test
    void deveDeletarCategoria() {
        when(categoriaRepository.existsById(1)).thenReturn(true);
        doNothing().when(categoriaRepository).deleteById(1);

        categoriaService.delete(1);
        verify(categoriaRepository, times(1)).deleteById(1);
    }
}
