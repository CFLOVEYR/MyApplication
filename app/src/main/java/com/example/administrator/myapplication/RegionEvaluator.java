package com.example.administrator.myapplication;

import android.animation.TypeEvaluator;

/**
 * Created by keguan on 2016/4/7.
 */
public class RegionEvaluator implements TypeEvaluator {

    @Override
    public Object evaluate(float fraction, Object startValue, Object endValue) {

        Redar endRadar = (Redar) endValue;
        Redar startRadar = (Redar) endValue;
        double[] startRadarData = startRadar.getData();
        double[] endRadarData = endRadar.getData();
        double[] delaRadarData = new double[startRadarData.length];
        for (int i = 0; i < startRadarData.length; i++) {
            double x = startRadarData[i] + fraction * (startRadarData[i] - endRadarData[i]);
            delaRadarData[i] = x;
        }
        Redar radar = new Redar(delaRadarData);
        return radar;
    }
}
