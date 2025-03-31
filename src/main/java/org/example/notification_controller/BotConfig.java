package org.example.notification_controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class BotConfig {

    @Bean
    public TelegramBotsApi telegramBotsApi(MyTelegramBot myTelegramBot) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(myTelegramBot);
        return botsApi;
    }

    @Bean
    public MyTelegramBot myTelegramBot() throws TelegramApiException {
        return new MyTelegramBot("7597164009:AAGoYwbZlWpUn-VSrRTQkt8y_6Bg62CHC-4");
    }
}
