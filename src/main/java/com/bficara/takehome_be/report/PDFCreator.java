package com.bficara.takehome_be.report;

import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;

public interface PDFCreator {
    void addTitle(Document document, PdfReportOptions options) throws IOException, java.io.IOException;
    void addDate(Document document, PdfReportOptions options);

    void addCellToTable(Table table, String text, PdfFont font, int fontSize, CellTypeOption cellType) throws java.io.IOException;

    void addEmptyRow(Table table, int number);

     void addLineSeperator(Document document, float marginTop, float marginBottom);
}