# My Finance Control

API REST para controle financeiro pessoal.

## Tecnologias

- Java 17
- Spring Boot 3.5 (Web, Security, Data JPA)
- PostgreSQL
- JWT Authentication
- Maven
- Docker

## Funcionalidades

- Cadastro e login com JWT (access token + refresh token)
- CRUD de transacoes (receitas e despesas)
- Categorias customizadas por usuario
- Dashboard com resumo mensal e despesas por categoria
- Paginacao nas listagens

## Endpoints

### Autenticacao
| Metodo | Rota | Descricao |
|--------|------|-----------|
| POST | `/auth/register` | Registra usuario |
| POST | `/auth/login` | Login |
| POST | `/auth/refresh` | Atualiza token |
| POST | `/auth/logout` | Logout |

### Transacoes
| Metodo | Rota | Descricao |
|--------|------|-----------|
| POST/GET | `/transactions` | Cria / Lista (mês/ano, paginado) |
| GET | `/transactions/{id}` | Busca por ID |
| PUT | `/transactions/{id}` | Atualiza |
| DELETE | `/transactions/{id}` | Remove |

### Categorias
| Metodo | Rota | Descricao |
|--------|------|-----------|
| POST/GET | `/categories` | Cria / Lista |
| GET/PUT | `/categories/{id}` | Busca / Atualiza |
| DELETE | `/categories/{id}` | Remove |

### Dashboard
| Metodo | Rota | Descricao |
|--------|------|-----------|
| GET | `/dashboard/summary` | Resumo financeiro do mes |
| GET | `/dashboard/expenses-by-category` | Despesas por categoria |

### Swagger UI
Apos iniciar a aplicacao, acesse: `http://localhost:8080/swagger-ui.html`

## Como Rodar

### Requisitos

- Java 17+
- PostgreSQL

### Executar

```bash
mvn spring-boot:run
```

### Via Docker

```bash
docker build -t mfc-api .
docker run -p 8080:8080 mfc-api
```

## Variaveis de Ambiente

| Variavel | Descricao |
|----------|-----------|
| `DB_URL` | URL do PostgreSQL |
| `DB_USERNAME` | Usuario do banco |
| `DB_PASSWORD` | Senha do banco |
| `JWT_SECRET` | Chave de assinatura do JWT |
| `SERVER_PORT` | Porta do servidor |
