package com.sistema.contas.auth.infrastructures.services;

import com.grandle.auth.dtos.UsuarioDTO;
import com.sistema.contas.auth.adapters.converts.UsuarioConverter;
import com.sistema.contas.auth.applications.ports.respository.UsuarioRepository;
import com.sistema.contas.auth.applications.ports.services.IUsuarioService;
import com.sistema.contas.auth.domain.entities.Perfil;
import com.sistema.contas.auth.domain.entities.Usuario;
import com.sistema.contas.auth.domain.entities.UsuarioPerfil;
import com.sistema.contas.auth.domain.exceptions.UsuarioNaoAutorizadoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO obterUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .map(UsuarioConverter::paraDTO)
                .orElseThrow(() -> new UsuarioNaoAutorizadoException("Usuário não encontrado para o email: " + email));
    }

    @Override
    @Transactional
    public void criarUsuario(UsuarioDTO usuarioDTO) {
        Usuario usuario = UsuarioConverter.paraEntidade(usuarioDTO);
        // Lembre-se de encriptar a senha aqui antes de salvar
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void atualizarUsuario(Long id, UsuarioDTO usuarioDTO) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoAutorizadoException("Usuário não encontrado para o ID: " + id));

        usuarioExistente.setNome(usuarioDTO.getNome());
        usuarioExistente.setEmail(usuarioDTO.getEmail());

        if (usuarioDTO.getRoles() != null) {
            // Cria os novos objetos de junção UsuarioPerfil
            Set<UsuarioPerfil> novosPerfis = usuarioDTO.getRoles().stream()
                    .map(roleName -> {
                        // O ideal seria buscar um Perfil existente por roleName para reutilizá-lo
                        Perfil perfil = new Perfil();
                        perfil.setDescricao(roleName);

                        UsuarioPerfil usuarioPerfil = new UsuarioPerfil();
                        usuarioPerfil.setPerfil(perfil);
                        usuarioPerfil.setUsuario(usuarioExistente);
                        return usuarioPerfil;
                    })
                    .collect(Collectors.toSet());

            // Atualiza a coleção de perfis do usuário
            usuarioExistente.getPerfis().clear();
            usuarioExistente.getPerfis().addAll(novosPerfis);
        }

        usuarioRepository.save(usuarioExistente);
    }

    @Override
    @Transactional
    public void deletarUsuario(Long id) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoAutorizadoException("Usuário não encontrado para o ID: " + id));

        usuarioRepository.delete(usuarioExistente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios.stream()
                .map(UsuarioConverter::paraDTO)
                .collect(Collectors.toList());
    }
}
