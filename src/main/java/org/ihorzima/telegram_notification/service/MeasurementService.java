package org.ihorzima.telegram_notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.ihorzima.telegram_notification.bot.TelegramBot;
import org.ihorzima.telegram_notification.builder.PdfFileBuilder;
import org.ihorzima.telegram_notification.model.Account;
import org.ihorzima.telegram_notification.model.Measurement;
import org.ihorzima.telegram_notification.repository.AccountLocalRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class MeasurementService {

    // TODO: extract to config
    private static final String PDF_FILENAME = "measurement.pdf";

    private final AccountLocalRepository accountLocalRepository;
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

            Account accountByLandId = accountLocalRepository.getAccountByLandId(landId);

            if (accountByLandId == null) {
                log.error("Account with landId {} not found", landId);
                return;
            }

            if (Strings.isEmpty(accountByLandId.getTelegramId())) {
                log.error("Telegram chatId for a landId {} not found", landId);
                return;
            }

            byte[] pdfFileContent = measurementPdfFileBuilder.build(measurement);
            String accountChatId = accountByLandId.getTelegramId();
            log.info("Sending measurement to {}", accountChatId);
            telegramBot.sendFile(accountChatId, PDF_FILENAME, pdfFileContent);
            log.info("Measurement is sent to {}", accountByLandId.getTelegramId());
        } catch (Exception ex) {
            log.error("Could not process measurement", ex);
        }
    }
}
