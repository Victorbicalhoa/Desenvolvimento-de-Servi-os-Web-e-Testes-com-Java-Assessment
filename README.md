# Projeto de API REST com Javalin e Testes Unitários

Este projeto implementa uma API REST simples utilizando o framework Javalin em Java, seguindo os requisitos de um caso de uso "To-Do" (Lista de Tarefas). Inclui testes unitários com JUnit 5 e clientes Java para consumir os endpoints da API usando `HttpURLConnection`.

## Requisitos

* JDK 21
* Gradle (já configurado no projeto)
* Postman ou `curl` (para testar a API manualmente)

## Estrutura do Projeto

ReAT/
├── docs/
│   └── img/                      # Screenshots da execução e testes
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── at/
│   │   │   │   ├── api/          # Contém a classe principal da API (Main.java)
│   │   │   │   └── models/       # Modelos de dados (StatusResponse.java, Tarefa.java)
│   │   │   │   └── client/       # Clientes Java para consumir a API
│   │   │   │       └── etapa3/   # Clientes específicos para cada exercício da Etapa 3
│   └── test/
│       └── java/
│           └── at/
│               └── etapa2/
│                   └── TarefasApiTest/ # Testes unitários para os endpoints de Tarefas da Etapa 2
├── .gitignore                  # Arquivo para ignorar arquivos gerados e de IDE
├── build.gradle                # Configuração do projeto Gradle e dependências
└── README.md                   # Este arquivo


## Como Construir e Rodar a API

1.  **Clone ou baixe o projeto.**
2.  **Navegue até o diretório raiz do projeto** (`ReAT`) no terminal.
3.  **Para compilar e construir o projeto:**
    ```bash
    ./gradlew build
    ```
4.  **Para iniciar a API:**
    ```bash
    ./gradlew run
    ```
    A API será iniciada na porta `7000`. Mantenha este terminal aberto enquanto estiver usando a API ou os clientes.

## Endpoints da API (Caso de Uso "To-Do")

A API expõe os seguintes endpoints:

* **`GET /hello`**
    * **Retorna:** `Hello, Javalin!`
    * **Exemplo de uso (`curl`):**
        ```bash
        curl http://localhost:7000/hello
        ```
    * **Exemplo de resposta:**
        ```
        Hello, Javalin!
        ```

* **`GET /status`**
    * **Retorna:** O status da API e o timestamp atual.
    * **Exemplo de uso (`curl`):**
        ```bash
        curl http://localhost:7000/status
        ```
    * **Exemplo de resposta:**
        ```json
        {"status":"ok","timestamp":"2025-06-25T15:00:00.000Z"}
        ```

* **`POST /echo`**
    * **Recebe:** Qualquer JSON.
    * **Retorna:** O mesmo JSON recebido.
    * **Exemplo de uso (`curl`):**
        ```bash
        curl -X POST -H "Content-Type: application/json" -d "{\"conteudo\": \"Teste de eco\"}" http://localhost:7000/echo
        ```
    * **Exemplo de resposta:**
        ```json
        {"conteudo": "Teste de eco"}
        ```

* **`GET /saudacao/{nome}`**
    * **Recebe:** Um `nome` como path parameter.
    * **Retorna:** Uma mensagem de saudação.
    * **Exemplo de uso (`curl`):**
        ```bash
        curl http://localhost:7000/saudacao/Victor
        ```
    * **Exemplo de resposta:**
        ```json
        {"mensagem":"Olá, Victor!"}
        ```

* **`POST /tarefas`**
    * **Descrição:** Cria uma nova tarefa.
    * **Recebe:** JSON com `titulo` (String) e `descricao` (String). `concluida` é opcional e padrão `false`.
    * **Retorna:** A tarefa criada com ID gerado e status `201 Created`.
    * **Exemplo de JSON de requisição:**
        ```json
        {
          "titulo": "Fazer compras",
          "descricao": "Leite, pão e ovos"
        }
        ```
    * **Exemplo de uso (`curl`):**
        ```bash
        curl -X POST -H "Content-Type: application/json" -d "{\"titulo\": \"Limpar quarto\", \"descricao\": \"Organizar armário e varrer\"}" http://localhost:7000/tarefas
        ```
    * **Exemplo de resposta (com ID gerado):**
        ```json
        {
          "id": 1,
          "titulo": "Limpar quarto",
          "descricao": "Organizar armário e varrer",
          "concluida": false
        }
        ```

* **`GET /tarefas`**
    * **Descrição:** Lista todas as tarefas cadastradas.
    * **Retorna:** Um array JSON de tarefas.
    * **Exemplo de uso (`curl`):**
        ```bash
        curl http://localhost:7000/tarefas
        ```
    * **Exemplo de resposta:**
        ```json
        [
          {"id":1,"titulo":"Limpar quarto","descricao":"Organizar armário e varrer","concluida":false},
          {"id":2,"titulo":"Pagar contas","descricao":"Contas de luz e água","concluida":false}
        ]
        ```

* **`GET /tarefas/{id}`**
    * **Descrição:** Busca uma tarefa pelo seu ID.
    * **Recebe:** Um `id` (inteiro) como path parameter.
    * **Retorna:** A tarefa correspondente e status `200 OK`, ou um erro `404 Not Found` se não encontrada.
    * **Exemplo de uso (`curl`):**
        ```bash
        curl http://localhost:7000/tarefas/1
        ```
    * **Exemplo de resposta:**
        ```json
        {"id":1,"titulo":"Limpar quarto","descricao":"Organizar armário e varrer","concluida":false}
        ```

## Como Rodar os Testes Unitários

1.  **Garanta que a API NÃO esteja rodando** (os testes iniciam e param a API automaticamente para isolamento).
2.  No terminal, na raiz do projeto (`ReAT`), execute:
    ```bash
    ./gradlew test
    ```
3.  A saída no terminal indicará o resultado de cada teste. Um relatório HTML detalhado também será gerado em `build/reports/tests/test/index.html`.

## Como Rodar os Clientes Java (Consumo da API)

Certifique-se de que a **API está rodando** em um terminal (`./gradlew run`) antes de executar os clientes.

Os clientes estão localizados em `src/main/java/at/client/etapa3/`.

* **Cliente para Criar Tarefa (POST):**
    ```bash
    ./gradlew run --args="at.client.etapa3.PostTarefaClient"
    ```
* **Cliente para Listar Todas as Tarefas (GET):**
    ```bash
    ./gradlew run --args="at.client.etapa3.GetTarefasClient"
    ```
* **Cliente para Buscar Tarefa por ID (GET):**
    ```bash
    ./gradlew run --args="at.client.etapa3.GetTarefaByIdClient"
    ```
* **Cliente para Obter Status da API (GET):**
    ```bash
    ./gradlew run --args="at.client.etapa3.GetStatusClient"
    ```

## Screenshots

Screenshots da execução da API, dos testes e dos clientes podem ser encontradas na pasta `docs/img/`.

---
**Desenvolvido por:** Hebert Victor Bicalho de Almeida

---

