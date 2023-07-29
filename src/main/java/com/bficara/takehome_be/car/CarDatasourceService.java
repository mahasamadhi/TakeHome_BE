package com.bficara.takehome_be.car;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarDatasourceService {
    private final H2CarDatasource h2CarDatasource;
    private final CsvCarDataSource csvCarDataSource;
    private final HttpDataSource httpCarDataSource;

    @Autowired
    public CarDatasourceService(H2CarDatasource h2CarDatasource,
                                CsvCarDataSource csvCarDataSource,
                                HttpDataSource httpCarDataSource) {
        this.h2CarDatasource = h2CarDatasource;
        this.csvCarDataSource = csvCarDataSource;
        this.httpCarDataSource = httpCarDataSource;
    }

    public ICarDataSource getDataSource(String sourceType) {
        switch(sourceType) {
            case "h2":
                return h2CarDatasource;
            case "csv":
                return csvCarDataSource;
            case "http":
                return httpCarDataSource;
            default:
                throw new IllegalArgumentException("Invalid sourceType: " + sourceType);
        }
    }
}
