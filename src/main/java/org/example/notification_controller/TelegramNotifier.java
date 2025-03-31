package org.example.notification_controller;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class TelegramNotifier {
    private static final String BOT_TOKEN = "7597164009:AAGoYwbZlWpUn-VSrRTQkt8y_6Bg62CHC-4";
    private static final String CHAT_ID = "ВАШ_CHAT_ID"; // Узнать можно через @userinfobot

    public static void sendMessage(String message) {
        try {
            String urlString = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage?chat_id=" + CHAT_ID + "&text=" + message;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
