package com.bficara.takehome_be.report;
import com.bficara.takehome_be.model.Car;
import com.bficara.takehome_be.tools.*;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CarPDFCreator extends AbstractPDFCreator {


    public byte[] createPdfToByteArray(List<Car> cars, PdfReportOptions options) throws Exception {
        GroupByOption groupBy = options.getGroupBy();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        //add title and date
        addTitle(document,options);
        addDate(document,options);
        //add price (msrp + tax)
        addPrice(cars, options.getTaxRate());

        float [] pointColumnWidths = {100F, 100F, 100F, 100F, 100F};
        Table table = new Table(pointColumnWidths);
        int numColumns = pointColumnWidths.length;

        switch (groupBy) {
            case YEAR:
                AddCarColumnNames(table,"Year");
                addEmptyRow(table, numColumns*2);
                Map<Integer, List<Car>> carMap = groupByYear(cars);
                fillPdfByYear(carMap,table,numColumns);
                break;

            case MAKE:
                AddCarColumnNames(table,"Make");
                addEmptyRow(table,numColumns*2);
                Map<String, List<Car>> carMapMake = groupByMake(cars);
                fillPdfByMake(carMapMake,table,numColumns);
                break;

            default:
                // You might want to throw an exception or handle the default case differently.
                throw new IllegalArgumentException("Invalid groupBy value: " + groupBy);
        }

        document.add(table);
        document.close();

        return byteArrayOutputStream.toByteArray();
    }

    public void addPrice(List<Car> cars, double taxRate) {
        for (Car car:cars) {
            double price = taxRate * car.getMsrp() ;
            price = Math.round(price * 100.0) / 100.0;
            car.setPrice(price);
        }
    }

    public Table fillPdfByYear(Map<Integer, List<Car>> carMap, Table table, int numColumns) throws IOException {

        for (Map.Entry<Integer, List<Car>> entry : carMap.entrySet()) {
            int year = entry.getKey();
            String strYear = String.valueOf(year);
            List<Car> carsInYear = entry.getValue();
            AddHeaderRow(table, strYear, numColumns);

            for (Car car : carsInYear) {
                // Create a blank cell
                Cell blankCell = new Cell();
                blankCell.setBorder(Border.NO_BORDER);
                table.addCell(blankCell);

                // Create cell for make
                String make = car.getMake();
                Paragraph makePara = new Paragraph(make);
                makePara.setFontSize(12);
                table.addCell(new Cell().setBorder(Border.NO_BORDER).add(makePara));

                // Create cell for model
                String model = car.getModel();
                Paragraph modelPara = new Paragraph(model);
                modelPara.setFontSize(12);
                table.addCell(new Cell().setBorder(Border.NO_BORDER).add(modelPara));

                // Create cell for msrp
                String msrp = "$" + car.getMsrp();
                Paragraph msrpPara = new Paragraph(msrp);
                msrpPara.setFontSize(12);
                table.addCell(new Cell().setBorder(Border.NO_BORDER).add(msrpPara));

                // Create cell for price
                String price = "$" + car.getPrice();
                Paragraph pricePara = new Paragraph(price);
                pricePara.setFontSize(12);
                table.addCell(new Cell().setBorder(Border.NO_BORDER).add(pricePara));
            }
        }

        return table;
    }

    public Table fillPdfByMake(Map<String, List<Car>> carMap, Table table, int numColumns) throws IOException {

        for (Map.Entry<String, List<Car>> entry : carMap.entrySet()) {
            String make = entry.getKey();
            List<Car> carsByMake = entry.getValue();

            AddHeaderRow(table, make,numColumns);
            addEmptyRow(table,numColumns);

            for (Car car : carsByMake) {
                // Create a blank cell
                Cell blankCell = new Cell();
                blankCell.setBorder(Border.NO_BORDER);
                table.addCell(blankCell);

                // Create cell for make
                String year = String.valueOf(car.getYear());
                Paragraph yearPara = new Paragraph(year);
                yearPara.setFontSize(12);
                table.addCell(new Cell().setBorder(Border.NO_BORDER).add(yearPara));

                // Create cell for model
                String model = car.getModel();
                Paragraph modelPara = new Paragraph(model);
                modelPara.setFontSize(12);
                table.addCell(new Cell().setBorder(Border.NO_BORDER).add(modelPara));

                // Create cell for msrp
                String msrp = "$" + car.getMsrp();
                Paragraph msrpPara = new Paragraph(msrp);
                msrpPara.setFontSize(12);
                table.addCell(new Cell().setBorder(Border.NO_BORDER).add(msrpPara));

                // Create cell for price
                String price = "$" + car.getPrice();
                Paragraph pricePara = new Paragraph(price);
                pricePara.setFontSize(12);
                table.addCell(new Cell().setBorder(Border.NO_BORDER).add(pricePara));
            }
        }

        return table;
    }

    public Table AddHeaderRow(Table table,String groupByValue,int numColumns) throws IOException {

        Paragraph para = new Paragraph(groupByValue);
        PdfFont font = PdfFontFactory.createFont(StandardFonts.COURIER);
        para.setFontSize(14);
        para.setFont(font);
        Cell cell = new Cell();
        cell.setBorder(Border.NO_BORDER);
        cell.add(para);
        table.addCell(cell);


        Paragraph emptyPara = new Paragraph("");
        Cell emptyCell = new Cell();
        emptyCell.setBorder(Border.NO_BORDER);
        emptyCell.add(emptyPara);
        for (int i = 1; i< numColumns; i++) {
            table.addCell(emptyCell);
        }

        return table;
    }


    public Table AddCarColumnNames(Table table, String groupBy) throws IOException {

            PdfFont font = PdfFontFactory.createFont(StandardFonts.TIMES_ITALIC);
            if (groupBy.equals("Year")) {
                addCellToTable(table, "Year", font, 16, CellTypeOption.UNDERLINE);
                addCellToTable(table, "Make", font, 16, CellTypeOption.UNDERLINE);
            } else if (groupBy.equals("Make")) {
                addCellToTable(table, "Make", font, 16, CellTypeOption.UNDERLINE);
                addCellToTable(table, "Year", font, 16, CellTypeOption.UNDERLINE);

            }
            addCellToTable(table, "Model", font, 16, CellTypeOption.UNDERLINE);
            addCellToTable(table, "MSRP", font, 16, CellTypeOption.UNDERLINE);
            addCellToTable(table, "Price", font, 16, CellTypeOption.UNDERLINE);

        return table;
    }


    public Map<String, List<Car>> groupByMake(List<Car> carList) {
        return carList.stream()
                .collect(Collectors.groupingBy(Car::getMake));
    }

    public Map<Integer, List<Car>> groupByYear(List<Car> carList) {
        return carList.stream()
                .collect(Collectors.groupingBy(Car::getYear));
    }

}