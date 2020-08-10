package com.example.mall.service.impl;

import com.example.mall.dao.UserMapper;
import com.example.mall.enums.ResponseEnum;
import com.example.mall.enums.RoleEnum;
import com.example.mall.pojo.User;
import com.example.mall.service.IUserService;
import com.example.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

@Service
public class IuserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;
    /**
     * 注册
     * @param user
     */
    @Override
    public ResponseVo register(User user) {
        user.setRole(RoleEnum.CUSTOMER.getCode());
        if (userMapper.CountByUsername(user.getUsername())!=0||userMapper.CountByEmail(user.getEmail())!=0){
            return ResponseVo.error(ResponseEnum.USER_EXIST);
        }
        //MD5摘要算法
        user.setPassword(DigestUtils.md5DigestAsHex(
                user.getPassword().getBytes(StandardCharsets.UTF_8))
        );
        int resultCount= userMapper.insertSelective(user);
        if (resultCount==0)
        {
           return ResponseVo.error(ResponseEnum.ERROR);
        }
        return ResponseVo.successByMsg("注册成功");
    }
    public ResponseVo<User> login(String username, String password){
        User user=userMapper.selectByUsername(username);
        if(user==null){
            return ResponseVo.error(ResponseEnum.USERNAME_OR_PASSWORD_ERROR);
        }
        if (!user.getPassword().equalsIgnoreCase(
                DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8))
        )){
            return  ResponseVo.error(ResponseEnum.USERNAME_OR_PASSWORD_ERROR);
        }
        user.setPassword(null);
        return ResponseVo.success(user);

    }
}
