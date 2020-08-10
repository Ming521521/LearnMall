package com.example.mall.dao;

import com.example.mall.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    int batchInsert(@Param("orderItemList")List<OrderItem> orderItems);

    List<OrderItem> selectByOrderNo (Long orderNo);

    List<OrderItem> selectByOrderNoSet(@Param("orderNoSet")Set<Long> orderNoSet);
}