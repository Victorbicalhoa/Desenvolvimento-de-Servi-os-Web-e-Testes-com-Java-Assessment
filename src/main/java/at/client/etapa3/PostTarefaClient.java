package at.client.etapa3;

import at.models.Tarefa;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class PostTarefaClient {

    private static final String BASE_URL = "http://localhost:7000";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        System.out.println("--- Cliente Java: POST para /tarefas (Criar Tarefa) ---");

        System.out.println("\nEnviando POST para /tarefas para criar uma nova tarefa...");
        Tarefa novaTarefa = new Tarefa("Preparar apresentação", "Criar slides e revisar conteúdo para a reunião de amanhã");

        try {
            Tarefa tarefaCriada = createTarefa(novaTarefa);
            if (tarefaCriada != null) {
                System.out.println("Tarefa criada com sucesso!");
                System.out.println("  ID: " + tarefaCriada.getId());
                System.out.println("  Título: " + tarefaCriada.getTitulo());
                System.out.println("  Descrição: " + tarefaCriada.getDescricao());
                System.out.println("  Concluída: " + tarefaCriada.isConcluida());
                System.out.println("  Data de Criação: " + tarefaCriada.getDataCriacao());
            } else {
                System.out.println("Falha ao criar a tarefa.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao executar a requisição POST: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n--- Cliente PostTarefaClient finalizado ---");
    }

    public static Tarefa createTarefa(Tarefa tarefa) throws Exception {
        URL url = new URL(BASE_URL + "/tarefas");
        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = objectMapper.writeValueAsString(tarefa);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            System.out.println("  Código de Resposta para POST /tarefas: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    return objectMapper.readValue(response.toString(), Tarefa.class);
                }
            } else {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String errorLine;
                    while ((errorLine = br.readLine()) != null) {
                        errorResponse.append(errorLine.trim());
                    }
                    System.err.println("  Erro do servidor: " + errorResponse.toString());
                }
                return null;
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}