package com.sistema.contas.pessoa.adapters.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grandle.pessoa.dtos.EnderecoDTO;
import com.grandle.pessoa.dtos.PessoaDTO;
import com.sistema.contas.pessoa.application.ports.service.IPessoaService;
import com.sistema.contas.pessoa.domain.entities.Pessoa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// CORREÇÃO: Usando Mockito puro para isolar o ambiente e evitar problemas de Filtros/Tokens
@ExtendWith(MockitoExtension.class)
public class PessoaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IPessoaService pessoaService;

    @InjectMocks
    private PessoaController pessoaController;

    private ObjectMapper objectMapper;
    private Pessoa pessoa;
    private PessoaDTO pessoaDTO;

    @BeforeEach
    void setUp() {
        // Inicializa o MockMvc olhando fixamente para a instância da controller
        mockMvc = MockMvcBuilders.standaloneSetup(pessoaController).build();
        objectMapper = new ObjectMapper();

        pessoa = new Pessoa();
        pessoa.setId(1L);
        pessoa.setNome("João da Silva");
        pessoa.setAtivo(true);

        EnderecoDTO enderecoDTO = new EnderecoDTO();
        enderecoDTO.setLogradouro("Rua teste");

        pessoaDTO = new PessoaDTO();
        pessoaDTO.setId(1);
        pessoaDTO.setNome("João da Silva");
        pessoaDTO.setAtivo(true);
        pessoaDTO.setEndereco(enderecoDTO);
    }

    @Test
    void testListarTodasAsPessoas() throws Exception {
        when(pessoaService.listarPessoas(any(), any(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(new PageImpl<>(Arrays.asList(pessoa)));

        mockMvc.perform(get("/api/pessoa/pessoas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome").value("João da Silva"));

        verify(pessoaService, times(1)).listarPessoas(any(), any(), anyInt(), anyInt(), anyString(), anyString());
    }

    @Test
    void testBuscarPessoaPorId_Sucesso() throws Exception {
        when(pessoaService.obterPessoaPorId(1L)).thenReturn(pessoa);

        mockMvc.perform(get("/api/pessoa/pessoas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João da Silva"));

        verify(pessoaService, times(1)).obterPessoaPorId(1L);
    }

    @Test
    void testBuscarPessoaPorId_NaoEncontrada() throws Exception {
        // Cobre a validação 'if (pessoa == null)' retornando 404
        when(pessoaService.obterPessoaPorId(1L)).thenReturn(null);

        mockMvc.perform(get("/api/pessoa/pessoas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCriarPessoa() throws Exception {
        when(pessoaService.criarPessoa(any(Pessoa.class))).thenReturn(pessoa);

        mockMvc.perform(post("/api/pessoa/pessoas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pessoaDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("João da Silva"));

        verify(pessoaService, times(1)).criarPessoa(any(Pessoa.class));
    }

    @Test
    void testAtualizarPessoa() throws Exception {
        when(pessoaService.atualizarPessoa(eq(1L), any(Pessoa.class))).thenReturn(pessoa);

        mockMvc.perform(put("/api/pessoa/pessoas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pessoaDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João da Silva"));

        verify(pessoaService, times(1)).atualizarPessoa(eq(1L), any(Pessoa.class));
    }

    @Test
    void testDeletarPessoa() throws Exception {
        doNothing().when(pessoaService).removerPessoa(1L);

        mockMvc.perform(delete("/api/pessoa/pessoas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(pessoaService, times(1)).removerPessoa(1L);
    }
}