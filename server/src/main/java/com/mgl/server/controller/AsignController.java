package com.mgl.server.controller;

import com.mgl.server.common.Result;
import com.mgl.server.controller.dto.AsignDTO;
import com.mgl.server.service.AsignService;
import lombok.Data;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/asign")
public class AsignController
{
    @Autowired
    AsignService asignService;

    @PostMapping("/assign")
    public Result assign(@RequestBody AsignDTO as){
        return asignService.assign(as);
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam String roomName,
                           @RequestParam String username,
                           @RequestParam String startTime,
                           @RequestParam String endTime,
                           @RequestParam String state){
        return asignService.findPage(pageNum,pageSize,roomName,username,startTime,endTime,state);
    }

    @DeleteMapping("/{asgid}")
    public Result delete(@PathVariable Integer asgid){
        return asignService.delete(asgid);
    }

    @DeleteMapping("/revoke/{asgid}")
    public Result revoke(@PathVariable Integer asgid){
        return asignService.revoke(asgid);
    }

    @PostMapping("/delbatch")
    public Result delBatch(@RequestBody List<Integer> asgids){
        return asignService.delBatch(asgids);
    }

}
