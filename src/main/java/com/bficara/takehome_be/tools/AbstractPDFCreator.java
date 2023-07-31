package com.bficara.takehome_be.tools;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.IOException;
import java.util.Date;

public abstract class AbstractPDFCreator {

    public void addTitle(Document document, PdfReportOptions options) throws IOException {
        PdfFont font = PdfFontFactory.createFont(StandardFonts.COURIER);
        document.add(new Paragraph(options.getTitle())
                .setTextAlignment(TextAlignment.CENTER)
                .setFont(font)
                .setFontSize(24)
                .setFontColor(ColorConstants.DARK_GRAY));
    }

    public void addDate(Document document, PdfReportOptions options) {
        if (options.includeDate()) {
            document.add(new Paragraph("Generated on: " + new Date().toString())
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(12));
        }
    }


    protected void addCellToTable(Table table, String text, PdfFont font, float fontSize, CellTypeOption cellType) throws IOException {
        Text txt = new Text(text).setFont(font).setFontSize(fontSize);
        if (cellType.equals(CellTypeOption.UNDERLINE)) {
            txt.setUnderline();
        }
        Paragraph para = new Paragraph().add(txt);
        Cell cell = new Cell();
        cell.setBorder(Border.NO_BORDER);
        cell.add(para);
        table.addCell(cell);
    }

    public void addEmptyRow(Table table, int cellsPerRow) {
        for(int i = 0; i < cellsPerRow; i++) {
            Cell cell = new Cell();
            cell.setBorder(Border.NO_BORDER);
            cell.add(new Paragraph(" "));
            table.addCell(cell);
        }
    }

}
