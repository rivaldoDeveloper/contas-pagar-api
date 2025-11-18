package com.sistema.contas.auth.adapters.converts;

import com.grandle.auth.dtos.UsuarioDTO;
import com.sistema.contas.auth.domain.entities.Perfil;
import com.sistema.contas.auth.domain.entities.Usuario;
import com.sistema.contas.auth.domain.entities.UsuarioPerfil;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UsuarioConverter {

    public static UsuarioDTO paraDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(Math.toIntExact(usuario.getId()));
        usuarioDTO.setNome(usuario.getNome());
        usuarioDTO.setEmail(usuario.getEmail());
        // A senha não deve ser exposta no DTO por segurança

        if (usuario.getPerfis() != null) {
            List<String> roles = usuario.getPerfis().stream()
                    // Correção: Navega de UsuarioPerfil para Perfil e então pega a descrição
                    .map(usuarioPerfil -> usuarioPerfil.getPerfil().getDescricao())
                    .collect(Collectors.toList());
            usuarioDTO.setRoles(roles);
        }

        return usuarioDTO;
    }

    public static Usuario paraEntidade(UsuarioDTO usuarioDTO) {
        if (usuarioDTO == null) {
            return null;
        }
        Usuario usuario = new Usuario();
        usuario.setId(usuarioDTO.getId() != null ? usuarioDTO.getId().longValue() : null);
        usuario.setNome(usuarioDTO.getNome());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setSenha(usuarioDTO.getSenha()); // A senha será tratada pelo serviço

        if (usuarioDTO.getRoles() != null) {
            Set<UsuarioPerfil> perfis = usuarioDTO.getRoles().stream()
                    .map(roleName -> {
                        Perfil perfil = new Perfil();
                        perfil.setDescricao(roleName);

                        UsuarioPerfil usuarioPerfil = new UsuarioPerfil();
                        usuarioPerfil.setPerfil(perfil);
                        usuarioPerfil.setUsuario(usuario);
                        return usuarioPerfil;
                    })
                    .collect(Collectors.toSet());
            usuario.setPerfis(perfis);
        }
        return usuario;
    }
}
