package com.example.mall.form;

import lombok.Data;

@Data
public class CartUpdateForm {
    private  Integer quantity;

    private  Boolean selected=true;
}
