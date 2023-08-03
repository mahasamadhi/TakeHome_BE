package com.bficara.takehome_be.datasource;

import com.bficara.takehome_be.model.Car;

import java.util.List;
import java.util.Map;

public interface ICarDataSource {

    List<Car> getAll();
    List<Car> getAllByYear(int year);
    List<Car> getAllByMake(String make);
    List<Car> getAllLessThanPrice(int price);

    Map<String, List<String>> getMakeOptions();
    Map<String, List<String>> getYearOptions();
}
