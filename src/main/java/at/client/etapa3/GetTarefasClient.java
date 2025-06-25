package at.client.etapa3;

import at.models.Tarefa; // Importa a classe Tarefa
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class GetTarefasClient {

    private static final String BASE_URL = "http://localhost:7000";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        System.out.println("--- Cliente Java: GET para /tarefas (Listar Todas as Tarefas) ---");

        System.out.println("\nCriando uma tarefa para garantir que a lista não esteja vazia para o GET...");
        try {
            Tarefa tempTarefa = new Tarefa("Regar plantas", "Plantas da sala e do escritório");
            Tarefa createdTempTarefa = PostTarefaClient.createTarefa(tempTarefa);
            if (createdTempTarefa != null) {
                System.out.println("  Tarefa temporária criada com sucesso! ID: " + createdTempTarefa.getId());
            } else {
                System.out.println("  Falha ao criar tarefa temporária.");
            }
        } catch (Exception e) {
            System.err.println("  Erro ao criar tarefa temporária: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\nEnviando GET para /tarefas para listar todas as tarefas...");
        try {
            List<Tarefa> tarefasListadas = listTarefas();
            if (tarefasListadas != null && !tarefasListadas.isEmpty()) {
                System.out.println("Tarefas listadas com sucesso (" + tarefasListadas.size() + " tarefas):");
                for (Tarefa tarefa : tarefasListadas) {
                    System.out.println("  ID: " + tarefa.getId() + ", Título: " + tarefa.getTitulo() + ", Descrição: " + tarefa.getDescricao() + ", Concluída: " + tarefa.isConcluida() + ", Data de Criação: " + tarefa.getDataCriacao());
                }
            } else if (tarefasListadas != null && tarefasListadas.isEmpty()) {
                System.out.println("Nenhuma tarefa encontrada na lista.");
            } else {
                System.out.println("Falha ao listar as tarefas.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao executar a requisição GET: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n--- Cliente GetTarefasClient finalizado ---");
    }

    public static List<Tarefa> listTarefas() throws Exception {
        URL url = new URL(BASE_URL + "/tarefas");
        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(false);

            int responseCode = conn.getResponseCode();
            System.out.println("  Código de Resposta para GET /tarefas: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    return objectMapper.readValue(response.toString(), new TypeReference<List<Tarefa>>() {});
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
                return Collections.emptyList();
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}