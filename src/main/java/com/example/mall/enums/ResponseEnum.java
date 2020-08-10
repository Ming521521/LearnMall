package com.example.mall.enums;

import lombok.Data;
import lombok.Getter;


@Getter
public enum ResponseEnum {
    ERROR(-1,"服务端错误"),
    SUCCESS(0,"成功"),
    PASSWORD_ERROR(1,"密码错误"),
    USER_EXIST(2,"用户已存在"),
    PARAM_ERROR(3,"验证码错误"),
    NEED_LOGIN(10,"未登录状态"),
    USERNAME_OR_PASSWORD_ERROR(11,"用户名或者密码错误"),
    PRODUCT_OFF_OR_DELETE(12,"商品下架或者删除"),
    PRODUCT_NOT_EXIXT(13,"商品不存在"),
    PRODUCT_STOCK_ERROR(14,"商品库存不足"),
    CART_PRODUCT_NOT_EXIXT(15,"购物车商品不存在"),
    DELETE_CITE(16,"删除地址失败"),
    SHIPPING_NOT_EXIST(17,"地址不存在"),
    CART_SELECTED_IS_EMPTY(18,"请用户选中商品"),

    ORDER_NOT_EXIST(19,"订单不存在"),
    ORDER_STATUS_ERROR(20,"订单状态有误"),
        ;
    Integer code;
    String msg;
    ResponseEnum(Integer code,String msg){
        this.code=code;
        this.msg=msg;
    }
}
