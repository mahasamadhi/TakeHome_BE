package com.bficara.takehome_be.car;

import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.CSVReader;

import java.util.ArrayList;
import java.util.List;

@Service("csvCarDataSource")
public class CsvCarDataSource implements ICarDataSource {
    private final String filePath = "C:\\pProjects\\problem\\TakeHome_BE\\pdfInput\\data.csv";

 // assuming csv.file.path is set in your application.properties
    public CsvCarDataSource() {
//        this.csvFilePath = csvFilePath;
    }

    @Override
    public List<Car> getData() {
        return processCSVData();
    }

    public List<Car> processCSVData() {
        List<CSVRecord> records;
        CSVReader csvReader = new CSVReader();
        records = csvReader.read(this.filePath);
        List<Car> cars = new ArrayList<>();

        for (CSVRecord record : records) {
            int year = Integer.parseInt(record.get("year"));
            String make = record.get("make");
            String model = record.get("model");
            double price = Double.parseDouble(record.get("msrp")); // adjust this as needed
            Car car = new Car(year, make, model, price);
            cars.add(car);
        }

        return cars;

    }
}
