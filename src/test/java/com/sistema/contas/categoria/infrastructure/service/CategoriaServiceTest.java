package com.sistema.contas.categoria.infrastructure.service;

import com.grandle.categoria.dtos.CategoriaDTO;
import com.grandle.categoria.dtos.CategoriaInput;
import com.sistema.contas.categoria.domain.entities.Categoria;
import com.sistema.contas.categoria.domain.usecases.CategoriaUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoriaServiceTest {

    @Mock
    private CategoriaUseCase categoriaUseCase;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria;
    private CategoriaInput categoriaInput;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Alimentação");

        categoriaInput = new CategoriaInput();
        categoriaInput.setNome("Alimentação");
    }

    @Test
    void deveListarCategorias() throws Exception {
        when(categoriaUseCase.listarCategorias()).thenReturn(Collections.singletonList(categoria));

        List<CategoriaDTO> resultado = categoriaService.listarCategorias();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Alimentação", resultado.get(0).getNome());
        verify(categoriaUseCase, times(1)).listarCategorias();
    }

    @Test
    void deveObterCategoriaPorId() throws Exception {
        when(categoriaUseCase.obterCategoriaPorId(1L)).thenReturn(Optional.of(categoria));

        CategoriaDTO resultado = categoriaService.obterCategoriaPorId(1);

        assertNotNull(resultado);
        assertEquals("Alimentação", resultado.getNome());
        verify(categoriaUseCase, times(1)).obterCategoriaPorId(1L);
    }

    @Test
    void deveLancarExcecaoAoObterCategoriaInexistente() {
        when(categoriaUseCase.obterCategoriaPorId(1L)).thenReturn(Optional.empty());

        Exception excecao = assertThrows(Exception.class, () -> categoriaService.obterCategoriaPorId(1));

        assertEquals("Categoria não encontrada.", excecao.getMessage());
        verify(categoriaUseCase, times(1)).obterCategoriaPorId(1L);
    }

    @Test
    void deveCriarCategoria() throws Exception {
        when(categoriaUseCase.criarCategoria(any(Categoria.class))).thenReturn(categoria);

        CategoriaDTO resultado = categoriaService.criarCategoria(categoriaInput);

        assertNotNull(resultado);
        assertEquals("Alimentação", resultado.getNome());
        verify(categoriaUseCase, times(1)).criarCategoria(any(Categoria.class));
    }

    @Test
    void deveAtualizarCategoria() throws Exception {
        // Arrange
        when(categoriaUseCase.obterCategoriaPorId(1L)).thenReturn(Optional.of(categoria));

        //  Envolvendo a categoria em Optional.of()
        when(categoriaUseCase.atualizarCategoria(eq(1L), any(Categoria.class)))
                .thenReturn(Optional.of(categoria));

        categoriaInput.setNome("Saúde");

        // Act
        CategoriaDTO resultado = categoriaService.atualizarCategoria(1, categoriaInput);

        // Assert
        assertNotNull(resultado);
        assertEquals("Saúde", resultado.getNome());
        verify(categoriaUseCase, times(1)).obterCategoriaPorId(1L);
        verify(categoriaUseCase, times(1)).atualizarCategoria(eq(1L), any(Categoria.class));
    }

    @Test
    void deveLancarExcecaoAoAtualizarCategoriaInexistente() {
        when(categoriaUseCase.obterCategoriaPorId(99L)).thenReturn(Optional.empty());

        Exception excecao = assertThrows(Exception.class, () -> categoriaService.atualizarCategoria(99, categoriaInput));

        assertEquals("Categoria não encontrada.", excecao.getMessage());
        verify(categoriaUseCase, times(1)).obterCategoriaPorId(99L);
        verify(categoriaUseCase, never()).atualizarCategoria(anyLong(), any(Categoria.class));
    }

    @Test
    void deveRemoverCategoria() throws Exception {
        when(categoriaUseCase.obterCategoriaPorId(1L)).thenReturn(Optional.of(categoria));
        doNothing().when(categoriaUseCase).removerCategoria(1L);

        categoriaService.removerCategoria(1);

        verify(categoriaUseCase, times(1)).obterCategoriaPorId(1L);
        verify(categoriaUseCase, times(1)).removerCategoria(1L);
    }

    @Test
    void deveLancarExcecaoAoRemoverCategoriaInexistente() {
        when(categoriaUseCase.obterCategoriaPorId(1L)).thenReturn(Optional.empty());

        Exception excecao = assertThrows(Exception.class, () -> categoriaService.removerCategoria(1));

        assertEquals("Categoria não encontrada.", excecao.getMessage());
        verify(categoriaUseCase, times(1)).obterCategoriaPorId(1L);
        verify(categoriaUseCase, never()).removerCategoria(anyLong());
    }
}