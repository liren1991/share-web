package com.example.shareweb.controller;

import com.example.shareweb.RequestUtil;
import com.example.shareweb.entity.*;
import com.example.shareweb.mapper.ShareMapper;
import com.example.shareweb.mapper.SymbolMapper;
import com.example.shareweb.model.OpenHigherModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.example.shareweb.Constant.转债详情页面;

@RestController
public class GetDataController {
    public static Gson gson = new Gson();
    Logger logger = LoggerFactory.getLogger(GetDataController.class);
    @Resource
    private SymbolMapper symbolMapper;
    @Resource
    private ShareMapper shareMapper;
    // 刷新股票代码
    @RequestMapping("/refresh.html")
    public void refreshSymbol(@RequestParam(name = "requestType", defaultValue = "1") Integer requestType) throws URISyntaxException, IOException {
        Type type = new TypeToken<List<Symbol>>() {
        }.getType();
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            List<Symbol> symbols;
            int index = 1;
            do {
                HttpGet httpGet = RequestUtil.getHttpRequest(requestType, index);
                CloseableHttpResponse response = httpclient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity);
                symbols = gson.fromJson(result, type);
                if (symbols != null && symbols.size() > 0) {
                    for (Symbol symbol : symbols) {
                        try {
                            symbol.setStatus(1);
                            // 新增转债代号
                            symbolMapper.addSymbol(symbol);
                            // 新增转债对应正股代号
                            Document parse = Jsoup.parse(new URL(String.format(转债详情页面, symbol.getSymbol())), 1000 * 10);
                            String string = parse.toString();
                            String stockSymbols = string.substring(string.indexOf("var relatedStock =") + "var relatedStock =".length() + 1, string.indexOf("//可转债相关股票") - 1);
                            symbol.setSymbol(stockSymbols.replace("'", ""));
                            symbol.setStatus(2);
                            symbolMapper.addSymbol(symbol);
                        } catch (Exception e) {
                            logger.info("添加股票代码失败：{}, 名称： {}", symbol.getSymbol(), symbol.getName());
                            e.printStackTrace();
                        }
                    }
                }
                index++;
            } while (symbols != null && symbols.size() > 0);
        }
    }
    // 初始化最近两年日成交数据
    @RequestMapping("/initData.html")
    public void initData() throws URISyntaxException, IOException {
        Type type = new TypeToken<List<ShareStr>>() {
        }.getType();
        List<Symbol> symbolList = symbolMapper.listSymbol(null);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            for (Symbol symbol : symbolList) {
                HttpGet httpGet = RequestUtil.getHttpRequest(3, symbol.getSymbol());
                CloseableHttpResponse response = httpclient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity);
                try {
                    result = result.substring(result.indexOf("["), result.lastIndexOf("]") + 1);
                } catch (Exception e) {
                    System.out.println("错误返回结果：  " + result);
                    e.printStackTrace();
                    continue;
                }
                List<ShareStr> shares = gson.fromJson(result, type);
                if (shares != null && shares.size() > 0) {
                    for (ShareStr item : shares) {
                        try {
                            item.setSymbol(symbol.getSymbol());
                            Share share = new Share(item);
                            if (share.getDay().after(dateFormat.parse("2020-01-01")))
                                shareMapper.addShare(share);
                        } catch (Exception e) {
                            logger.info("添加股票数据失败：{}, 名称： {}", symbol.getSymbol(), symbol.getName());
                            e.printStackTrace();
                        }
                    }

                }
            }

        }
        System.out.println("初始化完成");
    }



    private static final String url = "https://emh5.eastmoney.com/api/KeZhuanZhai/JiBenXinXi/GetJiBenTiaoKuan";
    private static final String 债券代码 = "BONDCODE";
    private static final String 债券简称 = "BONDNAME";
    private static final String 票面利率 = "CURRENTRATE";
    private static final String 距下一付息日 = "DAYTONXCUPNDATE";
    private static final String 转股溢价率 = "TOSTOCKMARGIN";
    private static final String 下一付息日 = "FRSTVALUEDATE";
    private static final String 余额 = "REMAINVOL";
    // 获取转债余额
    @RequestMapping("/getShareDetail.html")
    public void getShareDetail() {
        List<Symbol> shares = symbolMapper.listSymbol(1);
        List<Detail> detailList = getDataList(shares);
        for (Detail item : detailList) {
            if (item.remainValue.equals("--"))
                continue;
            String[] split = item.bondCode.split("\\.");
            try {
                String symbol = split[1].toLowerCase(Locale.ROOT) + split[0];
                symbolMapper.updateSymbolRemainVol(Double.parseDouble(item.remainValue.substring(0, item.remainValue.length() - 1)), symbol);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private static List<Detail> getDataList(List<Symbol> shareList) {
        List<Detail> detailList = new ArrayList<>();

        for (Symbol entity : shareList) {
            Map<String, String> resultMap = getLiXiEntity(entity.getSymbol().substring(2));
            if (resultMap == null)
                continue;
            Detail detail = new Detail();
            detail.bondCode = resultMap.get(债券代码);
            detail.bondName = resultMap.get(债券简称);
            detail.currentRate = resultMap.get(票面利率);
            detail.payInterest = resultMap.get(距下一付息日);
            detail.toStockMargin = resultMap.get(转股溢价率);
            detail.firstValueDate = resultMap.get(下一付息日);
            detail.remainValue = resultMap.get(余额);
            detailList.add(detail);
        }
        return detailList;
    }

    private static Map<String, String> getLiXiEntity(String param) {
        HashMap<String, Object> resultMap;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String result;
        try {
            //创建http请求
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "application/json");
            //创建请求内容
            String jsonStr = "";
            if (param.startsWith("11"))
                jsonStr = "{\"fc\":\"" + param + "01" + "\",\"color\":\"w\"}";
            else if (param.startsWith("12"))
                jsonStr = "{\"fc\":\"" + param + "02" + "\",\"color\":\"w\"}";

            StringEntity entity = new StringEntity(jsonStr);
            httpPost.setEntity(entity);
            response = httpClient.execute(httpPost);
            result = EntityUtils.toString(response.getEntity(), "utf-8");
            resultMap = gson.fromJson(result, HashMap.class);
            Map map = (Map) resultMap.get("Result");
            Object jiBenTiaoKuan = map.get("JiBenTiaoKuan");
            return (Map) jiBenTiaoKuan;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            if (response != null) {
                try {
                    response.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        return null;
    }

    private static class Detail {
        private String bondCode;    // 债券代码
        private String bondName;    // 债券简称
        private String currentRate; // 票面利率
        private String payInterest; // 距下一付息日
        private String toStockMargin; // 转股溢价率
        private String firstValueDate; // 下一付息日
        private String remainValue; //余额
    }
}
