# Biblioteca da Meia Noite

A **Biblioteca da Meia Noite** é uma aplicação desenvolvida para facilitar o gerenciamento de livros e empréstimos de forma simples, rápida e organizada. A plataforma permite que os usuários consultem o acervo disponível e realizem operações relacionadas aos livros e empréstimos.

### Principais funcionalidades

* **Cadastrar livro:** permite adicionar novos livros ao acervo, informando dados como título, autor, gênero e quantidade disponível.
* **Listar livros:** exibe todos os livros cadastrados, permitindo consultar as informações e verificar sua disponibilidade.
* **Pedir livro emprestado:** possibilita ao usuário realizar o empréstimo de um livro disponível, registrando a data do empréstimo e o prazo para devolução.
* **Deletar livro:** permite remover livros do sistema quando eles não fazem mais parte do acervo.
* **Renovar empréstimo:** possibilita estender o prazo de devolução de um livro que está emprestado, conforme as regras da biblioteca.

O sistema tem como objetivo **automatizar o controle do acervo e dos empréstimos**, reduzindo processos manuais e proporcionando uma experiência mais prática tanto para os usuários quanto para os responsáveis pela administração da biblioteca.

## Executando com Docker

A aplicação é totalmente containerizada, composta por três serviços orquestrados via Docker Compose:

* **mysql**: banco de dados MySQL 8.0, com persistência de dados via volume nomeado
* **backend**: API Spring Boot, construída a partir de `backend/Dockerfile`
* **frontend**: aplicação Angular servida por Nginx, construída a partir de `frontend/Dockerfile`, com **proxy reverso** configurado para redirecionar chamadas `/api/*` para o backend (eliminando a necessidade de configuração de CORS entre os serviços)

### Pré-requisitos

* [Docker](https://www.docker.com/) e [Docker Compose](https://docs.docker.com/compose/) instalados

### Como executar

1. (Opcional) Copie o arquivo de exemplo de variáveis de ambiente e ajuste os valores conforme necessário:

   ```bash
   cp .env.example .env
   ```

2. Suba os containers (o build das imagens é feito automaticamente na primeira execução):

   ```bash
   docker compose up --build
   ```

3. Acesse a aplicação:

   * **Frontend:** http://localhost:4200
   * **Backend (API):** http://localhost:8080
   * **Swagger/OpenAPI:** http://localhost:8080/swagger-ui.html
   * **MySQL:** localhost:3307 (porta 3307 no host para evitar conflito com instâncias locais na porta padrão 3306)

4. Para parar os containers:

   ```bash
   docker compose down
   ```

   Para remover também o volume de dados do MySQL:

   ```bash
   docker compose down -v
   ```

### Variáveis de ambiente

As credenciais e o nome do banco de dados podem ser customizados através do arquivo `.env` (veja `.env.example`):

| Variável      | Descrição                          | Padrão                    |
|---------------|-------------------------------------|----------------------------|
| `DB_NAME`     | Nome do banco de dados MySQL        | `biblioteca_meia_noite`   |
| `DB_PASSWORD` | Senha do usuário `root` do MySQL    | `admin`                   |
