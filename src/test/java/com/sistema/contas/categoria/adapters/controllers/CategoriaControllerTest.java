package com.sistema.contas.categoria.adapters.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grandle.categoria.dtos.CategoriaDTO;
import com.grandle.categoria.dtos.CategoriaInput;
import com.sistema.contas.auth.config.security.JwtAuthenticationFilter;
import com.sistema.contas.categoria.applications.ports.service.ICategoriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoriaController.class)
@AutoConfigureMockMvc(addFilters = false) // Desativa a execução dos filtros de segurança no teste
public class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ICategoriaService categoriaService;

    // Permite que o contexto inicie sem a SecretKey
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private CategoriaDTO categoriaDTO;
    private CategoriaInput categoriaInput;

    @BeforeEach
    void setUp() {
        categoriaDTO = new CategoriaDTO();
        categoriaDTO.setId(1);
        categoriaDTO.setNome("Lazer");

        categoriaInput = new CategoriaInput();
        categoriaInput.setNome("Lazer");
    }

    @Test
    void testListarCategorias() throws Exception {
        when(categoriaService.listarCategorias()).thenReturn(Collections.singletonList(categoriaDTO));

        mockMvc.perform(get("/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Lazer"));
    }

    @Test
    void testObterCategoria() throws Exception {
        when(categoriaService.obterCategoriaPorId(1)).thenReturn(categoriaDTO);

        mockMvc.perform(get("/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Lazer"));
    }

    @Test
    void testObterCategoria_NaoEncontrada() throws Exception {
        when(categoriaService.obterCategoriaPorId(1)).thenReturn(null);

        mockMvc.perform(get("/categorias/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCriarCategoria() throws Exception {
        when(categoriaService.criarCategoria(any(CategoriaInput.class))).thenReturn(categoriaDTO);

        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoriaInput)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Lazer"));
    }

    @Test
    void testAtualizarCategoria() throws Exception {
        when(categoriaService.atualizarCategoria(anyInt(), any(CategoriaInput.class))).thenReturn(categoriaDTO);

        mockMvc.perform(put("/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoriaInput)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Lazer"));
    }

    @Test
    void testAtualizarCategoria_NaoEncontrada() throws Exception {
        when(categoriaService.atualizarCategoria(anyInt(), any(CategoriaInput.class))).thenReturn(null);

        mockMvc.perform(put("/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoriaInput)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRemoverCategoria() throws Exception {
        doNothing().when(categoriaService).removerCategoria(1);

        mockMvc.perform(delete("/categorias/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testRemoverCategoria_NaoEncontrada() throws Exception {
        doThrow(new RuntimeException("Categoria não encontrada")).when(categoriaService).removerCategoria(1);

        mockMvc.perform(delete("/categorias/1"))
                .andExpect(status().isNotFound());
    }
}