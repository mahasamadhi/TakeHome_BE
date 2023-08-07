package com.bficara.takehome_be.controller;

import java.util.List;
import java.util.Map;

import com.bficara.takehome_be.datasource.H2CarRepository;

import com.bficara.takehome_be.datasource.CsvCarDataSource;
import com.bficara.takehome_be.model.Car;
import com.bficara.takehome_be.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.bficara.takehome_be.report.GroupByOption;
import com.bficara.takehome_be.report.CarPDFCreator;
import com.bficara.takehome_be.report.PdfReportOptions;


// This is the main controller class that handles car report-related requests
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/reportapi/h2/")
public class H2CarController {

    // Services are injected here for generating reports and selecting data sources
    private final CarService carService;
    private final CarPDFCreator carPDFCreator;
    private final H2CarRepository h2CarRepository;
    private final CsvCarDataSource csvCarDataSource;

    // This constructor is using dependency injection to set the services
    @Autowired
    public H2CarController(@Qualifier("carServiceWithH2") CarService carService, CarPDFCreator carPDFCreator,
                           CsvCarDataSource csvCarDataSource,H2CarRepository h2CarRepository) {
        this.carService = carService;
        this.carPDFCreator = carPDFCreator;
        this.h2CarRepository = h2CarRepository;
        this.csvCarDataSource = csvCarDataSource;
    }

//datasource = H2

    //grouping
    @GetMapping("report/group/{groupBy}/{sort}")
    public ResponseEntity<ByteArrayResource> getH2CarReport(@PathVariable String groupBy, @PathVariable String sort) {
        GroupByOption groupOption = GroupByOption.valueOf(groupBy.toUpperCase());
        PdfReportOptions options = new PdfReportOptions(true, "Car List by " + groupBy, groupOption, sort, 1.07);
        try {
            List<Car> cars = carService.getAll();
            byte[] data = carPDFCreator.createPdfToByteArray( cars, options);
            ByteArrayResource resource = new ByteArrayResource(data);
            // Building the response with the created PDF data
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(data.length)
                    .body(resource);
        } catch (Exception e) {
            // If any error occurs, print the stack trace and return a 500 status code
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    //filtering
    @GetMapping("report/year/{year}/{sortDir}")
    public ResponseEntity<ByteArrayResource> getH2ReportByYear(@PathVariable int year, @PathVariable String sortDir) {
        PdfReportOptions options = new PdfReportOptions(
                true, "Car List by Year", GroupByOption.YEAR,sortDir,1.07);
        try {
            List<Car> cars = carService.getAllByYear(year);
            byte[] data = carPDFCreator.createPdfToByteArray( cars, options);
            ByteArrayResource resource = new ByteArrayResource(data);
            // Building the response with the created PDF data
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(data.length)
                    .body(resource);
        } catch (Exception e) {
            // If any error occurs, print the stack trace and return a 500 status code
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("report/make/{make}/{sortDir}")
    public ResponseEntity<ByteArrayResource> getH2ReportByYear(@PathVariable String make, @PathVariable String sortDir) {
        PdfReportOptions options = new PdfReportOptions(
                true, "Car List by Make", GroupByOption.MAKE,sortDir,1.07);
        try {
            List<Car> cars = carService.getAllByMake(make);
            byte[] data = carPDFCreator.createPdfToByteArray( cars, options);
            ByteArrayResource resource = new ByteArrayResource(data);
            // Building the response with the created PDF data
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(data.length)
                    .body(resource);
        } catch (Exception e) {
            // If any error occurs, print the stack trace and return a 500 status code
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("report/price/{price}/{sortDir}")
    public ResponseEntity<ByteArrayResource> getH2ReportByPrice(@PathVariable int price, @PathVariable String sortDir) {
        PdfReportOptions options = new PdfReportOptions(
                true, "Car List by Price", GroupByOption.MAKE,sortDir,1.07);
        try {
            // The data source is selected by the service, abstracting this complexity from the controller
            List<Car> cars = carService.getAllLessThanPrice(price);
            byte[] data = carPDFCreator.createPdfToByteArray( cars, options);
            ByteArrayResource resource = new ByteArrayResource(data);
            // Building the response with the created PDF data
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(data.length)
                    .body(resource);
        } catch (Exception e) {
            // If any error occurs, print the stack trace and return a 500 status code
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    //inserting CSV file contents into the H2 database
    @PostMapping("insertCsv")
    public ResponseEntity<String> InsertCsvToH2(@RequestParam("file") MultipartFile file) {

        try {
            //get the csv file and process it using the csvDatasource class
            csvCarDataSource.setFile(file);
            csvCarDataSource.processData();
            List<Car> cars = csvCarDataSource.getAll();
            // Save the cars into your H2 database
            h2CarRepository.saveAll(cars);

            return ResponseEntity.ok().body("Success");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    //deleting all records from the database
    @DeleteMapping("deleteAll")
    public ResponseEntity<String> deleteAllFromH2() {
        h2CarRepository.deleteAll();
        return ResponseEntity.ok().body("Success");
    }

    //endpoints for populating select elements on the front-end
    @GetMapping("Car/makeOptions")
    public Map<String, List<String>> getMakeOptions() {
        return carService.getMakeOptions();
    }
    @GetMapping("Car/yearOptions")
    public Map<String, List<String>> getYearOptions() {
        return carService.getYearOptions();
    }

}