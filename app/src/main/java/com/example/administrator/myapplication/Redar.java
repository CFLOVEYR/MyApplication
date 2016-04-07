package com.example.administrator.myapplication;

/**
 * Created by keguan on 2016/4/7.
 */
public class Redar {
    public Redar(double[] data) {
        this.data = data;
    }

    private double[] data = {50, 100, 50, 54.5, 77.5, 50}; //各维度分值

    public double[] getData() {
        return data;
    }

    public void setData(double[] data) {
        this.data = data;
    }
}
