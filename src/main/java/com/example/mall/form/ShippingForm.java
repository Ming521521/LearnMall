package com.example.mall.form;

import lombok.Data;

/**
 * @author ：Ming
 * @date ：Created in 2020/8/2 0002 21:21
 */
@Data
public class ShippingForm {
    private String receiverName;

    private String receiverPhone;

    private String receiverMobile;

    private String receiverProvince;

    private String receiverCity;

    private String receiverDistrict;

    private String receiverAddress;

    private String receiverZip;
}
