# Sistema de Gestão de Finanças Pessoais

API REST em **Spring Boot** com **PostgreSQL** para registrar receitas e despesas de uma ou mais contas e calcular o saldo, seguindo as especificações do **Projeto 6 (Finanças Pessoais)**. O enunciado completo está em [`refs/Projetos_Spring_Boot_PostgreSQL 1.pdf`](refs/).

## 📁 Organização do Repositório

```
projeto_spring/
├── projetospring/     # Backend: API REST Spring Boot (este README cobre como rodar)
├── frontend/          # Frontend em Thymeleaf (em desenvolvimento em branch própria)
└── refs/              # Enunciado do projeto (PDF)
```

> ⚠️ Todos os comandos do backend (`docker compose`, `./mvnw`) devem ser executados **dentro da pasta `projetospring/`**.

## 📋 Funcionalidades

- **Contas**: criar, listar e consultar contas financeiras
- **Transações**: registrar, consultar, atualizar descrição e remover receitas e despesas por conta
- **Saldo**: calculado a partir das transações (saldo inicial + receitas − despesas), nunca armazenado
- **Resumo financeiro**: totais de receitas e despesas via **query nativa**
- **Despesas por categoria**: totais agrupados via **JPQL** (base para o gráfico do desafio opcional)
- **Filtros**: busca de transações por tipo, descrição e período, por conta ou em todas as contas
- **Validações e erros**: exceções de negócio e respostas de erro em JSON com status 400, 404 e 409
- **Documentação**: Swagger/OpenAPI gerado automaticamente

## 🛠 Stack Tecnológica

| Camada | Tecnologia |
|--------|------------|
| Linguagem | Java 17 |
| Framework | Spring Boot 4.1.3 |
| Banco de Dados | PostgreSQL 16 (Docker Compose) |
| ORM | Spring Data JPA / Hibernate |
| Migrações | Flyway |
| Validação | Bean Validation (Jakarta) |
| Documentação | SpringDoc OpenAPI (Swagger UI) |
| Testes | JUnit 5, Mockito, MockMvc, H2, JaCoCo |
| Build | Maven Wrapper |
| Frontend | Thymeleaf |

> O enunciado recomenda Spring Boot 3.x; o projeto foi criado no Spring Initializr com a versão 4.1.3, que mantém a mesma API usada no curso (Spring Web, Data JPA, Validation).

## 🚀 Como Executar

### Pré-requisitos
- Java 17+
- Docker e Docker Compose
- Maven (opcional, o projeto inclui o wrapper `./mvnw`)

### 1. Subir o banco de dados
```bash
cd projetospring
docker compose up -d
```
Verificar se subiu:
```bash
docker compose ps
docker compose logs postgres
```

> O projeto inclui `spring-boot-docker-compose`, então o passo 2 também sobe o container automaticamente se ele não estiver rodando.
> Se a porta **5432** já estiver em uso por outro PostgreSQL, pare-o ou altere a porta em `compose.yaml` e em `spring.datasource.url` (`application.properties`).

### 2. Executar o backend
```bash
./mvnw spring-boot:run        # Linux/macOS/Git Bash
mvnw.cmd spring-boot:run      # Windows (cmd/PowerShell)
```

Na inicialização, o Flyway cria as tabelas e o `DataLoader` insere dados de exemplo (3 contas e 11 transações), caso o banco esteja vazio.

A API fica disponível em **http://localhost:8080**.

### 3. Acessar o Swagger UI
**http://localhost:8080/swagger-ui.html** (JSON da especificação em `/api-docs`)

### 4. Executar o frontend
O frontend está sendo desenvolvido em **Thymeleaf** em uma branch separada. As instruções de execução serão adicionadas aqui quando ele for integrado.

## 🎨 Frontend: Thymeleaf

**Tecnologia escolhida:** Thymeleaf, com páginas renderizadas no servidor por uma aplicação Spring Boot separada, que consome esta API REST.

**Por quê:**
- Mantém o grupo no ecossistema **Java/Spring** que já usamos no backend, sem precisar de outra stack (Node, npm, framework JS).
- Renderização no servidor deixa as telas simples de construir e de demonstrar.
- Integração direta com a API usando o `RestClient` do Spring.

**Telas mínimas e endpoints usados:**

