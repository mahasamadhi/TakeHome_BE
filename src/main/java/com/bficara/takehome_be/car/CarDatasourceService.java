package com.bficara.takehome_be.car;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarDatasourceService {
    private final DatabaseCarDataSource databaseCarDataSource;
    private final CsvCarDataSource csvCarDataSource;
    private final HttpDataSource httpCarDataSource;

    @Autowired
    public CarDatasourceService(DatabaseCarDataSource databaseCarDataSource,
                                CsvCarDataSource csvCarDataSource,
                                HttpDataSource httpCarDataSource) {
        this.databaseCarDataSource = databaseCarDataSource;
        this.csvCarDataSource = csvCarDataSource;
        this.httpCarDataSource = httpCarDataSource;
    }

    public ICarDataSource getDataSource(String sourceType) {
        switch(sourceType) {
            case "database":
                return databaseCarDataSource;
            case "csv":
                return csvCarDataSource;
            case "http":
                return httpCarDataSource;
            default:
                throw new IllegalArgumentException("Invalid sourceType: " + sourceType);
        }
    }
}
