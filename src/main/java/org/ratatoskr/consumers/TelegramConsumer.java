package org.ratatoskr.consumers;

import com.rabbitmq.client.*;
import org.ratatoskr.telegram.TelegramSender;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class TelegramConsumer extends Consumer {

    private final TelegramSender telegramSender;

    public TelegramConsumer(String queueName,
                            String exchangeName,
                            String tag,
                            String telegramToken,
                            String telegramChatID) {
        super(queueName, exchangeName, tag);
        telegramSender = new TelegramSender(telegramToken, telegramChatID);
    }

    @Override
    public void startConsumer() {
        try (Connection connection = getFactory().newConnection();
            Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(getExchangeName(), BuiltinExchangeType.DIRECT, true);
            channel.queueDeclare(getQueueName(), true, false, false, null);
            channel.queueBind(getQueueName(), getExchangeName(), getQueueName());

            DeliverCallback callback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(getLogMessage(" Mensagem recebida: " + consumerTag));
                System.out.println(getLogMessage(" Processando..."));
                try {
                    System.out.println(getLogMessage(" Enviando mensagem via telegram..."));
                    telegramSender.sendMessage(message);
                    System.out.println(getLogMessage("Mensagem enviada!"));
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    System.out.println(getLogMessage(" ACK retornado ao RabbitMQ"));
                } catch (InterruptedException ex) {
                    System.out.println(getErrorMessage(" Mensagem não enviada!"));
                    System.out.println(getErrorMessage(ex.getMessage()));
                    channel.basicNack(delivery.getEnvelope().getDeliveryTag(), true, false);
                }
            };
            channel.basicConsume(getQueueName(), false, callback, consumerTag -> {});
            System.out.println(getLogMessage(" Aguardando mensagens..."));
            Thread.currentThread().join(); // mantém o consumer vivo
        } catch (IOException | TimeoutException | InterruptedException ex ) {
            System.out.println(getErrorMessage(ex.getMessage()));
            throw new RuntimeException(ex);
        }
    }
}
