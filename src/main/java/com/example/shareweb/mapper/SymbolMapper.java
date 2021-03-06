package com.example.shareweb.mapper;

import com.example.shareweb.entity.Symbol;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * SymbolDao.java
 * @version 1.0.0
 */
public interface SymbolMapper {


    String fields = " ts.name name, ts.symbol symbol, ts.status status ";

    /**
     * 根据ID查询 Symbol 实体
     */
    @Select(" select " + fields + " from t_symbol ts where ts.symbol = #{symbol}")
    Symbol getSymbolByPrimaryId(@Param(value = "symbol") String symbol);

    /**
     * 保存 Symbol 实体
     */
    @Insert("<script> insert into t_symbol " +
            "<trim prefix=' ( ' suffix=' ) ' suffixOverrides=' , '>" +
            "<if test='name !=null '>name,</if><if test='symbol !=null '>symbol,</if><if test='status !=null '>status</if>" +
            "</trim>" +
            "<trim prefix=' values( ' suffix=' ) ' suffixOverrides=','><if test='name !=null '>#{name},</if><if test='symbol !=null '>#{symbol},</if><if test='status !=null '>#{status}</if>"+
            "</trim></script>")
    Integer addSymbol( Symbol Symbol);


    @Select("<script>  select symbol from t_symbol  <if test='status !=0 '> where status = #{status} </if>  </script>")
    List<Symbol> listSymbol(@Param("status") Integer status);

    @Select(" select symbol from t_symbol  where symbol like 'sh11%' or symbol like 'sz12%' and status = 1")
    List<Symbol> listSymbolBond();

    @Update(" update t_symbol set remain_vol = #{remainVol} where symbol = #{symbol}")
    Integer updateSymbolRemainVol(@Param("remainVol") Double remainVol , @Param("symbol") String symbol);
}