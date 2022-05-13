package com.example.shareweb.entity;

import java.util.ArrayList;
import java.util.List;

public class ComputeEntity {
    private List<String> dayStrList = new ArrayList<>();
    private Integer symbol;
    private Integer probability;
    private Integer highOpenCount = 0;  // 高开次数
    private Integer highCount = 0;  // 最高金额大于前一天开盘金额的次数
    private Integer highOpenPointTotal =0;  // 高开金额总和 （当天最高金额 - 上一天收盘金额）/上一天收盘金额
    private Integer lowOpenCount = 0;  // 低开次数
    private Integer lowCount = 0;  // 最高金额小于前一天开盘金额的次数
    private Integer lowOpenPointTotal = 0;  // 低开点数总和 （上一天收盘金额 - 当天最高金额）/上一天收盘金额

    public Integer getProbability() {
        return probability;
    }

    public void setProbability(Integer probability) {
        this.probability = probability;
    }

    public List<String> getDayStrList() {
        return dayStrList;
    }

    public void setDayStrList(List<String> dayStrList) {
        this.dayStrList = dayStrList;
    }

    public Integer getSymbol() {
        return symbol;
    }

    public void setSymbol(Integer symbol) {
        this.symbol = symbol;
    }

    public Integer getHighOpenCount() {
        return highOpenCount;
    }

    public void setHighOpenCount(Integer highOpenCount) {
        this.highOpenCount = highOpenCount;
    }

    public Integer getHighCount() {
        return highCount;
    }

    public void setHighCount(Integer highCount) {
        this.highCount = highCount;
    }

    public Integer getHighOpenPointTotal() {
        return highOpenPointTotal;
    }

    public void setHighOpenPointTotal(Integer highOpenPointTotal) {
        this.highOpenPointTotal = highOpenPointTotal;
    }

    public Integer getLowOpenPointTotal() {
        return lowOpenPointTotal;
    }

    public void setLowOpenPointTotal(Integer lowOpenPointTotal) {
        this.lowOpenPointTotal = lowOpenPointTotal;
    }

    public Integer getLowOpenCount() {
        return lowOpenCount;
    }

    public void setLowOpenCount(Integer lowOpenCount) {
        this.lowOpenCount = lowOpenCount;
    }

    public Integer getLowCount() {
        return lowCount;
    }

    public void setLowCount(Integer lowCount) {
        this.lowCount = lowCount;
    }

}
