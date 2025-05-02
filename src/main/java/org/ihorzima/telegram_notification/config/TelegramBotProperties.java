package org.ihorzima.telegram_notification.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "telegram")
@EnableConfigurationProperties(TelegramBotProperties.class)
public class TelegramBotProperties {
    private String botToken;
    private String adminPassphrase;
    private String botUserName;
    private String pdfFileName;
}
