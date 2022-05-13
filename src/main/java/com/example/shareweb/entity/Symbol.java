package com.example.shareweb.entity;
import java.io.Serializable;

/**
* Symbol.java
* @version 1.0.0
*/
public class Symbol implements Serializable {

    private String name;  
    private String symbol;
    private Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setName(String name) { this.name = name; }
    public String getName() { return this.name; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public String getSymbol() { return this.symbol; }
}