| Tela | Endpoint |
|------|----------|
| Painel: saldo, total de receitas e de despesas | `GET /contas/{id}/saldo` |
| Extrato ordenado por data, com filtros por tipo e descrição | `GET /contas/{id}/transacoes?tipo=&descricao=` |
| Formulário de nova transação | `POST /contas/{id}/transacoes` |
| Seleção de conta | `GET /contas` |
| Gráfico de despesas por categoria (desafio opcional) | `GET /contas/{id}/despesas-por-categoria` |

## 📚 Endpoints da API

### Contas

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/contas` | Listar todas as contas |
| POST | `/contas` | Criar nova conta |
| GET | `/contas/{id}` | Buscar conta por ID |
| GET | `/contas/{id}/detalhes` | Buscar conta com suas transações |
| GET | `/contas/{id}/saldo` | Saldo atual com total de receitas e despesas |
| GET | `/contas/{id}/resumo` | Resumo financeiro (query nativa) |
| GET | `/contas/{id}/despesas-por-categoria` | Despesas agrupadas por categoria (JPQL) |

### Transações de uma conta

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/contas/{contaId}/transacoes` | Listar transações da conta, com filtros |
| POST | `/contas/{contaId}/transacoes` | Registrar nova transação |
| GET | `/contas/{contaId}/transacoes/{id}` | Buscar transação por ID |
| PATCH | `/contas/{contaId}/transacoes/{id}/descricao` | Atualizar descrição |
| DELETE | `/contas/{contaId}/transacoes/{id}` | Remover transação |

As rotas com `{id}` verificam se a transação pertence à conta informada; caso contrário, retornam **404**.

### Transações de todas as contas

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/transacoes` | Listar transações de todas as contas, ordenadas por data, com filtros |

### Filtros (query parameters)
Valem para `GET /contas/{contaId}/transacoes` e `GET /transacoes`, e podem ser combinados:

- `tipo`: `RECEITA` ou `DESPESA`
- `descricao`: busca parcial, sem diferenciar maiúsculas de minúsculas
- `inicio`: data inicial (formato `yyyy-MM-dd`)
- `fim`: data final (formato `yyyy-MM-dd`)

`inicio` e `fim` devem ser informados juntos, com `inicio <= fim`. Datas em formato inválido, período incompleto ou tipo inexistente retornam **400**.

## 🔎 Consultas do Repositório e Onde São Usadas

| Tipo | Consulta (`TransacaoRepository`) | Endpoint |
|------|----------------------------------|----------|
| Nativa | `somarReceitasEDespesas(contaId)` | `GET /contas/{id}/resumo` |
| Derivada | `findByTipo(tipo)` | `GET /transacoes?tipo=` |
| Derivada | `findByDescricaoContainingIgnoreCase(descricao)` | `GET /transacoes?descricao=` |
| Derivada | `findByDataBetween(inicio, fim)` | `GET /transacoes?inicio=&fim=` |
| Derivada | `findByIdAndContaId(id, contaId)` | `GET/PATCH/DELETE /contas/{contaId}/transacoes/{id}` |
| JPQL | `listarOrdenadasPorData()` | `GET /transacoes` |
| JPQL | `somarPorCategoria(contaId, tipo)` | `GET /contas/{id}/despesas-por-categoria` |
| JPQL | `findByContaId...` (por tipo, descrição, período) | `GET /contas/{contaId}/transacoes` |

Em `ContaRepository`: `findByIdWithTransacoes` (JPQL com `JOIN FETCH`), usada em `GET /contas/{id}/detalhes`, e `existsByNome` (derivada), usada para impedir nomes de conta duplicados.

## ❗ Respostas de Erro

Todos os erros retornam JSON no formato:

```json
{
  "status": 404,
  "error": "CONTA_NAO_ENCONTRADA",
  "message": "Conta não encontrada com ID: 99",
  "timestamp": "2026-01-10T14:30:00",
  "details": null
}
```

| Status | Código (`error`) | Quando |
|--------|------------------|--------|
| 400 | `VALIDATION_ERROR` | Campos inválidos no corpo da requisição (detalhes por campo em `details`) |
| 400 | `PARAMETRO_INVALIDO` | Data ou tipo em formato inválido nos filtros |
| 400 | `PERIODO_INVALIDO` | Período incompleto ou com `inicio` posterior a `fim` |
| 400 | `DESCRICAO_VAZIA` | Atualização de descrição com texto vazio |
| 400 | `MALFORMED_JSON` | Corpo da requisição não é um JSON válido |
| 404 | `CONTA_NAO_ENCONTRADA` | Conta inexistente |
| 404 | `TRANSACAO_NAO_ENCONTRADA` | Transação inexistente ou de outra conta |
| 409 | `DUPLICATE_ENTRY` | Já existe uma conta com o mesmo nome |
| 409 | `DATA_INTEGRITY_VIOLATION` | Dados violam uma restrição do banco |

Detalhes técnicos do banco (SQL, nomes de constraints) nunca são enviados ao cliente, apenas registrados no log.

## 📝 Exemplos de Uso

### Criar uma conta
```bash
curl -X POST http://localhost:8080/contas \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Minha Conta",
    "saldoInicial": "1000.00"
  }'
