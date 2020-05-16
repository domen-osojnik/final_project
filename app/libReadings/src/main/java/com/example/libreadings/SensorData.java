package com.example.libreadings;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SensorData {
    /*
     *  Properties
     */
    private DateFormat dateFormat;
    private Date date;
    private double Lat;
    private double Long;
    private int shakeDegree;
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
        dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
    }

    /*
     *  Methods
     */
    @Override
    public String toString() {
        return "GyroReading{" +
                "Date=" +   dateFormat.format(this.date) +
                ", Lat=" + this.Lat  +
                ", Long=" + this.Long +
                ", Degree=" + this.shakeDegree +
                ", Speed=" + this.speed +
                '}';
    }

    // Check difference of dates of readings between two GyroReadings
    // If degree of bump is higher, save the higher one
    public Boolean cooldown(GyroReading reading) {
        return  (Math.abs(reading.date.getTime() - this.date.getTime()) >= 5000 || reading.Degree > this.Degree);
    }
}


