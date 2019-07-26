package com.ilkiv.service;

import com.ilkiv.model.Hexagon;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.List;

public class UIService {

    public static Button saveButton;
    public static Button openButton;
    private static HexagonService hexagonService = new HexagonService();

    public static void createUI(Stage primaryStage) {
        primaryStage.setTitle("Hexagon tilemap");
        primaryStage.getIcons().add(new Image("file:resources/hexagon-icon.png"));
        primaryStage.setResizable(false);

        Canvas canvas = new Canvas(1000, 600);
        GraphicsContext context = canvas.getGraphicsContext2D();

        List<Hexagon> list = hexagonService.createHexagonTilemap();
        for (Hexagon h : list) {
            hexagonService.drawHexagon(h, context);
        }

        Group root = new Group();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, 1000, 600);

        saveButton = new Button("Save path to JSON");
        openButton = new Button("Open path from JSON");
        saveButton.setDisable(true);

        BorderPane borderPane = new BorderPane();
        borderPane.setLeft(saveButton);
        borderPane.setRight(openButton);
        root.getChildren().add(borderPane);

        canvas.setOnMouseClicked(event ->
                hexagonService.onCanvasClick(context, event)
        );

        saveButton.setOnMouseClicked(event ->
                hexagonService.onSaveButtonClick()
        );

        openButton.setOnMouseClicked(event ->
                hexagonService.onOpenButtonClick(context)
        );

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
