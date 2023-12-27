package com.mgl.server.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.mgl.server.entity.User;
import com.mgl.server.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class TokenUtils
{
    @Autowired
    private UserMapper userMapper;
    private static UserMapper staticUserMapper;

    @PostConstruct
    private void init(){
        staticUserMapper=userMapper;
    }
    /*
    生成token
     */


    public static String genToken(String userId,String sign){
        return JWT.create().withAudience(userId)//将userId保存至token中作为载荷
                .withExpiresAt(DateUtil.offsetHour(new Date(),2))// 2小时有效
                        .sign(Algorithm.HMAC256(sign));
    }

    public static User getCurrentUser(){
        try{
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token =request.getHeader("token");
            if(StrUtil.isNotBlank(token)){
                String username=JWT.decode(token).getAudience().get(0);
                return staticUserMapper.getUserByUsername(username);
            }
        }catch (Exception e){
            return null;
        }
        return null;
    }
}
