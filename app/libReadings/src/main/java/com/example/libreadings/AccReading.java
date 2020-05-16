package com.example.libreadings;

public class AccReading {
    private float xAcc;
    private float yAcc;
    private float zAcc;
    private int shakeLevel;

    public float getxAcc() {
        return xAcc;
    }

    public void setxAcc(float xAcc) {
        this.xAcc = xAcc;
    }

    public float getyAcc() {
        return yAcc;
    }

    public void setyAcc(float yAcc) {
        this.yAcc = yAcc;
    }

    public float getzAcc() {
        return zAcc;
    }

    public void setzAcc(float zAcc) {
        this.zAcc = zAcc;
    }

    public int getShakeLevel() {
        return shakeLevel;
    }

    public void setShakeLevel(int shakeLevel) {
        this.shakeLevel = shakeLevel;
    }

    public AccReading(float xAcc, float yAcc, float zAcc) {
        this.xAcc = xAcc;
        this.yAcc = yAcc;
        this.zAcc = zAcc;
        if(this.xAcc > 5 || this.yAcc > 5 || this.zAcc > 5)this.shakeLevel=1;
        else if (this.xAcc>15 || this.yAcc > 15 || this.zAcc > 15)this.shakeLevel=2;
        else this.shakeLevel = 0;
    }
}
