<div align="center">
  <img src="https://raw.githubusercontent.com/arthurspk/guiadevbrasil/master/src/assets/gifs/fundo-de-tela-programador.gif" alt="Banner Futurista" width="100%"/>
</div>

<h1 align="center">Sistema de Contas a Pagar API</h1>

<p align="center">
 <!--  <img alt="Status do Build" src="https://github.com/SEU_USUARIO/SEU_REPOSITORIO/actions/workflows/ci.yml/badge.svg"> -->
  <img alt="Java" src="https://img.shields.io/badge/Java-21-blue?style=flat-square&logo=java">
  <img alt="Spring Boot" src="https://img.shields.io/badge/Spring_Boot-3.3.5-brightgreen?style=flat-square&logo=spring">
  <img alt="Licença" src="https://img.shields.io/badge/licença-MIT-informational?style=flat-square">
</p>

API RESTful desenvolvida em **Java 21** com **Spring Boot 3**, projetada para o gerenciamento completo de contas a pagar, com um design de arquitetura robusto e foco em boas práticas de desenvolvimento.

---

## 🌌 Visão Geral da Arquitetura

O projeto é construído sobre a **Arquitetura Hexagonal (Ports and Adapters)**, uma abordagem que isola a lógica de negócio de detalhes de infraestrutura. Isso resulta em um sistema altamente testável, manutenível e adaptável a futuras mudanças tecnológicas.

*   **DDD Pragmático:** As entidades do domínio (`Domain`) são enriquecidas com anotações JPA (`@Entity`), combinando a riqueza do Domain-Driven Design com a produtividade do Spring Data JPA.
*   **Contract-First com OpenAPI:** A API é definida em arquivos YAML (`src/main/resources/swagger/*.yml`), garantindo um contrato claro e gerando as interfaces dos controllers automaticamente.

---

## 🛠️ Tecnologias Principais

*   **Java 21**: A mais recente versão LTS do Java.
*   **Spring Boot 3.3.5**: (Web, Data JPA, Security) para uma base robusta e ágil.
*   **PostgreSQL**: Banco de dados relacional para produção.
*   **H2 Database**: Banco de dados em memória para testes automatizados.
*   **Flyway**: Versionamento e migração de schema do banco de dados.
*   **JWT (JSON Web Token)**: Autenticação e autorização stateless.
*   **OpenAPI Generator**: Geração de código a partir da especificação da API.
*   **Lombok**: Redução de código boilerplate.
*   **Gradle**: Automação de build e gerenciamento de dependências.
*   **JUnit 5 & Mockito**: Testes unitários e de integração.
*   **GitHub Actions**: Pipeline de CI/CD para automação de testes.

---

## 📂 Estrutura de Diretórios

A estrutura do projeto é modularizada por domínios de negócio, com uma clara separação entre o código de produção e os testes.

```
.
├── .github/workflows/
│   └── ci.yml                     # ⚙️ Pipeline de CI/CD (GitHub Actions)
│
├── postman/
│   └── collection.json            # 🚀 Coleção do Postman (Opcional)
│
└── src/
    ├── main/
    │   ├── java/com/sistema/contas/
    │   │   ├── {dominio}/           # (auth, pessoa, lancamento, etc.)
    │   │   │   ├── adapters/        # Adaptadores (Controllers, Converters)
    │   │   │   ├── application/     # Portas (Interfaces de Serviço e Repositório)
    │   │   │   ├── domain/          # Entidades e Casos de Uso (Regras de Negócio)
    │   │   │   └── infrastructures/ # Implementação das Portas (Serviços)
    │   │   ├── core/                # Código compartilhado
    │   │   └── config/              # Configurações do Spring
    │   └── resources/
    │       ├── db/migration/        # Scripts de migração do Flyway (SQL)
    │       ├── swagger/             # Especificações da API (YAML)
    │       └── application.yml      # Configurações da aplicação
    │
    └── test/
        ├── java/com/sistema/contas/ # 🧪 Testes Unitários e de Integração
        └── resources/
            └── application.yml      # Configurações específicas para testes
```

---

## 🔒 Segurança: Autenticação e Autorização

A segurança da API é garantida pelo Spring Security, utilizando um fluxo de autenticação stateless com JWT.

1.  **Login**: O cliente envia as credenciais para `POST /auth/login`.
2.  **Token**: A API valida e retorna um Token JWT com as permissões (`Roles`) do usuário.
3.  **Acesso**: O cliente envia o token no header `Authorization: Bearer <token>` para acessar rotas protegidas.
4.  **Autorização**:
    *   **Global (`SecurityConfig.java`):** Define rotas públicas e quais exigem autenticação.
    *   **Por Método (`@PreAuthorize`):** Controla o acesso a endpoints específicos com base nas `Roles` do usuário (ex: `@PreAuthorize("hasRole('ADMIN')")`).

---

## 🚀 Executando a Aplicação

### Pré-requisitos
*   **Java 21** instalado e configurado no `JAVA_HOME`.
*   **PostgreSQL** em execução (porta `5432`).
*   Um banco de dados chamado `contas` criado no PostgreSQL.

### Passos
1.  Clone este repositório.
2.  Ajuste as credenciais do banco de dados em `src/main/resources/application.yml`, se necessário.
3.  Execute o comando na raiz do projeto:

    ```bash
    # No Windows
    gradlew.bat bootRun

    # No Linux/Mac
    ./gradlew bootRun
    ```
4.  O Flyway irá configurar o banco de dados automaticamente na primeira inicialização.

### 📄 Documentação da API (Swagger)

Com a aplicação em execução, a documentação completa da API está disponível em:

**[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

---

## 🧪 Testes Automatizados

O projeto tem uma cobertura de testes robusta para garantir a qualidade e a estabilidade do código.

### Executando os Testes
Para rodar a suíte completa de testes, utilize o comando:

```bash
# No Windows
gradlew.bat test

# No Linux/Mac
./gradlew test
```

Os testes utilizam um banco de dados **H2 em memória** configurado para simular o PostgreSQL, garantindo que os testes rodem de forma isolada e sem a necessidade de um banco de dados externo.

---

## ⚙️ CI/CD: Integração Contínua

O pipeline de CI/CD, configurado com **GitHub Actions**, é acionado a cada `push` ou `pull request` nas branches `main` e `master`. Ele executa automaticamente todos os testes para garantir que novas alterações não quebrem a funcionalidade existente.

<div align="center">
  <p>Feito com ❤️ por Rivaldo Souza</p>
</div>
