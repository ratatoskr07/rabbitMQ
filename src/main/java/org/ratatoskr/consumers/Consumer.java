package org.ratatoskr.consumers;

import com.rabbitmq.client.ConnectionFactory;
import io.github.cdimascio.dotenv.Dotenv;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


public abstract class Consumer {

    DateTimeFormatter TIMESTAMP_FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy|HH:mm:ss");
    private ConnectionFactory factory;
    private String queueName;
    private String exchangeName;
    private String tag;

    public Consumer(String queueName, String exchangeName, String tag) {
        setFactory();
        setQueueName(queueName);
        setExchangeName(exchangeName);
        setTag(tag);
        configFactory();
    }

    public abstract void startConsumer();

    public void configFactory() {
        Dotenv dotenv = Dotenv.load();
        String host = dotenv.get("RABBITMQ_HOST");
        String username = dotenv.get("RABBITMQ_USERNAME");
        String password = dotenv.get("RABBITMQ_PASSWORD");
        String vhost = dotenv.get("RABBITMQ_VHOST");
        this.factory.setHost(host);
        this.factory.setUsername(username);
        this.factory.setPassword(password);
        this.factory.setVirtualHost(vhost);
    }

    public void setFactory() {
        this.factory = new ConnectionFactory();
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public void setTag(String tag) {
        this.tag = tag + " ";
    }

    public ConnectionFactory getFactory() {
        return factory;
    }

    public String getQueueName() {
        return queueName;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public String getTag() {
        return tag;
    }

    public String getTimeStamp() {
        return "[" + LocalDateTime.now(ZoneId.of("America/Sao_Paulo")).format(TIMESTAMP_FMT) + "]";
    }

    public String getLogMessage(String message) {
        return getTimeStamp() + getTag() + message;
    }

    public String getErrorMessage(String message) {
        return getTimeStamp() + "[ERROR] " + message;
    }
}
