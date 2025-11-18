package com.sistema.contas.auth.config.security;

import com.sistema.contas.auth.applications.ports.respository.UsuarioRepository;
import com.sistema.contas.auth.domain.entities.Usuario;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Carregando usuário com perfis: {}", username);
        
        // Usando o novo método com JOIN FETCH para garantir que os perfis sejam carregados
        Usuario usuario = usuarioRepository.findByEmailWithPerfis(username)
                .orElseThrow(() -> {
                    log.error("Usuário não encontrado com o email: {}", username);
                    return new UsernameNotFoundException("Usuário não encontrado com o email: " + username);
                });

        log.info("Usuário encontrado: {}", usuario.getEmail());

        if (usuario.getPerfis() == null || usuario.getPerfis().isEmpty()) {
            log.warn("ATENÇÃO: Nenhum perfil encontrado para o usuário {} no banco de dados!", username);
        } else {
            log.info("Perfis encontrados para {}: {}", username, usuario.getPerfis().size());
            usuario.getPerfis().forEach(usuarioPerfil -> {
                log.info("  - Perfil carregado: {}", usuarioPerfil.getPerfil().getDescricao());
            });
        }

        Set<SimpleGrantedAuthority> authorities = usuario.getPerfis().stream()
                .map(usuarioPerfil -> {
                    String roleName = "ROLE_" + usuarioPerfil.getPerfil().getDescricao().toUpperCase();
                    log.info("  - Mapeando para autoridade: {}", roleName);
                    return new SimpleGrantedAuthority(roleName);
                })
                .collect(Collectors.toSet());

        return new User(
                usuario.getEmail(),
                usuario.getSenha(),
                authorities
        );
    }
}
