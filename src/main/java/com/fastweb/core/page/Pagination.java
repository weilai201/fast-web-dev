package com.fastweb.core.page;

public class Pagination {
    private int pageNo = 1;
    private int pageSize = 10;
    private int total;

    public Pagination() {
        super();
    }

    public Pagination(int pageNo, int pageSize) {
        super();
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public Pagination(int pageNo, int pageSize, int total) {
        super();
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.total = total;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Pagination [pageNo=");
        builder.append(pageNo);
        builder.append(", pageSize=");
        builder.append(pageSize);
        builder.append(", total=");
        builder.append(total);
        builder.append("]");
        return builder.toString();
    }


}
