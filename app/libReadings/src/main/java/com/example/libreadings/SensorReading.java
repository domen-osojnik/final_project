package com.example.libreadings;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

    @Expose(serialize = false)
    private ArrayList<String> filePaths;

    /*
     *  Constructor
     */
    public SensorReading() {
        // Initializing properties...
        this.date = new Date();
        this.dataValues = new ArrayList<SensorData>();
        this.filePaths = new ArrayList<String>();
        //dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
    }

    // Add new data to list
    public boolean addData(SensorData data){
        // Insert new data sensors reading, cooldown for new events is 5 seconds
        if (this.dataValues.isEmpty() || this.dataValues.get(this.dataValues.size()-1).cooldown(data)) {
            this.dataValues.add(data);
            return true;
        }
        return false;
    }

    // Add new data to list
    public void addFile(String path){
        this.filePaths.add(path);
    }

    // Return number of files
    public int filesCount(){
        return this.filePaths.size();
    }

    // Return number of files
    public String getFilePath(int i){
        return filePaths.get(i);
    }

    /*
     *  Methods
     */
    @Override
    public String toString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        StringBuilder retval = new StringBuilder("\"SensorReading\"={\n");
        retval.append("\"Date\"=\"").append(dateFormat.format(this.date)).append("\",\n").append("\"dataValues\"=[\n");
        for (SensorData data : this.dataValues) {
            retval.append(data.toString()).append(",\n");
        }
        retval.append("]}\n");
        return retval.toString();
    }

}
