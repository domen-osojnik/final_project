package com.example.libreadings;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GyroReading {


    /*
     *  Properties
     */
    private Date date;
    private double Lat;
    private double Long;
    private int Degree;
    private DateFormat dateFormat;


    /*
     *  Constructor
     */
    GyroReading(double Lat, double Long, int Degree) {
        this.Lat = Lat;
        this.Long = Long;
        this.Degree = Degree;
        this.date = new Date();
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
                ", Degree=" + this.Degree +
                '}';
    }

    // Check difference of dates of readings between two GyroReadings
    // If degree of bump is higher, save the higher one
    public Boolean cooldown(GyroReading reading) {
        return  (Math.abs(reading.date.getTime() - this.date.getTime()) >= 5000 || reading.Degree > this.Degree);
    }

}


