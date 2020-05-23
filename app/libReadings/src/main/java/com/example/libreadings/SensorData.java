package com.example.libreadings;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SensorData implements Serializable {
    /*
     *  Properties
     */
    //@Expose(serialize = false)
    //private DateFormat dateFormat;

    @SerializedName("date")
    private Date date;

    @SerializedName("lat")
    private double Lat;

    @SerializedName("long")
    private double Long;

    @SerializedName("degree")
    private int shakeDegree;

    @SerializedName("speed")
    private float speed;

    /*
     *  Constructor
     */
    public SensorData(double Lat, double Long, float speed, int shakeDegree) {
        this.Lat = Lat;
        this.Long = Long;
        this.date = new Date();
        this.speed = speed;
        this.shakeDegree = shakeDegree;
        //dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
    }

    /*
     *  Methods
     */
    @Override
    public String toString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");

        return "{\"Date\"=\"" +   dateFormat.format(this.date) +
                "\",\"Lat\"=\"" + this.Lat  +
                "\",\"Long\"=\"" + this.Long +
                "\",\"Degree\"=\"" + this.shakeDegree +
                "\",\"Speed\"=\"" + this.speed +
                "\"}";
    }

    // Check difference of dates of readings between two GyroReadings
    // If degree of bump is higher, save the higher one
    public Boolean cooldown(SensorData reading) {
        return  (Math.abs(reading.date.getTime() - this.date.getTime()) >= 5000 || reading.shakeDegree > this.shakeDegree);
    }

}


