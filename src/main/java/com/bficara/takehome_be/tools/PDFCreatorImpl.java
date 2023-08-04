package com.bficara.takehome_be.tools;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

@Service
public class PDFCreatorImpl implements PDFCreator {

    @Override
    public void addTitle(Document document, PdfReportOptions options) throws IOException {
        PdfFont font = PdfFontFactory.createFont(StandardFonts.COURIER);
        document.add(new Paragraph(options.getTitle())
                .setTextAlignment(TextAlignment.CENTER)
                .setFont(font)
                .setFontSize(24)
                .setFontColor(ColorConstants.DARK_GRAY));
    }
    @Override
    public void addDate(Document document, PdfReportOptions options) {
        if (options.includeDate()) {
            document.add(new Paragraph("Generated on: " + new Date().toString())
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(12));
        }
    }


    @Override
    public void addCellToTable(Table table, String text, PdfFont font, int fontSize, CellTypeOption cellType) throws java.io.IOException {
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
    @Override
    public void addEmptyRow(Table table, int cellsPerRow) {
        for(int i = 0; i < cellsPerRow; i++) {
            Cell cell = new Cell();
            cell.setBorder(Border.NO_BORDER);
            cell.add(new Paragraph(" "));
            table.addCell(cell);
        }
    }

    @Override
    public void addLineSeperator(Document document, float marginTop, float marginBottom) {
        SolidLine line = new SolidLine(1f);
        LineSeparator ls = new LineSeparator(line);
        ls.setMarginTop(marginTop);  // this will add space above the line
        ls.setMarginBottom(marginBottom);  // this will add space below the line
        document.add(ls);
    }

}
