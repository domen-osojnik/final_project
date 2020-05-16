package com.example.libreadings;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import sun.rmi.runtime.Log;

public class SensorReading {
    /*
     *  Properties
     */
    private Date date;
    private ArrayList<SensorData> dataValues;
    private DateFormat dateFormat;

    // ...

    /*
     *  Constructor
     */
    public SensorReading() {
        // Initializing properties...
        this.date = new Date();
        this.dataValues = new ArrayList<SensorData>();
        dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
    }

    public void addData(SensorData data){
        this.dataValues.add(data);
    }

     /*
     *  Methods
     */
    public void readGyro(double Lat, double Long, int Degree) {
        /*
        // Insert new gyroscope sensor reading, cooldown for new events is 5 seconds
        GyroReading reading = new GyroReading(Lat, Long, Degree);
        if (this.gyroValues.isEmpty() || this.gyroValues.get(this.gyroValues.size()-1).cooldown(reading)) {
            this.gyroValues.add(new GyroReading(Lat, Long, Degree));
        }
        */
    }

    public String gyroToString() {

        StringBuilder retval = new StringBuilder();

        // Gyroscope readings to string
        /*
        for (GyroReading reading: this.gyroValues) {
            retval.append(reading.toString()).append(",\n");
        }
        */

        retval.append("]");
        return retval.toString();
    }


    @Override
    public String toString() {

        StringBuilder retval = new StringBuilder("SensorReading{\n");

        // Date to string
        retval.append("Date=").append(dateFormat.format(this.date)).append(",\n");

        // Gyroscope readings to string
        retval.append("GyroReadings=[\n").append(this.gyroToString()).append("],\n");

        // GPS readings to string
        // ...

        // Accelerometer readings to string
        // ...

        retval.append("}\n");

        return retval.toString();
    }
}
