package com.example.projeto.service;

import com.example.projeto.model.Categoria;
import com.example.projeto.repository.CategoriaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public List<Categoria> findAll() {
        return categoriaRepository.findAll();
    }

    public Categoria findById(Integer id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria com ID " + id + " não encontrada."));
    }

    @Transactional
    public Categoria save(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public Categoria update(Integer id, Categoria novaCategoria) {
        Categoria existente = findById(id);
        existente.setNome(novaCategoria.getNome());
        existente.setDescricao(novaCategoria.getDescricao());
        return categoriaRepository.save(existente);
    }

    @Transactional
    public void delete(Integer id) {
        if (!categoriaRepository.existsById(id)) {
            throw new EntityNotFoundException("Categoria com ID " + id + " não encontrada.");
        }
        categoriaRepository.deleteById(id);
    }
}
