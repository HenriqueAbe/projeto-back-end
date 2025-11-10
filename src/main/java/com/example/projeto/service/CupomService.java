package com.example.projeto.service;

import com.example.projeto.dto.CupomDTO;
import com.example.projeto.model.Cupom;
import com.example.projeto.repository.CupomRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CupomService {

    private final CupomRepository repository;

    public CupomService(CupomRepository repository) {
        this.repository = repository;
    }

    public CupomDTO create(CupomDTO dto) {
        Cupom cupom = new Cupom(
                dto.getCodigo(),
                dto.getValor(),
                dto.getValidade(),
                dto.getMinimoCompra()
        );
        repository.save(cupom);
        return toDTO(cupom);
    }

    public List<CupomDTO> findAllAtivos() {
        return repository.findAll().stream()
                .filter(c -> c.isAtivo() && c.getValidade().isAfter(LocalDate.now()))
                .map(this::toDTO)
                .toList();
    }

    public Optional<CupomDTO> findById(Integer id) {
        return repository.findById(id).map(this::toDTO);
    }

    public Optional<CupomDTO> update(Integer id, CupomDTO dto) {
        return repository.findById(id).map(cupom -> {
            if (dto.getCodigo() != null && !dto.getCodigo().isBlank())
                cupom.setCodigo(dto.getCodigo());

            if (dto.getValor() != null && dto.getValor() > 0)
                cupom.setValor(dto.getValor());

            if (dto.getMinimoCompra() != null && dto.getMinimoCompra() >= 0)
                cupom.setMinimoCompra(dto.getMinimoCompra());

            if (dto.getValidade() != null)
                cupom.setValidade(dto.getValidade());

            cupom.setAtivo(cupom.getValidade().isAfter(LocalDate.now()));

            repository.save(cupom);
            return toDTO(cupom);
        });
    }

    public boolean delete(Integer id) {
        Optional<Cupom> cupomOpt = repository.findById(id);
        if (cupomOpt.isPresent()) {
            Cupom cupom = cupomOpt.get();
            if (cupom.getValidade().isAfter(LocalDate.now())) {
                return false;
            }
            repository.delete(cupom);
            return true;
        }
        return false;
    }

    private CupomDTO toDTO(Cupom c) {
        CupomDTO dto = new CupomDTO();
        dto.setId(c.getId());
        dto.setCodigo(c.getCodigo());
        dto.setValor(c.getValor());
        dto.setValidade(c.getValidade());
        dto.setMinimoCompra(c.getMinimoCompra());
        dto.setAtivo(c.isAtivo());
        return dto;
    }
}
