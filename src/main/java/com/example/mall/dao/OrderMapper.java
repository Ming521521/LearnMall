package com.example.mall.dao;

import com.example.mall.pojo.Order;
import com.example.mall.vo.OrderVo;

import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    List<Order> selectByUid(Integer uid);
    Order selectByOrderNo(Long orderNo);
}