package com.bficara.takehome_be.controller;

import com.bficara.takehome_be.datasource.CsvCarDataSource;
import com.bficara.takehome_be.model.Car;
import com.bficara.takehome_be.model.ReportOptions;
import com.bficara.takehome_be.service.CarService;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.bficara.takehome_be.tools.GroupByOption;
import com.bficara.takehome_be.report.CarPDFCreator;
import com.bficara.takehome_be.tools.PdfReportOptions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/reportapi/fs/")
public class FsCarController {
    private final CarService carService;
    private final CarPDFCreator carPDFCreator;
    @Autowired
    public FsCarController(@Qualifier("carServiceWithFilesystem") CarService carService, CarPDFCreator carPDFCreator) {
        this.carService = carService;
        this.carPDFCreator = carPDFCreator;
    }

    //datasource = filesystem


    //filtering
    @GetMapping("report/year/{year}/{sortDir}")
    public ResponseEntity<ByteArrayResource> getReportByYear(@PathVariable int year, @PathVariable String sortDir) {
        PdfReportOptions options = new PdfReportOptions(
                true, "Car List by Year", GroupByOption.YEAR,sortDir,1.07);
        try {
            // The data source is selected by the service, abstracting this complexity from the controller
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
    public ResponseEntity<ByteArrayResource> getReportByYear(@PathVariable String make, @PathVariable String sortDir) {
        PdfReportOptions options = new PdfReportOptions(
                true, "Car List by Year", GroupByOption.MAKE,sortDir,1.07);
        try {
            // The data source is selected by the service, abstracting this complexity from the controller
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

    //grouping

    @PostMapping(value = "report/group/download", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> downloadGroupFS(@ModelAttribute ReportOptions reportOptions) {
        try {
            List<Car> cars = carService.getAll();
            PdfReportOptions options = new PdfReportOptions(
                    true,
                    "Car List grouped by " + reportOptions.getGroupBy(),
                    GroupByOption.valueOf(reportOptions.getGroupBy().toUpperCase()),
                    reportOptions.getSort(),
                    1.07
            );
            HttpHeaders headers = new HttpHeaders();
            byte[] doc = carPDFCreator.createPdfToByteArray(cars, options);
            headers.setContentType(MediaType.APPLICATION_PDF);
            return new ResponseEntity<>(doc, headers, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage().getBytes());
        }
    }

    //get environment variable as file output location
    @Value("${output.path}")
    private String outputPath;
    @PostMapping(value = "report/group/saveToFs", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String,String>> saveToFsGroupFS(@ModelAttribute ReportOptions reportOptions) {
        try {
            List<Car> cars = carService.getAll();
            PdfReportOptions options = new PdfReportOptions(
                    true,
                    "Car List grouped by " + reportOptions.getGroupBy(),
                    GroupByOption.valueOf(reportOptions.getGroupBy().toUpperCase()),
                    reportOptions.getSort(),
                    1.07
            );
            HttpHeaders headers = new HttpHeaders();
            // generate unique filename based on current date-time
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            LocalDateTime now = LocalDateTime.now();
            String uniqueFilename = dtf.format(now) + "_CarReport.pdf";
            String finalPath = outputPath + uniqueFilename;
            carPDFCreator.createPdfToServerFs(finalPath,cars,options);
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String,String> res = new HashMap<>();
            res.put("Success","true");
            res.put("OutputPath",finalPath);

            return new ResponseEntity<>(res, headers, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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