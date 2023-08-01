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

    @PostMapping("/report/csvToPdf/byYear")
    public ResponseEntity<byte[]> GetCsvCarReportByYear(@RequestParam("file") MultipartFile file) {

        try {
            CsvCarDataSource dataSource = (CsvCarDataSource) carDatasourceService.getDataSource("csv");
            dataSource.setFile(file);
            dataSource.processData();
            carService.setDataSource(dataSource);
            List<Car> cars = carService.getAll();
            PdfReportOptions options = new PdfReportOptions(
                    true, "Car List by Year","year", GroupByOption.YEAR, 1.07);
            Collections.sort(cars, Comparator.comparing(Car::getYear));
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

    @PostMapping("/report/csvToPdf/byMake")
    public ResponseEntity<byte[]> GetCsvCarReportByMake(@RequestParam("file") MultipartFile file) {

        try {
            CsvCarDataSource dataSource = (CsvCarDataSource) carDatasourceService.getDataSource("csv");
            dataSource.setFile(file);
            dataSource.processData();
            carService.setDataSource(dataSource);
            List<Car> cars = carService.getAll();
            PdfReportOptions options = new PdfReportOptions(
                    true,"Car List by Make","year", GroupByOption.MAKE,1.07);
            Collections.sort(cars, Comparator.comparing(Car::getYear));
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
    @GetMapping("/report/h2/year/{year}")
    public ResponseEntity<ByteArrayResource> getH2ReportByYear(@PathVariable int year) {
        PdfReportOptions options = new PdfReportOptions(
                true, "Car List by Year", "year", GroupByOption.YEAR,1.07);
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

    // Endpoint for generating a report based on CSV data on the filesystem

    //get all from H2 database
    @GetMapping("/report/h2/groupByYear")
    public ResponseEntity<ByteArrayResource> getCarReportByYear() {
        PdfReportOptions options = new PdfReportOptions(
                true, "Car Details", "year", GroupByOption.YEAR,1.07);
        try {
            // The data source is selected by the service, abstracting this complexity from the controller
            ICarDataSource dataSource = carDatasourceService.getDataSource("h2");
            CarPDFCreator pdf = new CarPDFCreator();
            carService.setDataSource(dataSource);
            List<Car> cars = carService.getAll();
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

    @GetMapping("/report/h2/groupByMake")
    public ResponseEntity<ByteArrayResource> getH2CarReportByMake() {
        PdfReportOptions options = new PdfReportOptions(
                true, "Car Details", "year", GroupByOption.MAKE,1.07);
        try {
            // The data source is selected by the service, abstracting this complexity from the controller
            ICarDataSource dataSource = carDatasourceService.getDataSource("h2");
            CarPDFCreator pdf = new CarPDFCreator();
            carService.setDataSource(dataSource);
            List<Car> cars = carService.getAll();
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
    @GetMapping("/report/csv")
    public ResponseEntity<ByteArrayResource> getCsvCarReportbyYear() {
        try {
            //Class to create pdfs using iText package
            PdfReportOptions options = new PdfReportOptions(
                    true, "Car List", "year",GroupByOption.YEAR,1.07);
            CarPDFCreator pdf = new CarPDFCreator();
            // The CSV data source is selected here
            ICarDataSource dataSource = carDatasourceService.getDataSource("csv");
            carService.setDataSource(dataSource);
            List<Car> carsList = carService.getAll();
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

}