package com.example.shareweb;

import org.apache.http.client.methods.HttpGet;

import java.net.URI;
import java.net.URISyntaxException;

import static com.example.shareweb.Constant.*;

public class RequestUtil {


    public static HttpGet getHttpRequest(int type,Object... param) throws URISyntaxException {
        HttpGet httpget = new HttpGet();
        switch (type) {
            case 1:
                httpget.setURI(new URI(String.format(股票代码列表, param)));
                break;
            case 2:
                httpget.setURI(new URI(String.format(转债代码列表, param)));
                break;
            case 3:
                httpget.setURI(new URI(String.format(获取当天数据, param)));
                break;
            case 4:
                httpget.setURI(new URI(String.format(转债详情页面, param)));
                break;
        }
        return httpget;
    }
}
