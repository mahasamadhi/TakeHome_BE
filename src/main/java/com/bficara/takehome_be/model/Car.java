package com.bficara.takehome_be.model;

public class Car {

    private int year;
    private String make;
    private String model;
    private double price;

    public Car() {
    }

    public Car(int year, String make, String model, double price) {
        this.year = year;
        this.make = make;
        this.model = model;
        this.price = price;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Car{" +
                "year=" + year +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", price=" + price +
                '}';
    }
}
