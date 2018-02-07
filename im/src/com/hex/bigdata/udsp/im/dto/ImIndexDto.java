package com.hex.bigdata.udsp.im.dto;

/**
 * Created by PC on 2018/1/22.
 */
public class ImIndexDto {
    public ImIndexDto() {
    }

    public ImIndexDto(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    private int rowIndex;

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }
}
