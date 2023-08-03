package com.bficara.takehome_be.datasource;

import com.bficara.takehome_be.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CarServiceConfig {

    private final CsvCarDataSource csvCarDataSource;
    private final FilesystemDataSource filesystemDataSource;
    private final H2CarRepository h2CarRepository;

    @Autowired
    public CarServiceConfig(CsvCarDataSource csvCarDataSource,
                            FilesystemDataSource filesystemDataSource,
                            H2CarRepository h2CarRepository) {
        this.csvCarDataSource = csvCarDataSource;
        this.filesystemDataSource = filesystemDataSource;
        this.h2CarRepository = h2CarRepository;
    }

    @Bean
    @Qualifier("carServiceWithCsv")
    public CarService carServiceWithCsv() {
        return new CarService(csvCarDataSource);
    }

    @Bean
    @Qualifier("carServiceWithFilesystem")
    public CarService carServiceWithFilesystem() {
        return new CarService(filesystemDataSource);
    }

    @Bean
    @Qualifier("carServiceWithH2")
    public CarService carServiceWithH2() {
        return new CarService(h2CarRepository);
    }
}
