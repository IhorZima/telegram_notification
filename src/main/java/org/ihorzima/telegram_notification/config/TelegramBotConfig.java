package org.ihorzima.telegram_notification.config;

import org.ihorzima.telegram_notification.bot.TelegramBot;
import org.ihorzima.telegram_notification.repository.AccountLocalRepository;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@EnableConfigurationProperties(TelegramBotProperties.class)
public class TelegramBotConfig {

    private final TelegramBotProperties telegramBotProperties;
    private final AdminChatIdHolder adminChatIdHolder;

    public TelegramBotConfig(TelegramBotProperties telegramBotProperties, AdminChatIdHolder adminChatIdHolder) {
        this.telegramBotProperties = telegramBotProperties;
        this.adminChatIdHolder = adminChatIdHolder;
    }

    @Bean
    public TelegramBotsApi telegramBotsApi(TelegramBot telegramBot) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(telegramBot);
        return botsApi;
    }

    @Bean
    public TelegramBot myTelegramBot(AccountLocalRepository accountRepository) {
        return new TelegramBot(telegramBotProperties.getBotToken() , accountRepository, adminChatIdHolder, telegramBotProperties);
    }
}
