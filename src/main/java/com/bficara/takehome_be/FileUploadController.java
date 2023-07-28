package com.bficara.takehome_be;

import com.bficara.takehome_be.car.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import tools.PDFCreator;
import tools.PdfReportOptions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api")
public class FileUploadController {
    private final CarService carService;
    private final CarDatasourceService carDatasourceService;

    @Autowired
    public FileUploadController(CarService carService, CarDatasourceService carDataSourceService) {
        this.carService = carService;
        this.carDatasourceService = carDataSourceService;
    }



    @PostMapping("/csvToPdf")
    public ResponseEntity<byte[]> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            HttpDataSource dataSource = (HttpDataSource) carDatasourceService.getDataSource("http");
            dataSource.setFile(file);
            carService.setDataSource(dataSource);
            List<Car> cars = carService.getData();
            PdfReportOptions options = new PdfReportOptions(true,"Car Details","year");
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
}