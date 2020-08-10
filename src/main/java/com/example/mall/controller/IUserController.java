package com.example.mall.controller;


import com.example.mall.consts.Const;
import com.example.mall.enums.ResponseEnum;
import com.example.mall.form.UserLoginForm;
import com.example.mall.form.UserRegisterForm;
import com.example.mall.pojo.User;
import com.example.mall.service.IUserService;
import com.example.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import sun.net.httpserver.HttpsServerImpl;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@RequestMapping("/user")
@Slf4j
public class IUserController {
    @Autowired
    IUserService iUserService;
    @PostMapping("/register")
    @ResponseBody
    public ResponseVo<Object> register(@Valid @RequestBody UserRegisterForm userRegisterForm, BindingResult bindingResult){
       User  user=new User();
        BeanUtils.copyProperties(userRegisterForm,user);
        return iUserService.register(user);

    }

    @PostMapping("/login")
    @ResponseBody
    public  ResponseVo<User> login(@Valid @RequestBody UserLoginForm userLoginForm, BindingResult bindingResult, HttpSession session){

        ResponseVo<User> userResponseVo= iUserService.login(userLoginForm.getUsername(),userLoginForm.getPassword());
        session.setAttribute(Const.SESSION_KEY,userResponseVo.getData());
        return  userResponseVo;
    }

    @GetMapping("/info")
    public  ResponseVo<User> userInfo(HttpSession session){
        User user= (User) session.getAttribute(Const.SESSION_KEY);
        return ResponseVo.success(user);
    }

    @PostMapping("/logout")
    public  ResponseVo logOut(HttpSession session){
        session.removeAttribute(Const.SESSION_KEY);
        return ResponseVo.success();
    }

}
