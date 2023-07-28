package com.bficara.takehome_be.car;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import tools.PDFCreator;
import tools.PdfReportOptions;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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

    public List<Car> getData() {
        return dataSource.getData();
    }


    public List<Car> getDataByYear(int year) {
        if (dataSource instanceof DatabaseCarDataSource) {
            return ((DatabaseCarDataSource) dataSource).getDataByYear(year);
        }
        throw new UnsupportedOperationException("Can't get data by year for this data source");
    }

    public byte[] generateReportByYear(int year, PdfReportOptions options) throws Exception {

        PDFCreator pdfCreator = new PDFCreator();
        List<Car> cars = getDataByYear(year);

        Collections.sort(cars, Comparator.comparing(Car::getYear));
        byte[] data = pdfCreator.createPdfToByteArray(cars, options);
        return data;
    }



}

