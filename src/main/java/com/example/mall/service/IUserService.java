package com.example.mall.service;

import com.example.mall.pojo.User;
import com.example.mall.vo.ResponseVo;

public interface IUserService {

    ResponseVo register(User user);
    ResponseVo<User> login(String username,String password);
}
