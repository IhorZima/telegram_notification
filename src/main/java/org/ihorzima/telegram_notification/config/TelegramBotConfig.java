package org.ihorzima.telegram_notification.config;

import org.ihorzima.telegram_notification.bot.TelegramBot;
import org.ihorzima.telegram_notification.builder.PdfFileBuilder;
import org.ihorzima.telegram_notification.model.Measurement;
import org.ihorzima.telegram_notification.repository.AccountLocalRepository;
import org.ihorzima.telegram_notification.service.MeasurementService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@EnableConfigurationProperties(TelegramBotProperties.class)
public class TelegramBotConfig {

    @Bean
    public TelegramBotsApi telegramBotsApi(TelegramBot telegramBot) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(telegramBot);
        return botsApi;
    }

    @Bean
    public TelegramBot myTelegramBot(AccountLocalRepository accountRepository,
                                     TelegramBotProperties telegramBotProperties) {
        return new TelegramBot(telegramBotProperties.getToken(), accountRepository, telegramBotProperties);
    }
}
