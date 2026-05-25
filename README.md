# Sistema de Contas a Pagar API

API RESTful desenvolvida em **Java 21** com **Spring Boot 3**, voltada para o gerenciamento de contas a pagar, controle de pessoas, categorias de despesas, lançamentos e autenticação de usuários.

## 🛠 Tecnologias Utilizadas

*   **Java 21**
*   **Spring Boot 3.3.5** (Web, Data JPA, Security)
*   **PostgreSQL** (Banco de Dados)
*   **Flyway** (Migrations / Versionamento de Banco de Dados)
*   **JWT (JSON Web Token)** (Autenticação e Autorização Stateless)
*   **OpenAPI Generator / Swagger UI** (Documentação e design Contract-First)
*   **Lombok** (Redução de Boilerplate)
*   **Gradle** (Gerenciador de dependências e build)

---

## 🏗 Arquitetura do Projeto

O projeto utiliza a **Arquitetura Hexagonal (Ports and Adapters)**, combinada com uma abordagem pragmática de **Domain-Driven Design (DDD)**. 

O objetivo desta arquitetura é isolar a lógica de negócio ("o coração da aplicação") de detalhes de infraestrutura, como banco de dados e interfaces web. Isso garante que a aplicação seja testável, de fácil manutenção e tolerante a mudanças tecnológicas.

*   **Abordagem Pragmática (JPA no Domínio):** Em um modelo DDD puro, as entidades de domínio seriam POJOs (Plain Old Java Objects) isolados, necessitando de uma camada de mapeamento extra para o banco de dados. Para maior produtividade e menor complexidade, adotamos uma abordagem pragmática comum no ecossistema Spring: nossas entidades de domínio (`Domain`) também atuam como modelos de persistência (`@Entity` do JPA).

*   **Contract-First (OpenAPI):** A interface da nossa API (os endpoints, métodos e DTOs) é projetada primeiro através de arquivos YAML (`src/main/resources/swagger/*.yml`). O Gradle gera as interfaces Java automaticamente, que nossos controladores então implementam.

---

## 📂 Estrutura de Pastas

O código-fonte está dividido por **Módulos/Domínios** de negócio (ex: `auth`, `pessoa`, `lancamento`, `categoria`). Dentro de cada domínio, a estrutura segue a Arquitetura Hexagonal:

```text
src/main/java/com/sistema/contas/
├── {dominio}/                     # Ex: auth, pessoa, lancamento
│   ├── adapters/                  # Adaptadores (Comunicação com o mundo externo)
│   │   ├── controllers/           # Endpoints da API REST (Implementam as interfaces geradas pelo OpenAPI)
│   │   ├── converts/              # Classes que traduzem DTOs (entrada/saída) para Entidades de Domínio
│   │   └── dto/                   # Data Transfer Objects (Caso não sejam gerados pelo OpenAPI)
│   │
│   ├── application/               # Aplicação (Orquestração e Contratos)
│   │   └── ports/                 # As "Portas" do hexágono (Interfaces)
│   │       ├── repository/        # Contratos que a infraestrutura de dados deve implementar (Outbound Ports)
│   │       └── services/          # Contratos que os serviços devem implementar (Inbound Ports)
│   │
│   ├── domain/                    # Domínio (O coração do negócio)
│   │   ├── entities/              # Entidades ricas de negócio e mapeamento JPA (@Entity, @Table)
│   │   └── exceptions/            # Exceções específicas das regras de negócio
│   │
│   └── infrastructures/           # Infraestrutura (Implementação de detalhes técnicos)
│       └── services/              # Implementação das portas de serviço (Orquestra repositórios e domínio)
│
├── core/                          # Códigos compartilhados entre todos os domínios (Utils, exceções globais)
└── config/                        # Configurações do Spring (Segurança, Swagger, CORS, etc.)
```

### Explicando o Fluxo (Exemplo: Salvar uma Pessoa)

1.  **Adapter (Controller):** O `PessoaController` recebe uma requisição HTTP (POST) contendo um `PessoaDTO`.
2.  **Adapter (Converter):** O Controller usa o `PessoaConverters` para transformar o `PessoaDTO` na entidade de domínio `Pessoa`.
3.  **Port (Service Interface):** O Controller chama o método `criarPessoa` na interface `IPessoaService`.
4.  **Infrastructure (Service Impl):** O `PessoaService` executa as regras de negócio usando a entidade `Pessoa`.
5.  **Port (Repository Interface) -> JPA:** O serviço chama o `PessoaRepository` (Spring Data JPA) para persistir a entidade no PostgreSQL.

---

## 🔒 Segurança (Spring Security + JWT)

A aplicação é protegida por um sistema de autenticação Stateless usando JWT.

1.  O cliente envia credenciais para o endpoint `POST /auth/login`.
2.  Se validadas, a API retorna um Token JWT contendo as **Roles** (permissões) do usuário (ex: `ROLE_ADMIN`, `ROLE_USUARIO`).
3.  O cliente deve enviar este token no header HTTP `Authorization: Bearer <token>` nas requisições seguintes.
4.  A autorização ocorre em duas camadas:
    *   **Configuração Global (`SecurityConfig.java`):** Define rotas públicas e rotas que exigem apenas que o usuário esteja logado (`.authenticated()`).
    *   **Configuração de Método (`@PreAuthorize`):** Protege métodos específicos nos Controllers, exigindo permissões finas (ex: `@PreAuthorize("hasRole('ADMIN')")`).

---

## 🚀 Como Executar

### Pré-requisitos
*   Java 21 instalado (`JAVA_HOME` configurado).
*   PostgreSQL em execução (Porta padrão `5432`).
*   Banco de dados criado chamado `contas`.

### Passos

1.  Clone o repositório.
2.  Verifique/ajuste as credenciais do banco de dados no arquivo `src/main/resources/application.yml`.
3.  Na raiz do projeto, execute o comando (Windows):
    ```bash
    gradlew.bat bootRun
    ```
    Ou no Linux/Mac:
    ```bash
    ./gradlew bootRun
    ```
4.  O Flyway executará as migrações automaticamente, criando as tabelas necessárias no banco de dados.

### Documentação da API

Com a aplicação rodando, acesse a interface do Swagger UI para testar e visualizar os endpoints documentados:

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

*(Dica: Para testar rotas protegidas no Swagger, faça o login, copie o token, clique no botão "Authorize" no topo da página e cole seu token JWT).*
