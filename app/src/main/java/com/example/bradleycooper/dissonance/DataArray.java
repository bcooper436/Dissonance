package com.example.bradleycooper.dissonance;

import java.util.ArrayList;

/**
 * Created by williammcclain on 10/22/16.
 */

public class DataArray {
    private ArrayList <DataPoint> dataArray;

    public DataArray() {
        this.dataArray = new ArrayList<DataPoint>();
    }

    public ArrayList<DataPoint> getDataArray() {
        return dataArray;
    }

    public void setDataArray(ArrayList<DataPoint> dataArray) {
        this.dataArray = dataArray;
    }
    public void addData(String data, long time){
        DataPoint x = new DataPoint(data, time);
        dataArray.add(x);
    }
}

