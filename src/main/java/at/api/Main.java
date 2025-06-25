package at.api;

import io.javalin.Javalin;
import at.models.StatusResponse;
import at.models.Tarefa;

import io.javalin.http.HttpStatus;

import java.time.LocalDateTime; // Para gerar a dataCriacao
import java.time.format.DateTimeFormatter; // Para formatar a data ISO-8601
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static Javalin app;

    // LISTA EM MEMÓRIA PARA ARMAZENAR AS TAREFAS
    private static final List<Tarefa> tarefas = Collections.synchronizedList(new ArrayList<>());
    // CONTADOR PARA GERAR IDS DE TAREFAS
    private static final AtomicInteger taskIdCounter = new AtomicInteger(1);


    public static Javalin buildApp() {
        Javalin appInstance = Javalin.create(config -> {
            config.http.defaultContentType = "application/json";
        });

        // --- DEFINIÇÃO DE TODOS OS ENDPOINTS ---

        // Endpoint: GET /hello (Exercício 1.1)
        appInstance.get("/hello", ctx -> {
            ctx.result("Hello, Javalin!");
        });

        // Endpoint: GET /status (Exercício 1.2)
        appInstance.get("/status", ctx -> {
            StatusResponse response = new StatusResponse();
            ctx.json(response);
        });

        // Endpoint: POST /echo (Exercício 1.3)
        appInstance.post("/echo", ctx -> {
            String requestBody = ctx.body();
            ctx.contentType("application/json");
            ctx.result(requestBody);
            System.out.println("POST /echo recebido: " + requestBody);
        });

        // Endpoint: GET /saudacao/{nome} (Exercício 1.4)
        appInstance.get("/saudacao/{nome}", ctx -> {
            String nome = ctx.pathParam("nome");
            ctx.json(Collections.singletonMap("mensagem", "Olá, " + nome + "!"));
            System.out.println("GET /saudacao/" + nome + " solicitado.");
        });

        // Endpoint: POST /tarefas - Criação de Tarefa
        appInstance.post("/tarefas", ctx -> {
            Tarefa novaTarefa = ctx.bodyAsClass(Tarefa.class);

            // Validações básicas
            if (novaTarefa.getTitulo() == null || novaTarefa.getTitulo().trim().isEmpty()) {
                ctx.status(HttpStatus.BAD_REQUEST).json(Collections.singletonMap("erro", "O título da tarefa é obrigatório."));
                return;
            }

            novaTarefa.setId(taskIdCounter.getAndIncrement()); // Atribui ID
            // Se a dataCriacao não veio do JSON, gera uma nova.
            if (novaTarefa.getDataCriacao() == null || novaTarefa.getDataCriacao().isEmpty()) {
                novaTarefa.setDataCriacao(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }

            // Garante que 'concluida' seja false por padrão se não for fornecido no JSON de entrada.

            tarefas.add(novaTarefa);
            ctx.status(HttpStatus.CREATED).json(novaTarefa);
            System.out.println("POST /tarefas - Tarefa criada: " + novaTarefa.getTitulo() + " (ID: " + novaTarefa.getId() + ")");
        });

        // Endpoint: GET /tarefas - Listar Todas as Tarefas
        appInstance.get("/tarefas", ctx -> {
            ctx.json(tarefas);
            System.out.println("GET /tarefas - Retornando todas as tarefas. Total: " + tarefas.size());
        });

        // Endpoint: GET /tarefas/{id} - Buscar Tarefa por ID
        appInstance.get("/tarefas/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                Optional<Tarefa> tarefaEncontrada = tarefas.stream()
                        .filter(t -> t.getId() == id)
                        .findFirst();

                if (tarefaEncontrada.isPresent()) {
                    ctx.json(tarefaEncontrada.get());
                    System.out.println("GET /tarefas/" + id + " - Tarefa encontrada: " + tarefaEncontrada.get().getTitulo());
                } else {
                    ctx.status(HttpStatus.NOT_FOUND).json(Collections.singletonMap("erro", "Tarefa com ID " + id + " não encontrada."));
                    System.out.println("GET /tarefas/" + id + " - Tarefa não encontrada.");
                }
            } catch (NumberFormatException e) {
                ctx.status(HttpStatus.BAD_REQUEST).json(Collections.singletonMap("erro", "ID inválido. O ID deve ser um número inteiro."));
                System.err.println("GET /tarefas/{id} - Erro: ID inválido, não é um número. Valor recebido: " + ctx.pathParam("id"));
            }
        });

        // Tratamento de erros genéricos para a aplicação
        appInstance.exception(Exception.class, (e, ctx) -> {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(Collections.singletonMap("erro", "Ocorreu um erro interno na API: " + e.getMessage()));
            System.err.println("Erro interno da API: " + e.getMessage());
            e.printStackTrace();
        });

        return appInstance;
    }

    public static void main(String[] args) {
        startJavalinApp();
    }

    public static Javalin startJavalinApp() {
        if (app == null) {
            app = buildApp().start(7000);

            System.out.println("API (ReAT) consolidada iniciada na porta 7000.");
            System.out.println("Endpoints disponíveis:");
            System.out.println("- http://localhost:7000/hello");
            System.out.println("- http://localhost:7000/status");
            System.out.println("- http://localhost:7000/echo (POST)");
            System.out.println("- http://localhost:7000/saudacao/{nome} (GET)");
            System.out.println("- http://localhost:7000/tarefas (POST)");
            System.out.println("- http://localhost:7000/tarefas (GET)");
            System.out.println("- http://localhost:7000/tarefas/{id} (GET)");
        }
        return app;
    }

    public static void stopJavalinApp() {
        if (app != null) {
            app.stop();
            app = null;
            tarefas.clear(); // Limpa as tarefas ao parar a aplicação
            taskIdCounter.set(1); // Reseta o contador de ID
            System.out.println("API (ReAT) consolidada parada.");
        }
    }
}