package com.photoanalyzer.model;

import java.io.Serializable;

public class DominantColor implements Serializable {
    private static final long serialVersionUID = 1L;

    private String hex;
    private int r;
    private int g;
    private int b;
    private double percentage;

    public DominantColor() {}

    public DominantColor(String hex, int r, int g, int b, double percentage) {
        this.hex = hex;
        this.r = r;
        this.g = g;
        this.b = b;
        this.percentage = percentage;
    }

    public String getHex() { return hex; }
    public void setHex(String hex) { this.hex = hex; }

    public int getR() { return r; }
    public void setR(int r) { this.r = r; }

    public int getG() { return g; }
    public void setG(int g) { this.g = g; }

    public int getB() { return b; }
    public void setB(int b) { this.b = b; }

    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }
}
