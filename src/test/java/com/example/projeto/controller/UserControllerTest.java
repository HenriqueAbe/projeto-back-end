    package com.example.projeto.controller;

    import com.example.projeto.model.User;
    import com.example.projeto.security.Role;
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

    import java.util.List;

    import static org.mockito.Mockito.*;
    import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
    import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

    @SpringBootTest
    @AutoConfigureMockMvc(addFilters = false) // ✅ Desativa o Spring Security durante os testes
    class UserControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private UserService userService;

        @Autowired
        private ObjectMapper objectMapper;

        private User user;

        @BeforeEach
        void setup() {
            user = new User();
            user.setId(1L);
            user.setNome("João Silva");
            user.setEmail("joao@example.com");
            user.setPassword("123456");
            user.setRole(Role.USER);
        }

        @Test
        @WithMockUser(username = "admin", roles = {"ADMIN"})
        void deveListarUsuarios() throws Exception {
            when(userService.findAll()).thenReturn(List.of(user));

            mockMvc.perform(get("/api/usuarios"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].email").value("joao@example.com"));

            verify(userService, times(1)).findAll();
        }

        @Test
        @WithMockUser(username = "admin", roles = {"ADMIN"})
        void deveBuscarUsuarioPorId() throws Exception {
            when(userService.findById(1L)).thenReturn(user);

            mockMvc.perform(get("/api/usuarios/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nome").value("João Silva"));

            verify(userService, times(1)).findById(1L);
        }

        @Test
        @WithMockUser(username = "admin", roles = {"ADMIN"})
        void deveCriarUsuario() throws Exception {
            when(userService.save(any(User.class))).thenReturn(user);

            mockMvc.perform(post("/api/usuarios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(user)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("joao@example.com"));

            verify(userService, times(1)).save(any(User.class));
        }

        @Test
        @WithMockUser(username = "admin", roles = {"ADMIN"})
        void deveAtualizarUsuario() throws Exception {
            when(userService.update(eq(1L), any(User.class))).thenReturn(user);

            mockMvc.perform(put("/api/usuarios/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(user)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("joao@example.com"));

            verify(userService, times(1)).update(eq(1L), any(User.class));
        }

        @Test
        @WithMockUser(username = "admin", roles = {"ADMIN"})
        void deveDeletarUsuario() throws Exception {
            doNothing().when(userService).delete(1L);

            mockMvc.perform(delete("/api/usuarios/1"))
                    .andExpect(status().isNoContent());

            verify(userService, times(1)).delete(1L);
        }

        @Test
        void deveRetornarBadRequestAoCriarUsuarioSemEmail() throws Exception {
            user.setEmail(null);

            mockMvc.perform(post("/api/usuarios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(user)))
                    .andExpect(status().isBadRequest());
        }
    }