```

### Registrar transação (receita)
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

### Registrar transação (despesa)
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

### Consultar saldo
```bash
curl http://localhost:8080/contas/1/saldo
```
Resposta com os dados de exemplo:
```json
{"contaId":1,"contaNome":"Conta Corrente","saldoInicial":1000.00,"totalReceitas":6200.00,"totalDespesas":2720.00,"saldoAtual":4480.00}
```

### Consultar resumo financeiro (query nativa)
```bash
curl http://localhost:8080/contas/1/resumo
```

### Despesas por categoria (JPQL)
```bash
curl http://localhost:8080/contas/1/despesas-por-categoria
```
```json
[{"categoria":"Moradia","total":1500.00},{"categoria":"Alimentação","total":800.00},{"categoria":"Transporte","total":300.00},{"categoria":"Serviços","total":120.00}]
```

### Listar transações com filtros
```bash
# Apenas receitas da conta 1
curl "http://localhost:8080/contas/1/transacoes?tipo=RECEITA"

# Por período
curl "http://localhost:8080/contas/1/transacoes?inicio=2026-01-01&fim=2026-01-31"

# Por descrição
curl "http://localhost:8080/contas/1/transacoes?descricao=salario"

# Combinados
curl "http://localhost:8080/contas/1/transacoes?tipo=DESPESA&inicio=2026-01-01&fim=2026-01-31"

# Todas as contas
curl "http://localhost:8080/transacoes?tipo=DESPESA&descricao=uber"
```

### Atualizar descrição
```bash
curl -X PATCH http://localhost:8080/contas/1/transacoes/1/descricao \
  -H "Content-Type: application/json" \
  -d '{"novaDescricao": "Salário Janeiro 2026"}'
```

### Remover transação
```bash
curl -X DELETE http://localhost:8080/contas/1/transacoes/1
```

## 🗄 Migrações e Dados de Exemplo

- **Migrações Flyway** em `src/main/resources/db/migration/`:
  - `V1__create_tables.sql`: tabelas `conta` e `transacao`, constraints (`saldo_inicial >= 0`, `valor > 0`, tipo `RECEITA`/`DESPESA`) e índices.
- **Dados de exemplo**: `runner/DataLoader.java` cria, se o banco estiver vazio, as contas *Conta Corrente*, *Poupança* e *Cartão de Crédito* com 11 transações de janeiro/2026.

O Hibernate roda com `ddl-auto=validate`: o esquema é responsabilidade do Flyway, e a aplicação não sobe se as entidades divergirem das tabelas.

## 🧪 Testes

Os testes usam banco **H2 em memória** (perfil `test`), então não precisam do Docker.

```bash
cd projetospring
./mvnw test
```

O relatório de cobertura do **JaCoCo** é gerado automaticamente ao rodar os testes, em `target/site/jacoco/index.html`.

### Estrutura dos testes
```
src/test/java/br/edu/fatecpg/projetospring/
├── ProjetospringApplicationTests.java  # Contexto da aplicação sobe corretamente
├── controller/
│   ├── RotasControllerTest.java        # Rotas sem ambiguidade e endpoints novos (MockMvc)
│   └── TratamentoErrosTest.java        # Respostas 400/409 para parâmetros e erros de banco
├── entity/
│   ├── ContaTest.java                  # calcularSaldo, adicionar/remover/buscarTransacao
│   └── TransacaoTest.java              # atualizarDescricao, exibirInformacoes
├── exception/
│   └── GlobalExceptionHandlerTest.java # Erros de validação de campo e de classe
├── repository/
│   ├── ContaRepositoryTest.java        # @DataJpaTest: derivadas e JOIN FETCH
│   └── TransacaoRepositoryTest.java    # @DataJpaTest: nativa, derivadas e JPQL
└── service/
    ├── ContaServiceTest.java           # Saldo, resumo e despesas por categoria (Mockito)
    └── TransacaoServiceTest.java       # Filtros, período e transações de outra conta (Mockito)
