package com.example.telegram_bot_call;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class BotInitializer {

    private static final Logger logger = LoggerFactory.getLogger(BotInitializer.class);

    private final MyTelegramBot myTelegramBot;

    @Autowired
    public BotInitializer(MyTelegramBot myTelegramBot) {
        this.myTelegramBot = myTelegramBot;
    }

    @PostConstruct
    public void init() {
        try {
            // Проверка наличия токена
            if (myTelegramBot.getBotToken() == null || myTelegramBot.getBotToken().isEmpty()) {
                logger.error("Токен бота не загружен!");
                return;
            }

            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(myTelegramBot);
            logger.info("Бот успешно запущен!");
        } catch (TelegramApiException e) {
            logger.error("Ошибка при запуске бота: ", e);
        }
    }
}
