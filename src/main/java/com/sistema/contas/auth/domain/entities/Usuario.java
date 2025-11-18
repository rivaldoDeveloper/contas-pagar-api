package com.sistema.contas.auth.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "id") // Correção: Basear equals/hashCode apenas no ID
@Entity
@Table(name = "tb_usuario", schema = "contas")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_usuario")
    @SequenceGenerator(name = "sq_usuario", sequenceName = "contas.sq_usuario", allocationSize = 1)
    @Column(nullable = false)
    @NotNull(message = "O id não pode ser nulo")
    private Long id;

    @Column(nullable = false, length = 200)
    @NotBlank(message = "O nome não pode ser vazio")
    private String nome;

    @Column(nullable = false, unique = true, length = 255)
    @NotBlank(message = "O email não pode ser vazio")
    @Email(message = "O email deve ser válido")
    private String email;

    @Column(nullable = false, unique = true, length = 255)
    @NotBlank(message = "A senha não pode ser vazia")
    private String senha;

    @Column(name = "id_usuario_criacao", nullable = false)
    @NotNull(message = "O id do usuário criador não pode ser nulo")
    private Long idUsuarioCriacao;

    @Column(name = "data_criacao", nullable = false)
    @NotNull(message = "A data de criação não pode ser nula")
    private LocalDateTime dataCriacao;

    @Column(name = "data_ultima_atualizacao")
    private LocalDateTime dataUltimaAtualizacao;

    @Column(name = "id_usuario_ultima_atualizacao")
    private Long idUsuarioUltimaAtualizacao;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UsuarioPerfil> perfis = new HashSet<>(); // Correção: Inicializar a coleção

    // Método utilitário para obter as roles como strings
    public List<String> getRoles() {
        return perfis != null
                ? perfis.stream().map(up -> up.getPerfil().getDescricao()).collect(Collectors.toList())
                : List.of();
    }
}
