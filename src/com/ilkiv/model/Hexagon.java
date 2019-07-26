package com.ilkiv.model;

public class Hexagon implements Comparable {

    private Long id;
    private double x;
    private double y;
    private boolean isClicked;
    private boolean isStart;
    private boolean isEnd;

    public Hexagon(Long id, double x, double y, boolean isClicked, boolean isStart, boolean isEnd) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.isClicked = isClicked;
        this.isStart = isStart;
        this.isEnd = isEnd;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }

    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    @Override
    public String toString() {
        return "Hexagon{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", isClicked=" + isClicked +
                ", isStart=" + isStart +
                ", isEnd=" + isEnd +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        return this.getId() < ((Hexagon) o).getId() ? -1 : 1;
    }
}
