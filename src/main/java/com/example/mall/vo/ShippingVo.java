package com.example.mall.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author ：Ming
 * @date ：Created in 2020/8/2 0002 21:23
 */
@Data
public class ShippingVo {
    private Integer id;

    private Integer userId;

    private String receiverName;

    private String receiverPhone;

    private String receiverMobile;

    private String receiverProvince;

    private String receiverCity;

    private String receiverDistrict;

    private String receiverAddress;

    private String receiverZip;

    private Date createTime;

    private Date updateTime;
}
