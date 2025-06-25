package at.client.etapa3;

import at.models.Tarefa;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GetTarefaByIdClient {

    private static final String BASE_URL = "http://localhost:7000";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        System.out.println("--- Cliente Java: GET para /tarefas/{id} (Buscar Tarefa por ID) ---");

        int tarefaIdToSearch = -1;
        System.out.println("\nCriando uma tarefa para garantir um ID válido para a busca...");
        try {
            Tarefa tempTarefa = new Tarefa("Ligar para dentista", "Agendar consulta de rotina");
            Tarefa createdTempTarefa = PostTarefaClient.createTarefa(tempTarefa);
            if (createdTempTarefa != null) {
                tarefaIdToSearch = createdTempTarefa.getId();
                System.out.println("  Tarefa temporária criada com sucesso! ID para busca: " + tarefaIdToSearch);
            } else {
                System.out.println("  Falha ao criar tarefa temporária. Não será possível testar a busca por ID com sucesso.");
            }
        } catch (Exception e) {
            System.err.println("  Erro ao criar tarefa temporária: " + e.getMessage());
            e.printStackTrace();
        }

        // Buscar uma tarefa existente pelo ID
        if (tarefaIdToSearch != -1) {
            System.out.println("\nEnviando GET para /tarefas/" + tarefaIdToSearch + " para buscar uma tarefa existente...");
            try {
                Tarefa foundTarefa = getTarefaById(tarefaIdToSearch);
                if (foundTarefa != null) {
                    System.out.println("Tarefa encontrada com sucesso!");
                    System.out.println("  ID: " + foundTarefa.getId());
                    System.out.println("  Título: " + foundTarefa.getTitulo());
                    System.out.println("  Descrição: " + foundTarefa.getDescricao());
                    System.out.println("  Concluída: " + foundTarefa.isConcluida());
                    System.out.println("  Data de Criação: " + foundTarefa.getDataCriacao());
                } else {
                    System.out.println("Tarefa não encontrada (ou erro na requisição).");
                }
            } catch (Exception e) {
                System.err.println("Erro ao executar a requisição GET por ID: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Buscar uma tarefa inexistente pelo ID
        int nonExistentId = 9999;
        System.out.println("\nEnviando GET para /tarefas/" + nonExistentId + " para buscar uma tarefa inexistente...");
        try {
            Tarefa notFoundTarefa = getTarefaById(nonExistentId);
            if (notFoundTarefa == null) {
                System.out.println("Comportamento esperado: Tarefa com ID " + nonExistentId + " não foi encontrada.");
            } else {
                System.out.println("Erro: Tarefa com ID " + nonExistentId + " foi encontrada inesperadamente.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao executar a requisição GET por ID para tarefa inexistente: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n--- Cliente GetTarefaByIdClient finalizado ---");
    }

    public static Tarefa getTarefaById(int id) throws Exception {
        URL url = new URL(BASE_URL + "/tarefas/" + id);
        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(false);

            int responseCode = conn.getResponseCode();
            System.out.println("  Código de Resposta para GET /tarefas/" + id + ": " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    return objectMapper.readValue(response.toString(), Tarefa.class);
                }
            } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                System.out.println("  Tarefa com ID " + id + " não encontrada na API.");
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String errorLine;
                    while ((errorLine = br.readLine()) != null) {
                        errorResponse.append(errorLine.trim());
                    }
                    if (!errorResponse.isEmpty()) {
                        System.err.println("  Mensagem de erro da API: " + errorResponse.toString());
                    }
                }
                return null;
            } else {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String errorLine;
                    while ((errorLine = br.readLine()) != null) {
                        errorResponse.append(errorLine.trim());
                    }
                    System.err.println("  Erro inesperado do servidor: " + errorResponse.toString());
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