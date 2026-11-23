# Sistema de Gestão de Finanças Pessoais

API REST desenvolvida em **Spring Boot 4.1.3** com **PostgreSQL** para gerenciamento de finanças pessoais, seguindo as especificações do Projeto 6 (Finanças Pessoais).

## 📋 Funcionalidades

- **Contas**: Criar, listar e consultar contas financeiras
- **Transações**: Registrar receitas e despesas por conta
- **Saldo**: Cálculo automático do saldo (saldo inicial + receitas - despesas)
- **Resumo Financeiro**: Consulta de totais de receitas e despesas via query nativa
- **Filtros**: Busca de transações por tipo, descrição e período
- **Validações**: Regras de negócio com exceções personalizadas
- **Documentação**: Swagger/OpenAPI automático

## 🛠 Stack Tecnológica

| Camada | Tecnologia |
|--------|------------|
| Linguagem | Java 17 |
| Framework | Spring Boot 4.1.3 |
| Banco de Dados | PostgreSQL 16 |
| ORM | Spring Data JPA / Hibernate |
| Migração | Flyway |
| Validação | Bean Validation (Jakarta) |
| Documentação | SpringDoc OpenAPI 3 (Swagger UI) |
| Build | Maven Wrapper |

## 📁 Estrutura do Projeto

```
src/main/java/br/edu/fatecpg/projetospring/
├── ProjetospringApplication.java
├── config/
│   └── CorsConfig.java                    # Configuração CORS para frontend
├── controller/
│   ├── ContaController.java               # Endpoints de contas
│   └── TransacaoController.java           # Endpoints de transações
├── dto/
│   ├── request/
│   │   ├── ContaRequestDTO.java
│   │   ├── TransacaoRequestDTO.java
│   │   └── AtualizarDescricaoRequestDTO.java
│   └── response/
│       ├── ContaResponseDTO.java
│       ├── TransacaoResponseDTO.java
│       ├── SaldoResponseDTO.java
│       └── ResumoFinanceiroResponseDTO.java
├── exception/
│   ├── BusinessException.java
│   ├── ContaNaoEncontradaException.java
│   ├── TransacaoNaoEncontradaException.java
│   ├── DescricaoVaziaException.java
│   └── GlobalExceptionHandler.java        # Tratamento global de erros
├── model/
│   ├── entity/
│   │   ├── Conta.java                     # Entidade Conta (com regras de negócio)
│   │   └── Transacao.java                 # Entidade Transação (com regras de negócio)
│   ├── enum_
│   │   └── TipoTransacao.java             # RECEITA | DESPESA
│   └── projection/
│       └── ResumoFinanceiro.java          # Interface de projeção para query nativa
├── repository/
│   ├── ContaRepository.java
│   └── TransacaoRepository.java           # Queries nativas, derivadas e JPQL
├── runner/
│   └── DataLoader.java                    # Dados de exemplo na inicialização
└── service/
    ├── ContaService.java                  # Regras de negócio de contas
    └── TransacaoService.java              # Regras de negócio de transações
```

## 🚀 Como Executar

### Pré-requisitos
- Java 17+
- Docker e Docker Compose
- Maven (ou usar o wrapper `./mvnw`)

### 1. Subir o Banco de Dados
```bash
docker compose up -d
```
Verificar se subiu:
```bash
docker compose ps
docker compose logs postgres
```

### 2. Executar a Aplicação
```bash
./mvnw spring-boot:run
```

A API estará disponível em: **http://localhost:8080**

### 3. Acessar Swagger UI
**http://localhost:8080/swagger-ui.html**

## 📚 Endpoints da API

### Contas

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/contas` | Listar todas as contas |
| POST | `/contas` | Criar nova conta |
| GET | `/contas/{id}` | Buscar conta por ID |
| GET | `/contas/{id}/detalhes` | Buscar conta com transações |
| GET | `/contas/{id}/saldo` | Calcular saldo atual |
| GET | `/contas/{id}/resumo` | Resumo financeiro (query nativa) |

### Transações

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/contas/{contaId}/transacoes` | Listar transações com filtros |
| POST | `/contas/{contaId}/transacoes` | Registrar nova transação |
| GET | `/contas/{contaId}/transacoes/{id}` | Buscar transação por ID |
| PATCH | `/contas/{contaId}/transacoes/{id}/descricao` | Atualizar descrição |
| DELETE | `/contas/{contaId}/transacoes/{id}` | Remover transação |

### Filtros de Transações (Query Parameters)
- `tipo`: `RECEITA` ou `DESPESA`
- `descricao`: Busca parcial (case-insensitive)
- `inicio`: Data inicial (formato `yyyy-MM-dd`)
- `fim`: Data final (formato `yyyy-MM-dd`)

## 📝 Exemplos de Uso

### Criar uma Conta
```bash
curl -X POST http://localhost:8080/contas \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Minha Conta",
    "saldoInicial": "1000.00"
  }'
```

