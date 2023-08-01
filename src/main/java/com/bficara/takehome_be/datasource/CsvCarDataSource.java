package com.bficara.takehome_be.datasource;

import com.bficara.takehome_be.model.Car;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Primary
@Repository
public class CsvCarDataSource implements ICarDataSource {

    public CsvCarDataSource() {

    }
    private MultipartFile csvFile;
    public List<Car> carList;

    public void setFile(MultipartFile file) {
        this.csvFile = file;
    }

    public void processData() {
        if (!this.csvFile.equals(null)) {
            this.carList = parseData(readFile(csvFile));
        } else {
            throw new RuntimeException("Must set the file first");
        }
    }


    private Iterable<CSVRecord> readFile(MultipartFile file) {
        try {
            Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            return CSVFormat.DEFAULT
                    .withHeader("year", "make", "model", "msrp")
                    .withFirstRecordAsHeader()
                    .parse(reader);
        } catch (IOException e) {
            // Consider throwing a custom exception here
            throw new RuntimeException("Error reading file", e);
        }
    }

    private List<Car> parseData(Iterable<CSVRecord> records) {
        List<Car> cars = new ArrayList<>();

        for (CSVRecord record : records) {
            int year = Integer.parseInt(record.get("year"));
            String make = record.get("make");
            String model = record.get("model");
            double msrp = Double.parseDouble(record.get("msrp"));
            Car car = new Car(year, make, model, msrp);
            cars.add(car);
        }

        return cars;
    }

    public List<Car> getAll() {
        return carList;
    }

    @Override
    public List<Car> getAllByYear(int year) {
        return carList.stream()
                .filter(car -> car.getYear() == year)
                .collect(Collectors.toList());
    }

    @Override
    public List<Car> getAllByMake(String make) {
        List<Car> cars = new ArrayList<>();

        for (Car car:this.carList) {
            if (car.getMake().equals(make)) {
                cars.add(car);
            }
        }
        return cars;
    }
}
