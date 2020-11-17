package com.at.multiple_datasource.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ProductDao {

    @Select("SELECT stock FROM product WHERE ID = #{productId}")
    Integer getStock(@Param("productId") Long productId);

    @Update("UPDATE product SET stock = stock - #{amount} WHERE id=#{productId} AND stock >= #{amount}")
    int reduceStock(@Param("productId") Long productId, @Param("amount") Integer amount);
}
