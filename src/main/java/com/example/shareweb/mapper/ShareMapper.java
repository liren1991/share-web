package com.example.shareweb.mapper;

import com.example.shareweb.entity.Share;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ShareMapper {

    String fields = " ts.day day, ts.open open, ts.high high, ts.low low, ts.close close, ts.volume volume, ts.symbol symbol ";

    /**
     * 根据ID查询 Share 实体
     */
    @Select(" select " + fields + " from t_share ts where ts.symbol = #{symbol}")
    List<Share> listShareBySymbol(@Param(value = "symbol") Integer symbol);


    /**
     * 保存 Share 实体
     */
    @Insert("<script> insert into t_share " +
            "<trim prefix=' ( ' suffix=' ) ' suffixOverrides=' , '>" +
            "<if test='day !=null '>day,</if><if test='open !=null '>open,</if><if test='high !=null '>high,</if><if test='low !=null '>low,</if><if test='close !=null '>close,</if><if test='volume !=null '>volume,</if><if test='symbol !=null '>symbol</if>" +
            "</trim>" +
            "<trim prefix=' values( ' suffix=' ) ' suffixOverrides=','><if test='day !=null '>#{day},</if><if test='open !=null '>#{open},</if><if test='high !=null '>#{high},</if><if test='low !=null '>#{low},</if><if test='close !=null '>#{close},</if><if test='volume !=null '>#{volume},</if><if test='symbol !=null '>#{symbol}</if>"+
            "</trim></script>")
    Integer addShare( Share Share);

}
