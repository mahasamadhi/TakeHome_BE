package com.bficara.takehome_be.car;

import org.springframework.stereotype.Service;
import tools.PDFCreator;
import tools.PdfReportOptions;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CarService {


    private ICarDataSource dataSource;

    public CarService(ICarDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public CarService() {
        // no initialization of carDataSource
    }

    public void setDataSource(ICarDataSource ds) {
        this.dataSource = ds;
    }

    public List<Car> getAll() {
        return dataSource.getAll();
    }


    public List<Car> getAllByYear(int year) {
            return dataSource.getAllByYear(year);
        }

    public byte[] generateReportByYear(int year, PdfReportOptions options) throws Exception {

        PDFCreator pdfCreator = new PDFCreator();
        List<Car> cars = getAllByYear(year);
        Collections.sort(cars, Comparator.comparing(Car::getYear));
        byte[] data = pdfCreator.createPdfToByteArray(cars, options);
        return data;
    }




    public static Map<String, List<Car>> groupByMake(List<Car> carList) {
        Map<String, List<Car>> carsByMake = carList.stream()
                .collect(Collectors.groupingBy(Car::getMake));

        return carsByMake;
    }

    public static Map<Integer, List<Car>> groupByYear(List<Car> carList) {
        Map<Integer, List<Car>> carsByYear = carList.stream()
                .collect(Collectors.groupingBy(Car::getYear));
        return carsByYear;
    }



}

