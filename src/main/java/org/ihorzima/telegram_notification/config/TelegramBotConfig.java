package org.ihorzima.telegram_notification.config;

import org.ihorzima.telegram_notification.bot.TelegramBot;
import org.ihorzima.telegram_notification.repository.AccountRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramBotConfig {

    @Bean
    public TelegramBotsApi telegramBotsApi(TelegramBot telegramBot) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(telegramBot);
        return botsApi;
    }

    @Bean
    public TelegramBot myTelegramBot(AccountRepository accountRepository) {
        // TODO: move to application properties or env
        return new TelegramBot("7597164009:AAGoYwbZlWpUn-VSrRTQkt8y_6Bg62CHC-4", accountRepository);
    }
}
