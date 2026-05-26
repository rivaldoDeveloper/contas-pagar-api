package com.sistema.contas.lancamentos.adapters.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grandle.lancamento.dtos.LancamentoDTO;
import com.sistema.contas.lancamentos.adapters.converters.LancamentoConverter;
import com.sistema.contas.lancamentos.applications.ports.service.ILancamentoService;
import com.sistema.contas.lancamentos.domain.entities.Lancamento;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class LancamentoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ILancamentoService lancamentoService;

    @Mock
    private LancamentoConverter lancamentoConverter;

    @InjectMocks
    private LancamentoController lancamentoController;

    private ObjectMapper objectMapper;
    private Lancamento lancamento;
    private LancamentoDTO lancamentoDTO;

    @BeforeEach
    void setUp() {
        // Inicializa o MockMvc em modo isolado programático
        mockMvc = MockMvcBuilders.standaloneSetup(lancamentoController).build();
        objectMapper = new ObjectMapper();

        lancamento = new Lancamento();
        lancamento.setId(1L);
        lancamento.setDescricao("Salário");

        lancamentoDTO = new LancamentoDTO();
        lancamentoDTO.setId(1);
        lancamentoDTO.setDescricao("Salário");

        // Configuração segura para interceptar conversores com suporte ao padrão builder
        LancamentoDTO.LancamentoDTOBuilder builderMock = mock(LancamentoDTO.LancamentoDTOBuilder.class);

        lenient().when(lancamentoConverter.toEntity(any(LancamentoDTO.class))).thenReturn(lancamento);
        lenient().when(lancamentoConverter.criarDTOBase(any(Lancamento.class))).thenReturn(builderMock);
        lenient().when(builderMock.build()).thenReturn(lancamentoDTO);
    }

    @Test
    void testAdicionarLancamento() throws Exception {
        when(lancamentoService.criarLancamento(any(Lancamento.class))).thenReturn(lancamento);

        mockMvc.perform(post("/api/lancamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lancamentoDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao").value("Salário"));

        verify(lancamentoService, times(1)).criarLancamento(any(Lancamento.class));
    }

    @Test
    void testAtualizarLancamento() throws Exception {
        when(lancamentoService.atualizarLancamento(anyInt(), any(Lancamento.class))).thenReturn(lancamento);

        mockMvc.perform(put("/api/lancamentos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lancamentoDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao").value("Salário"));

        verify(lancamentoService, times(1)).atualizarLancamento(eq(1), any(Lancamento.class));
    }

    @Test
    void testListarLancamentos() throws Exception {
        when(lancamentoService.listarLancamentosFiltrados(any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(Arrays.asList(lancamento)));

        mockMvc.perform(get("/api/lancamentos/lancamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].descricao").value("Salário"));

        verify(lancamentoService, times(1)).listarLancamentosFiltrados(any(), any(), any(), anyInt(), anyInt());
    }

    @Test
    void testObterLancamento_Sucesso() throws Exception {
        // Controller faz a busca usando Long em obterLancamentoPorId
        when(lancamentoService.obterLancamentoPorId(1L)).thenReturn(Optional.of(lancamento));

        mockMvc.perform(get("/api/lancamentos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao").value("Salário"));

        verify(lancamentoService, times(1)).obterLancamentoPorId(1L);
    }

    @Test
    void testObterLancamento_NaoEncontrado() throws Exception {
        when(lancamentoService.obterLancamentoPorId(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/lancamentos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRemoverLancamento() throws Exception {
        doNothing().when(lancamentoService).deletarLancamento(1L);

        mockMvc.perform(delete("/api/lancamentos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(lancamentoService, times(1)).deletarLancamento(1L);
    }
}