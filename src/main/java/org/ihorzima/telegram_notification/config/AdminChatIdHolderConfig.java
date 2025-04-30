package org.ihorzima.telegram_notification.config;

import org.springframework.stereotype.Component;

@Component
public class AdminChatIdHolderConfig {
    private String chatId;

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getChatId() {
        return chatId;
    }
}
