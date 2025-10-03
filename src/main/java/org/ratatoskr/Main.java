package org.ratatoskr;

import io.github.cdimascio.dotenv.Dotenv;
import org.ratatoskr.consumers.TelegramConsumer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        String telegramChatID = dotenv.get("TELEGRAM_CHAT_ID");
        String infoTelegramToken = dotenv.get("INFO_TELEGRAM_TOKEN");
        String alertaTelegramToken = dotenv.get("ALERTA_TELEGRAM_TOKEN");
        String criticoTelegramToken = dotenv.get("CRITICO_TELEGRAM_TOKEN");
        TelegramConsumer infoTelegramConsumer = new TelegramConsumer("info",
                                                            "messenger.direct",
                                                                    "[INFO]",
                                                                        infoTelegramToken,
                                                                        telegramChatID);
        TelegramConsumer alertaTelegramConsumer = new TelegramConsumer("alerta",
                "messenger.direct",
                "[ALERTA]",
                alertaTelegramToken,
                telegramChatID);
        TelegramConsumer criticoTelegramConsumer = new TelegramConsumer("critico",
                "messenger.direct",
                "[CRITICO]",
                criticoTelegramToken,
                telegramChatID);

        ExecutorService executor = Executors.newFixedThreadPool(3);
        executor.submit(infoTelegramConsumer::startConsumer);
        executor.submit(alertaTelegramConsumer::startConsumer);
        executor.submit(criticoTelegramConsumer::startConsumer);
    }
}
