package tools;

public class PdfReportOptions {

        public PdfReportOptions(boolean includeDate, String title, String orderByColumn) {
            this.includeDate = includeDate;
            this.title = title;
            this.orderByColumn = orderByColumn;
        }


        private boolean includeDate;
        private String title;
        private String orderByColumn;




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

