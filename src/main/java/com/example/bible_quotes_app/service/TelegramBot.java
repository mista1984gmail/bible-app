package com.example.bible_quotes_app.service;

import com.example.bible_quotes_app.config.BotConfig;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@AllArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        String answer = "";

        if(update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText){
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                default:
                    try {
                        answer = BibleService.getAnswer();

                    } catch (Exception e) {
                        sendMessage(chatId, "Что-то пошло не так..." + "\n" +
                                "Попробуй еще раз.");
                    }
                    sendMessage(chatId, answer);
                    continueCommandReceived(chatId);
            }
        }

    }

    private void startCommandReceived(Long chatId, String name) {
        String answer = "Привет, " + name + ", рад тебя приветствовать!" + "\n" +
                "Я сервис, который выдает случайную цитату из Библии.\n" +
                "Она вдохновит тебя и направит на верный путь." + "\n" +
                "Опишу, что тебя волнует или тревожит.";
        sendMessage(chatId, answer);
    }

    private void continueCommandReceived(Long chatId) {
        String answer = "Опишу, что тебя волнует или тревожит.";
        sendMessage(chatId, answer);
    }

    private void sendMessage(Long chatId, String textToSend){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {

        }
    }

}
