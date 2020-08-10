package com.example.mall.form;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author ：Ming
 * @date ：Created in 2020/8/5 0005 22:47
 */
@Data
public class OrderForm {
    @NotNull
    Integer shippingId;
}
