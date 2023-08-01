package com.bficara.takehome_be.tools;

public class PdfReportOptions {

        public PdfReportOptions(boolean includeDate, String title, String orderByColumn, GroupByOption groupBy, String sortDir, double taxRate) {
            this.includeDate = includeDate;
            this.title = title;
            this.orderByColumn = orderByColumn;
            this.groupBy = groupBy;
            this.taxRate = taxRate;
            this.sortDir = sortDir;
        }


        private boolean includeDate;
        private String title;
        private String orderByColumn;
        private GroupByOption groupBy;
        private double taxRate;
        private String sortDir;

    public String getSortDir() {
        return sortDir;
    }

    public void setSortDir(String sortDir) {
        this.sortDir = sortDir;
    }

    public GroupByOption  getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(GroupByOption groupBy) {
        this.groupBy = groupBy;
    }
    public double getTaxRate() {
        return taxRate;
    }
    public void setTaxRate(double taxRate) {
        this.taxRate = taxRate;
    }

    public boolean includeDate() {
            return includeDate;
        }

        public void setIncludeDate(boolean includeDate) {
            this.includeDate = includeDate;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getOrderByColumn() {
            return orderByColumn;
        }

        public void setOrderByColumn(String orderByColumn) {
            this.orderByColumn = orderByColumn;
        }

    }

