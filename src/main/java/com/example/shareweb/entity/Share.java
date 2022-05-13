package com.example.shareweb.entity;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
* Share.java
* @version 1.0.0
*/
public class Share implements Serializable {

    private Date day;    
    private Integer open;  
    private Integer high;  
    private Integer low;  
    private Integer close;  
    private Long volume;
    private Integer symbol;

    public Share() {
    }

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static BigDecimal temp = new BigDecimal("1000");
    public Share(ShareStr shareStr) throws ParseException {
        this.day = dateFormat.parse(shareStr.getDay());
        this.open = new BigDecimal(shareStr.getOpen()).multiply(temp).intValue();
        this.high = new BigDecimal(shareStr.getHigh()).multiply(temp).intValue();
        this.low = new BigDecimal(shareStr.getLow()).multiply(temp).intValue();
        this.close = new BigDecimal(shareStr.getClose()).multiply(temp).intValue();

        String substring = shareStr.getSymbol().substring(2);
        if (substring.startsWith("11") || substring.startsWith("12"))
            this.volume = Long.parseLong(shareStr.getVolume())/10;
        else
            this.volume = Long.parseLong(shareStr.getVolume())/100;
        this.symbol = Integer.parseInt(substring);
    }

    public void setDay(Date day) { this.day = day; }
    public Date getDay() { return this.day; }
    public void setOpen(Integer open) { this.open = open; }
    public Integer getOpen() { return this.open; }
    public void setHigh(Integer high) { this.high = high; }
    public Integer getHigh() { return this.high; }
    public void setLow(Integer low) { this.low = low; }
    public Integer getLow() { return this.low; }
    public void setClose(Integer close) { this.close = close; }
    public Integer getClose() { return this.close; }
    public void setSymbol(Integer symbol) { this.symbol = symbol; }
    public Integer getSymbol() { return this.symbol; }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }
}
