package com.sistema.contas.endereco.adapters.controllers;

import com.grandle.endereco.controllers.EnderecoApi;
import com.grandle.endereco.dtos.EnderecoDTO;
import com.sistema.contas.endereco.application.ports.service.IEnderecoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/enderecos")
public class EnderecoController implements EnderecoApi {

    private final IEnderecoService enderecoService;

    @Override
    @GetMapping
    public ResponseEntity<List<EnderecoDTO>> listarEnderecos(@RequestParam(value = "logradouro", required = false) String logradouro,
                                                             @RequestParam(value = "bairro", required = false) String bairro,
                                                             @RequestParam(value = "cidade", required = false) String cidade,
                                                             @RequestParam(value = "estado", required = false) String estado) throws Exception {
        String filtro = "";
        if (logradouro != null) filtro += logradouro + " ";
        if (bairro != null) filtro += bairro + " ";
        if (cidade != null) filtro += cidade + " ";
        if (estado != null) filtro += estado;

        List<EnderecoDTO> enderecos = enderecoService.listarEnderecos(filtro.trim());
        return ResponseEntity.ok(enderecos);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<EnderecoDTO> obterEndereco(@PathVariable("id") Integer id) throws Exception {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }

        EnderecoDTO endereco = enderecoService.obterEndereco(Long.valueOf(id));
        if (endereco == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(endereco);
    }

    @Override
    @PostMapping
    public ResponseEntity<EnderecoDTO> criarEndereco(@RequestBody EnderecoDTO enderecoDTO) throws Exception {
        if (enderecoDTO == null) {
            return ResponseEntity.badRequest().build();
        }

        EnderecoDTO criado = enderecoService.criarEndereco(enderecoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<EnderecoDTO> atualizarEndereco(@PathVariable("id") Integer id, @RequestBody EnderecoDTO enderecoDTO) throws Exception {
        if (id == null || id <= 0 || enderecoDTO == null) {
            return ResponseEntity.badRequest().build();
        }

        EnderecoDTO atualizado = enderecoService.atualizarEndereco(Long.valueOf(id), enderecoDTO);
        if (atualizado == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(atualizado);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerEndereco(@PathVariable("id") Integer id) throws Exception {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }

        enderecoService.removerEndereco(Long.valueOf(id));
        return ResponseEntity.noContent().build();
    }
}