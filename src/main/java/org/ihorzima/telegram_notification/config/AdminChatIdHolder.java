package org.ihorzima.telegram_notification.config;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Data
public class AdminChatIdHolder {
    private final List<String> listChatId = new ArrayList<>();
}

