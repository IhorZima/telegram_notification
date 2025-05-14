package org.ihorzima.telegram_notification;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.ihorzima.telegram_notification.controller.MeasurementController;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class JavaFxApp extends Application {

    private ConfigurableApplicationContext context;

    @Override
    public void init() {
        context = new SpringApplicationBuilder(TelegramNotificationApp.class).run();
    }

    @Override
    public void start(Stage stage) {
        var controller = context.getBean(MeasurementController.class);

        Button btn = new Button("надіслати квитанції");
        btn.setOnAction(e -> controller.retrieveAndProcessMeasurements());

        Button exitBtn = new Button("завершити роботу додатку");
        exitBtn.setOnAction(e -> {
            stage.close();
            Platform.exit();
            System.exit(0);
        });

        VBox layout = new VBox(20, btn, exitBtn);
        layout.setPadding(new Insets(20, 0, 0, 20));

        StackPane background = background(layout);

        Scene scene = new Scene(background, 400, 200);
        stage.setScene(scene);
        stage.setTitle("Telegram Notifier");
        stage.show();
    }

    private StackPane background(VBox content) {
        Canvas canvas = new Canvas(400, 200);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.TOMATO);
        gc.fillRect(0, 0, 400, 200);

        gc.setFill(Color.rgb(200, 200, 200, 0.5));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        gc.save();
        gc.translate(0, 200);
        gc.rotate(-45);

        for (int i = -400; i < 800; i += 100) {
            gc.fillText("21312321 ".repeat(10), i, 0);
        }
        gc.restore();

        return new StackPane(canvas, content);
    }

    @Override
    public void stop() {
        context.close();
    }
}
