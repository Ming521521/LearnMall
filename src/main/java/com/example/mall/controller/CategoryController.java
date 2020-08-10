package com.example.mall.controller;

import com.example.mall.service.ICategoryService;
import com.example.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.enterprise.inject.Alternative;

@RestController
public class CategoryController {
    @Autowired
    private ICategoryService iCategoryService;
    @GetMapping("/categories")
    public ResponseVo categories()
    {
        return iCategoryService.selectAll();
    }
}
