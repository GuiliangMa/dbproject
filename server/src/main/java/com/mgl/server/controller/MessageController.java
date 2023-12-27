package com.mgl.server.controller;

import com.mgl.server.common.Result;
import com.mgl.server.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/message")
public class MessageController
{
    @Autowired
    MessageService messageService;

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,@RequestParam Integer pageSize ,@RequestParam String username){
        return messageService.findPage(pageNum,pageSize,username);
    }
}
