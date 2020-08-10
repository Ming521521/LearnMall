package com.example.mall.service.impl;

import com.example.mall.consts.Const;
import com.example.mall.dao.CategoryMapper;
import com.example.mall.pojo.Category;
import com.example.mall.service.ICategoryService;
import com.example.mall.vo.CategoryVo;
import com.example.mall.vo.ResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ICategoryServiceImpl implements ICategoryService {
    @Override
    public Set<Integer> findSubCategoryId(Integer id, Set<Integer> result) {
        List<Category> categories=categoryMapper.selectAll();
        for (Category category :categories){
            if (category.getParentId().equals(id)){
                result.add(category.getId());
                findSubCategoryId(category.getId(),result,categories);
            }
        }
        return result;
    }
    private void findSubCategoryId(Integer id, Set<Integer> result,List<Category> categories) {
        for (Category category :categories){
            if (category.getParentId().equals(id)){
                result.add(category.getId());

                findSubCategoryId(category.getId(),result,categories);
            }
        }
    }

    @Autowired
    private CategoryMapper categoryMapper;
    @Override
    public ResponseVo<List<CategoryVo>> selectAll() {
        List<Category> categories=categoryMapper.selectAll();
        List<CategoryVo> categoryVoList;
        //lambda +stream
        //父级目录
        categoryVoList=categories.stream()
                .filter(e->e.getParentId().equals(Const.ROOT_PARENT_ID))
                .map(this::ToCategoryVo)
                .collect(Collectors.toList());
//        for (Category category:categories){
//            if (category.getParentId().equals(Const.ROOT_PARENT_ID)){
//                CategoryVo categoryVo=new CategoryVo();
//                BeanUtils.copyProperties(category,categoryVo);
//                categoryVoList.add(categoryVo);
//            }
//        }
        //查询一级子目录
        findSubCategory(categories,categoryVoList);
        return ResponseVo.success(categoryVoList);
    }

    /**
     * 类型转换
     * @param category
     * @return
     */
    private  CategoryVo ToCategoryVo(Category category){
        CategoryVo categoryVo=new CategoryVo();
        BeanUtils.copyProperties(category,categoryVo);
        return categoryVo;
    }

    private  void findSubCategory(List<Category> categories,List<CategoryVo> categoryVos){
        for (CategoryVo categoryVo : categoryVos){
            List<CategoryVo> subCategoryList=new ArrayList<>();
            for (Category category :categories){
                if (categoryVo.getId().equals(category.getParentId())){
                    CategoryVo SubcategoryVo1=ToCategoryVo(category);
                    subCategoryList.add(SubcategoryVo1);
                }
            }
            //subCategoryList.sort(Comparator.comparing(CategoryVo::getSortOrder).reversed());
            categoryVo.setSubCategories(subCategoryList);
            findSubCategory(categories,subCategoryList);
        }
    }
}
