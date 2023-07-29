package com.bficara.takehome_be;

import com.bficara.takehome_be.car.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.PDFCreator;
import tools.PdfReportOptions;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// This is the main controller class that handles car report-related requests
@RestController
@RequestMapping("/api")
public class CarReportController {

    // Services are injected here for generating reports and selecting data sources
    private final CarService carService;
    private final CarDatasourceService carDataSourceService;

    // This constructor is using dependency injection to set the services
    @Autowired
    public CarReportController(CarService carService, CarDatasourceService carDataSourceService) {
        this.carService = carService;
        this.carDataSourceService = carDataSourceService;
    }

    // Endpoint for generating a report for a given year
    @GetMapping("/report/year/{year}")
    public ResponseEntity<ByteArrayResource> getReportByYear(@PathVariable int year) {
        PdfReportOptions options = new PdfReportOptions(true, "Car Details", "year", "caryear");
        try {
            // The data source is selected by the service, abstracting this complexity from the controller
            ICarDataSource dataSource = carDataSourceService.getDataSource("h2");
            carService.setDataSource(dataSource);
            byte[] data = carService.generateReportByYear(year, options);
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

    // Endpoint for generating a report based on CSV data on the filesystem
    @GetMapping("/report/csv")
    public ResponseEntity<ByteArrayResource> generateCSVReport() {
        try {
            //Class to create pdfs using iText package
            PdfReportOptions options = new PdfReportOptions(true, "Car List", "year","caryear");
            PDFCreator pdf = new PDFCreator();
            // The CSV data source is selected here
            ICarDataSource dataSource = carDataSourceService.getDataSource("csv");
            carService.setDataSource(dataSource);

            List<Car> carsList = carService.getData();
            // Sorting the car list by year before creating the PDF
            Collections.sort(carsList, Comparator.comparing(Car::getYear));
            //method to create a byte array representing a pdf file
            byte[] data = pdf.createPdfToByteArray(carsList, options);
            ByteArrayResource resource = new ByteArrayResource(data);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=csv_car_report.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(data.length)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/report/all")
    public ResponseEntity<ByteArrayResource> getAllCarsReport() {
        PdfReportOptions options = new PdfReportOptions(true, "Car Details", "year", "caryear");
        try {
            // The data source is selected by the service, abstracting this complexity from the controller
            ICarDataSource dataSource = carDataSourceService.getDataSource("database");
            PDFCreator pdf = new PDFCreator();
            carService.setDataSource(dataSource);
            List<Car> cars = carService.getData();
            // sort the car list by year
            Collections.sort(cars, Comparator.comparing(Car::getYear));
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
}