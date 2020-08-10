package com.example.mall.service.impl;

import com.example.mall.dao.CategoryMapper;
import com.example.mall.dao.ProductMapper;
import com.example.mall.enums.ProductEnum;
import com.example.mall.enums.ResponseEnum;
import com.example.mall.pojo.Product;
import com.example.mall.service.ICategoryService;
import com.example.mall.service.IProductService;
import com.example.mall.vo.ProductDetailVo;
import com.example.mall.vo.ProductVo;
import com.example.mall.vo.ResponseVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class IProductServiceImpl implements IProductService {

    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private ProductMapper productMapper;
     @Override
    public ResponseVo<PageInfo> list(Integer categoryId, Integer pageNum, Integer pageSize) {
        Set<Integer> categoryIdSet=new HashSet<>();
        if(categoryId!=null) {
            categoryIdSet = categoryService.findSubCategoryId(categoryId, categoryIdSet);
            categoryIdSet.add(categoryId);
        }
        PageHelper.startPage(pageNum,pageSize);
        List<Product>  products=productMapper.selectByCategoryIdSet(categoryIdSet.size()==0 ? null : categoryIdSet);
        List<ProductVo> productVos= products
                .stream().map(e-> {
            ProductVo productVo = new ProductVo();
            BeanUtils.copyProperties(e, productVo);
            return productVo;
        }).collect(Collectors.toList());
        PageInfo pageInfo=new PageInfo(products);
        pageInfo.setList(productVos);
        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<ProductDetailVo> detail(Integer id) {
        Product product= productMapper.selectByPrimaryKey(id);
        if(product.getStatus().equals(ProductEnum.OFF_SALE.getCode())|| product.getStatus().equals(ProductEnum.DELETE.getCode()))
        {
            return  ResponseVo.error(ResponseEnum.PRODUCT_OFF_OR_DELETE);
        }
        ProductDetailVo productVo=new ProductDetailVo();
        BeanUtils.copyProperties(product,productVo);
        productVo.setStock(product.getStock()>100?100:product.getStock());
        return ResponseVo.success(productVo);
    }
}
