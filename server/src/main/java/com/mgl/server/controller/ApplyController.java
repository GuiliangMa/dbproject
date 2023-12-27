package com.mgl.server.controller;

import com.mgl.server.common.Result;
import com.mgl.server.controller.dto.ApplyDTO;
import com.mgl.server.entity.Apply;
import com.mgl.server.service.ApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/apply")
public class ApplyController
{
    @Autowired
    ApplyService applyService;

    @GetMapping("/free")
    public Result findFreePage(@RequestParam Integer pageNum,
                               @RequestParam Integer pageSize,
                               @RequestParam String useDate,
                               @RequestParam String startTime,
                               @RequestParam String endTime,
                               @RequestParam String roomName,
                               @RequestParam Integer capacity,
                               @RequestParam Integer multiMedia,
                               @RequestParam Integer AC,
                               @RequestParam String type){
        return applyService.findFreePage(pageNum,pageSize,useDate,startTime,endTime,
                roomName,capacity,multiMedia,AC,type);
    }

    @GetMapping("/get")
    public Result getIdAndName(@RequestParam String username){
        return applyService.getIdAndName(username);
    }

    @PostMapping("/apply")
    public Result applyRoom(@RequestBody ApplyDTO applyDTO){
        return applyService.applyRoom(applyDTO);
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam String roomName,
                           @RequestParam String name,
                           @RequestParam String status,
                           @RequestParam String startTime,
                           @RequestParam String endTime,
                           @RequestParam String mode){

        return applyService.findPage(pageNum,pageSize,roomName,name,status,startTime,endTime,mode);
    }

    @PostMapping("/accept")
    public Result accept(@RequestBody Apply apply){
        return applyService.accepte(apply);
    }

    @GetMapping("/mypage")
    public Result findMyPage(@RequestParam Integer pageNum,
                             @RequestParam Integer pageSize,
                             @RequestParam String roomName,
                             @RequestParam String username,
                             @RequestParam String startTime,
                             @RequestParam String endTime,
                             @RequestParam String mode){
        return applyService.findMyPage(pageNum,pageSize,roomName,username,startTime,endTime,mode);
    }

    @PostMapping("/refuse")
    public Result refuse(@RequestBody Apply apply){
        return applyService.refuse(apply);
    }

    @PostMapping("/revoke")
    public Result revoke(@RequestBody Integer apid){
        return applyService.revoke(apid);
    }
}
