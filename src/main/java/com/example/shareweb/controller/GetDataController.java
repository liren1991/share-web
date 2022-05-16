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
import java.net.URI;
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
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final BigDecimal decimal = new BigDecimal(100);
    ;

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
                            symbol.setSymbol(stockSymbols.replace("'",""));
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

    @RequestMapping("/initData.html")
    public void initData() throws URISyntaxException, IOException {
        Type type = new TypeToken<List<ShareStr>>() {
        }.getType();
        List<Symbol> symbolList = symbolMapper.listSymbol();
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

    /**
     * 统计下影线第二天高开或高走 概率
     */
    @RequestMapping("/OpenHigherProbability.html")
    public void OpenHigherProbability() {
        List<Symbol> symbolList = symbolMapper.listSymbolBond();
        Set<Integer> set = new HashSet<>();
        Map<Integer, List<CompareEntity>> compareEntityMap = new HashMap<>();

        List<CompareEntity> compareEntityList = new ArrayList<>();

        for (Symbol symbol : symbolList) {
            List<Share> shares = shareMapper.listShareBySymbol(Integer.parseInt(symbol.getSymbol().substring(2)));
            compareEntityList.addAll(OpenHigherModel.getOpenHigherProbability(shares));
        }

        for (CompareEntity compareEntity : compareEntityList) {
            if (set.add(compareEntity.getProbability())) {
                compareEntityList = new ArrayList<>();
                compareEntityList.add(compareEntity);
                compareEntityMap.put(compareEntity.getProbability(), compareEntityList);
            } else {
                compareEntityMap.get(compareEntity.getProbability()).add(compareEntity);
            }
        }

        ArrayList<Integer> indexList = new ArrayList<>(compareEntityMap.keySet());
        indexList.sort(Comparator.reverseOrder());

        for (Integer key : indexList) {
            List<CompareEntity> tempList = compareEntityMap.get(key);

            double openHigher = 0, nextHigh = 0, average = 0, lowPoint = 0;
            for (CompareEntity item : tempList) {
                if (item.getNextOpen() > item.getClose())
                    openHigher++;
                if (item.getNextHigh() > item.getClose()) {
                    nextHigh++;
                    average += (item.getNextHigh().doubleValue() - item.getClose()) / item.getClose();
                } else {
                    lowPoint += (item.getNextLow().doubleValue() - item.getClose()) / item.getClose();
                }

            }
            logger.info("上隐线点位 {} : 总次数{},  高开概率 {} , 变红概率 {} ,  变红平均点位 {} , 最少亏损点位 {}", key, tempList.size(), openHigher / tempList.size() * 100,
                    nextHigh / tempList.size() * 100, average / tempList.size() * 100, lowPoint / tempList.size() * 100);
        }
        System.out.println();
    }
}
