package com.example.schedulemanager.vo;

/**
 * Created by bjj on 2017-09-07.
 */
public class CalendarCellInfo {
    int originalLeft;
    int originalTop;

    int left;
    int top;
    int width;
    int height;

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void updateLeft(int columNumber){
        left = originalLeft + ((columNumber % 7) * width);
    }

    public void updateTop(int lineNumber) {
        top = originalTop + ((lineNumber / 7) * height);
    }

    public int getOriginalLeft() {
        return originalLeft;
    }

    public void setOriginalLeft(int originalLeft) {
        this.originalLeft = originalLeft;
    }

    public void setOriginalTop(int originalTop) {
        this.originalTop = originalTop;
    }
}

