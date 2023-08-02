package com.bficara.takehome_be.tools;

public class PdfReportOptions {

        public PdfReportOptions(boolean includeDate, String title, GroupByOption groupBy, String groupSortDir, double taxRate) {
            this.includeDate = includeDate;
            this.title = title;
            this.groupBy = groupBy;
            this.groupSortDir = groupSortDir;
            this.taxRate = taxRate;
        }

        private boolean includeDate;
        private String title;
        private GroupByOption groupBy;
        private double taxRate;
        private String groupSortDir;

    public String getGroupSortDir() {
        return groupSortDir;
    }

    public void setGroupSortDir(String groupSortDir) {
        this.groupSortDir = groupSortDir;
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


    }

