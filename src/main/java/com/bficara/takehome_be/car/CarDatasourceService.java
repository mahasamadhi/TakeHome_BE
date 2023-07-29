package com.bficara.takehome_be.car;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarDatasourceService {
    private final H2CarDatasource h2CarDatasource;
    private final FilesystemDataSource filesystemDataSource;
    private final HttpDataSource httpCarDataSource;

    @Autowired
    public CarDatasourceService(H2CarDatasource h2CarDatasource,
                                FilesystemDataSource filesystemDataSource,
                                HttpDataSource httpCarDataSource) {
        this.h2CarDatasource = h2CarDatasource;
        this.filesystemDataSource = filesystemDataSource;
        this.httpCarDataSource = httpCarDataSource;
    }

    public ICarDataSource getDataSource(String sourceType) {
        switch(sourceType) {
            case "h2":
                return h2CarDatasource;
            case "csv":
                return filesystemDataSource;
            case "http":
                return httpCarDataSource;
            default:
                throw new IllegalArgumentException("Invalid sourceType: " + sourceType);
        }
    }
}
