package org.ratatoskr.telegram;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TelegramSender {

    private final String apiUrl;
    private final String chatId;
    private final HttpClient client;

    public TelegramSender(String token, String chatId) {
        this.apiUrl = "https://api.telegram.org/bot" + token + "/sendMessage";
        this.chatId = chatId;
        this.client = HttpClient.newHttpClient();
    }

    public void sendMessage(String message) throws IOException, InterruptedException {
        /*
         * Envia uma mensagem com POST em JSON.
         *
         * @param message   Mensagem de texto
         * @throws IOException
         * @throws InterruptedException
         */

        String parseMode = "HTML";
        String json = String.format(
                "{\"chat_id\":\"%s\", \"text\":\"%s\"%s}",
                chatId,
                message.replace("\"", "\\\""),
                ", \"parse_mode\":\"" + parseMode + "\""
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Erro ao enviar mensagem: " + response.body());
        }
    }
}

