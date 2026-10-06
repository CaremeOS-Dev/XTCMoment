package com.xtc.web.core.data.resp;

/** 加速度传感器结果。 */
public class RespAccelerometer {

    public interface Code {
        String FAIL = "000002";
        String NOT_PERMISSION = "000003";
        String SUCCESS = "000001";
    }

    private String code;
    private float x;
    private float y;
    private float z;

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public float getX() {
        return this.x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return this.y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return this.z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return "RespAccelerometer{x=" + this.x + ", y=" + this.y + ", z=" + this.z + '}';
    }
}