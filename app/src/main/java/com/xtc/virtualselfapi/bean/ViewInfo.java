package com.xtc.virtualselfapi.bean;

import java.util.Comparator;
import java.util.Objects;

/**
 * 虚拟形象展示元素：位置、层级、旋转与资源地址。
 */
public class ViewInfo implements Comparator<ViewInfo> {

    private int angle;
    private int height;
    private int id;
    private int layer;
    private float rotateX;
    private float rotateY;
    private int type;
    private String url;
    private int width;
    private int x;
    private int y;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getLayer() {
        return this.layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public float getRotateX() {
        return this.rotateX;
    }

    public void setRotateX(float rotateX) {
        this.rotateX = rotateX;
    }

    public float getRotateY() {
        return this.rotateY;
    }

    public void setRotateY(float rotateY) {
        this.rotateY = rotateY;
    }

    public int getAngle() {
        return this.angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    @Override
    public String toString() {
        return "ViewInfo{id=" + this.id + ", type=" + this.type + ", url='" + this.url + "', width=" + this.width
                + ", height=" + this.height + ", x=" + this.x + ", y=" + this.y + ", layer=" + this.layer
                + ", rotateX=" + this.rotateX + ", rotateY=" + this.rotateY + ", angle=" + this.angle + '}';
    }

    @Override
    public int compare(ViewInfo first, ViewInfo second) {
        if (first.getLayer() > second.getLayer()) {
            return -1;
        }
        return Objects.equals(Integer.valueOf(first.getLayer()), Integer.valueOf(second.getLayer())) ? 0 : 1;
    }
}