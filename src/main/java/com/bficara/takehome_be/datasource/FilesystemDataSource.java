package com.bficara.takehome_be.datasource;

import com.bficara.takehome_be.model.Car;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bficara.takehome_be.tools.CSVReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FilesystemDataSource implements ICarDataSource {
    //work laptop
//    private final String filePath = "C:\\pProjects\\problem\\TakeHome_BE\\pdfInput\\data.csv";
    private final String filePath = "../TakeHome_BE/pdfInput/data.csv";

    @Autowired
    public FilesystemDataSource() {
        this.processCSVData();
    }

    private List<Car> carList;

    @Override
    public List<Car> getAll() {
        return carList;
    }

    @Override
    public List<Car> getAllByYear(int year) {
        return this.carList.stream()
                .filter(car -> car.getYear() == year)
                .collect(Collectors.toList());
    }


    @Override
    public List<Car> getAllLessThanPrice(int price) {
        return this.carList.stream()
                .filter(car -> car.getPrice() < price)
                .collect(Collectors.toList());
    }

    @Override
    public List<Car> getAllByMake(String make) {
        return this.carList.stream().filter(car->car.getMake() == make).collect(Collectors.toList());
    }

    @Override
    public Map<String, List<String>> getMakeOptions() {
        Map<String, List<String>> result = new HashMap<>();
        List<String> distinctMakes = this.carList.stream()
                .map(Car::getMake)
                .distinct()
                .collect(Collectors.toList());

        result.put("makeOptions", distinctMakes);
        return  result;
    }

    @Override
    public Map<String, List<String>> getYearOptions() {
        //get a distinct list of year options from the current car list
        List<String> distinctYears = this.carList.stream()
                .map(car -> String.valueOf(car.getYear()))
                .distinct()
                .collect(Collectors.toList());

        Map<String, List<String>> result = new HashMap<>();
        result.put("yearOptions", distinctYears);
        return result;
    }

    private void processCSVData() {
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
        this.carList = cars;
    }
}
