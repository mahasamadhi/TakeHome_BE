package com.bficara.takehome_be.model;

import org.springframework.web.multipart.MultipartFile;

public class ReportOptions {
    private MultipartFile file;
    private String groupBy;
    private String sort;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

}