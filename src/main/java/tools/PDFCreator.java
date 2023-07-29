package tools;
import com.bficara.takehome_be.car.Car;
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
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.kernel.colors.ColorConstants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PDFCreator {

    public void createPdfToFilesystem(String dest, List<Car> cars, PdfReportOptions options) throws Exception {
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph(options.getTitle())
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(20))
                .setFontColor(ColorConstants.GRAY);

        if (options.includeDate()) {
            document.add(new Paragraph("Generated on: " + new Date().toString())
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(12));

        }

        document.add(new Paragraph("")
                .setFontSize(12))
                .setFontColor(ColorConstants.BLACK);



        float [] pointColumnWidths = {150F, 150F, 150F, 150F};
        Table table = new Table(pointColumnWidths);

        for (Car car : cars) {
            String[] carDetails = {String.valueOf(car.getYear()), car.getMake(), car.getModel(), String.valueOf(car.getPrice())};
            addCellsToTable(table, carDetails);
        }

        document.add(table);
        document.close();
    }

    public byte[] createPdfToByteArray(List<Car> cars, PdfReportOptions options) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        PdfFont font = PdfFontFactory.createFont(StandardFonts.COURIER);
        document.add(new Paragraph(options.getTitle())
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFont(font)
                        .setFontSize(24))
                .setFontColor(ColorConstants.GRAY);

        if (options.includeDate()) {
            document.add(new Paragraph("Generated on: " + new Date().toString())
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(12));

        }

        document.add(new Paragraph("")
                        .setFontSize(12))
                .setFontColor(ColorConstants.BLACK);

        float [] pointColumnWidths = {120F, 80F, 80F, 80F};
        Table table = new Table(pointColumnWidths);

        String currentGroupValue = "";
        for (Car car : cars) {
            String[] carDetails =  new String[]{"", "","",""};
                switch (options.getGroupBy()) {
                    case "caryear":
                        String strValue = String.valueOf(car.getYear());
                        if (currentGroupValue == "") {
                            currentGroupValue = strValue;
                            carDetails = new String[]{String.valueOf(car.getYear()), "","",""};
                            addCellsToTable(table, carDetails);
                            carDetails = new String[]{"", car.getMake(), car.getModel(), String.valueOf(car.getPrice())};
                        } else if (currentGroupValue.equals(strValue)) {
                            carDetails = new String[]{"", car.getMake(), car.getModel(), String.valueOf(car.getPrice())};
                        } else if (currentGroupValue != "" &&  currentGroupValue != strValue){
                            currentGroupValue = strValue;
                            carDetails = new String[]{String.valueOf(car.getYear()), "","",""};
                            addCellsToTable(table, carDetails);
                            carDetails = new String[]{"", car.getMake(), car.getModel(), String.valueOf(car.getPrice())};
                        }
                        addCellsToTable(table, carDetails);

                        break;
                }


            }


        document.add(table);
        document.close();

        return byteArrayOutputStream.toByteArray();
    }


    public byte[] createPdfToByteArrayNew(List<Car> cars, PdfReportOptions options) throws Exception {
        String groupBy = options.getGroupBy();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        PdfFont font = PdfFontFactory.createFont(StandardFonts.COURIER);
        document.add(new Paragraph(options.getTitle())
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFont(font)
                        .setFontSize(24))
                .setFontColor(ColorConstants.GRAY);

        if (options.includeDate()) {
            document.add(new Paragraph("Generated on: " + new Date().toString())
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(12));

        }

        document.add(new Paragraph("")
                        .setFontSize(12))
                .setFontColor(ColorConstants.BLACK);

        float [] pointColumnWidths = {120F, 100F, 100F, 100F};
        Table table = new Table(pointColumnWidths);

        if(groupBy.equals("Year")) {
            AddColumnNames(table,"Year");
            addEmptyRow(table,8);
            fillPdfByYear(cars,table);
        } else if (groupBy.equals("Make")) {
            AddColumnNames(table,"Make");
            addEmptyRow(table,8);
            fillPdfByMake(cars,table);
        }

        document.add(table);
        document.close();

        return byteArrayOutputStream.toByteArray();
    }

    public Table fillPdfByYear(List<Car> cars, Table table) throws IOException {
        Map<Integer, List<Car>> carMap = groupByYear(cars);

        for (Map.Entry<Integer, List<Car>> entry : carMap.entrySet()) {
            int year = entry.getKey();
            String strYear = String.valueOf(year);
            List<Car> carsInYear = entry.getValue();

            AddHeaderRow(table, strYear);

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

                // Create cell for price
                String price = "$" + car.getPrice();
                Paragraph pricePara = new Paragraph(price);
                pricePara.setFontSize(12);
                table.addCell(new Cell().setBorder(Border.NO_BORDER).add(pricePara));
            }
        }

        return table;
    }

    public Table fillPdfByMake(List<Car> cars, Table table) throws IOException {
        Map<String, List<Car>> carMap = groupByMake(cars);

        for (Map.Entry<String, List<Car>> entry : carMap.entrySet()) {
            String make = entry.getKey();
            List<Car> carsByMake = entry.getValue();

            AddHeaderRow(table, make);
            addEmptyRow(table,4);

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

                // Create cell for price
                String price = "$" + car.getPrice();
                Paragraph pricePara = new Paragraph(price);
                pricePara.setFontSize(12);
                table.addCell(new Cell().setBorder(Border.NO_BORDER).add(pricePara));
            }
        }

        return table;
    }

    public Table AddHeaderRow(Table table,String groupByValue) throws IOException {

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
        table.addCell(emptyCell);
        table.addCell(emptyCell);
        table.addCell(emptyCell);

        return table;
    }

    public Table AddColumnNames(Table table,String groupBy) throws IOException {

            PdfFont font = PdfFontFactory.createFont(StandardFonts.TIMES_ITALIC);
            if (groupBy.equals("Year")) {
                addCellToTable(table, "Year", font, 16, "columnName");
                addCellToTable(table, "Make", font, 16, "columnName");
                addCellToTable(table, "Model", font, 16, "columnName");
                addCellToTable(table, "Price", font, 16, "columnName");
            } else if (groupBy.equals("Make")) {
                addCellToTable(table, "Make", font, 16, "columnName");
                addCellToTable(table, "Year", font, 16, "columnName");
                addCellToTable(table, "Model", font, 16, "columnName");
                addCellToTable(table, "Price", font, 16, "columnName");
            }


        return table;
    }

    private void addCellToTable(Table table, String text, PdfFont font, float fontSize, String cellType) throws IOException {
        Text txt = new Text(text).setFont(font).setFontSize(fontSize);
        if ("columnName".equals(cellType)) {
            txt.setUnderline();
        }
        Paragraph para = new Paragraph().add(txt);
        Cell cell = new Cell();
        cell.setBorder(Border.NO_BORDER);
        cell.add(para);
        table.addCell(cell);
    }


    private void addCellsToTable(Table table, String[] data) throws IOException {
        for(var i = 0; i<data.length; i++){
            String value = data[i];
            Paragraph para = new Paragraph(value);
            PdfFont font = PdfFontFactory.createFont(StandardFonts.COURIER);
            para.setFont(font);
            if (i == 0) {
                font = PdfFontFactory.createFont(StandardFonts.COURIER_BOLD);
                para.setFontSize(14);
                para.setFont(font);
            }
            Cell cell = new Cell();
            cell.setBorder(Border.NO_BORDER);
            cell.add(para);
            table.addCell(cell);
        }
        // Adding a new line after each row.
        addEmptyRow(table,4);
    }

    public void addEmptyRow(Table table, int cellsPerRow) {
        for(int i = 0; i < cellsPerRow; i++) {
            Cell cell = new Cell();
            cell.setBorder(Border.NO_BORDER);
            cell.add(new Paragraph(" "));
            table.addCell(cell);
        }
    }
    public Map<String, List<Car>> groupByMake(List<Car> carList) {
        Map<String, List<Car>> carsByMake = carList.stream()
                .collect(Collectors.groupingBy(Car::getMake));

        return carsByMake;
    }

    public Map<Integer, List<Car>> groupByYear(List<Car> carList) {
        Map<Integer, List<Car>> carsByYear = carList.stream()
                .collect(Collectors.groupingBy(Car::getYear));
        return carsByYear;
    }




}
