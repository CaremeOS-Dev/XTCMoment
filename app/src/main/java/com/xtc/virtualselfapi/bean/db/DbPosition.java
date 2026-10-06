package com.xtc.virtualselfapi.bean.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/** Position of a costume ornament on the virtual self. */
@DatabaseTable(tableName = "position")
public class DbPosition {

    /** Column names. */
    public interface Key {
        String ORNAMENT_ID = "ornamentId";
        String SUIT_ID = "suitId";
    }

    @DatabaseField
    private int angle;

    @DatabaseField
    private int height;

    @DatabaseField(id = true)
    private int id;

    @DatabaseField
    private int ornamentId;

    @DatabaseField
    private float rotateX;

    @DatabaseField
    private float rotateY;

    @DatabaseField
    private int suitId;

    @DatabaseField
    private int width;

    @DatabaseField
    private int x;

    @DatabaseField
    private int y;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSuitId() {
        return this.suitId;
    }

    public void setSuitId(int suitId) {
        this.suitId = suitId;
    }

    public int getOrnamentId() {
        return this.ornamentId;
    }

    public void setOrnamentId(int ornamentId) {
        this.ornamentId = ornamentId;
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
        return "DbPosition{id=" + this.id + ", suitId=" + this.suitId + ", ornamentId=" + this.ornamentId
                + ", width=" + this.width + ", height=" + this.height + ", x=" + this.x + ", y=" + this.y
                + ", rotateX=" + this.rotateX + ", rotateY=" + this.rotateY + ", angle=" + this.angle + '}';
    }
}