package com.bficara.takehome_be.car;

import com.bficara.takehome_be.car.Obsolete.FilesystemDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarDatasourceService {
    private final H2CarDatasource h2CarDatasource;
    private final FilesystemDataSource filesystemDataSource;
    private final CsvCarDataSource csvCarDataSource;

    @Autowired
    public CarDatasourceService(H2CarDatasource h2CarDatasource,
                                FilesystemDataSource filesystemDataSource,
                                CsvCarDataSource csvCarDataSource) {
        this.h2CarDatasource = h2CarDatasource;
        this.filesystemDataSource = filesystemDataSource;
        this.csvCarDataSource = csvCarDataSource;
    }

    public ICarDataSource getDataSource(String sourceType) {
        switch(sourceType) {
            case "h2":
                return h2CarDatasource;
            case "filesystem":
                return filesystemDataSource;
            case "csv":
                return csvCarDataSource;
            default:
                throw new IllegalArgumentException("Invalid sourceType: " + sourceType);
        }
    }
}
