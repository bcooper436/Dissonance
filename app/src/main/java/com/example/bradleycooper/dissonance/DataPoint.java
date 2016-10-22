package com.example.bradleycooper.dissonance;

/**
 * Created by williammcclain on 10/22/16.
 */

public class DataPoint {
    private String axisInfo;
    private long timeInfo;

    public DataPoint(String inputS, long inputI){
        this.axisInfo = inputS;
        this.timeInfo = inputI;
    }

    public String getAxisInfo() {
        return axisInfo;
    }

    public void setAxisInfo(String axisInfo) {
        this.axisInfo = axisInfo;
    }

    public long getTimeInfo() {
        return timeInfo;
    }

    public void setTimeInfo(int timeInfo) {
        this.timeInfo = timeInfo;
    }
}
