package com.sistema.contas.auth.applications.ports.respository;

import com.sistema.contas.auth.domain.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca um usuário pelo email e força o carregamento de toda a árvore de perfis
     * (Usuario -> UsuarioPerfil -> Perfil) em uma única consulta para evitar
     * qualquer problema de Lazy Loading.
     * @param email O email do usuário a ser buscado.
     * @return Um Optional contendo o usuário com seus perfis completamente inicializados.
     */
    @Query("SELECT u FROM Usuario u JOIN FETCH u.perfis up JOIN FETCH up.perfil WHERE u.email = :email")
    Optional<Usuario> findByEmailWithPerfis(@Param("email") String email);

    // Mantém o método original caso seja usado em outros lugares sem a necessidade dos perfis
    Optional<Usuario> findByEmail(String email);
}
