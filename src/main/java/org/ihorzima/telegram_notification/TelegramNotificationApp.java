package org.ihorzima.telegram_notification;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TelegramNotificationApp {

    public static void main(String[] args) {
        Application.launch(JavaFxApp.class, args);
    }
}

