package com.example.mall.service.impl;

import com.example.mall.dao.ShippingMapper;
import com.example.mall.enums.ResponseEnum;
import com.example.mall.form.ShippingForm;
import com.example.mall.pojo.Shipping;
import com.example.mall.service.ShippingService;
import com.example.mall.vo.ResponseVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：Ming
 * @date ：Created in 2020/8/2 0002 21:16
 */
@Service
public class ShippingServiceImpl implements ShippingService {
    @Autowired
    private ShippingMapper shippingMapper;
    @Override
    public ResponseVo<Map<String,Integer>> add(Integer uid, ShippingForm form) {
        Shipping shipping=new Shipping();
        shipping.setUserId(uid);
        BeanUtils.copyProperties(form,shipping);
        int row= shippingMapper.insertSelective(shipping);
        if (row==0){
            return  ResponseVo.error(ResponseEnum.ERROR,"新建地址失败");
        }
        Map<String,Integer> map=new HashMap<>();
        map.put("shippingId",shipping.getId());
        return ResponseVo.success(map);
    }

    @Override
    public ResponseVo delete(Integer uid, Integer shippingId) {
        int row= shippingMapper.deleteByIdAndUid(uid, shippingId);
        if (row==0){
            return  ResponseVo.error(ResponseEnum.DELETE_CITE);
        }
        return ResponseVo.success();
    }

    @Override
    public ResponseVo update(Integer uid, Integer shippingId, ShippingForm form) {
        Shipping shipping=new Shipping();
        BeanUtils.copyProperties(shipping,form);
        shipping.setUserId(uid);
        shipping.setId(shippingId);
        int row= shippingMapper.updateByPrimaryKeySelective(shipping);
        if (row==0){
            return ResponseVo.error(ResponseEnum.ERROR,"更新地址失败");
        }
        return ResponseVo.success();
    }

    @Override
    public ResponseVo<PageInfo> list(Integer uid, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippings=shippingMapper.selectByUserId(uid);
        PageInfo pageInfo=new PageInfo(shippings);
        return  ResponseVo.success(pageInfo);
    }
}
