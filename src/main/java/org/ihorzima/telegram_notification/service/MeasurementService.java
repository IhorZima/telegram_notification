package org.ihorzima.telegram_notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.ihorzima.telegram_notification.bot.TelegramBot;
import org.ihorzima.telegram_notification.builder.PdfFileBuilder;
import org.ihorzima.telegram_notification.config.TelegramBotConfig;
import org.ihorzima.telegram_notification.config.TelegramBotProperties;
import org.ihorzima.telegram_notification.model.Measurement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
@EnableConfigurationProperties(TelegramBotProperties.class)
public class MeasurementService {

    private final TelegramBotProperties telegramBotProperties;
    private final TelegramBot telegramBot;
    private final PdfFileBuilder<Measurement> measurementPdfFileBuilder;

    public void processMeasurements(List<Measurement> measurements) {
        log.info("Processing {} measurements", measurements.size());
        measurements.forEach(this::processMeasurement);
        log.info("Processed {} measurements", measurements.size());
    }

    private void processMeasurement(Measurement measurement) {
        try {
            String landId = measurement.getLandId();
            if (Strings.isEmpty(measurement.getTelegramId())) {
                log.error("Telegram chatId for a landId {} not found", landId);
                return;
            }

            byte[] pdfFileContent = measurementPdfFileBuilder.build(measurement);
            String accountChatId = measurement.getTelegramId();
            log.info("Sending measurement to {}", accountChatId);
            telegramBot.sendFile(accountChatId, telegramBotProperties.getPdfFileName() , pdfFileContent);
            log.info("Measurement is sent to {}", measurement.getTelegramId());
        } catch (Exception ex) {
            log.error("Could not process measurement", ex);
        }
    }
}
