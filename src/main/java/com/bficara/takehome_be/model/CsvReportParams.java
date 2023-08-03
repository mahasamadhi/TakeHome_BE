package com.bficara.takehome_be.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
@Data
public class CsvReportParams {

    private MultipartFile file;
    private String filterBy;
    private String sort;
    private String value;



}