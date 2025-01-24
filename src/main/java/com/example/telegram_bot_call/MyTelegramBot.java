package com.example.telegram_bot_call;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Component
public class MyTelegramBot extends TelegramLongPollingBot {

    // Словарь доступных команд
    private final Map<String, String> commands = new HashMap<>();

    private final Set<String> bannedWords = new HashSet<>();


    public MyTelegramBot() {
        commands.put("callAll", "Вызов всех участников!");
        commands.put("pokrovka", "Вызов всех участников из Покровки!");
        commands.put("help", "Показать списокдоступных команд");

        bannedWords.add("gay");
        bannedWords.add("loh");
        bannedWords.add("гей");
        bannedWords.add("гау");
        bannedWords.add("даун");
        bannedWords.add("лох");
        bannedWords.add("пидор");
        bannedWords.add("далбаеб");
        bannedWords.add("гот");

    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String userInput = message.getText().trim();

            // Проверяем, начинается ли сообщение с '/'
            if (!userInput.startsWith("/")) {
                return; // Если это не команда, то игнорируем сообщение
            }

            // Проверяем на наличие запрещённых слов
            String bannedWord = findBannedWord(userInput);
            if (bannedWord != null) {
                sendTextMessage(message.getChatId(), "Сам ты " + bannedWord);
                return; // Не продолжаем обработку команды, если нашли запрещённое слово
            }

            // Убираем символ '/' из начала команды
            if (userInput.startsWith("/")) {
                userInput = userInput.substring(1);
            }

            String response;
            if (commands.containsKey(userInput)) {
                response = handleCommand(userInput);
            } else {
                response = "Неизвестная команда. Напишите 'help', чтобы увидеть список доступных.";
            }

            // Отправляем ответ пользователю
            sendTextMessage(message.getChatId(), response);
        }
    }

    private String findBannedWord(String text) {
        // Проверка текста на наличие запрещённых слов
        for (String word : bannedWords) {
            if (text.toLowerCase().contains(word.toLowerCase())) {
                return word; // Возвращаем первое найденное запрещённое слово
            }
        }
        return null; // Нет запрещённых слов
    }



    private String handleCommand(String command) {
        switch (command) {
            case "callAll":
                return "@zhndlu @gabagoo1 @abdurahim_bv @Ackerman_A @ssshuuunga @kutb1dinov @somevariable @mrvoidfunction @hhghvp @GQAQF @oktanol";
            case "pokrovka":
                return "@zhndlu @Ackerman_A @hhghvp @GQAQF";
            case "help":
                StringBuilder helpMessage = new StringBuilder("Доступные команды:\n");
                // Теперь выводим команды с '/'
                commands.forEach((cmd, desc) -> helpMessage.append("/").append(cmd).append(" - ").append(desc).append("\n"));
                return helpMessage.toString();
            default:
                return "Команда не распознана!";
        }
    }


    private void sendTextMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "@call_all_guys_bot"; // Укажите имя бота, зарегистрированное в BotFather
    }

    @Override
    public String getBotToken() {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (inputStream == null) {
                throw new IOException("Не удалось найти файл application.properties в classpath");
            }
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при загрузке токена!", e);
        }
        return properties.getProperty("telegram.bot.token");
    }
}

