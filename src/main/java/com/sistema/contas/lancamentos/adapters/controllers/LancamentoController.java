package com.sistema.contas.lancamentos.adapters.controllers;

import com.grandle.lancamento.controllers.LancamentoApi;
import com.grandle.lancamento.dtos.LancamentoDTO;
import com.grandle.lancamento.dtos.ListaLancamentosResponse;
import com.sistema.contas.lancamentos.adapters.converters.LancamentoConverter;
import com.sistema.contas.lancamentos.applications.ports.service.ILancamentoService;
import com.sistema.contas.lancamentos.domain.entities.Lancamento;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lancamentos")
public class LancamentoController implements LancamentoApi {

    private final ILancamentoService lancamentoService;
    private final LancamentoConverter lancamentoConverter;

    @Override
    @PostMapping
    public ResponseEntity<LancamentoDTO> adicionarLancamento(@RequestBody LancamentoDTO lancamentoDTO) throws Exception {
        Lancamento lancamento = lancamentoConverter.toEntity(lancamentoDTO);
        Lancamento novoLancamento = lancamentoService.criarLancamento(lancamento);
        LancamentoDTO lancamentoCriadoDTO = lancamentoConverter.criarDTOBase(novoLancamento).build();
        return ResponseEntity.ok(lancamentoCriadoDTO);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<LancamentoDTO> atualizarLancamento(@PathVariable("id") Integer id, @RequestBody LancamentoDTO lancamentoDTO) throws Exception {
        Lancamento lancamento = lancamentoConverter.toEntity(lancamentoDTO);
        Lancamento lancamentoAtualizado = lancamentoService.atualizarLancamento(id, lancamento);
        LancamentoDTO lancamentoAtualizadoDTO = lancamentoConverter.criarDTOBase(lancamentoAtualizado).build();
        return ResponseEntity.ok(lancamentoAtualizadoDTO);
    }

    @Override
    @GetMapping("/lancamentos")
    public ResponseEntity<ListaLancamentosResponse> listarLancamentos(
            @RequestParam(value = "descricao", required = false) String descricao,
            @RequestParam(value = "dataInicio", required = false) LocalDate dataInicio,
            @RequestParam(value = "dataFim", required = false) LocalDate dataFim,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size
    ) throws Exception {
        var lancamentosPage = lancamentoService.listarLancamentosFiltrados(descricao, dataInicio, dataFim, page, size);
        ListaLancamentosResponse response = ListaLancamentosResponse.builder()
                .content(lancamentosPage.getContent().stream()
                        .map(lancamento -> lancamentoConverter.criarDTOBase(lancamento).build())
                        .toList())
                .totalElements((int) lancamentosPage.getTotalElements())
                .totalPages(lancamentosPage.getTotalPages())
                .first(lancamentosPage.isFirst())
                .last(lancamentosPage.isLast())
                .empty(lancamentosPage.isEmpty())
                .build();
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<LancamentoDTO> obterLancamento(@PathVariable("id") Integer id) throws Exception {
        Optional<Lancamento> lancamentoOptional = lancamentoService.obterLancamentoPorId(Long.valueOf(id));
        if (lancamentoOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        LancamentoDTO lancamentoDTO = lancamentoConverter.criarDTOBase(lancamentoOptional.get()).build();
        return ResponseEntity.ok(lancamentoDTO);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerLancamento(@PathVariable("id") Integer id) throws Exception {
        lancamentoService.deletarLancamento(Long.valueOf(id));
        return ResponseEntity.noContent().build();
    }
}