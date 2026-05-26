package com.sistema.contas.endereco.adapters.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grandle.endereco.dtos.EnderecoDTO;
import com.sistema.contas.endereco.application.ports.service.IEnderecoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class EnderecoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private IEnderecoService enderecoService;

    @InjectMocks
    private EnderecoController enderecoController;

    private ObjectMapper objectMapper;
    private EnderecoDTO enderecoDTO;

    @BeforeEach
    void setUp() {
        // Inicializa o MockMvc olhando diretamente para a instância da controller
        mockMvc = MockMvcBuilders.standaloneSetup(enderecoController).build();
        objectMapper = new ObjectMapper();

        enderecoDTO = new EnderecoDTO();
        enderecoDTO.setId(1);
        enderecoDTO.setLogradouro("Rua das Flores");
        enderecoDTO.setNumero("123");
        enderecoDTO.setCidade("São Paulo");
        enderecoDTO.setEstado("SP");
    }

    @Test
    void testListarEnderecos() throws Exception {
        when(enderecoService.listarEnderecos(anyString())).thenReturn(Collections.singletonList(enderecoDTO));

        mockMvc.perform(get("/enderecos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].logradouro").value("Rua das Flores"));

        verify(enderecoService, times(1)).listarEnderecos(anyString());
    }

    @Test
    void testObterEndereco_Sucesso() throws Exception {
        when(enderecoService.obterEndereco(1L)).thenReturn(enderecoDTO);

        mockMvc.perform(get("/enderecos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.logradouro").value("Rua das Flores"));

        verify(enderecoService, times(1)).obterEndereco(1L);
    }

    @Test
    void testObterEndereco_IdInvalido() throws Exception {
        // Testa a validação "id <= 0" da sua controller
        mockMvc.perform(get("/enderecos/0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(enderecoService, never()).obterEndereco(anyLong());
    }

    @Test
    void testObterEndereco_NaoEncontrado() throws Exception {
        when(enderecoService.obterEndereco(1L)).thenReturn(null);

        mockMvc.perform(get("/enderecos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCriarEndereco_Sucesso() throws Exception {
        when(enderecoService.criarEndereco(any(EnderecoDTO.class))).thenReturn(enderecoDTO);

        mockMvc.perform(post("/enderecos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enderecoDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.logradouro").value("Rua das Flores"));

        verify(enderecoService, times(1)).criarEndereco(any(EnderecoDTO.class));
    }

    @Test
    void testAtualizarEndereco_Sucesso() throws Exception {
        when(enderecoService.atualizarEndereco(eq(1L), any(EnderecoDTO.class))).thenReturn(enderecoDTO);

        mockMvc.perform(put("/enderecos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enderecoDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.logradouro").value("Rua das Flores"));

        verify(enderecoService, times(1)).atualizarEndereco(eq(1L), any(EnderecoDTO.class));
    }

    @Test
    void testAtualizarEndereco_NaoEncontrado() throws Exception {
        when(enderecoService.atualizarEndereco(eq(1L), any(EnderecoDTO.class))).thenReturn(null);

        mockMvc.perform(put("/enderecos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enderecoDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRemoverEndereco_Sucesso() throws Exception {
        doNothing().when(enderecoService).removerEndereco(1L);

        mockMvc.perform(delete("/enderecos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(enderecoService, times(1)).removerEndereco(1L);
    }
}