package com.example.projeto.service;

import com.example.projeto.model.Produto;
import com.example.projeto.repository.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public List<Produto> findAll() {
        return produtoRepository.findAll();
    }

    public Produto findById(Integer id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto com ID " + id + " não encontrado."));
    }

    @Transactional
    public Produto save(Produto produto) {
        return produtoRepository.save(produto);
    }

    @Transactional
    public Produto update(Integer id, Produto novoProduto) {
        Produto existente = findById(id);
        existente.setNome(novoProduto.getNome());
        existente.setPreco(novoProduto.getPreco());
        existente.setDescricao(novoProduto.getDescricao());
        existente.setEstoque(novoProduto.getEstoque());
        existente.setCategoria(novoProduto.getCategoria());
        return produtoRepository.save(existente);
    }

    @Transactional
    public void delete(Integer id) {
        if (!produtoRepository.existsById(id)) {
            throw new EntityNotFoundException("Produto com ID " + id + " não encontrado.");
        }
        produtoRepository.deleteById(id);
    }
}
