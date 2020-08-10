package com.example.mall.interceptor;

import com.example.mall.consts.Const;
import com.example.mall.exception.UserLoginException;
import com.example.mall.pojo.User;
import com.sun.xml.internal.fastinfoset.tools.SAXEventSerializer;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class UserLoginInterceptor implements HandlerInterceptor {

    /**
     * 登录状态检测
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session=request.getSession();
        User user= (User) session.getAttribute(Const.SESSION_KEY);
        if (user==null){
            throw new UserLoginException();
        }
        return  true;
    }
}
