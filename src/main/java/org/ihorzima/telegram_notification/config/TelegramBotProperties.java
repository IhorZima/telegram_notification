package org.ihorzima.telegram_notification.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

@Data
@Configuration
@ConfigurationProperties(prefix = "telegram.bot")
public class TelegramBotProperties {

    private String token;
    private String userName;
    private String adminPassphrase;
    private Set<String> AdminChatIds = new HashSet<>();
}
