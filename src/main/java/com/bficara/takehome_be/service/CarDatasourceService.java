package com.bficara.takehome_be.service;

import com.bficara.takehome_be.datasource.FilesystemDataSource;
import com.bficara.takehome_be.datasource.CsvCarDataSource;
import com.bficara.takehome_be.datasource.H2CarRepository;
import com.bficara.takehome_be.datasource.ICarDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarDatasourceService {
    private final H2CarRepository h2CarRepository;
    private final FilesystemDataSource filesystemDataSource;
    private final CsvCarDataSource csvCarDataSource;

    @Autowired
    public CarDatasourceService(H2CarRepository h2CarRepository,
                                FilesystemDataSource filesystemDataSource,
                                CsvCarDataSource csvCarDataSource) {
        this.h2CarRepository = h2CarRepository;
        this.filesystemDataSource = filesystemDataSource;
        this.csvCarDataSource = csvCarDataSource;
    }

    public ICarDataSource getDataSource(String sourceType) {
        switch(sourceType) {
            case "h2":
                return h2CarRepository;
            case "fs":
                return filesystemDataSource;
            case "csv":
                return csvCarDataSource;
            default:
                throw new IllegalArgumentException("Invalid sourceType: " + sourceType);
        }
    }
}