```

## 📁 Estrutura do Backend

```
projetospring/src/main/java/br/edu/fatecpg/projetospring/
├── ProjetospringApplication.java
├── config/
│   └── CorsConfig.java                    # CORS para o frontend
├── controller/
│   ├── ContaController.java               # /contas
│   ├── TransacaoController.java           # /contas/{contaId}/transacoes
│   └── TransacaoGeralController.java      # /transacoes (todas as contas)
├── dto/
│   ├── request/                           # ContaRequestDTO, TransacaoRequestDTO, AtualizarDescricaoRequestDTO
│   └── response/                          # Conta, Transacao, Saldo, ResumoFinanceiro, DespesaPorCategoria
├── exception/
│   ├── BusinessException.java             # Base das exceções de negócio (com código de erro)
│   ├── ContaNaoEncontradaException.java
│   ├── TransacaoNaoEncontradaException.java
│   ├── DescricaoVaziaException.java
│   └── GlobalExceptionHandler.java        # @RestControllerAdvice: erros em JSON
├── model/
│   ├── entity/
│   │   ├── Conta.java                     # Regras: calcularSaldo, adicionar/remover/buscarTransacao
│   │   └── Transacao.java                 # Regras: atualizarDescricao, exibirInformacoes
│   ├── enum_/
│   │   └── TipoTransacao.java             # RECEITA | DESPESA
│   └── projection/
│       ├── ResumoFinanceiro.java          # Projeção da query nativa
│       └── DespesaPorCategoria.java       # Projeção da JPQL por categoria
├── repository/
│   ├── ContaRepository.java
│   └── TransacaoRepository.java           # Queries nativas, derivadas e JPQL
├── runner/
│   └── DataLoader.java                    # Dados de exemplo na inicialização
└── service/
    ├── ContaService.java
    └── TransacaoService.java
```

## 📊 Modelo de Dados

### Conta
| Campo | Tipo | Restrições |
|-------|------|------------|
| id | Long | PK, gerado pelo banco (`IDENTITY`) |
| nome | String | Único, obrigatório |
| saldo_inicial | BigDecimal | Obrigatório, >= 0 |
| transacoes | List\<Transacao\> | `@OneToMany`, cascade ALL |

### Transacao
| Campo | Tipo | Restrições |
|-------|------|------------|
| id | Long | PK, gerado pelo banco (`IDENTITY`) |
| descricao | String | Obrigatório, até 255 caracteres |
| valor | BigDecimal | Obrigatório, > 0 |
| tipo | Enum | Obrigatório (`RECEITA`/`DESPESA`), salvo como texto |
| data | LocalDate | Obrigatório |
| categoria | String | Opcional, até 100 caracteres |
| conta_id | Long | FK → conta(id), obrigatório (`@ManyToOne`) |

## ⚙️ Regras de Negócio

1. **Conta**
   - `calcularSaldo()`: saldoInicial + Σ receitas − Σ despesas
   - `calcularTotalReceitas()` / `calcularTotalDespesas()`: totais usados no endpoint de saldo
   - `adicionarTransacao()` / `removerTransacao()`: mantêm o vínculo bidirecional
   - `buscarTransacao(id)`: busca na lista da conta
   - `exibirTransacoes()`: imprime as transações da conta
2. **Transacao**
   - `atualizarDescricao(nova)`: rejeita texto vazio ou nulo
   - `exibirInformacoes()`: imprime os detalhes (via `toString`)
3. **Boas práticas**
   - Dinheiro sempre em `BigDecimal` (nunca `double`)
   - Datas em `LocalDate`
   - Enum persistido como `STRING`
   - Entidades nunca expostas diretamente: a API usa DTOs

## 🌐 Configuração CORS

Configurado em `CorsConfig.java` para permitir:
- http://localhost:3000
- http://localhost:5173
- http://localhost:8080
- http://127.0.0.1:5500

## 📦 Build para Produção

```bash
cd projetospring
./mvnw clean package -DskipTests
java -jar target/projetospring-0.0.1-SNAPSHOT.jar
```
