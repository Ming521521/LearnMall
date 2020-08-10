package com.example.mall.enums;

import lombok.Getter;

/**
 * @author ：Ming
 * @date ：Created in 2020/8/4 0004 22:53
 */
@Getter
public enum  PaymentType {
    PAY_ONLINE(1),
    ;
    Integer code;
    PaymentType(Integer code){
        this.code=code;
    }
}
