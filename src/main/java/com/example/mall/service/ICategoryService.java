package com.example.mall.service;

import com.example.mall.vo.CategoryVo;
import com.example.mall.vo.ResponseVo;

import java.util.List;
import java.util.Set;

public interface ICategoryService {
    ResponseVo<List<CategoryVo>> selectAll();
    Set<Integer> findSubCategoryId(Integer id, Set<Integer> result);
}
