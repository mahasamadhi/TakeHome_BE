package com.bficara.takehome_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tools.PDFCreator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class TakeHomeBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(TakeHomeBeApplication.class, args);

		System.out.println("Project is running awesome");
		List<List<String>> fullList = new ArrayList<>();
		List<String> fordList = new ArrayList<>(Arrays.asList("Ford","Ranger","2023","60,000.00"));
		List<String> acuraList = new ArrayList<>(Arrays.asList("acura","rsx","2006","30,000.00","acura","TL","20016","90,000.00"));

		fullList.add(fordList);
		fullList.add(acuraList);


		PDFCreator pdf = new PDFCreator();

		try {
			pdf.createPDF("C:\\pProjects\\problem\\TakeHome_BE\\PdfOutput\\report.pdf", fullList);

		} catch (Exception e) {
			System.out.println(e.toString());
		}

		System.out.println("success");
	}


}
