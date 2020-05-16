package com.example.libreadings;

import java.util.ArrayList;
import java.util.Date;

import sun.rmi.runtime.Log;

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
    public SensorReading() {
        // Initializing properties...
        this.date = new Date();
        this.gyroValues = new ArrayList<GyroReading>();
        this.accValues = new ArrayList<AccReading>();
        this.gpsValues = new ArrayList<GPSReading>();
    }


    /*
     *  Methods
     */
    public void readGyro(double Lat, double Long, int Degree) {
        // Insert new gyroscope sensor reading, cooldown for new events is 5 seconds
        GyroReading reading = new GyroReading(Lat, Long, Degree);
        if (this.gyroValues.isEmpty() || this.gyroValues.get(this.gyroValues.size()-1).cooldown(reading)) {
            this.gyroValues.add(new GyroReading(Lat, Long, Degree));
        }
    }

    public String gyroToString() {
        // Return string of all gyroscope readings
        StringBuilder retval = new StringBuilder("GyroReading: [\n");
        for (GyroReading reading: this.gyroValues) {
            retval.append(reading.toString());
            retval.append("\n");
        }
        retval.append("]");
        return retval.toString();
    }


    @Override
    public String toString() {
        StringBuilder retval = new StringBuilder("SensorReading{");
        retval.append(this.gyroToString());

        // ...
        retval.append("}\n");
        return retval.toString();
    }
}
