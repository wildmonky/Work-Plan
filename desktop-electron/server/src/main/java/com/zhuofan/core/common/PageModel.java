/**
 *
 */
package com.zhuofan.core.common;

/**
 * @author Administrator
 *
 */
public class PageModel {
    // 当前页
    private int pageCurrent;

    // 每页显示的最大记录数
    private int pageSize;

    // 总页数
    private int pageCnt;

    // 查询的记录数
    private int recordCnt;

    public int getPageCurrent() {
        return pageCurrent;
    }

    public void setPageCurrent(int pageCurrent) {
        this.pageCurrent = pageCurrent;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageCnt() {
        return pageCnt;
    }

    public void setPageCnt(int pageCnt) {
        this.pageCnt = pageCnt;
    }

    public int getRecordCnt() {
        return recordCnt;
    }

    public void setRecordCnt(int recordCnt) {
        this.recordCnt = recordCnt;
    }

    public void init(int pageCurrent, int pageSize, int recordCnt) {
        this.pageCurrent = pageCurrent;
        this.pageSize = pageSize;
        this.recordCnt = recordCnt;

        //计算总页数
        this.pageCnt = (int)(recordCnt/pageSize + (recordCnt%pageSize == 0?0:1));
    }

}
