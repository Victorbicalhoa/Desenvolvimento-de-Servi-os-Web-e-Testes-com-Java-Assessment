package at.client.etapa3;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GetStatusClient {

    private static final String BASE_URL = "http://localhost:7000";

    public static void main(String[] args) {
        System.out.println("--- Cliente Java: Envio de GET para /status (Exercício 3.4) ---");

        System.out.println("\nEnviando GET para /status para obter o status da API...");
        try {
            String statusJson = getStatus();
            if (statusJson != null) {
                System.out.println("Status da API (JSON):");
                System.out.println(statusJson);
            } else {
                System.out.println("Falha ao obter o status da API.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao executar a requisição GET /status: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n--- Cliente GetStatusClient finalizado ---");
    }

    public static String getStatus() throws Exception {
        URL url = new URL(BASE_URL + "/status");
        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(false);

            // Lê o código de resposta HTTP
            int responseCode = conn.getResponseCode();
            System.out.println("  Código de Resposta para GET /status: " + responseCode);

            // Se a requisição foi bem-sucedida (200 OK)
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    return response.toString(); // Retorna a string JSON diretamente
                }
            } else {
                // Se a requisição não foi bem-sucedida, imprime a mensagem de erro do servidor
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
            // Garante que a conexão seja fechada
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}