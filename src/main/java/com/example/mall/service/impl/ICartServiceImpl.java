package com.example.mall.service.impl;

import com.example.mall.dao.ProductMapper;
import com.example.mall.enums.ProductEnum;
import com.example.mall.enums.ResponseEnum;
import com.example.mall.form.CartAddForm;
import com.example.mall.form.CartUpdateForm;
import com.example.mall.pojo.Cart;
import com.example.mall.pojo.Product;
import com.example.mall.service.ICartService;
import com.example.mall.vo.CartProductVo;
import com.example.mall.vo.CartVo;
import com.example.mall.vo.ResponseVo;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
@Service
public class ICartServiceImpl implements ICartService {
    private  final  static  String CART_REDIS_TEMPLE="cart_%d";
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    private  Gson  gson = new Gson();
    @Override
    public ResponseVo<CartVo> add(Integer uid,CartAddForm form) {
        Integer quantity=1;
        Product product=productMapper.selectByPrimaryKey(form.getProductId());

        if (product==null){
            return  ResponseVo.error(ResponseEnum.PRODUCT_NOT_EXIXT);
        }
        if (!product.getStatus().equals(ProductEnum.ON_SALE.getCode()))
        {
            return ResponseVo.error(ResponseEnum.PRODUCT_OFF_OR_DELETE);
        }
        if (product.getStock()<=0)
        {
            return  ResponseVo.error(ResponseEnum.PRODUCT_STOCK_ERROR);
        }
//        //写入redis
//        redisTemplate.opsForValue().set(String.format(CART_REDIS_TEMPLE,uid)
//                ,gson.toJson(new Cart(product.getId(),quantity,form.getSelected())));
        Cart cart;

        HashOperations<String,String,String> operations=redisTemplate.opsForHash();
        String redisKey=String.format(CART_REDIS_TEMPLE,uid);

        String value=operations.get(redisKey,String.valueOf(product.getId()));
        if(StringUtils.isEmpty(value)){
            //add
            cart=new Cart(product.getId(),quantity,form.getSelected());
        }else {
            //incr
            cart=gson.fromJson(value,Cart.class);
            cart.setQuantity(cart.getQuantity()+quantity);
        }
        operations.put(String.format(CART_REDIS_TEMPLE,uid),
                String.valueOf(product.getId()),gson.toJson(cart));
        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> list(Integer uid) {
        HashOperations<String, String, String> operations = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_TEMPLE, uid);
        Map<String, String> entries = operations.entries(redisKey);
        Boolean selectAll = true;
        Integer quantity = 0;
        BigDecimal totalPrice = BigDecimal.valueOf(0);

        CartVo cartVo = new CartVo();

        String[] array = entries.keySet().toArray(new String[0]);
        Set<Integer> set = new HashSet<>();
        for (String id : array){
            set.add(Integer.parseInt(id));
        }
        List<Product> products = productMapper.selectByPrimaryKeySet(set);
        Map<Integer, Product> productMap = products.stream().collect(Collectors.toMap(Product::getId, Function.identity()));

        List<CartProductVo> cartProductVos = new ArrayList<>();
        //购物车所有产品
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            Integer productId = Integer.valueOf(entry.getKey());
            Cart cart = gson.fromJson(entry.getValue(), Cart.class);
            Product product = productMap.get(productId);
            if (product != null) {
                CartProductVo cartProductVo = new CartProductVo(productId,
                        cart.getQuantity(),
                        product.getName(),
                        product.getSubtitle(),
                        product.getMainImage(),
                        product.getPrice(),
                        product.getStatus(),
                        product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())),
                        product.getStock(),
                        cart.getProductSelected());
                cartProductVos.add(cartProductVo);
                if (!cart.getProductSelected()) {
                    selectAll = false;
                }
                if (cart.getProductSelected()) {
                    totalPrice = totalPrice.add(cartProductVo.getProductTotalPrice());
                }
                quantity += cart.getQuantity();
            }
            cartVo.setSelectAll(selectAll);
            cartVo.setCartProductVoList(cartProductVos);
            cartVo.setCartTotalQuantity(quantity);
            cartVo.setCartTotalPrice(totalPrice);
        }
        return ResponseVo.success(cartVo);
    }

    @Override
    public ResponseVo<CartVo> update(Integer uid, Integer productId, CartUpdateForm cartUpdateForm) {
        HashOperations<String,String,String> operations=redisTemplate.opsForHash();
        String redisKey=String.format(CART_REDIS_TEMPLE,uid);
        Cart cart;
        String value=operations.get(redisKey,String.valueOf(productId));
        if(StringUtils.isEmpty(value)){
            return ResponseVo.error(ResponseEnum.CART_PRODUCT_NOT_EXIXT);
        }else {
            cart=gson.fromJson(value,Cart.class);
            if (cartUpdateForm.getQuantity()!=null&&cartUpdateForm.getQuantity()>0){
                cart.setQuantity(cartUpdateForm.getQuantity());
            }
            if (cartUpdateForm.getSelected()!=null){
                cart.setProductSelected(cartUpdateForm.getSelected());
            }
            operations.put(redisKey,String.valueOf(productId),gson.toJson(cart));
            return  list(uid);
        }
    }

    @Override
    public ResponseVo<CartVo> delete(Integer uid, Integer productId) {
        HashOperations<String,String,String> operations=redisTemplate.opsForHash();
        String redisKey=String.format(CART_REDIS_TEMPLE,uid);

        String value=operations.get(redisKey,String.valueOf(productId));
        if(StringUtils.isEmpty(value)){
            return ResponseVo.error(ResponseEnum.CART_PRODUCT_NOT_EXIXT);
        }
        operations.delete(redisKey,String.valueOf(productId));
        return list(uid);
    }
    public List<Cart> listForCart(Integer uid){
        HashOperations<String, String, String> operations = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_TEMPLE, uid);
        Map<String, String> entries = operations.entries(redisKey);

        List<Cart> carts=new ArrayList<>();
        for (Map.Entry<String,String> entry : entries.entrySet()){
            carts.add(gson.fromJson(entry.getValue(),Cart.class));
        }
        return carts;
    }

    @Override
    public ResponseVo<CartVo> selectAll(Integer uid) {
        HashOperations<String,String,String> operations=redisTemplate.opsForHash();
        String rediskey=String.format(CART_REDIS_TEMPLE,uid);
        for (Cart cart : listForCart(uid)) {
            cart.setProductSelected(true);
            operations.put(rediskey,String.valueOf(cart.getProductId())
                    ,gson.toJson(cart));
        }
        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> unselectAll(Integer uid) {
        HashOperations<String,String,String> operations=redisTemplate.opsForHash();
        String rediskey=String.format(CART_REDIS_TEMPLE,uid);
        for (Cart cart : listForCart(uid)) {
            cart.setProductSelected(false);
            operations.put(rediskey,String.valueOf(cart.getProductId())
                    ,gson.toJson(cart));
        }
        return list(uid);
    }

    @Override
    public ResponseVo<Integer> sum(Integer uid) {
        Integer sum=listForCart(uid).stream().map(Cart::getQuantity).reduce(0,Integer::sum);
        return ResponseVo.success(sum);
    }
}
