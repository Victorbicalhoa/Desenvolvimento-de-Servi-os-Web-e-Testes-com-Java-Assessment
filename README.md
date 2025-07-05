# ReAT: API REST de Tarefas com Javalin e Testes em Java

Este projeto implementa uma **API RESTful** simples e eficaz para gerenciamento de uma **lista de tarefas (To-Do)**. Desenvolvida em **Java** utilizando o leve framework **Javalin**, esta solução demonstra as melhores práticas de construção de APIs, incluindo **testes unitários robustos com JUnit 5** e **clientes Java nativos (`HttpURLConnection`)** para consumo dos endpoints.

---

## Destaques do Projeto

* **API RESTful:** Implementação de endpoints para operações CRUD de tarefas.
* **Javalin Framework:** Uso de um framework moderno e leve para Java web.
* **Testes Unitários:** Cobertura de testes com JUnit 5 para garantir a confiabilidade dos endpoints.
* **Clientes Java Nativos:** Exemplos práticos de como consumir a API usando `HttpURLConnection`.
* **Caso de Uso "To-Do":** Uma aplicação familiar e compreensível para demonstrar os conceitos.
* **Estrutura Clara:** Organização de código que facilita a compreensão e futuras expansões.

---

## Requisitos

Para construir e executar este projeto, você precisará do seguinte:

* **JDK 21**
* **Gradle** (já configurado no projeto e incluído como wrapper)
* **Postman** ou `curl` (opcional, para testes manuais da API)

---
## Como Construir e Rodar a API

Siga estes passos para ter a API funcionando em seu ambiente local:

1.  **Clone o repositório:**
    ```bash
    git clone [https://github.com/Victorbicalhoa/ReAT.git](https://github.com/Victorbicalhoa/ReAT.git)
    cd ReAT
    ```
2.  **Construa o projeto:**
    ```bash
    ./gradlew build
    ```
3.  **Inicie a API:**
    ```bash
    ./gradlew run
    ```
    A API será iniciada na porta `7000`. Mantenha este terminal aberto enquanto estiver interagindo com a API ou executando os clientes.

---

## Endpoints da API (Caso de Uso "To-Do")

A API expõe os seguintes endpoints, simulando operações de uma lista de tarefas:

* **`GET /hello`**
    * **Retorna:** Uma simples mensagem de boas-vindas do Javalin.
    * **Exemplo (`curl`):** `curl http://localhost:7000/hello`
    * **Resposta:** `Hello, Javalin!`

* **`GET /status`**
    * **Retorna:** O status atual da API (`ok`) e um timestamp.
    * **Exemplo (`curl`):** `curl http://localhost:7000/status`
    * **Resposta:** `{"status":"ok","timestamp":"2025-06-25T15:00:00.000Z"}`

* **`POST /echo`**
    * **Recebe:** Qualquer JSON no corpo da requisição.
    * **Retorna:** O mesmo JSON recebido (útil para testar o envio de dados).
    * **Exemplo (`curl`):** `curl -X POST -H "Content-Type: application/json" -d "{\"conteudo\": \"Teste de eco\"}" http://localhost:7000/echo`
    * **Resposta:** `{"conteudo": "Teste de eco"}`

* **`GET /saudacao/{nome}`**
    * **Recebe:** Um `nome` como parâmetro de caminho.
    * **Retorna:** Uma mensagem de saudação personalizada.
    * **Exemplo (`curl`):** `curl http://localhost:7000/saudacao/Victor`
    * **Resposta:** `{"mensagem":"Olá, Victor!"}`

* **`POST /tarefas`**
    * **Descrição:** Cria uma nova tarefa.
    * **Recebe:** JSON com `titulo` (String) e `descricao` (String). `concluida` é opcional e padrão `false`.
    * **Retorna:** A tarefa criada com um `id` gerado e status `201 Created`.
    * **Exemplo de Requisição:**
        ```json
        {
          "titulo": "Fazer compras",
          "descricao": "Leite, pão e ovos"
        }
        ```
    * **Exemplo (`curl`):** `curl -X POST -H "Content-Type: application/json" -d "{\"titulo\": \"Limpar quarto\", \"descricao\": \"Organizar armário e varrer\"}" http://localhost:7000/tarefas`
    * **Exemplo de Resposta:**
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
    * **Exemplo (`curl`):** `curl http://localhost:7000/tarefas`
    * **Exemplo de Resposta:**
        ```json
        [
          {"id":1,"titulo":"Limpar quarto","descricao":"Organizar armário e varrer","concluida":false},
          {"id":2,"titulo":"Pagar contas","descricao":"Contas de luz e água","concluida":false}
        ]
        ```

* **`GET /tarefas/{id}`**
    * **Descrição:** Busca uma tarefa específica pelo seu ID.
    * **Recebe:** Um `id` (inteiro) como parâmetro de caminho.
    * **Retorna:** A tarefa correspondente (`200 OK`) ou um erro `404 Not Found` se o ID não for encontrado.
    * **Exemplo (`curl`):** `curl http://localhost:7000/tarefas/1`
    * **Exemplo de Resposta:**
        ```json
        {"id":1,"titulo":"Limpar quarto","descricao":"Organizar armário e varrer","concluida":false}
        ```

---

## Como Rodar os Testes Unitários

Para garantir a qualidade e o comportamento esperado da API, execute os testes unitários:

1.  **Certifique-se de que a API NÃO esteja rodando** (os testes iniciam e param a API automaticamente para garantir isolamento e um ambiente limpo para cada execução).
2.  No terminal, na raiz do projeto (`ReAT`), execute:
    ```bash
    ./gradlew test
    ```
3.  A saída no terminal mostrará os resultados dos testes. Um relatório HTML detalhado também será gerado em `build/reports/tests/test/index.html`.

---

## Como Rodar os Clientes Java (Consumo da API)

Os clientes Java demonstram como consumir os endpoints da API programaticamente usando `HttpURLConnection`. **Certifique-se de que a API esteja rodando** em um terminal separado (`./gradlew run`) antes de executar os clientes.

Os clientes estão localizados em `src/main/java/at/client/etapa3/`. Use o seguinte comando para executá-los:

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

---

## Screenshots

Você pode encontrar screenshots da execução da API, dos resultados dos testes e da saída dos clientes na pasta `docs/img/`.

---

**Desenvolvido por:** Hebert Victor Bicalho de Almeida
