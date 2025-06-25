package at.etapa2;

import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import at.api.Main;
import at.models.Tarefa;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class TarefaApiTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // --- Teste para GET /hello ---
    @Test
    void testHelloEndpointRetorna200ETextoCorreto() {
        JavalinTest.test(Main.buildApp(), (servidor, cliente) -> {
            var resposta = cliente.get("/hello");
            Assertions.assertEquals(200, resposta.code(), "O status code deveria ser 200 OK.");
            Assertions.assertEquals("Hello, Javalin!", resposta.body().string(), "O corpo da resposta deveria ser 'Hello, Javalin!'.");
            System.out.println("Teste para GET /hello passou com sucesso!");
        });
    }

    // --- Teste para POST /tarefas (createTaskTest) ---
    @Test
    void createTaskTest_Retorna201ECorpoCorreto() {
        JavalinTest.test(Main.buildApp(), (servidor, cliente) -> {
            Tarefa novaTarefa = new Tarefa("Estudar Java", "Revisar Javalin e POO");
            String tarefaJson = objectMapper.writeValueAsString(novaTarefa);

            var resposta = cliente.post("/tarefas", tarefaJson, req -> req.header("Content-Type", "application/json"));
            Assertions.assertEquals(201, resposta.code(), "O status code deveria ser 201 Created.");

            String corpoResposta = resposta.body().string();
            Tarefa tarefaRetornada = objectMapper.readValue(corpoResposta, Tarefa.class);

            Assertions.assertNotNull(tarefaRetornada.getId(), "A tarefa retornada deve ter um ID.");
            Assertions.assertTrue(tarefaRetornada.getId() > 0, "O ID da tarefa deve ser maior que 0.");
            Assertions.assertEquals(novaTarefa.getTitulo(), tarefaRetornada.getTitulo(), "O título da tarefa retornada deve ser o mesmo do enviado.");
            Assertions.assertEquals(novaTarefa.getDescricao(), tarefaRetornada.getDescricao(), "A descrição da tarefa retornada deve ser a mesma da enviada.");
            Assertions.assertFalse(tarefaRetornada.isConcluida(), "A tarefa nova não deve estar concluída por padrão.");
            Assertions.assertNotNull(tarefaRetornada.getDataCriacao(), "A data de criação não deve ser nula.");
            try {
                LocalDateTime.parse(tarefaRetornada.getDataCriacao());
            } catch (DateTimeParseException e) {
                Assertions.fail("A data de criação não está no formato ISO-8601 válido: " + e.getMessage());
            }

            System.out.println("Teste createTaskTest (POST /tarefas) passou com sucesso! Tarefa ID: " + tarefaRetornada.getId());
        });
    }

    @Test
    void createTaskTest_Retorna400ParaTituloVazio() {
        JavalinTest.test(Main.buildApp(), (servidor, cliente) -> {
            Tarefa novaTarefa = new Tarefa("", "Descrição com título vazio");
            String tarefaJson = objectMapper.writeValueAsString(novaTarefa);

            var resposta = cliente.post("/tarefas", tarefaJson, req -> req.header("Content-Type", "application/json"));
            Assertions.assertEquals(400, resposta.code(), "O status code deveria ser 400 Bad Request para título vazio.");
            Assertions.assertTrue(resposta.body().string().contains("título da tarefa é obrigatório"), "A mensagem de erro deve indicar título obrigatório.");
            System.out.println("Teste createTaskTest para título vazio passou com sucesso!");
        });
    }

    @Test
    void createTaskTest_Retorna400ParaTituloNulo() {
        JavalinTest.test(Main.buildApp(), (servidor, cliente) -> {
            Tarefa novaTarefa = new Tarefa(null, "Descrição com título nulo");
            String tarefaJson = objectMapper.writeValueAsString(novaTarefa);

            var resposta = cliente.post("/tarefas", tarefaJson, req -> req.header("Content-Type", "application/json"));
            Assertions.assertEquals(400, resposta.code(), "O status code deveria ser 400 Bad Request para título nulo.");
            Assertions.assertTrue(resposta.body().string().contains("título da tarefa é obrigatório"), "A mensagem de erro deve indicar título obrigatório.");
            System.out.println("Teste createTaskTest para título nulo passou com sucesso!");
        });
    }


    // --- Teste para GET /tarefas/{id} (findTaskByIdTest) ---
    @Test
    void findTaskByIdTest_RecuperaCorretamente() {
        JavalinTest.test(Main.buildApp(), (servidor, cliente) -> {
            Main.stopJavalinApp(); // Garante que a lista está limpa
            Main.startJavalinApp();

            // 1. Cria uma tarefa para garantir que ela exista e ter um ID para buscar
            Tarefa tarefaParaCriar = new Tarefa("Pagar contas", "Contas de luz e internet");
            String tarefaJson = objectMapper.writeValueAsString(tarefaParaCriar);

            var postResponse = cliente.post("/tarefas", tarefaJson, req -> req.header("Content-Type", "application/json"));
            Assertions.assertEquals(201, postResponse.code(), "Setup: POST deveria retornar 201.");

            Tarefa createdTarefa = objectMapper.readValue(postResponse.body().string(), Tarefa.class);
            int createdTarefaId = createdTarefa.getId();

            // 2. Busca a tarefa recém-criada
            var getResponse = cliente.get("/tarefas/" + createdTarefaId);

            // 3. Valida a resposta
            Assertions.assertEquals(200, getResponse.code(), "GET deveria retornar 200 OK para uma tarefa existente.");
            Assertions.assertNotNull(getResponse.body(), "O corpo da resposta GET não deve ser nulo.");

            Tarefa retrievedTarefa = objectMapper.readValue(getResponse.body().string(), Tarefa.class);

            // 4. Valida se a tarefa recuperada é a mesma que foi criada
            Assertions.assertEquals(createdTarefa.getId(), retrievedTarefa.getId(), "O ID da tarefa recuperada deve ser o mesmo do criada.");
            Assertions.assertEquals(createdTarefa.getTitulo(), retrievedTarefa.getTitulo(), "O título da tarefa recuperada deve ser o mesmo do criado.");
            Assertions.assertEquals(createdTarefa.getDescricao(), retrievedTarefa.getDescricao(), "A descrição da tarefa recuperada deve ser a mesma da enviada.");
            Assertions.assertEquals(createdTarefa.isConcluida(), retrievedTarefa.isConcluida(), "O status de concluída deve ser o mesmo.");
            Assertions.assertEquals(createdTarefa.getDataCriacao(), retrievedTarefa.getDataCriacao(), "A data de criação deve ser a mesma.");

            System.out.println("Teste findTaskByIdTest (GET /tarefas/{id}) passou com sucesso para o ID: " + createdTarefaId);
        });
    }

    @Test
    void findTaskByIdTest_Retorna404ParaInexistente() {
        JavalinTest.test(Main.buildApp(), (servidor, cliente) -> {
            Main.stopJavalinApp(); // Garante que a lista está limpa
            Main.startJavalinApp();

            int idInexistente = 9999;
            var getResponse = cliente.get("/tarefas/" + idInexistente);
            Assertions.assertEquals(404, getResponse.code(), "GET para tarefa inexistente deveria retornar 404 Not Found.");
            String corpoResposta = getResponse.body().string();
            Assertions.assertTrue(corpoResposta.contains("não encontrada"), "A mensagem de erro deve indicar que a tarefa não foi encontrada.");
            System.out.println("Teste findTaskByIdTest (GET /tarefas/{id}) para ID inexistente passou com sucesso!");
        });
    }

    @Test
    void findTaskByIdTest_Retorna400ParaIdInvalido() {
        JavalinTest.test(Main.buildApp(), (servidor, cliente) -> {
            var getResponse = cliente.get("/tarefas/abc"); // ID inválido
            Assertions.assertEquals(400, getResponse.code(), "GET para ID inválido deveria retornar 400 Bad Request.");
            String corpoResposta = getResponse.body().string();
            Assertions.assertTrue(corpoResposta.contains("ID inválido"), "A mensagem de erro deve indicar ID inválido.");
            System.out.println("Teste findTaskByIdTest para ID inválido passou com sucesso!");
        });
    }

    // --- Teste para GET /tarefas (listTasksTest) ---
    @Test
    void listTasksTest_RetornaArrayNaoVazioAposCriacao() {
        JavalinTest.test(Main.buildApp(), (servidor, cliente) -> {
            Main.stopJavalinApp(); // Garante que a lista está limpa
            Main.startJavalinApp();

            // 1. Criar algumas tarefas
            Tarefa tarefa1 = new Tarefa("Lavar carro", "Lava rápido ou em casa");
            Tarefa tarefa2 = new Tarefa("Pagar faturas", "Contas de luz e água");

            String tarefaJson1 = objectMapper.writeValueAsString(tarefa1);
            cliente.post("/tarefas", tarefaJson1, req -> req.header("Content-Type", "application/json"));

            String tarefaJson2 = objectMapper.writeValueAsString(tarefa2);
            cliente.post("/tarefas", tarefaJson2, req -> req.header("Content-Type", "application/json"));

            // 2. Chamar o endpoint GET /tarefas
            var getResponse = cliente.get("/tarefas");

            // 3. Validar a resposta
            Assertions.assertEquals(200, getResponse.code(), "GET /tarefas deveria retornar 200 OK.");
            Assertions.assertNotNull(getResponse.body(), "O corpo da resposta GET /tarefas não deve ser nulo.");

            List<Tarefa> tarefasListadas = objectMapper.readValue(getResponse.body().string(), new TypeReference<List<Tarefa>>() {});

            Assertions.assertFalse(tarefasListadas.isEmpty(), "A lista de tarefas retornada não deve estar vazia.");
            Assertions.assertEquals(2, tarefasListadas.size(), "A lista de tarefas deveria conter 2 itens.");

            // Opcional: Validar conteúdo das tarefas na lista
            Assertions.assertTrue(tarefasListadas.stream().anyMatch(t -> t.getTitulo().equals("Lavar carro")), "Deveria conter 'Lavar carro'.");
            Assertions.assertTrue(tarefasListadas.stream().anyMatch(t -> t.getTitulo().equals("Pagar faturas")), "Deveria conter 'Pagar faturas'.");

            System.out.println("Teste listTasksTest (GET /tarefas) passou com sucesso! Total: " + tarefasListadas.size());
        });
    }

    @Test
    void listTasksTest_RetornaArrayVazioQuandoNenhumaTarefaExiste() {
        JavalinTest.test(Main.buildApp(), (servidor, cliente) -> {
            Main.stopJavalinApp(); // Garante que a lista está limpa
            Main.startJavalinApp();

            var getResponse = cliente.get("/tarefas");

            Assertions.assertEquals(200, getResponse.code(), "GET /tarefas deveria retornar 200 OK mesmo com lista vazia.");
            List<Tarefa> tarefasListadas = objectMapper.readValue(getResponse.body().string(), new TypeReference<List<Tarefa>>() {});
            Assertions.assertTrue(tarefasListadas.isEmpty(), "A lista de tarefas deveria estar vazia se nenhuma tarefa foi criada.");
            System.out.println("Teste listTasksTest (GET /tarefas) para lista vazia passou com sucesso!");
        });
    }
}