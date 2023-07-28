package tools;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.List;

public class PDFCreator {

    public void createPDF(String dest, List<List<String>> data) throws Exception {


        PdfWriter writer = new PdfWriter(dest);


        com.itextpdf.kernel.pdf.PdfDocument pdf = new com.itextpdf.kernel.pdf.PdfDocument(writer);


        Document document = new Document(pdf);

        // adding a title
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        Paragraph title = new Paragraph("Title")
                .setFont(font)
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(title);

        // adding a date
        Paragraph dateParagraph = new Paragraph("Date: " + LocalDate.now().toString())
                .setFont(font)
                .setFontSize(10);
        document.add(dateParagraph);

        // Creating table to hold car data
        float [] pointColumnWidths = {100F, 100F, 100F, 100F};
        Table table = new Table(pointColumnWidths);

        // Adding cells to the table
        for (List<String> row : data) {
            for (String cell_ : row) {
                Cell cell = new Cell();
                cell.setBorder(Border.NO_BORDER);
                cell.add(new Paragraph(cell_));
                table.addCell(cell);
            }
            //adding spaces after set of makes
            for(int i = 0; i < row.size(); i++) {
                Cell cell = new Cell();
                cell.setBorder(Border.NO_BORDER);
                cell.add(new Paragraph(" "));
                table.addCell(cell);
            }

        }

        document.add(table);

        document.close();
    }

}
