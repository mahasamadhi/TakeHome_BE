package com.bficara.takehome_be.report;
import com.bficara.takehome_be.model.Car;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CarPDFCreator {

    private final PDFCreatorImpl pdfCreator;

    @Autowired
    public CarPDFCreator(PDFCreatorImpl pdfCreator) {
        this.pdfCreator = pdfCreator;
    }


    public byte[] createPdfToByteArray(List<Car> cars, PdfReportOptions options) throws Exception {
        //declare and initialize variables
        GroupByOption groupBy = options.getGroupBy();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);


        //create the main element of the PDF, the table
        float[] pointColumnWidths = {100F, 100F, 100F, 100F, 100F};
        Table table = new Table(pointColumnWidths);
        int numColumns = pointColumnWidths.length;

        //add title and date
        pdfCreator.addTitle(document, options);
        pdfCreator.addDate(document, options);

        pdfCreator.addLineSeperator(document,5, 15);

        //add price (msrp + tax)
        addPrice(cars, options.getTaxRate());


        switch (groupBy) {
            case YEAR, PRICE:
                AddCarColumnNames(table, "Year");
                pdfCreator.addEmptyRow(table, numColumns * 2);
                Map<Integer, List<Car>> carMap = groupByYear(cars,options.getGroupSortDir());
                fillPdfByYear(carMap, table, numColumns);
                break;

            case MAKE:
                AddCarColumnNames(table, "Make");
                pdfCreator.addEmptyRow(table, numColumns * 2);
                Map<String, List<Car>> carMapMake = groupByMake(cars, options.getGroupSortDir());
                fillPdfByMake(carMapMake, table, numColumns);
                break;

            default:
                throw new IllegalArgumentException("Invalid groupBy value: " + groupBy);
        }



        document.add(table);

        pdfCreator.addLineSeperator(document,70, 5);

        addDisclaimerFooter(document,3);

        document.close();

        return byteArrayOutputStream.toByteArray();
    }



    public void addDisclaimerFooter(Document document, int validForDays) throws IOException {
        PdfFont font = PdfFontFactory.createFont(StandardFonts.COURIER);
        LocalDate date = LocalDate.now().plusDays(validForDays);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String formatted = dtf.format(date);

        document.add(new Paragraph("Disclaimer: Prices valid until " + formatted)
                .setTextAlignment(TextAlignment.LEFT)
                .setFont(font)
                .setFontSize(12)
                .setFontColor(ColorConstants.GRAY));
    }

    public void createPdfToServerFs(String dest, List<Car> cars, PdfReportOptions options) throws Exception {
        GroupByOption groupBy = options.getGroupBy();

        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        //create the main element of the PDF, the table
        float[] pointColumnWidths = {100F, 100F, 100F, 100F, 100F};
        Table table = new Table(pointColumnWidths);
        int numColumns = pointColumnWidths.length;

        //add title and date
        pdfCreator.addTitle(document, options);
        pdfCreator.addDate(document, options);

        //add price (msrp + tax)
        addPrice(cars, options.getTaxRate());


        switch (groupBy) {
            case YEAR, PRICE:
                AddCarColumnNames(table, "Year");
                pdfCreator.addEmptyRow(table, numColumns * 2);
                Map<Integer, List<Car>> carMap = groupByYear(cars,options.getGroupSortDir());
                fillPdfByYear(carMap, table, numColumns);
                break;

            case MAKE:
                AddCarColumnNames(table, "Make");
                pdfCreator.addEmptyRow(table, numColumns * 2);
                Map<String, List<Car>> carMapMake = groupByMake(cars, options.getGroupSortDir());
                fillPdfByMake(carMapMake, table, numColumns);
                break;

            default:
                throw new IllegalArgumentException("Invalid groupBy value: " + groupBy);
        }

            document.add(table);
            document.close();
        }

    public void addPrice(List<Car> cars, double taxRate) {
        for (Car car : cars) {
            double price = taxRate * car.getMsrp();
            price = Math.round(price * 100.0) / 100.0;
            car.setPrice(price);
        }
    }

    public Table fillPdfByYear(Map<Integer, List<Car>> carMap, Table table, int numColumns) throws IOException {

        //for each entry in the map (a car year) append the rest of the details within the Car list (make,model,price)
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
        //for each entry in the map (a car make) append the rest of the details within the Car list (year,model,price)
        for (Map.Entry<String, List<Car>> entry : carMap.entrySet()) {
            String make = entry.getKey();
            List<Car> carsByMake = entry.getValue();

            AddHeaderRow(table, make, numColumns);
            pdfCreator.addEmptyRow(table, numColumns);

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

    public Table AddHeaderRow(Table table, String groupByValue, int numColumns) throws IOException {
        //add the first row of the pdf grouped by year or make, creating a visually nested grouping below
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
        for (int i = 1; i < numColumns; i++) {
            table.addCell(emptyCell);
        }

        return table;
    }


    public Table AddCarColumnNames(Table table, String groupBy) throws IOException {
        //add the column names at the top of the table depending on the type of report
        PdfFont font = PdfFontFactory.createFont(StandardFonts.TIMES_ITALIC);
        if (groupBy.equals("Year")) {
            pdfCreator.addCellToTable(table, "Year", font, 16, CellTypeOption.UNDERLINE);
            pdfCreator.addCellToTable(table, "Make", font, 16, CellTypeOption.UNDERLINE);
        } else if (groupBy.equals("Make")) {
            pdfCreator.addCellToTable(table, "Make", font, 16, CellTypeOption.UNDERLINE);
            pdfCreator.addCellToTable(table, "Year", font, 16, CellTypeOption.UNDERLINE);

        }
        pdfCreator.addCellToTable(table, "Model", font, 16, CellTypeOption.UNDERLINE);
        pdfCreator.addCellToTable(table, "MSRP", font, 16, CellTypeOption.UNDERLINE);
        pdfCreator.addCellToTable(table, "Price", font, 16, CellTypeOption.UNDERLINE);

        return table;
    }


    public TreeMap<String, List<Car>> groupByMake(List<Car> carList, String order) {
        //all of the sorting and grouping logic happens here
        final boolean isAsc = order.equals("asc");
        if (isAsc) {
            //creates a stream from the List<Car> input so operations can be performed on it's data
            return carList.stream()
                    //collects the result of the operations within, which in this case is a TreeMap<String, List<Car>>
                    .collect(
                            //method within the Java Collections package that groups the input based on the supplied parameters
                            Collectors.groupingBy(
                            //the key that the group is mapped by, called the Classifier
                            Car::getMake,
                            //the type of Map that the result is stored in
                            TreeMap::new,
                            //performs additional operations on the grouped List before it's stored in the Map
                            Collectors.collectingAndThen(
                                    //collects the cars into a list
                                    Collectors.toList(),
                                    //a function that performs operations on the list
                                    cars -> {
                                        //for each car list, use the comparator to sort by year (default ascending)
                                        Comparator<Car> YearComparator = Comparator.comparing(Car::getYear);
                                        return cars.stream().sorted(YearComparator).collect(Collectors.toList());
                                    }
                            )
                        )
                    );
        } else {
            return carList.stream()
                    .collect(Collectors.groupingBy(
                            Car::getMake,
                            () -> new TreeMap<>(Comparator.reverseOrder()), // TreeMap with reverse order comparator
                            Collectors.collectingAndThen(
                                    Collectors.toList(),
                                    cars -> {
                                        Comparator<Car> YearComparator = Comparator.comparing(Car::getYear).reversed();
                                        return cars.stream().sorted(YearComparator).collect(Collectors.toList());
                                    }
                            )));
        }
    }

    public TreeMap<Integer, List<Car>> groupByYear(List<Car> carList, String order) {
        final boolean isAsc = order.equals("asc");
        if (isAsc) {
            return carList.stream()
                    .collect(Collectors.groupingBy(
                            Car::getYear,
                            TreeMap::new,
                            Collectors.collectingAndThen(
                                    Collectors.toList(),
                                    cars -> {
                                        Comparator<Car> makeComparator = Comparator.comparing(Car::getMake);
                                        return cars.stream().sorted(makeComparator).collect(Collectors.toList());
                                    }
                            )
                    ));
        } else {
            return carList.stream()
                    .collect(Collectors.groupingBy(
                            Car::getYear,
                            () -> new TreeMap<>(Comparator.reverseOrder()),
                            Collectors.collectingAndThen(
                                    Collectors.toList(),
                                    cars -> {
                                        Comparator<Car> makeComparator = Comparator.comparing(Car::getMake).reversed();
                                        return cars.stream().sorted(makeComparator).collect(Collectors.toList());
                                    }
                            )
                    ));
        }

    }

}
