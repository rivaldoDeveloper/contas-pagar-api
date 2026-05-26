package com.sistema.contas.endereco.infrastructure.services;

import com.grandle.endereco.dtos.EnderecoDTO;
import com.sistema.contas.endereco.domain.usecases.EnderecoUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EnderecoServiceTest {

    @Mock
    private EnderecoUseCase enderecoUseCase;

    @InjectMocks
    private EnderecoService enderecoService;

    private EnderecoDTO enderecoDTO;

    @BeforeEach
    void setUp() {
        enderecoDTO = new EnderecoDTO();
        enderecoDTO.setId(1);
        enderecoDTO.setLogradouro("Avenida Paulista");
    }

    @Test
    void deveListarEnderecos() {
        when(enderecoUseCase.listarEnderecos(anyString())).thenReturn(Collections.singletonList(enderecoDTO));

        List<EnderecoDTO> resultado = enderecoService.listarEnderecos("");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Avenida Paulista", resultado.get(0).getLogradouro());
        verify(enderecoUseCase, times(1)).listarEnderecos(anyString());
    }

    @Test
    void deveObterEndereco() {
        when(enderecoUseCase.obterEndereco(1L)).thenReturn(enderecoDTO);

        EnderecoDTO resultado = enderecoService.obterEndereco(1L);

        assertNotNull(resultado);
        assertEquals("Avenida Paulista", resultado.getLogradouro());
        verify(enderecoUseCase, times(1)).obterEndereco(1L);
    }

    @Test
    void deveCriarEndereco() {
        EnderecoDTO dtoParaCriar = new EnderecoDTO();
        dtoParaCriar.setCep("01310-100");
        dtoParaCriar.setLogradouro("Avenida Paulista");

        when(enderecoUseCase.salvarEndereco(any(EnderecoDTO.class))).thenReturn(dtoParaCriar);

        EnderecoDTO resultado = enderecoService.criarEndereco(dtoParaCriar);

        assertNotNull(resultado);
        assertEquals("Avenida Paulista", resultado.getLogradouro());
        verify(enderecoUseCase, times(1)).salvarEndereco(any(EnderecoDTO.class));
    }

    @Test
    void deveAtualizarEndereco() {
        when(enderecoUseCase.atualizarEndereco(anyLong(), any(EnderecoDTO.class))).thenReturn(enderecoDTO);

        enderecoDTO.setLogradouro("Rua Augusta");
        EnderecoDTO resultado = enderecoService.atualizarEndereco(1L, enderecoDTO);

        assertNotNull(resultado);
        assertEquals("Rua Augusta", resultado.getLogradouro());
        verify(enderecoUseCase, times(1)).atualizarEndereco(anyLong(), any(EnderecoDTO.class));
    }

    @Test
    void deveRemoverEndereco() {
        doNothing().when(enderecoUseCase).removerEndereco(1L);

        enderecoService.removerEndereco(1L);

        verify(enderecoUseCase, times(1)).removerEndereco(1L);
    }
}