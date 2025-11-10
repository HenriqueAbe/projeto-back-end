package com.example.projeto.service;

import com.example.projeto.model.User;
import com.example.projeto.repository.UserRepository;
import com.example.projeto.security.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService service;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setNome("Maria");
        user.setEmail("maria@example.com");
        user.setPassword("123");
        user.setRole(Role.USER);
    }

    @Test
    void deveRetornarTodosUsuarios() {
        when(repository.findAll()).thenReturn(List.of(user));

        List<User> resultado = service.findAll();

        assertEquals(1, resultado.size());
        assertEquals("maria@example.com", resultado.get(0).getEmail());
        verify(repository, times(1)).findAll();
    }

    @Test
    void deveSalvarUsuario() {
        when(repository.save(any(User.class))).thenReturn(user);

        User salvo = service.save(user);

        assertNotNull(salvo);
        assertEquals("Maria", salvo.getNome());
        verify(repository, times(1)).save(user);
    }

    @Test
    void deveAtualizarUsuario() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.save(any(User.class))).thenReturn(user);

        User atualizado = service.update(1L, user);

        assertEquals("maria@example.com", atualizado.getEmail());
        verify(repository, times(1)).save(any(User.class));
    }

    @Test
    void deveLancarErroAoAtualizarUsuarioInexistente() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.update(1L, user));
    }

    @Test
    void deveExcluirUsuario() {
        doNothing().when(repository).deleteById(1L);

        service.delete(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void deveEncontrarPorId() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        User encontrado = service.findById(1L);

        assertNotNull(encontrado);
        assertEquals("Maria", encontrado.getNome());
        verify(repository, times(1)).findById(1L);
    }
}
