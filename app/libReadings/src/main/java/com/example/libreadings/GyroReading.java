package com.example.libreadings;


public class GyroReading {


    /*
     *  Properties
     */
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

}


