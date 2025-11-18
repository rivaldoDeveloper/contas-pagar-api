package com.sistema.contas.pessoa.adapters.controllers;

import com.grandle.pessoa.controllers.PessoaApi;
import com.grandle.pessoa.dtos.ListaPessoasResponse;
import com.grandle.pessoa.dtos.PessoaDTO;
import com.sistema.contas.pessoa.adapters.converters.PessoaConverters;
import com.sistema.contas.pessoa.application.ports.service.IPessoaService;
import com.sistema.contas.pessoa.domain.entities.Pessoa;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/pessoa") // Adicionando um prefixo base para consistência
public class PessoaController implements PessoaApi {

    private final IPessoaService pessoaService;

    @Override
    @PostMapping("/pessoas") // Mapeamento explícito
    public ResponseEntity<PessoaDTO> adicionarPessoa(@RequestBody PessoaDTO pessoaDTO) throws Exception {
        Pessoa pessoa = PessoaConverters.toEntity(pessoaDTO);
        Pessoa pessoaSalva = pessoaService.criarPessoa(pessoa);
        PessoaDTO pessoaDTOResponse = PessoaConverters.toDto(pessoaSalva);
        return ResponseEntity.status(201).body(pessoaDTOResponse);
    }

    @Override
    @PutMapping("/pessoas/{id}") // Mapeamento explícito
    public ResponseEntity<PessoaDTO> atualizarPessoa(@PathVariable("id") Integer id, @RequestBody PessoaDTO pessoaDTO) throws Exception {
        Pessoa pessoa = PessoaConverters.toEntity(pessoaDTO);
        Pessoa pessoaAtualizada = pessoaService.atualizarPessoa(Long.valueOf(id), pessoa);
        PessoaDTO pessoaDTOResponse = PessoaConverters.toDto(pessoaAtualizada);
        return ResponseEntity.ok(pessoaDTOResponse);
    }

    @Override
    @GetMapping("/pessoas") // Mapeamento explícito
    public ResponseEntity<ListaPessoasResponse> listarPessoas(
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "ativo", required = false) Boolean ativo,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) throws Exception {
        
        System.out.println(">>> ACESSANDO O ENDPOINT DE LISTAR PESSOAS <<<");

        int pageNum = (page != null) ? page : 0;
        int pageSize = (size != null) ? size : 20;
        String sortBy = "nome";
        String direction = "asc";

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        PageRequest pageable = PageRequest.of(pageNum, pageSize, Sort.by(sortDirection, sortBy));

        Page<Pessoa> pessoasPage = pessoaService.listarPessoas(nome, ativo, pageNum, pageSize, sortBy, direction);

        List<PessoaDTO> pessoasDTO = pessoasPage.getContent().stream()
                .map(PessoaConverters::toDto)
                .collect(Collectors.toList());

        ListaPessoasResponse response = new ListaPessoasResponse();
        response.setContent(pessoasDTO);
        response.setTotalElements((int) pessoasPage.getTotalElements());
        response.setTotalPages(pessoasPage.getTotalPages());
        response.setPageable(null);

        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/pessoas/{id}") // Mapeamento explícito
    public ResponseEntity<PessoaDTO> obterPessoa(@PathVariable("id") Integer id) throws Exception {
        Pessoa pessoa = pessoaService.obterPessoaPorId(Long.valueOf(id));
        if (pessoa == null) {
            return ResponseEntity.notFound().build();
        }
        PessoaDTO pessoaDTOResponse = PessoaConverters.toDto(pessoa);
        return ResponseEntity.ok(pessoaDTOResponse);
    }

    @Override
    @DeleteMapping("/pessoas/{id}") // Mapeamento explícito
    public ResponseEntity<Void> removerPessoa(@PathVariable("id") Integer id) throws Exception {
        pessoaService.removerPessoa(Long.valueOf(id));
        return ResponseEntity.noContent().build();
    }
}
