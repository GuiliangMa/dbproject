package com.mgl.server.service;

import cn.hutool.core.util.StrUtil;
import com.mgl.server.common.Result;
import com.mgl.server.controller.dto.UserDTO;
import com.mgl.server.entity.User;
import com.mgl.server.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.mgl.server.utils.TokenUtils.genToken;

@Service
//将该类注入至springboot容器
public class UserService
{
    @Autowired
    private UserMapper userMapper;

    public int save(User user)
    {
        String mapperResult=userMapper.mapperUsername(user.getUsername());
        if (mapperResult == null)
        {
            user.setPassword(passwordMD5(user.getUsername(),"123456"));
            // System.out.println(user.getPassword());
            System.out.println("!!!Service");
            return userMapper.insert(user);
        }
        else
        {
            return userMapper.update(user);
        }
    }

    // md5加密
    public String passwordMD5(String userName, String userPassword) {
        // 需要加密的字符串
        String src = userName + userPassword;
        try {
            // 加密对象，指定加密方式
            MessageDigest md5 = MessageDigest.getInstance("md5");
            // 准备要加密的数据
            byte[] b = src.getBytes();
            // 加密：MD5加密一种被广泛使用的密码散列函数，
            // 可以产生出一个128位（16字节）的散列值（hash value），用于确保信息传输完整一致
            byte[] digest = md5.digest(b);
            // 十六进制的字符
            char[] chars = new char[]{'0', '1', '2', '3', '4', '5',
                    '6', '7', 'A', 'B', 'C', 'd', 'o', '*', '#', '/'};
            StringBuffer sb = new StringBuffer();
            // 处理成十六进制的字符串(通常)
            // 遍历加密后的密码，将每个元素向右位移4位，然后与15进行与运算(byte变成数字)
            for (byte bb : digest)
            {
                sb.append(chars[(bb >> 4) & 15]);
                sb.append(chars[bb & 15]);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, Object> findPage(Integer pageNum,
                                        Integer pageSize,
                                        String username,
                                        String email,
                                        String phone)
    {
        Map<String, Object> res = new HashMap<>();
        // limit 第一个参数为 (pageNum-1)*pageSize, 第二个参数为pageSize
        pageNum = (pageNum - 1) * pageSize;
        List<User> data = userMapper.searchPage(pageNum, pageSize, username, email, phone);
        int total = userMapper.countTotal(username, email, phone);

        res.put("data", data);
        res.put("total", total);
        return res;
    }

    public Integer deleteBatch(List<String> users)
    {
        int ans = 0;
        for (String username : users)
        {
            ans += userMapper.deleteByUserName(username);
        }
        return ans;
    }

    public List<User> list(String username,
                           String email,
                           String phone)
    {
        return userMapper.findAll(username,email,phone);
    }

    public Result login(UserDTO userDTO)
    {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)){
            return Result.error("500","账号或密码为空");
        }
        if(userMapper.mapperUsername(username)==null){
            return Result.error("500","当前账号不存在");
        }
        password=passwordMD5(username,password);
        if(userMapper.mapperUser(username,password)==null)
        {
            return Result.error("500","密码错误");
        }
        else{
            UserDTO data=userMapper.getUser(username);
            data.setToken(genToken(username, password));
            return Result.success(data);
        }
    }

    public Result register(UserDTO userDTO)
    {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        String status = userDTO.getStatus();
        String nickname=userDTO.getNickname();
        if(StrUtil.isBlank(username)||StrUtil.isBlank(password)||StrUtil.isBlank(status)||StrUtil.isBlank(nickname)){
            return Result.error("500","信息存在空值");
        }
        if(Objects.equals(userMapper.mapperUsername(username), "1")){
            return Result.error("500","当前账号已存在");
        }
        if(StrUtil.length(password)<=5||StrUtil.length(password)>20){
            return Result.error("500","密码长度错误");
        }
        if(! status.equals("学生") && ! status.equals("教师")){
            return Result.error("500","该身份不存在");
        }
        userDTO.setPassword(passwordMD5(username,password));

        userMapper.register(userDTO);
        userDTO.setPassword(null);
        return Result.success(userDTO);
    }
}
