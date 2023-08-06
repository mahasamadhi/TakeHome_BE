package com.bficara.takehome_be.model;

//The primary object of the app, the car
public class Car {

    private int year;
    private String make;
    private String model;
    private double msrp;
    private double price;

    public Car() {
    }

    public Car(int year, String make, String model, double msrp) {
        this.year = year;
        this.make = make;
        this.model = model;
        this.msrp = msrp;
    }


    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getMsrp() {
        return msrp;
    }

    public void setMsrp(double msrp) {
        this.msrp = msrp;
    }

    @Override
    public String toString() {
        return "Car{" +
                "year=" + year +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", msrp=" + msrp +
                '}';
    }
}
