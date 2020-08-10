package com.example.mall.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CartProductVo {

    private Integer id;
    private  Integer quantity;
    private  String productName;
    private  String productSubtitle;
    private  String productMainImage;
    private BigDecimal productPrice;
    private Integer productStatus;
    /**
     * 总价  productPrice*quantity
     */
    private BigDecimal productTotalPrice;
    private  Integer productStock;
    /**
     * 是否选中
     */
    private  Boolean productSelected;

    public CartProductVo(Integer id, Integer quantity, String productName, String productSubtitle, String productMainImage, BigDecimal productPrice, Integer productStatus, BigDecimal productTotalPrice, Integer productStock, Boolean productSelected) {
        this.id = id;
        this.quantity = quantity;
        this.productName = productName;
        this.productSubtitle = productSubtitle;
        this.productMainImage = productMainImage;
        this.productPrice = productPrice;
        this.productStatus = productStatus;
        this.productTotalPrice = productTotalPrice;
        this.productStock = productStock;
        this.productSelected = productSelected;
    }
}
