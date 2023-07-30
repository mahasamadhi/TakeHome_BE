package com.bficara.takehome_be.tools;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;


public class CSVReader {

    public List<CSVRecord> read(String path) {
        List<CSVRecord> records;
        try (Reader in = new FileReader(path)) {
            CSVParser parser = CSVFormat.DEFAULT
                    .withHeader("year", "make", "model", "msrp") // define the header
                    .withFirstRecordAsHeader() // if your CSV file already has a header
                    .parse(in);
            records = parser.getRecords();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return records;
    }
}
