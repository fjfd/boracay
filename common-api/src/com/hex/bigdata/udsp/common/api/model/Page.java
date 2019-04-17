package com.hex.bigdata.udsp.common.api.model;

/**
 * Created by junjiem on 2017-3-2.
 */
public class Page {
    private int pageIndex = 1; // 页号
    private int pageSize = 10; // 条数

    private long totalPage; // 总页数
    private long totalCount; // 总记录数

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(long totalPage) {
        this.totalPage = totalPage;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
        this.totalPage = totalCount % pageSize == 0 ? totalCount / pageSize : (totalCount / pageSize + 1);
    }
}
