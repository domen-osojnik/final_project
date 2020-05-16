package com.example.libreadings;


import java.util.Date;

public class GyroReading {


    /*
     *  Properties
     */
    private Date date;
    public double Lat;
    public double Long;
    public int Degree;


    /*
     *  Constructor
     */
    GyroReading(double Lat, double Long, int Degree) {
        this.Lat = Lat;
        this.Long = Long;
        this.Degree = Degree;
        this.date = new Date();
    }

    /*
     *  Methods
     */
    @Override
    public String toString() {
        return "GyroReading{" +
                "Lat=" + this.Lat  +
                ", Long=" + this.Long +
                ", Degree=" + this.Degree +
                '}';
    }

    // Check difference of dates of readings between two GyroReadings
    public Boolean cooldown(GyroReading reading) { return  Math.abs(reading.date.getTime() - this.date.getTime()) >= 5000; }

}


