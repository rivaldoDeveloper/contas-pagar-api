package com.sistema.contas.lancamentos.infrastructure.repository;

import com.sistema.contas.lancamentos.applications.ports.repository.LancamentoRepository;
import com.sistema.contas.lancamentos.domain.entities.Lancamento;
import com.sistema.contas.lancamentos.domain.enums.TipoLancamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LancamentoServiceTest {

    @Mock
    private LancamentoRepository lancamentoRepository;

    private LancamentoService lancamentoService;
    private Lancamento lancamento;

    @BeforeEach
    void setUp() {
        lancamentoService = new LancamentoService(lancamentoRepository);

        lancamento = new Lancamento();
        lancamento.setId(1L);
        lancamento.setDescricao("Conta de Luz");
        lancamento.setValor(BigDecimal.valueOf(150.00));
        lancamento.setDataPagamento(new java.util.Date());
        lancamento.setTipo(TipoLancamento.Despesa);
    }

    @Test
    void deveListarLancamentosPaginados() {
        Page<Lancamento> pageFake = new PageImpl<>(Arrays.asList(lancamento));
        when(lancamentoRepository.findAll(any(Pageable.class))).thenReturn(pageFake);

        Page<Lancamento> resultado = lancamentoService.listarLancamentosPaginados(0, 10);

        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        verify(lancamentoRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void deveBuscarLancamentoPorId() {
        when(lancamentoRepository.findById(anyLong())).thenReturn(Optional.of(lancamento));

        Optional<Lancamento> resultado = lancamentoService.obterLancamentoPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Conta de Luz", resultado.get().getDescricao());
        verify(lancamentoRepository, times(1)).findById(1L);
    }

    @Test
    void deveCriarLancamento() {
        when(lancamentoRepository.save(any(Lancamento.class))).thenReturn(lancamento);

        Lancamento resultado = lancamentoService.criarLancamento(lancamento);

        assertNotNull(resultado);
        assertEquals("Conta de Luz", resultado.getDescricao());
        verify(lancamentoRepository, times(1)).save(lancamento);
    }

    @Test
    void deveAtualizarLancamento() {
        when(lancamentoRepository.findById(anyLong())).thenReturn(Optional.of(lancamento));
        when(lancamentoRepository.save(any(Lancamento.class))).thenReturn(lancamento);

        lancamento.setDescricao("Conta de Água");
        Lancamento resultado = lancamentoService.atualizarLancamento(1, lancamento);

        assertNotNull(resultado);
        assertEquals("Conta de Água", resultado.getDescricao());
        verify(lancamentoRepository, times(1)).findById(1L);
        verify(lancamentoRepository, times(1)).save(lancamento);
    }

    @Test
    void deveDeletarLancamento() {
        // Arrange: Configure o mock para o comportamento esperado dentro do UseCase.
        // 1. Diga ao mock que o lançamento com ID 1 EXISTE.
        when(lancamentoRepository.existsById(1L)).thenReturn(true);
        // 2. Diga ao mock para não fazer nada quando 'deleteById' for chamado.
        doNothing().when(lancamentoRepository).deleteById(1L);

        // Act & Assert: Chame o método do serviço e verifique que ele NÃO lança uma exceção.
        assertDoesNotThrow(() -> lancamentoService.deletarLancamento(1L));

        // Verify: Confirme que a lógica do UseCase chamou os métodos corretos no repositório.
        verify(lancamentoRepository, times(1)).existsById(1L);
        verify(lancamentoRepository, times(1)).deleteById(1L);
    }
}