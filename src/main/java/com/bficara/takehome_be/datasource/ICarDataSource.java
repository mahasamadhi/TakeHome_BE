package com.bficara.takehome_be.datasource;

import com.bficara.takehome_be.model.Car;

import java.util.List;

public interface ICarDataSource {

    List<Car> getAll();
    List<Car> getAllByYear(int year);
    List<Car> getAllByMake(String make);
}
