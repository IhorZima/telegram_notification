package org.ihorzima.telegram_notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.ihorzima.telegram_notification.bot.TelegramBot;
import org.ihorzima.telegram_notification.builder.PdfFileBuilder;
import org.ihorzima.telegram_notification.config.TelegramBotProperties;
import org.ihorzima.telegram_notification.model.Measurement;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(TelegramBotProperties.class)
public class MeasurementService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
            telegramBot.sendFile(accountChatId, buildPdfFileName(landId), pdfFileContent);
            log.info("Measurement is sent to {}", measurement.getTelegramId());
        } catch (Exception ex) {
            log.error("Could not process measurement", ex);
        }
    }

    private String buildPdfFileName(String landId) {
        return landId + "_" + LocalDateTime.now().format(DATE_TIME_FORMATTER) + ".pdf";
    }
}
