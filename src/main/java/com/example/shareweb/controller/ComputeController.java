package com.example.shareweb.controller;

import com.example.shareweb.entity.CompareEntity;
import com.example.shareweb.entity.Share;
import com.example.shareweb.entity.Symbol;
import com.example.shareweb.mapper.ShareMapper;
import com.example.shareweb.mapper.SymbolMapper;
import com.example.shareweb.model.OpenHigherModel;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class ComputeController {
    @Resource
    private SymbolMapper symbolMapper;
    @Resource
    private ShareMapper shareMapper;
    Logger logger = LoggerFactory.getLogger(ComputeController.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final BigDecimal decimal = new BigDecimal(100);
    public static Gson gson = new Gson();

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
