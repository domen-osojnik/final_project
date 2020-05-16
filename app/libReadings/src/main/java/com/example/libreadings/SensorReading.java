package com.example.libreadings;

import java.util.ArrayList;
import java.util.Date;

public class SensorReading {

    /*
     *  Properties
     */
    private Date date;
    private ArrayList<GyroReading> gyroValues;
    private ArrayList<AccReading> accValues;
    private ArrayList<GPSReading> gpsValues;
    // ...


    /*
     *  Constructor
     */
    SensorReading() {
        // Initializing properties...
        this.date = new Date();
        this.gyroValues = new ArrayList<GyroReading>();
        this.accValues = new ArrayList<AccReading>();
        this.gpsValues = new ArrayList<GPSReading>();
    }


    /*
     *  Methods
     */
    public void readGyro(GyroReading reading) {
        // Insert new gyroscope sensor reading
        this.gyroValues.add(reading);
    }

    /*
    @Override
    public String toString() {
        return "Park{" +
                "mName='" + mName + '\'' +
                ", mDateOfEstablishment=" + mDateOfEstablishment +
                ", mSizeSquareKm=" + mSizeSquareKm +
                ", mTreeList=" + mTreeList +
                '}';
     }
     */



}
