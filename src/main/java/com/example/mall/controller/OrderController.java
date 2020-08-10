package com.example.mall.controller;

import com.example.mall.consts.Const;
import com.example.mall.form.OrderForm;
import com.example.mall.pojo.User;
import com.example.mall.service.IOrderService;
import com.example.mall.vo.OrderVo;
import com.example.mall.vo.ResponseVo;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * @author ：Ming
 * @date ：Created in 2020/8/5 0005 22:46
 */
@RestController
public class OrderController {
    @Autowired
    private IOrderService orderService;
    @PostMapping("/orders")
    public ResponseVo<OrderVo> create(@Valid @RequestBody OrderForm orderForm, HttpSession session){
        User user= (User) session.getAttribute(Const.SESSION_KEY);
        return orderService.create(user.getId(),orderForm.getShippingId());
    }
    @GetMapping("/orders")
    public ResponseVo<PageInfo> list( @RequestParam(required = false ,defaultValue = "10") Integer pageSize,
                                      @RequestParam(required = false,defaultValue = "1") Integer pageNumber,
                                      HttpSession session){
        User user= (User) session.getAttribute(Const.SESSION_KEY);
        return  orderService.list(user.getId(),pageNumber,pageSize);
    }
    @GetMapping("/orders/{orderNo}")
    public  ResponseVo<OrderVo> detail(@PathVariable("orderNo") Long orderNo,HttpSession session){
        User user= (User) session.getAttribute(Const.SESSION_KEY);
        return  orderService.detail(user.getId(),orderNo);
    }
    @PutMapping("/orders/{orderNo}")
    public  ResponseVo cancel(@PathVariable Long orderNo,HttpSession session){
        User user= (User) session.getAttribute(Const.SESSION_KEY);
        return  orderService.cancel(user.getId(),orderNo);
    }

}
