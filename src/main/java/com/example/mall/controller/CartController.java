package com.example.mall.controller;

import com.example.mall.consts.Const;
import com.example.mall.form.CartAddForm;
import com.example.mall.form.CartUpdateForm;
import com.example.mall.pojo.User;
import com.example.mall.service.ICartService;
import com.example.mall.vo.CartVo;
import com.example.mall.vo.ResponseVo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
public class CartController {

    @Autowired
    private ICartService cartService;

    @GetMapping("/carts")
    public  ResponseVo<CartVo> list(HttpSession session){
        User user= (User) session.getAttribute(Const.SESSION_KEY);
        return cartService.list(user.getId());
    }

    @PostMapping("/carts")
    public ResponseVo<CartVo> add(@RequestBody @Valid CartAddForm cartAddForm, HttpSession session){
        User user= (User) session.getAttribute(Const.SESSION_KEY);
        return cartService.add(user.getId(),cartAddForm);
    }
    @PutMapping("/carts/{productId}")
    public  ResponseVo<CartVo> update(@PathVariable Integer productId,
                                      @Valid @RequestBody CartUpdateForm cartUpdateForm,
                                      HttpSession session){
        User user= (User) session.getAttribute(Const.SESSION_KEY);
        return cartService.update(user.getId(),productId,cartUpdateForm);
    }
    @DeleteMapping("/carts/{productId}")
    public  ResponseVo<CartVo> delete(@PathVariable Integer productId,HttpSession session){
        User user=(User) session.getAttribute(Const.SESSION_KEY);
        return cartService.delete(user.getId(),productId);
    }
    @PutMapping("carts/selectAll")
    public  ResponseVo<CartVo> select(HttpSession session){
        User user= (User) session.getAttribute(Const.SESSION_KEY);
        return cartService.selectAll(user.getId());
    }
    @PutMapping("carts/unselectAll")
    public  ResponseVo<CartVo> Unselect(HttpSession session){
        User user= (User) session.getAttribute(Const.SESSION_KEY);
        return cartService.unselectAll(user.getId());
    }
    @GetMapping("/carts/product/sum")
    public  ResponseVo<Integer> sum(HttpSession session){
        User user= (User) session.getAttribute(Const.SESSION_KEY);
        return cartService.sum(user.getId());
    }


}
