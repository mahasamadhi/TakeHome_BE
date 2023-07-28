package com.bficara.takehome_be;

import com.bficara.takehome_be.car.Car;
import com.bficara.takehome_be.car.CarService;
import com.bficara.takehome_be.car.CsvCarDataSource;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tools.PDFCreator;
import tools.PdfReportOptions;

import java.util.*;

@SpringBootApplication
public class TakeHomeBeApplication {

//	@Autowired
//	CarService carService;

	public static void main(String[] args) {
		SpringApplication.run(TakeHomeBeApplication.class, args);
		System.out.println("Project is running awesome");
	}

	@PostConstruct
	public void runAfterStartup() {
//		sort by year
//		Collections.sort(fullList, Comparator.comparing(Car::getYear));

		PDFCreator pdf = new PDFCreator();
//
		PdfReportOptions options = new PdfReportOptions(true,"Car Details","year");

		String csvFilepath = "C:\\pProjects\\problem\\TakeHome_BE\\pdfInput\\data.csv";

		CsvCarDataSource ds = new CsvCarDataSource();


//		List<Car> fullListSQL = carService.getData();


		CarService cs1 = new CarService(ds);

		List<Car> fullList = cs1.getData();
		Collections.sort(fullList, Comparator.comparing(Car::getYear));

		try {
			byte[] doc = pdf.createPdfToByteArray( fullList, options);

		} catch (Exception e) {
			System.out.println(e.toString());
		}

		System.out.println("success");
	}


}
