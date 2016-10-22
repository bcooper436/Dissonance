package com.example.bradleycooper.dissonance;

import java.util.ArrayList;

/**
 * Created by williammcclain on 10/22/16.
 */

public class StartingValue {
    private ArrayList<DataPoint> startingValues = new ArrayList<DataPoint>();
    double gyroXtotal = 0;
    double gyroYtotal = 0;
    double gyroZtotal = 0;
    double accelXtotal = 0;
    double accelYtotal = 0;
    double accelZtotal = 0;
    double MagnXtotal = 0;
    double MagnYtotal = 0;
    double MagnZtotal = 0;

    public void StartingValues(){
        double gyroXtotal = 0;
        double gyroYtotal = 0;
        double gyroZtotal = 0;
        double accelXtotal = 0;
        double accelYtotal = 0;
        double accelZtotal = 0;
        double MagnXtotal = 0;
        double MagnYtotal = 0;
        double MagnZtotal = 0;

    }
    public void addStartingValue(String axis, long time){
        DataPoint x = new DataPoint(axis, time);
        startingValues.add(x);
    }
    public DataPoint getAverage(){



        for (int i = 0; i < 10; i++) {
            String point = startingValues.get(i).getAxisInfo();
            parseAxis(point);
        }
        double avGyroX = gyroXtotal / 10;
        double avGyroY = gyroYtotal / 10;
        double avGyroZ = gyroZtotal / 10;
        double avAccelX = accelXtotal / 10;
        double avAccelY = accelYtotal / 10;
        double avAccelZ = accelZtotal / 10;
        double avMagnX = MagnXtotal / 10;
        double avMagnY = MagnYtotal / 10;
        double avMagnZ = MagnZtotal / 10;
        String merged = avGyroX+ ", "+avGyroY+ ", "+ avGyroZ+ ", "+ avAccelX + ", " + avAccelY + ", " +avAccelZ + ", " + avMagnX + ", " + avMagnY + ", "+ avMagnZ + ", ";
        long t = 0;
        DataPoint average = new DataPoint(merged, t);


        return average;
    }
    public void parseAxis(String axis){
        String[] dataPointArray = axis.split(",");
        for (int i = 0; i<dataPointArray.length; i++){
            switch (i){
                case 0:
                    gyroXtotal = gyroXtotal + Double.parseDouble(dataPointArray[0]);
                case 1:
                    gyroYtotal = gyroYtotal + Double.parseDouble(dataPointArray[1]);
                case 2:
                    gyroZtotal = gyroZtotal + Double.parseDouble(dataPointArray[2]);
                case 3:
                    accelXtotal = accelXtotal + Double.parseDouble(dataPointArray[3]);
                case 4:
                    accelYtotal = accelYtotal + Double.parseDouble(dataPointArray[4]);
                case 5:
                    accelZtotal = accelZtotal + Double.parseDouble(dataPointArray[5]);
                case 6:
                    MagnXtotal = MagnXtotal + Double.parseDouble(dataPointArray[6]);
                case 7:
                    MagnYtotal = MagnYtotal + Double.parseDouble(dataPointArray[7]);
                case 8:
                    MagnZtotal = MagnZtotal + Double.parseDouble(dataPointArray[8]);

        }
    }


}}
