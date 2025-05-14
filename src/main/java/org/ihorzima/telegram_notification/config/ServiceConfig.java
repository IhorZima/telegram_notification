package org.ihorzima.telegram_notification.config;

import org.ihorzima.telegram_notification.bot.TelegramBot;
import org.ihorzima.telegram_notification.builder.MeasurementPdfFileBuilder;
import org.ihorzima.telegram_notification.builder.PdfFileBuilder;
import org.ihorzima.telegram_notification.model.Measurement;
import org.ihorzima.telegram_notification.repository.AccountLocalRepository;
import org.ihorzima.telegram_notification.service.MeasurementService;
import org.ihorzima.telegram_notification.service.MeasurementsGoogleSheetReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

    @Value("${google.api.auth.key.path:null}")
    private String authKeyPath;

    @Bean
    public AccountLocalRepository accountLocalRepository() {
        return new AccountLocalRepository();
    }

    @Bean
    public MeasurementPdfFileBuilder measurementPdfFileBuilder() {
        return new MeasurementPdfFileBuilder();
    }

    @Bean
    public MeasurementService measurementService(TelegramBot telegramBot, PdfFileBuilder<Measurement> pdfFileBuilder) {
        return new MeasurementService(telegramBot, pdfFileBuilder);
    }

    @Bean
    public MeasurementsGoogleSheetReader googleSheetsReader() {
        return new MeasurementsGoogleSheetReader(authKeyPath);
    }
}
