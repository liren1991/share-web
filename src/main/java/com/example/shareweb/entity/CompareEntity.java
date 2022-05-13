package com.example.shareweb.entity;

import java.util.Date;

public class CompareEntity {
    private Date day;
    private Integer symbol;
    private Integer open;
    private Integer high;
    private Integer low;
    private Integer close;
    private Long volume;
    private Integer nextOpen;
    private Integer nextHigh;
    private Integer nextLow;
    private Integer nextClose;
    private Long nextVolume;
    private Integer probability;

    public CompareEntity( Share currentShare,Share nextShare) {
        this.day = currentShare.getDay();
        this.symbol = currentShare.getSymbol();
        this.open = currentShare.getOpen();
        this.high = currentShare.getHigh();
        this.low = currentShare.getLow();
        this.close = currentShare.getClose();
        this.volume = currentShare.getVolume();
        this.nextOpen = nextShare.getOpen();
        this.nextHigh = nextShare.getHigh();
        this.nextLow = nextShare.getLow();
        this.nextClose = nextShare.getClose();
        this.nextVolume = nextShare.getVolume();
    }

    public Integer getProbability() {
        return probability;
    }

    public void setProbability(Integer probability) {
        this.probability = probability;
    }

    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    public Integer getSymbol() {
        return symbol;
    }

    public void setSymbol(Integer symbol) {
        this.symbol = symbol;
    }

    public Integer getOpen() {
        return open;
    }

    public void setOpen(Integer open) {
        this.open = open;
    }

    public Integer getHigh() {
        return high;
    }

    public void setHigh(Integer high) {
        this.high = high;
    }

    public Integer getLow() {
        return low;
    }

    public void setLow(Integer low) {
        this.low = low;
    }

    public Integer getClose() {
        return close;
    }

    public void setClose(Integer close) {
        this.close = close;
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }

    public Integer getNextOpen() {
        return nextOpen;
    }

    public void setNextOpen(Integer nextOpen) {
        this.nextOpen = nextOpen;
    }

    public Integer getNextHigh() {
        return nextHigh;
    }

    public void setNextHigh(Integer nextHigh) {
        this.nextHigh = nextHigh;
    }

    public Integer getNextLow() {
        return nextLow;
    }

    public void setNextLow(Integer nextLow) {
        this.nextLow = nextLow;
    }

    public Integer getNextClose() {
        return nextClose;
    }

    public void setNextClose(Integer nextClose) {
        this.nextClose = nextClose;
    }

    public Long getNextVolume() {
        return nextVolume;
    }

    public void setNextVolume(Long nextVolume) {
        this.nextVolume = nextVolume;
    }
}
