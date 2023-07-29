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
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.kernel.colors.ColorConstants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

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
                            carDetails = new String[]{" ", car.getMake(), car.getModel(), String.valueOf(car.getPrice())};
                        }
                        addCellsToTable(table, carDetails);

                        break;
                }


            }


        document.add(table);
        document.close();

        return byteArrayOutputStream.toByteArray();
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
        newLine(table,data);
    }

    public void newLine(Table table,String[] data) {
        for(int i = 0; i < data.length; i++) {
            Cell cell = new Cell();
            cell.setBorder(Border.NO_BORDER);
            cell.add(new Paragraph(" "));
            table.addCell(cell);
        }
    }



}
