package com.mgl.server.controller;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.mgl.server.common.Result;
import com.mgl.server.controller.dto.UserDTO;
import com.mgl.server.entity.User;
import com.mgl.server.mapper.UserMapper;
import com.mgl.server.service.UserService;
import com.mgl.server.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/user")
//统一给接口加前缀
public class UserController
{
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result login(@RequestBody UserDTO userDTO){
        return userService.login(userDTO);
    }

    @PostMapping("/register")
    public Result register(@RequestBody UserDTO userDTO){
        return userService.register(userDTO);
    }


    // 分页查询接口
    // 接口路径，localhost:9090/user/page
    // @RequestParam:将?pageNum=1&pageSize=10 映射
    // 即将/user/page?pageNum=1&pageSize=10 映射到对应的参数上
    // 若存在多个GetMapping，其url不能相同，
    @GetMapping("/page")
    public Map<String, Object> findPage(@RequestParam Integer pageNum,
                                        @RequestParam Integer pageSize,
                                        @RequestParam String username,
                                        @RequestParam String email,
                                        @RequestParam String phone)
    {
//        System.out.printf('!'+String.valueOf(pageNum)+'\n');
        User person =TokenUtils.getCurrentUser();
        System.out.println("--------------------------------");
        System.out.println(person);
        System.out.println("--------------------------------");
        return userService.findPage(pageNum,pageSize,username,email,phone);
    }

    @PostMapping
    public Integer save(@RequestBody User user)
    {
//        @RequestBody 可以将前台传来的Json转换成对应的类，将Json对象转换为Java对象
        //新增或者更新，都在save中
        System.out.println();
        System.out.println(user);
        return userService.save(user);
    }

    @DeleteMapping("/{username}")
    public Integer delete(@PathVariable String username)
    {
        return userMapper.deleteByUserName(username);
    }

    @PostMapping("/delbatch")
    public Integer deleteBatch(@RequestBody List<String> users){
//        System.out.println(1);
        return userService.deleteBatch(users);
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception{
        //System.out.println(filePath);
        List<User> list=userService.list("","","");

        // 通过hutool的 ExcelWrite类创建 writer，在getWriter中可以指定磁盘路径
        ExcelWriter writer= ExcelUtil.getWriter(true);
        // 自定义别名
//        writer.addHeaderAlias("username","用户名");
//        writer.addHeaderAlias("password","密码");
//        writer.addHeaderAlias("nickname","昵称");
//        writer.addHeaderAlias("email","电子邮箱");
//        writer.addHeaderAlias("phone","电话");
//        writer.addHeaderAlias("address","地址");
//        writer.addHeaderAlias("create_time","创建时间");

        // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
        writer.write(list,true);
        // 设置浏览器的响应格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("用户信息","UTF-8");
        response.setHeader("Content-Disposition","attachment;filename="+fileName+".xlsx");

        ServletOutputStream out =response.getOutputStream();
        writer.flush(out,true);//刷新至输出流
        out.close();
        writer.close();
    }

    @PostMapping("/import")
    public Boolean impo(MultipartFile file) throws Exception{
        System.out.println("!");
        InputStream inputStream = file.getInputStream();
        ExcelReader reader =ExcelUtil.getReader(inputStream);
        List<User> list=reader.readAll(User.class);
        for(User user:list){
            userService.save(user);
        }
        return true;
    }
}
