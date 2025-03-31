package org.example;

import org.example.notification_controller.MyTelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NotificationControllerApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationControllerApplication.class, args);
    }

}
