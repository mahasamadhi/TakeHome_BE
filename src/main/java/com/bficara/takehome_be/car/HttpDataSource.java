package com.bficara.takehome_be.car;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

@Service("httpDataSource")
public class HttpDataSource implements ICarDataSource {
    private MultipartFile file;
    public void setFile(MultipartFile file) {
        this.file = file;
    }

    @Override
    public List<Car> getData() {
        List<Car> cars = new ArrayList<>();
        try {
            Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withHeader("year", "make", "model", "msrp")
                    .withFirstRecordAsHeader()
                    .parse(reader);

            for (CSVRecord record : records) {
                int year = Integer.parseInt(record.get("year"));
                String make = record.get("make");
                String model = record.get("model");
                double price = Double.parseDouble(record.get("msrp"));
                Car car = new Car(year, make, model, price);
                cars.add(car);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return cars;
    }
}
