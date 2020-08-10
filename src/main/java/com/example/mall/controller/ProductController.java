package com.example.mall.controller;

import com.example.mall.service.IProductService;
import com.example.mall.vo.ProductDetailVo;
import com.example.mall.vo.ResponseVo;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {
    @Autowired
    private IProductService productService;
    @GetMapping("/products")
    public ResponseVo<PageInfo> list(@RequestParam(required = false) Integer categoryId,
                                     @RequestParam(required = false ,defaultValue = "1") Integer pageSize,
                                     @RequestParam(required = false,defaultValue = "10") Integer pageNumber){
       return   productService.list(categoryId,pageSize,pageNumber);
    }
    @GetMapping("/products/{id}")
    public  ResponseVo<ProductDetailVo> detail(@PathVariable(name = "id") Integer productId){
        return  productService.detail(productId);
    }

}
