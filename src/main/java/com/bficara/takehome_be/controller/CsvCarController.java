package com.bficara.takehome_be.controller;
import com.bficara.takehome_be.datasource.H2CarRepository;

import com.bficara.takehome_be.datasource.CsvCarDataSource;
import com.bficara.takehome_be.datasource.ICarDataSource;
import com.bficara.takehome_be.model.Car;
import com.bficara.takehome_be.model.ReportOptions;
import com.bficara.takehome_be.service.CarDatasourceService;
import com.bficara.takehome_be.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

import java.util.List;
import java.util.Map;

// This is the main controller class that handles car report-related requests
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/csv/")
public class CsvCarController {

    // Services are injected here for generating reports and selecting data sources
    private final CarService carService;
    private final CarPDFCreator carPDFCreator;

    // This constructor is using dependency injection to set the services
    @Autowired
    public CsvCarController(@Qualifier("carServiceWithCsv") CarService carService, CarPDFCreator carPDFCreator) {
        this.carService = carService;
        this.carPDFCreator = carPDFCreator;
    }

    //datasource = CSV

        //grouping
        @PostMapping(value = "/report/group", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<byte[]> GroupCSV(@ModelAttribute ReportOptions reportOptions) {
        try {
            CsvCarDataSource dataSource = (CsvCarDataSource) carDatasourceService.getDataSource("csv");
            dataSource.setFile(reportOptions.getFile());
            dataSource.processData();
            carService.setDataSource(dataSource);
            List<Car> cars = carService.getAll();
            PdfReportOptions options = new PdfReportOptions(
                    true,
                    "Car List grouped by " + reportOptions.getGroupBy(),
                    GroupByOption.valueOf(reportOptions.getGroupBy().toUpperCase()),
                    reportOptions.getSort(),
                    1.07
            );
            byte[] doc = carPDFCreator.createPdfToByteArray(cars, options);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            return new ResponseEntity<>(doc, headers, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage().getBytes());
            }
        }

        //filtering
        @PostMapping("/report/year/{year}/{sortDir}")
        public ResponseEntity<byte[]> AllByYearCSV(@RequestParam("file") MultipartFile file, @PathVariable int year, @PathVariable String sortDir) {
    PdfReportOptions options = new PdfReportOptions(
            true, "Car List:" + year + " models", GroupByOption.YEAR,sortDir,1.07);
    try {
        CsvCarDataSource dataSource = (CsvCarDataSource) carDatasourceService.getDataSource("csv");
        dataSource.setFile(file);
        dataSource.processData();
        carService.setDataSource(dataSource);
        List<Car> cars = carService.getAllByYear(year);
        byte[] doc = carPDFCreator.createPdfToByteArray( cars, options);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        return new ResponseEntity<>(doc, headers, HttpStatus.OK);
    } catch (Exception e) {
        // If any error occurs, print the stack trace and return a 500 status code
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}

        @PostMapping("/report/make/{make}/{sortDir}")
        public ResponseEntity<byte[]> AllByMakeCSV(@RequestParam("file") MultipartFile file, @PathVariable String make, @PathVariable String sortDir) {
            PdfReportOptions options = new PdfReportOptions(
                    true,  make +" Car List", GroupByOption.MAKE,sortDir,1.07);
            try {
                CsvCarDataSource dataSource = (CsvCarDataSource) carDatasourceService.getDataSource("csv");
                dataSource.setFile(file);
                dataSource.processData();
                carService.setDataSource(dataSource);
                List<Car> cars = carService.getAllByMake(make);
                byte[] doc = carPDFCreator.createPdfToByteArray( cars, options);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                return new ResponseEntity<>(doc, headers, HttpStatus.OK);
            } catch (Exception e) {
                // If any error occurs, print the stack trace and return a 500 status code
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }

        @PostMapping("/report/price/{price}/{sortDir}")
        public ResponseEntity<byte[]> AllByPriceCSV(@RequestParam("file") MultipartFile file, @PathVariable int price, @PathVariable String sortDir) {
        PdfReportOptions options = new PdfReportOptions(
            true, "Car List by Price", GroupByOption.MAKE,sortDir,1.07);
        try {
            CsvCarDataSource dataSource = (CsvCarDataSource) carDatasourceService.getDataSource("csv");
            dataSource.setFile(file);
            dataSource.processData();
            carService.setDataSource(dataSource);
            List<Car> cars = carService.getAllLessThanPrice(price);
            byte[] doc = carPDFCreator.createPdfToByteArray( cars, options);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            // Here you can set the name of the PDF that will be downloaded
            return new ResponseEntity<>(doc, headers, HttpStatus.OK);
        } catch (Exception e) {
            // If any error occurs, print the stack trace and return a 500 status code
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }

       //get year and make options

        @GetMapping("Car/makeOptions")
        public Map<String, List<String>> getMakeOptions() {
            return carService.getMakeOptions();
        }
        @GetMapping("Car/yearOptions")
        public Map<String, List<String>> getYearOptions() {
            return carService.getYearOptions();
        }

}