### Registrar Transação (Receita)
```bash
curl -X POST http://localhost:8080/contas/1/transacoes \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "Salário Janeiro",
    "valor": "5000.00",
    "tipo": "RECEITA",
    "data": "2026-01-05",
    "categoria": "Salário"
  }'
```

### Registrar Transação (Despesa)
```bash
curl -X POST http://localhost:8080/contas/1/transacoes \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "Aluguel",
    "valor": "1500.00",
    "tipo": "DESPESA",
    "data": "2026-01-10",
    "categoria": "Moradia"
  }'
```

### Consultar Saldo
```bash
curl http://localhost:8080/contas/1/saldo
```

### Consultar Resumo Financeiro (Query Nativa)
```bash
curl http://localhost:8080/contas/1/resumo
```

### Listar Transações com Filtros
```bash
# Apenas receitas
curl "http://localhost:8080/contas/1/transacoes?tipo=RECEITA"

# Por período
curl "http://localhost:8080/contas/1/transacoes?inicio=2026-01-01&fim=2026-01-31"

# Por descrição
curl "http://localhost:8080/contas/1/transacoes?descricao=salario"

# Combinados
curl "http://localhost:8080/contas/1/transacoes?tipo=DESPESA&inicio=2026-01-01&fim=2026-01-31"
```

### Atualizar Descrição
```bash
curl -X PATCH http://localhost:8080/contas/1/transacoes/1/descricao \
  -H "Content-Type: application/json" \
  -d '{"novaDescricao": "Salário Janeiro 2026"}'
```

### Remover Transação
```bash
curl -X DELETE http://localhost:8080/contas/1/transacoes/1
```

## 🗄 Migrações Flyway

As migrações ficam em `src/main/resources/db/migration/`:

- `V1__create_tables.sql` - Criação das tabelas `conta` e `transacao` com índices

##  Testes

```bash
# Executar todos os testes
./mvnw test

# Executar testes com relatório
./mvnw test jacoco:report
```

### Estrutura de Testes Sugerida
```
src/test/java/br/edu/fatecpg/projetospring/
├── entity/
│   ├── ContaTest.java              # Testes de regras de negócio (calcularSaldo, adicionarTransacao)
│   └── TransacaoTest.java          # Testes de regras (atualizarDescricao, exibirInformacoes)
├── repository/
│   ├── ContaRepositoryTest.java    # @DataJpaTest para queries derivadas
│   └── TransacaoRepositoryTest.java # @DataJpaTest para queries nativas, JPQL e derivadas
└── service/
    ├── ContaServiceTest.java       # Testes de serviço com @MockBean
    └── TransacaoServiceTest.java
```

## 🐳 Docker Compose

```yaml
services:
  postgres:
    image: 'postgres:16-alpine'
    container_name: financas-postgres
    environment:
      - 'POSTGRES_DB=financas_pessoais'
      - 'POSTGRES_PASSWORD=postgres'
      - 'POSTGRES_USER=postgres'
    ports:
      - '5432:5432'
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres -d financas_pessoais"]
      interval: 10s
      timeout: 5s
      retries: 5

volumes:
  postgres_data:
```

## 📊 Modelo de Dados

### Entidade Conta
| Campo | Tipo | Restrições |
|-------|------|------------|
| id | Long | PK, Auto-increment |
| nome | String | Unique, Not Null |
| saldo_inicial | BigDecimal | Not Null, >= 0 |
| transacoes | List<Transacao> | OneToMany, Cascade ALL |

### Entidade Transacao
| Campo | Tipo | Restrições |
|-------|------|------------|
| id | Long | PK, Auto-increment |
| descricao | String | Not Null, max 255 |
| valor | BigDecimal | Not Null, > 0 |
| tipo | Enum | Not Null (RECEITA/DESPESA) |
| data | LocalDate | Not Null |
| categoria | String | Nullable, max 100 |
| conta_id | Long | FK → conta(id), Not Null |

## ⚙️ Regras de Negócio Implementadas

1. **Conta**:
   - `calcularSaldo()`: saldoInicial + Σ(receitas) - Σ(despesas)
   - `adicionarTransacao()`: adiciona e vincula bidirecionalmente
   - `removerTransacao()`: remove e desvincula
   - `buscarTransacao(id)`: busca na lista da conta

2. **Transacao**:
   - `atualizarDescricao(nova)`: rejeita texto vazio/nulo
   - `exibirInformacoes()`: imprime detalhes (toString)

3. **Validações**:
   - Dinheiro sempre em `BigDecimal` (nunca `double`)
   - Datas em `LocalDate`/`LocalDateTime`
   - Enums persistidos como `STRING`
   - IDs gerados pelo banco (`IDENTITY`)

##  Configuração CORS

Configurado em `CorsConfig.java` para permitir:
- http://localhost:3000 (React)
- http://localhost:5173 (Vite)
- http://localhost:8080
- http://127.0.0.1:5500 (Live Server)

##  Build para Produção

```bash
# Gerar JAR
./mvnw clean package -DskipTests

# Executar JAR
java -jar target/projetospring-0.0.1-SNAPSHOT.jar
```

