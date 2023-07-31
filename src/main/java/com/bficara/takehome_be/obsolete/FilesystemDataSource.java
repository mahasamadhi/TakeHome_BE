package com.bficara.takehome_be.obsolete;

import com.bficara.takehome_be.model.Car;
import com.bficara.takehome_be.datasource.ICarDataSource;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import com.bficara.takehome_be.tools.CSVReader;

import java.util.ArrayList;
import java.util.List;

@Service("filesystemDataSource")
public class FilesystemDataSource implements ICarDataSource {
    private final String filePath = "C:\\pProjects\\problem\\TakeHome_BE\\pdfInput\\data.csv";

 // assuming csv.file.path is set in your application.properties
    public FilesystemDataSource() {
//        this.csvFilePath = csvFilePath;
    }

    @Override
    public List<Car> getAll() {
        return processCSVData();
    }

    @Override
    public List<Car> getAllByYear(int year) {
        return null;
    }

    @Override
    public List<Car> getAllByMake(String make) {
        return null;
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
            double msrp = Double.parseDouble(record.get("msrp")); // adjust this as needed
            Car car = new Car(year, make, model, msrp);
            cars.add(car);
        }
        return cars;
    }
}
