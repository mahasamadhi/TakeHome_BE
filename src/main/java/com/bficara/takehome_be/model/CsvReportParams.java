package com.bficara.takehome_be.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/*
This class is used to define the parameter object sent from the front-end when
doing a filter-by request with a  CSV datasource
 */
@Data
public class CsvReportParams {

    private MultipartFile file;
    private String filterBy;
    private String sort;
    private String value;



}