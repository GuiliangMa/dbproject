package com.mgl.server.common.interceptor;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.mgl.server.entity.User;
import com.mgl.server.mapper.UserMapper;
import com.mgl.server.service.UserService;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class JwtInterceptor implements HandlerInterceptor
{
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        String token=request.getHeader("token"); // 从http请求中获得token

        // 如果不是映射到方法直接通过
        if(!(handler instanceof HandlerMethod)){
            return true;
        }

        if(StrUtil.isBlank(token)){
            throw new RuntimeException("无token，请重新登陆");
        }

        // 获取 token 中的username
        String username;
        try{
            username = JWT.decode(token).getAudience().get(0);
        }catch (JWTDecodeException j){
            throw new RuntimeException("401,token验证失败");
        }

        User user=userMapper.getUserByUsername(username);
        System.out.println(user);
        if(user==null){
            throw new RuntimeException("401,用户不存在，请重新登录");
        }

        JWTVerifier jwtVerifier=JWT.require(Algorithm.HMAC256(user.getPassword())).build();
        try{
            jwtVerifier.verify(token);
        }catch (JWTVerificationException e){
            throw new RuntimeException("401");
        }

        return true;
    }
}
