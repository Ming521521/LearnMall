package com.example.mall.controller;

import com.example.mall.consts.Const;
import com.example.mall.form.ShippingForm;
import com.example.mall.pojo.User;
import com.example.mall.service.ShippingService;
import com.example.mall.vo.ResponseVo;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * @author ：Ming
 * @date ：Created in 2020/8/2 0002 21:48
 */
@RestController
public class ShippingController {
    @Autowired
    private ShippingService shippingService;
    @PostMapping("/shippings")
    public ResponseVo add (@Valid @RequestBody ShippingForm shippingForm, HttpSession session){
        User user= (User) session.getAttribute(Const.SESSION_KEY);
        return shippingService.add(user.getId(),shippingForm);
    }
    @DeleteMapping("/shippings/{shippingId}")
    public  ResponseVo delete(@PathVariable Integer shippingId, HttpSession session){
        User user= (User) session.getAttribute(Const.SESSION_KEY);
        return  shippingService.delete(user.getId(),shippingId);
    }
    @PutMapping("/shippings/{shippingId}")
    public  ResponseVo update(@PathVariable Integer shippingId,HttpSession session,ShippingForm form){
        User user= (User) session.getAttribute(Const.SESSION_KEY);
        return  shippingService.update(user.getId(),shippingId,form);
    }
    @GetMapping("/shippings")
    public  ResponseVo<PageInfo> list(@RequestParam(required = false,defaultValue ="1") Integer pageNum,
                                      @RequestParam(required = false,defaultValue = "10") Integer pageSize,
                                      HttpSession session){
        User user= (User) session.getAttribute(Const.SESSION_KEY);
        return shippingService.list(user.getId(),pageNum,pageSize);
    }
}
