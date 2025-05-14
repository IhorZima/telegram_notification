package org.ihorzima.telegram_notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    private static final String SEND_MEASUREMENT_STATE_COLUMN_VALUE = "TRUE";

    private final TelegramBot telegramBot;
    private final PdfFileBuilder<Measurement> measurementPdfFileBuilder;

    public boolean processMeasurements(List<Measurement> measurements) {
        log.info("Received {} measurements", measurements.size());

        List<Measurement> validMeasurements = measurements.stream()
                .filter(measurement -> StringUtils.isNotBlank(measurement.getLandId()))
                .filter(measurement -> SEND_MEASUREMENT_STATE_COLUMN_VALUE.equals(measurement.getState()))
                .toList();

        log.info("Going to process {} measurements", validMeasurements.size());

        List<Boolean> processedMeasurementResults = validMeasurements.stream().map(this::processMeasurement).toList();

        long successfullyProcessed = processedMeasurementResults.stream().filter(bool -> bool).count();

        log.info("Processed {} measurements", successfullyProcessed);

        return processedMeasurementResults.stream().allMatch(bool -> bool);
    }

    private boolean processMeasurement(Measurement measurement) {
        try {
            String landId = measurement.getLandId();
            if (Strings.isEmpty(measurement.getTelegramId())) {
                log.error("Telegram chatId for a landId {} not found", landId);
                return false;
            }

            byte[] pdfFileContent = measurementPdfFileBuilder.build(measurement);
            String accountChatId = measurement.getTelegramId();
            log.info("Sending measurement to {}", accountChatId);
            telegramBot.sendFile(accountChatId, buildPdfFileName(landId), pdfFileContent);
            log.info("Measurement is sent to {}", measurement.getTelegramId());
            return true;
        } catch (Exception ex) {
            log.error("Could not process measurement", ex);
            return false;
        }
    }

    private String buildPdfFileName(String landId) {
        String enhancedLandId = landId.replace("/", "_");
        return "рахунок_" + enhancedLandId + "_" + LocalDateTime.now().format(DATE_TIME_FORMATTER) + ".pdf";
    }
}
