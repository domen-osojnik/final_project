package com.example.libreadings;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import sun.rmi.runtime.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SensorReading implements Serializable {
    /*
     *  Properties
     */
    @SerializedName("date")
    private Date date;

    @SerializedName("events")
    private ArrayList<SensorData> dataValues;

    //@Expose(serialize = false)
    //private DateFormat dateFormat;

    /*
     *  Constructor
     */
    public SensorReading() {
        // Initializing properties...
        this.date = new Date();
        this.dataValues = new ArrayList<SensorData>();
        //dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
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
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        StringBuilder retval = new StringBuilder("SensorReading{\n");
        retval.append("Date=").append(dateFormat.format(this.date)).append(",\n").append("dataValues=[\n");
        for (SensorData data : this.dataValues) {
            retval.append(data.toString()).append("\n");
        }
        retval.append("]}\n");
        return retval.toString();
    }

}
