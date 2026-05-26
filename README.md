# Sistema de Contas a Pagar API

API RESTful desenvolvida em **Java 21** com **Spring Boot 3**, voltada para o gerenciamento de contas a pagar, controle de pessoas, categorias de despesas, lançamentos e autenticação de usuários.

## 🛠 Tecnologias Utilizadas

*   **Java 21**
*   **Spring Boot 3.3.5** (Web, Data JPA, Security)
*   **PostgreSQL** (Banco de Dados)
*   **H2 Database** (Banco de Dados em memória para testes)
*   **Flyway** (Migrations / Versionamento de Banco de Dados)
*   **JWT (JSON Web Token)** (Autenticação e Autorização Stateless)
*   **OpenAPI Generator / Swagger UI** (Documentação e design Contract-First)
*   **Lombok** (Redução de Boilerplate)
*   **Gradle** (Gerenciador de dependências e build)
*   **JUnit 5 & Mockito** (Testes Unitários e de Integração)
*   **GitHub Actions** (CI/CD Pipeline)

---

## 🏗 Arquitetura do Projeto

O projeto utiliza a **Arquitetura Hexagonal (Ports and Adapters)**, combinada com uma abordagem pragmática de **Domain-Driven Design (DDD)**. 

O objetivo desta arquitetura é isolar a lógica de negócio ("o coração da aplicação") de detalhes de infraestrutura, como banco de dados e interfaces web. Isso garante que a aplicação seja testável, de fácil manutenção e tolerante a mudanças tecnológicas.

*   **Abordagem Pragmática (JPA no Domínio):** Em um modelo DDD puro, as entidades de domínio seriam POJOs (Plain Old Java Objects) isolados, necessitando de uma camada de mapeamento extra para o banco de dados. Para maior produtividade e menor complexidade, adotamos uma abordagem pragmática comum no ecossistema Spring: nossas entidades de domínio (`Domain`) também atuam como modelos de persistência (`@Entity` do JPA).

*   **Contract-First (OpenAPI):** A interface da nossa API (os endpoints, métodos e DTOs) é projetada primeiro através de arquivos YAML (`src/main/resources/swagger/*.yml`). O Gradle gera as interfaces Java automaticamente, que nossos controladores então implementam.

---

## 📂 Estrutura de Pastas

O código-fonte está dividido por **Módulos/Domínios** de negócio (ex: `auth`, `pessoa`, `lancamento`, `categoria`). A estrutura principal é separada entre código de produção (`src/main`) e testes (`src/test`):

```text
├── src/
│   ├── main/java/com/sistema/contas/
│   │   ├── {dominio}/                     # Ex: auth, pessoa, lancamento
│   │   │   ├── adapters/                  # Adaptadores (Comunicação com o mundo externo)
│   │   │   │   ├── controllers/           # Endpoints da API REST (Implementam interfaces do OpenAPI)
│   │   │   │   ├── converts/              # Traduzem DTOs para Entidades de Domínio
│   │   │   │   └── dto/                   # Data Transfer Objects (Caso não sejam gerados pelo OpenAPI)
│   │   │   │
│   │   │   ├── application/               # Aplicação (Orquestração e Contratos)
│   │   │   │   └── ports/                 # As "Portas" do hexágono (Interfaces)
│   │   │   │       ├── repository/        # Contratos para infraestrutura de dados (Outbound Ports)
│   │   │   │       └── service/           # Contratos para serviços (Inbound Ports)
│   │   │   │
│   │   │   ├── domain/                    # Domínio (O coração do negócio)
│   │   │   │   ├── entities/              # Entidades ricas de negócio e mapeamento JPA (@Entity)
│   │   │   │   └── usecases/              # Casos de uso contendo as regras de negócio
│   │   │   │
│   │   │   └── infrastructures/           # Infraestrutura (Implementação de detalhes técnicos)
│   │   │       └── services/              # Implementação das portas de serviço (Orquestra repositórios e casos de uso)
│   │   │
│   │   ├── core/                          # Códigos compartilhados (Utils, exceções globais)
│   │   └── config/                        # Configurações do Spring (Segurança, Swagger, etc.)
│   │
│   └── test/java/com/sistema/contas/      # 🧪 Testes Unitários e de Integração
│       ├── {dominio}/
│       │   ├── adapters/controllers/      # Testes isolados dos Controllers (@WebMvcTest)
│       │   └── infrastructures/services/  # Testes isolados da camada de Serviço com Mocks
│       └── ContasApplicationTests.java    # Teste de integração principal (@SpringBootTest)
│
├── .github/workflows/                     # ⚙️ Pipeline de CI/CD (GitHub Actions)
│   └── ci.yml                             # Definição do fluxo de testes automatizados
│
└── postman/                               # 🚀 Coleção do Postman para testar a API (opcional)
    └── ContasAPagar.postman_collection.json
```

### Explicando o Fluxo (Exemplo: Salvar uma Pessoa)

1.  **Adapter (Controller):** O `PessoaController` recebe uma requisição HTTP (POST) contendo um `PessoaDTO`.
2.  **Adapter (Converter):** O Controller usa o `PessoaConverters` para transformar o `PessoaDTO` na entidade de domínio `Pessoa`.
3.  **Port (Service Interface):** O Controller chama o método `criarPessoa` na interface `IPessoaService`.
4.  **Infrastructure (Service Impl):** O `PessoaService` executa as regras de negócio usando o caso de uso correspondente (`PessoaUseCase`).
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

## 🚀 Como Executar a Aplicação

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

---

## 🧪 Testes Unitários e de Integração

O projeto possui uma suíte abrangente de testes automatizados utilizando **JUnit 5** e **Mockito**, focando nas camadas de Controle (Controllers) e Serviço (Services).

### Executando os Testes Localmente

Para rodar todos os testes do projeto, abra o terminal na raiz do projeto e execute:

**No Windows:**
```bash
gradlew.bat test
```

**No Linux/Mac:**
```bash
./gradlew test
```

Os testes de integração utilizam o banco de dados em memória **H2 Database**, configurado em modo de compatibilidade com PostgreSQL através do arquivo `src/test/resources/application.yml`. O Flyway é desabilitado durante a fase de testes para garantir um ambiente limpo.

---

## ⚙️ CI/CD (GitHub Actions)

A aplicação conta com um pipeline de Integração Contínua (CI) configurado via **GitHub Actions**. 

Sempre que um **Push** ou um **Pull Request** for realizado para as branches `main` ou `master`, o GitHub Actions irá:

1.  Provisionar uma máquina virtual Ubuntu.
2.  Configurar o ambiente Java (JDK 21).
3.  Fazer o checkout do código fonte.
4.  Executar a rotina completa de testes unitários e de integração de forma isolada (`./gradlew test`).

Isso garante que código quebrado ou que não passe nas validações de negócio não seja integrado à branch principal da aplicação.

---

## 📮 Postman Collection

Para facilitar os testes da API além do Swagger UI, você pode utilizar a coleção do Postman.

1.  Se existir uma pasta chamada `postman` na raiz do projeto, procure pelo arquivo `.json` dentro dela (ex: `ContasAPagar.postman_collection.json`).
2.  Abra o aplicativo Postman.
3.  Vá em **File > Import** e selecione o arquivo `.json`.
4.  A coleção será carregada, contendo exemplos de requisições para login, criação de categorias, pessoas, etc. Lembre-se de autenticar primeiro na rota `/auth/login` e configurar o token JWT na aba de *Authorization* (Bearer Token) para as demais requisições.
