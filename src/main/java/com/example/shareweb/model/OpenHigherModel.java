package com.example.shareweb.model;

import com.example.shareweb.entity.CompareEntity;
import com.example.shareweb.entity.Share;
import java.math.BigDecimal;
import java.util.*;

public class OpenHigherModel {
    // day open high low close volume symbol


    /**
     * 统计下影线
     */
    public static List<CompareEntity> getOpenHigherProbability(List<Share> list) {
        List<CompareEntity> compareEntityList = new ArrayList<>();
        BigDecimal decimal = new BigDecimal(100);
        for (int i = 0; i < list.size(); i++) {
            Share share = list.get(i);
            int subtract = share.getOpen() > share.getClose() ? share.getClose() : share.getOpen();
            int probability = new BigDecimal(subtract).subtract(new BigDecimal(share.getLow())).divide(new BigDecimal(share.getOpen()), 2, BigDecimal.ROUND_HALF_UP).multiply(decimal).intValue();

            if (probability > 0 && i < list.size() - 1) {
                CompareEntity compareEntity = new CompareEntity(share, list.get(i + 1));
                compareEntity.setProbability(probability);
                compareEntityList.add(compareEntity);
            }
        }
        return compareEntityList;
    }


}
