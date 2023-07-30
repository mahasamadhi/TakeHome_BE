package com.bficara.takehome_be;

import com.bficara.takehome_be.car.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.GroupByOption;
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
    private final CarDatasourceService carDatasourceService;

    // This constructor is using dependency injection to set the services
    @Autowired
    public CarReportController(CarService carService, CarDatasourceService carDatasourceService) {
        this.carService = carService;
        this.carDatasourceService = carDatasourceService;
    }

    @PostMapping("/csvToPdf/byYear")
    public ResponseEntity<byte[]> GetCsvCarReportByYear(@RequestParam("file") MultipartFile file) {

        try {
            CsvCarDataSource dataSource = (CsvCarDataSource) carDatasourceService.getDataSource("csv");
            dataSource.setFile(file);
            dataSource.processData();
            carService.setDataSource(dataSource);
            List<Car> cars = carService.getAll();
            PdfReportOptions options = new PdfReportOptions(true,"Car List by Year","year", GroupByOption.YEAR);
            Collections.sort(cars, Comparator.comparing(Car::getYear));
            PDFCreator pdf = new PDFCreator();
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

    @PostMapping("/csvToPdf/byMake")
    public ResponseEntity<byte[]> GetCsvCarReportByMake(@RequestParam("file") MultipartFile file) {

        try {
            CsvCarDataSource dataSource = (CsvCarDataSource) carDatasourceService.getDataSource("csv");
            dataSource.setFile(file);
            dataSource.processData();
            carService.setDataSource(dataSource);
            List<Car> cars = carService.getAll();
            PdfReportOptions options = new PdfReportOptions(true,"Car List by Year","year", GroupByOption.MAKE);
            Collections.sort(cars, Comparator.comparing(Car::getYear));
            PDFCreator pdf = new PDFCreator();
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

    // Endpoint for generating a report for a given year
    @GetMapping("/report/year/{year}")
    public ResponseEntity<ByteArrayResource> getReportByYear(@PathVariable int year) {
        PdfReportOptions options = new PdfReportOptions(true, "Car Details", "year", GroupByOption.YEAR);
        try {
            // The data source is selected by the service, abstracting this complexity from the controller
            ICarDataSource dataSource = carDatasourceService.getDataSource("h2");
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

    //get all from H2 database
    @GetMapping("/report/all/groupByYear")
    public ResponseEntity<ByteArrayResource> getCarReportByYear() {
        PdfReportOptions options = new PdfReportOptions(true, "Car Details", "year", GroupByOption.YEAR);
        try {
            // The data source is selected by the service, abstracting this complexity from the controller
            ICarDataSource dataSource = carDatasourceService.getDataSource("h2");
            PDFCreator pdf = new PDFCreator();
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

    @GetMapping("/report/all/groupByMake")
    public ResponseEntity<ByteArrayResource> getH2CarReportByMake() {
        PdfReportOptions options = new PdfReportOptions(true, "Car Details", "year", GroupByOption.MAKE);
        try {
            // The data source is selected by the service, abstracting this complexity from the controller
            ICarDataSource dataSource = carDatasourceService.getDataSource("h2");
            PDFCreator pdf = new PDFCreator();
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

    @GetMapping("/report/csv")
    public ResponseEntity<ByteArrayResource> getCsvCarReportbyYear() {
        try {
            //Class to create pdfs using iText package
            PdfReportOptions options = new PdfReportOptions(true, "Car List", "year",GroupByOption.YEAR);
            PDFCreator pdf = new PDFCreator();
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