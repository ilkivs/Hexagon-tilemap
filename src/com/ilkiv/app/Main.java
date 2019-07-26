package com.ilkiv.app;

import com.ilkiv.service.UIService;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        UIService.createUI(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
