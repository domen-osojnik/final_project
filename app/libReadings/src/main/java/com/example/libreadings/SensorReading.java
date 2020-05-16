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

    // Add new data to list
    public void addData(SensorData data){
        // Insert new data sensors reading, cooldown for new events is 5 seconds
        if (this.dataValues.isEmpty() || this.dataValues.get(this.dataValues.size()-1).cooldown(data)) this.dataValues.add(data);
    }


    /*
     *  Methods
     */
    @Override
    public String toString() {
        StringBuilder retval = new StringBuilder("SensorReading{\n");
        retval.append("Date=").append(dateFormat.format(this.date)).append(",\n").append("dataValues=[\n");
        for (SensorData data : this.dataValues) {
            retval.append(data.toString()).append("\n");
        }
        retval.append("]}\n");
        return retval.toString();
    }

}
