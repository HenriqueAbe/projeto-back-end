package com.example.projeto.service;

import com.example.projeto.dto.CupomDTO;
import com.example.projeto.model.Cupom;
import com.example.projeto.repository.CupomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CupomServiceTest {

    @Mock
    private CupomRepository repository;

    @InjectMocks
    private CupomService service;

    private Cupom cupom;
    private CupomDTO dto;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        cupom = new Cupom("CUPOM10", 10.0, LocalDate.now().plusDays(5), 50.0);
        cupom.setId(1);

        dto = new CupomDTO();
        dto.setCodigo("CUPOM10");
        dto.setValor(10.0);
        dto.setValidade(LocalDate.now().plusDays(5));
        dto.setMinimoCompra(50.0);
    }

    @Test
    void deveCriarCupom() {
        when(repository.save(any(Cupom.class))).thenReturn(cupom);

        CupomDTO result = service.create(dto);

        assertThat(result.getCodigo()).isEqualTo("CUPOM10");
        verify(repository, times(1)).save(any(Cupom.class));
    }

    @Test
    void deveListarCuponsAtivos() {
        cupom.setAtivo(true);
        when(repository.findAll()).thenReturn(List.of(cupom));

        List<CupomDTO> result = service.findAllAtivos();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCodigo()).isEqualTo("CUPOM10");
        verify(repository, times(1)).findAll();
    }

    @Test
    void deveBuscarCupomPorId() {
        when(repository.findById(1)).thenReturn(Optional.of(cupom));

        Optional<CupomDTO> result = service.findById(1);

        assertThat(result).isPresent();
        assertThat(result.get().getCodigo()).isEqualTo("CUPOM10");
        verify(repository, times(1)).findById(1);
    }

    @Test
    void deveAtualizarCupom() {
        when(repository.findById(1)).thenReturn(Optional.of(cupom));
        when(repository.save(any(Cupom.class))).thenReturn(cupom);

        dto.setValor(20.0);
        Optional<CupomDTO> result = service.update(1, dto);

        assertThat(result).isPresent();
        assertThat(result.get().getValor()).isEqualTo(20.0);
        verify(repository, times(1)).save(cupom);
    }

    @Test
    void deveDeletarCupomExpirado() {
        cupom.setValidade(LocalDate.now().minusDays(1));
        cupom.setAtivo(false);
        when(repository.findById(1)).thenReturn(Optional.of(cupom));

        boolean result = service.delete(1);

        assertThat(result).isTrue();
        verify(repository, times(1)).delete(cupom);
    }

    @Test
    void naoDeveDeletarCupomAtivo() {
        cupom.setValidade(LocalDate.now().plusDays(1));
        cupom.setAtivo(true);
        when(repository.findById(1)).thenReturn(Optional.of(cupom));

        boolean result = service.delete(1);

        assertThat(result).isFalse();
        verify(repository, never()).delete(any());
    }
}
