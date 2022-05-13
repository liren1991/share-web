package com.example.shareweb;

public interface Constant {

    String 获取当天数据 = "https://quotes.sina.cn/cn/api/jsonp_v2.php/=/CN_MarketDataService.getKLineData?symbol=%s&scale=240&ma=no&datalen=512";
    String 转债代码列表 = "https://vip.stock.finance.sina.com.cn/quotes_service/api/json_v2.php/Market_Center.getHQNodeDataSimple?page=%s&num=100&sort=symbol&asc=1&node=hskzz_z";
    String 股票代码列表 = "https://vip.stock.finance.sina.com.cn/quotes_service/api/json_v2.php/Market_Center.getHQNodeData?page=%s&num=100&sort=changepercent&asc=0&node=hs_a&symbol=&_s_r_a=page";
}
