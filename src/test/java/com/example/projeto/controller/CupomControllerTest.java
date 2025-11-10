package com.example.projeto.controller;

import com.example.projeto.dto.CupomDTO;
import com.example.projeto.exception.BusinessException;
import com.example.projeto.service.CupomService;
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
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class CupomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CupomService cupomService;

    @Autowired
    private ObjectMapper objectMapper;

    private CupomDTO dto;

    @BeforeEach
    void setup() {
        dto = new CupomDTO();
        dto.setId(1);
        dto.setCodigo("CUPOM10");
        dto.setValor(10.0);
        dto.setValidade(LocalDate.now().plusDays(5));
        dto.setMinimoCompra(50.0);
        dto.setAtivo(true);
    }

    @Test
    void deveListarCuponsAtivos() throws Exception {
        when(cupomService.findAllAtivos()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/cupons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value("CUPOM10"));

        verify(cupomService, times(1)).findAllAtivos();
    }

    @Test
    void deveBuscarCupomPorId() throws Exception {
        when(cupomService.findById(1)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/cupons/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("CUPOM10"));

        verify(cupomService, times(1)).findById(1);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveCriarCupom() throws Exception {
        when(cupomService.create(any(CupomDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/cupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("CUPOM10"));

        verify(cupomService, times(1)).create(any(CupomDTO.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveAtualizarCupom() throws Exception {
        when(cupomService.update(eq(1), any(CupomDTO.class))).thenReturn(Optional.of(dto));

        mockMvc.perform(put("/api/cupons/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("CUPOM10"));

        verify(cupomService, times(1)).update(eq(1), any(CupomDTO.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveDeletarCupom() throws Exception {
        when(cupomService.delete(1)).thenReturn(true);

        mockMvc.perform(delete("/api/cupons/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Cupom removido com sucesso."));

        verify(cupomService, times(1)).delete(1);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveFalharAoDeletarCupomAtivo() throws Exception {
        when(cupomService.delete(1)).thenReturn(false);

        mockMvc.perform(delete("/api/cupons/1"))
                .andExpect(status().isBadRequest()) // ❌ Alterado de 500 para 400
                .andExpect(jsonPath("$.codigo").value("CUP003"))
                .andExpect(jsonPath("$.mensagem").value("Cupom ainda válido, não pode ser removido."));

        verify(cupomService, times(1)).delete(1);
    }
}
