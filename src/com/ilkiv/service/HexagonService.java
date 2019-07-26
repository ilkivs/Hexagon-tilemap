package com.ilkiv.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ilkiv.model.Hexagon;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HexagonService {

    private List<Hexagon> list = new ArrayList<>();
    private Hexagon startHexagon;
    private Hexagon endHexagon;
    private int clickCounter;

    public List<Hexagon> createHexagonTilemap() {
        for (int i = 0; i < 20; i += 2) {
            for (int j = 0; j < 10; j++) {
                Long id = (long) (i * 10 + j);
                Hexagon hexagon1 = new Hexagon(id, i * 61, j * 72, false, false, false);
                id += 10;
                Hexagon hexagon2 = new Hexagon(id, i * 61 + 61, j * 72 + 36, false, false, false);
                list.add(hexagon1);
                list.add(hexagon2);
            }
        }
        Collections.sort(list);
        return list;
    }

    public void drawHexagon(Hexagon hexagon, GraphicsContext context) {
        context.setFill(Color.BLACK);
        if (hexagon.isClicked()) {
            context.setFill(Color.YELLOW);
        }
        if (hexagon.isStart()) {
            context.setFill(Color.GREEN);
        }
        if (hexagon.isEnd()) {
            context.setFill(Color.RED);
        }
        context.fillPolygon(getXPoints(hexagon.getX()), getYPoints(hexagon.getY()), 6);
        context.setFill(Color.WHITE);
        context.fillText(String.valueOf(hexagon.getId()), hexagon.getX() - 5, hexagon.getY() + 2);
    }

    public void onCanvasClick(GraphicsContext context, MouseEvent event) {
        Hexagon hexagon = findHexagonByClick(event.getX(), event.getY());
        clickCounter++;
        if (clickCounter % 2 == 1) {
            for (Hexagon h : list) {
                h.setEnd(false);
                h.setStart(false);
                h.setClicked(false);
            }
            hexagon.setClicked(true);
            startHexagon = hexagon;
            UIService.saveButton.setDisable(true);
        } else {
            startHexagon.setClicked(false);
            startHexagon.setStart(true);
            endHexagon = hexagon;
            endHexagon.setEnd(true);
            UIService.saveButton.setDisable(false);
            drawPath();
        }
        context.clearRect(0, 0, 1000, 600);
        for (Hexagon h : list) {
            drawHexagon(h, context);
        }
    }

    public void onSaveButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save path to JSON file");
        File file = fileChooser.showOpenDialog(new Stage());
        writeToJson(file.getAbsolutePath());
    }

    public void onOpenButtonClick(GraphicsContext context) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open path from JSON file");
        File file = fileChooser.showOpenDialog(new Stage());
        list.clear();
        list.addAll(readFromJson(file.getAbsolutePath()));
        for (Hexagon h : list) {
            drawHexagon(h, context);
        }
    }

    private void writeToJson(String pathToFile) {
        Path path = Paths.get(pathToFile);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF-8"))) {
            for (Hexagon hexagon : list) {
                String str = gson.toJson(hexagon);
                writer.write(str + "\n\n");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private List<Hexagon> readFromJson(String pathToFile) {
        List<Hexagon> parsedList = new ArrayList<>();
        Path path = Paths.get(pathToFile);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String data = "";
        try (BufferedReader reader = Files.newBufferedReader(path, Charset.forName("UTF-8"))) {
            Stream<String> lines = Files.lines(path);
            data = lines.collect(Collectors.joining("\n"));
            lines.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        String[] stringHexagons = data.split("\n\n");
        for (int i = 0; i < stringHexagons.length; i++) {
            parsedList.add(gson.fromJson(stringHexagons[i], Hexagon.class));
        }
        return parsedList;
    }

    private void drawPath() {
        double distance = getDistance(startHexagon.getX(), startHexagon.getY(), endHexagon);
        int n = (int) (distance / 45);
        double xStep = Math.abs(endHexagon.getX() - startHexagon.getX()) / n;
        double yStep = Math.abs(endHexagon.getY() - startHexagon.getY()) / n;

        List<Double> pathXPoints = new ArrayList<>();
        List<Double> pathYPoints = new ArrayList<>();
        boolean x = startHexagon.getX() < endHexagon.getX();
        boolean y = startHexagon.getY() < endHexagon.getY();
        if (x && y) {
            for (int i = 0; i < n; i++) {
                pathXPoints.add(startHexagon.getX() + i * xStep);
                pathYPoints.add(startHexagon.getY() + i * yStep);
                Hexagon hex = findHexagonByClick(pathXPoints.get(i), pathYPoints.get(i));
                hex.setClicked(true);
            }
        } else if (x && !y) {
            for (int i = 0; i < n; i++) {
                pathXPoints.add(startHexagon.getX() + i * xStep);
                pathYPoints.add(startHexagon.getY() - i * yStep);
                Hexagon hex = findHexagonByClick(pathXPoints.get(i), pathYPoints.get(i));
                hex.setClicked(true);
            }
        } else if (!x && y) {
            for (int i = 0; i < n; i++) {
                pathXPoints.add(startHexagon.getX() - i * xStep);
                pathYPoints.add(startHexagon.getY() + i * yStep);
                Hexagon hex = findHexagonByClick(pathXPoints.get(i), pathYPoints.get(i));
                hex.setClicked(true);
            }
        } else {
            for (int i = 0; i < n; i++) {
                pathXPoints.add(startHexagon.getX() - i * xStep);
                pathYPoints.add(startHexagon.getY() - i * yStep);
                Hexagon hex = findHexagonByClick(pathXPoints.get(i), pathYPoints.get(i));
                hex.setClicked(true);
            }
        }
    }

    private Hexagon findHexagonByClick(double x, double y) {
        int i1 = ((int) x + 61) / 122;
        int j1 = ((int) y + 36) / 72;
        int i2 = ((int) x) / 122;
        int j2 = ((int) y) / 72;

        Hexagon hexagon1 = list.get(i1 * 20 + j1);
        Hexagon hexagon2 = list.get(i2 * 20 + j2 + 10);
        double distance1 = getDistance(x, y, hexagon1);
        double distance2 = getDistance(x, y, hexagon2);

        return distance1 < distance2 ? hexagon1 : hexagon2;
    }

    private double[] getXPoints(double xCenter) {
        double[] points = new double[6];
        points[0] = xCenter + 20;
        points[1] = xCenter + 40;
        points[2] = xCenter + 20;
        points[3] = xCenter - 20;
        points[4] = xCenter - 40;
        points[5] = xCenter - 20;
        return points;
    }

    private double[] getYPoints(double yCenter) {
        double[] points = new double[6];
        points[0] = yCenter + 35;
        points[1] = yCenter;
        points[2] = yCenter - 35;
        points[3] = yCenter - 35;
        points[4] = yCenter;
        points[5] = yCenter + 35;
        return points;
    }

    private double getDistance(double i, double j, Hexagon hexagon) {
        return Math.sqrt(Math.pow(i - hexagon.getX(), 2) + Math.pow(j - hexagon.getY(), 2));
    }
}
