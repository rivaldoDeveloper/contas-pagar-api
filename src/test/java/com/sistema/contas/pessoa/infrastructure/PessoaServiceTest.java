package com.sistema.contas.pessoa.infrastructure;

import com.sistema.contas.endereco.domain.entities.Endereco;
import com.sistema.contas.pessoa.application.ports.repository.PessoaRepository;
import com.sistema.contas.pessoa.domain.entities.Pessoa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PessoaServiceTest {

    @Mock
    private PessoaRepository pessoaRepository;

    private PessoaService pessoaService;
    private Pessoa pessoa;

    @BeforeEach
    void setUp() {
        pessoaService = new PessoaService(pessoaRepository);

        pessoa = new Pessoa();
        pessoa.setId(1L);
        pessoa.setNome("Maria da Silva");
        // CORREÇÃO: Atender às validações do PessoaUseCase
        pessoa.setAtivo(true);
        pessoa.setEndereco(new Endereco()); // Adiciona um endereço para não ser nulo
    }

    @Test
    void deveListarPessoas() {
        Page<Pessoa> pageFake = new PageImpl<>(Arrays.asList(pessoa));
        // CORREÇÃO: Mockar o método que o UseCase realmente chama
        when(pessoaRepository.findAllByFiltroAndAtivo(any(), any(), any(Pageable.class))).thenReturn(pageFake);

        Page<Pessoa> resultado = pessoaService.listarPessoas("Maria", true, 0, 10, "nome", "asc");

        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        assertEquals("Maria da Silva", resultado.getContent().get(0).getNome());
        verify(pessoaRepository, times(1)).findAllByFiltroAndAtivo(any(), any(), any(Pageable.class));
    }

    @Test
    void deveObterPessoaPorId() {
        when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa));

        Pessoa resultado = pessoaService.obterPessoaPorId(1L);

        assertNotNull(resultado);
        assertEquals("Maria da Silva", resultado.getNome());
        verify(pessoaRepository, times(1)).findById(1L);
    }

    @Test
    void deveCriarPessoa() {
        when(pessoaRepository.save(any(Pessoa.class))).thenReturn(pessoa);

        Pessoa resultado = pessoaService.criarPessoa(pessoa);

        assertNotNull(resultado);
        assertEquals("Maria da Silva", resultado.getNome());
        verify(pessoaRepository, times(1)).save(any(Pessoa.class));
    }

    @Test
    void deveAtualizarPessoa() {
        when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa));
        when(pessoaRepository.save(any(Pessoa.class))).thenReturn(pessoa);

        pessoa.setNome("Maria de Souza");
        Pessoa resultado = pessoaService.atualizarPessoa(1L, pessoa);

        assertNotNull(resultado);
        assertEquals("Maria de Souza", resultado.getNome());
        verify(pessoaRepository, times(1)).findById(1L);
        verify(pessoaRepository, times(1)).save(any(Pessoa.class));
    }

    @Test
    void deveRemoverPessoa() {
        // CORREÇÃO: Mockar o findById que é chamado antes do delete
        when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa));
        doNothing().when(pessoaRepository).deleteById(1L);

        assertDoesNotThrow(() -> pessoaService.removerPessoa(1L));

        verify(pessoaRepository, times(1)).findById(1L);
        verify(pessoaRepository, times(1)).deleteById(1L);
    }
}