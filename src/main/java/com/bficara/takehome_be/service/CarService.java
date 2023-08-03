package com.bficara.takehome_be.service;

import com.bficara.takehome_be.datasource.ICarDataSource;
import com.bficara.takehome_be.model.Car;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class CarService {

    private ICarDataSource dataSource;

    @Autowired
    public CarService(ICarDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setDataSource(ICarDataSource ds) {
        this.dataSource = ds;
    }

    public List<Car> getAll() {
        return dataSource.getAll();
    }

    public List<Car> getAllByYear(int year) {
            return dataSource.getAllByYear(year);
        }

    public List<Car> getAllByMake(String make) {
        return dataSource.getAllByMake(make);
    }

    public List<Car> getAllLessThanPrice(int price) {
        return dataSource.getAllLessThanPrice(price);
    }


    public Map<String, List<String>> getMakeOptions() {return dataSource.getMakeOptions();}
    public Map<String, List<String>> getYearOptions() {return dataSource.getYearOptions();}
}

