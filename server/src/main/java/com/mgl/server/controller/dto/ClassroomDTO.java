package com.mgl.server.controller.dto;

import cn.hutool.core.util.StrUtil;
import com.mgl.server.entity.Classroom;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ClassroomDTO
{
    private String rid;// 房间编号
    private String roomName;
    private int capacity;
    private String multiMedia;
    private String AC;
    private String part;
    private String type;
    private String location;
    private String available;

    public ClassroomDTO(Classroom classroom){
        this.rid = String.valueOf(classroom.getRid());
        this.roomName = classroom.getRoomName();
        this.capacity = classroom.getCapacity();
        if(classroom.isMultiMedia()){
            this.multiMedia="有";
        }else{
            this.multiMedia="无";
        }
        if(classroom.isAC()){
            this.AC="有";
        }else{
            this.AC="无";
        }
        this.part=classroom.getPart();
        this.type= classroom.getType();
        this.location= classroom.getLocation();
        if(classroom.isAvailable()){
            this.available="可用";
        }else{
            this.available="停用";
        }

    }
}
