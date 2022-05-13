package com.example.shareweb.mapper;

import com.example.shareweb.entity.Symbol;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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


    @Select(" select symbol from t_symbol  where status = 1")
    List<Symbol> listSymbol();

    @Select(" select symbol from t_symbol  where symbol like 'sh11%' or symbol like 'sz12%' and status = 1")
    List<Symbol> listSymbolBond();
}