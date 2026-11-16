package com.zhuofan.oms.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class Page<T> {

    private List<T> rows;

    private long total;

    private long current;

    private long size;

    public Page(List<T> rows, long total) {
        this.rows = rows;
        this.total = total;
    }

    public Page(long current, long size) {
        this.current = current;
        this.size = size;
    }

    public static <T> Page<T> emptyPage() {
        return new Page<>(Collections.emptyList(), 0);
    }

    public static <T> Page<T> of(List<T> rows, long total) {
        return new Page<>(rows, total);
    }

}
