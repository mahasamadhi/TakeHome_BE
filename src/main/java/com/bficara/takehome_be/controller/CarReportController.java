package com.bficara.takehome_be.controller;
import com.bficara.takehome_be.datasource.H2CarRepository;

import com.bficara.takehome_be.datasource.CsvCarDataSource;
import com.bficara.takehome_be.datasource.ICarDataSource;
import com.bficara.takehome_be.model.Car;
import com.bficara.takehome_be.service.CarDatasourceService;
import com.bficara.takehome_be.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.bficara.takehome_be.tools.GroupByOption;
import com.bficara.takehome_be.report.CarPDFCreator;
import com.bficara.takehome_be.tools.PdfReportOptions;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

// This is the main controller class that handles car report-related requests
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
public class CarReportController {

    // Services are injected here for generating reports and selecting data sources
    private final CarService carService;
    private final CarDatasourceService carDatasourceService;


    // This constructor is using dependency injection to set the services
    @Autowired
    public CarReportController(CarService carService, CarDatasourceService carDatasourceService) {
        this.carService = carService;
        this.carDatasourceService = carDatasourceService;
    }

    // GROUP BY YEAR AND MAKE

        //datasource = CSV

            //grouping
            @PostMapping("/report/csv/groupByYear/{sort}")
            public ResponseEntity<byte[]> GetCsvCarReportByYear(@RequestParam("file") MultipartFile file,@PathVariable String sort) {

                try {
                    CsvCarDataSource dataSource = (CsvCarDataSource) carDatasourceService.getDataSource("csv");
                    dataSource.setFile(file);
                    dataSource.processData();
                    carService.setDataSource(dataSource);
                    List<Car> cars = carService.getAll();
                    PdfReportOptions options = new PdfReportOptions(
                            true, "Car List by Year","year", GroupByOption.YEAR, sort,1.07);
                    CarPDFCreator pdf = new CarPDFCreator();
                    byte[] doc = pdf.createPdfToByteArray( cars, options);

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_PDF);
                    // Here you can set the name of the PDF that will be downloaded
                    headers.setContentDispositionFormData("filename", "report.pdf");
                    return new ResponseEntity<>(doc, headers, HttpStatus.OK);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage().getBytes());
                }
            }

            @PostMapping("/report/csv/groupByMake/{sort}")
            public ResponseEntity<byte[]> GetCsvCarReportByMake(@RequestParam("file") MultipartFile file,@PathVariable String sort) {

                try {
                    CsvCarDataSource dataSource = (CsvCarDataSource) carDatasourceService.getDataSource("csv");
                    dataSource.setFile(file);
                    dataSource.processData();
                    carService.setDataSource(dataSource);
                    List<Car> cars = carService.getAll();
                    PdfReportOptions options = new PdfReportOptions(
                            true,"Car List by Make","year", GroupByOption.MAKE,sort,1.07);
                    CarPDFCreator pdf = new CarPDFCreator();
                    byte[] doc = pdf.createPdfToByteArray( cars, options);

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_PDF);
                    // Here you can set the name of the PDF that will be downloaded
                    headers.setContentDispositionFormData("filename", "report.pdf");
                    return new ResponseEntity<>(doc, headers, HttpStatus.OK);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage().getBytes());
                }
            }

            //filtering
            @PostMapping("/report/csv/price/{price}/{sortDir}")
            public ResponseEntity<byte[]> getH2ReportByPrice(@RequestParam("file") MultipartFile file, @PathVariable int price, @PathVariable String sortDir) {
                PdfReportOptions options = new PdfReportOptions(
                        true, "Car List by Price", "make", GroupByOption.MAKE,sortDir,1.07);
                try {
                    CsvCarDataSource dataSource = (CsvCarDataSource) carDatasourceService.getDataSource("csv");
                    dataSource.setFile(file);
                    dataSource.processData();
                    carService.setDataSource(dataSource);
                    CarPDFCreator pdf = new CarPDFCreator();
                    List<Car> cars = carService.getAllLessThanPrice(price);
                    byte[] doc = pdf.createPdfToByteArray( cars, options);
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_PDF);
                    // Here you can set the name of the PDF that will be downloaded
                    headers.setContentDispositionFormData("filename", "report.pdf");
                    return new ResponseEntity<>(doc, headers, HttpStatus.OK);
                } catch (Exception e) {
                    // If any error occurs, print the stack trace and return a 500 status code
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                }
            }

        //datasource = H2

            //grouping
            @GetMapping("/report/h2/groupByYear/{sort}")
            public ResponseEntity<ByteArrayResource> getCarReportByYear(@PathVariable String sort) {
            PdfReportOptions options = new PdfReportOptions(
                    true, "Car Details", "year", GroupByOption.YEAR,sort,1.07);
            try {
                // The data source is selected by the service, abstracting this complexity from the controller
                ICarDataSource dataSource = carDatasourceService.getDataSource("h2");
                CarPDFCreator pdf = new CarPDFCreator();
                carService.setDataSource(dataSource);
                List<Car> cars = carService.getAll();
                byte[] data = pdf.createPdfToByteArray( cars, options);
                ByteArrayResource resource = new ByteArrayResource(data);
                // Building the response with the created PDF data
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=car_report_all" + ".pdf")
                        .contentType(MediaType.APPLICATION_PDF)
                        .contentLength(data.length)
                        .body(resource);

            } catch (Exception e) {
                // If any error occurs, print the stack trace and return a 500 status code
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }
            @GetMapping("/report/h2/groupByMake/{sort}")
            public ResponseEntity<ByteArrayResource> getH2CarReportByMake(@PathVariable String sort) {
            PdfReportOptions options = new PdfReportOptions(
                    true, "Car Details", "year", GroupByOption.MAKE,sort,1.07);
            try {
                // The data source is selected by the service, abstracting this complexity from the controller
                ICarDataSource dataSource = carDatasourceService.getDataSource("h2");
                CarPDFCreator pdf = new CarPDFCreator();
                carService.setDataSource(dataSource);
                List<Car> cars = carService.getAll();
                byte[] data = pdf.createPdfToByteArray( cars, options);
                ByteArrayResource resource = new ByteArrayResource(data);
                // Building the response with the created PDF data
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=car_report_all" + ".pdf")
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
            @GetMapping("/report/h2/year/{year}/{sortDir}")
            public ResponseEntity<ByteArrayResource> getH2ReportByYear(@PathVariable int year, @PathVariable String sortDir) {
                PdfReportOptions options = new PdfReportOptions(
                        true, "Car List by Year", "year", GroupByOption.YEAR,sortDir,1.07);
                try {
                    // The data source is selected by the service, abstracting this complexity from the controller
                    ICarDataSource dataSource = carDatasourceService.getDataSource("h2");
                    carService.setDataSource(dataSource);
                    CarPDFCreator pdf = new CarPDFCreator();
                    List<Car> cars = carService.getAllByYear(year);
                    byte[] data = pdf.createPdfToByteArray( cars, options);
                    ByteArrayResource resource = new ByteArrayResource(data);
                    // Building the response with the created PDF data
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=car_report_" + year + ".pdf")
                            .contentType(MediaType.APPLICATION_PDF)
                            .contentLength(data.length)
                            .body(resource);
                } catch (Exception e) {
                    // If any error occurs, print the stack trace and return a 500 status code
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                }
            }
            @GetMapping("/report/h2/make/{make}/{sortDir}")
            public ResponseEntity<ByteArrayResource> getH2ReportByYear(@PathVariable String make, @PathVariable String sortDir) {
                PdfReportOptions options = new PdfReportOptions(
                        true, "Car List by Year", "make", GroupByOption.MAKE,sortDir,1.07);
                try {
                    // The data source is selected by the service, abstracting this complexity from the controller
                    ICarDataSource dataSource = carDatasourceService.getDataSource("h2");
                    carService.setDataSource(dataSource);
                    CarPDFCreator pdf = new CarPDFCreator();
                    List<Car> cars = carService.getAllByMake(make);
                    byte[] data = pdf.createPdfToByteArray( cars, options);
                    ByteArrayResource resource = new ByteArrayResource(data);
                    // Building the response with the created PDF data
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=car_report_" + make + ".pdf")
                            .contentType(MediaType.APPLICATION_PDF)
                            .contentLength(data.length)
                            .body(resource);
                } catch (Exception e) {
                    // If any error occurs, print the stack trace and return a 500 status code
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                }
            }
            @GetMapping("/report/h2/price/{price}/{sortDir}")
            public ResponseEntity<ByteArrayResource> getH2ReportByPrice(@PathVariable int price, @PathVariable String sortDir) {
                PdfReportOptions options = new PdfReportOptions(
                        true, "Car List by Price", "make", GroupByOption.MAKE,sortDir,1.07);
                try {
                    // The data source is selected by the service, abstracting this complexity from the controller
                    ICarDataSource dataSource = carDatasourceService.getDataSource("h2");
                    carService.setDataSource(dataSource);
                    CarPDFCreator pdf = new CarPDFCreator();
                    List<Car> cars = carService.getAllLessThanPrice(price);
                    byte[] data = pdf.createPdfToByteArray( cars, options);
                    ByteArrayResource resource = new ByteArrayResource(data);
                    // Building the response with the created PDF data
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=car_report_" + "<" + price + ".pdf")
                            .contentType(MediaType.APPLICATION_PDF)
                            .contentLength(data.length)
                            .body(resource);
                } catch (Exception e) {
                    // If any error occurs, print the stack trace and return a 500 status code
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                }
            }


    //datasource = filesystem
    //todo


    //Insert (via CSV) and Delete for Database
    @PostMapping("/h2/insertCsv")
    public ResponseEntity<String> InsertCsvToH2(@RequestParam("file") MultipartFile file) {

        try {
            CsvCarDataSource dataSource = (CsvCarDataSource) carDatasourceService.getDataSource("csv");
            dataSource.setFile(file);
            dataSource.processData();
            carService.setDataSource(dataSource);
            List<Car> cars = carService.getAll();

            // Inject H2CarRepository into your controller
            H2CarRepository h2CarRepository = (H2CarRepository) carDatasourceService.getDataSource("h2");

            // Save the cars into your H2 database
            h2CarRepository.saveAll(cars);

            return ResponseEntity.ok().body("Success");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/h2/deleteAll")
    public ResponseEntity<String> deleteAllFromH2() {
        H2CarRepository h2CarRepository = (H2CarRepository) carDatasourceService.getDataSource("h2");
        h2CarRepository.deleteAll();
        return ResponseEntity.ok().body("Success");
    }


    // Endpoint for generating a report for a given year


    // Endpoint for generating a report based on CSV data on the filesystem
    //todo



    @GetMapping("/h2/Car/makeOptions")
    public Map<String, List<String>> getMakeOptions() {
        H2CarRepository repo = (H2CarRepository) carDatasourceService.getDataSource("h2");
        return repo.getMakeOptions();
    }

    @GetMapping("/h2/Car/yearOptions")
    public Map<String, List<String>> getYearOptions() {
        H2CarRepository repo = (H2CarRepository) carDatasourceService.getDataSource("h2");
        return repo.getYearOptions();
    }

}