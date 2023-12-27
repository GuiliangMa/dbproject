package com.mgl.server.entity;

import com.mgl.server.controller.dto.ClassroomDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Objects;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Classroom
{
    private int rid;// 房间编号
    private String roomName;
    private int capacity;
    private boolean multiMedia;
    private boolean AC;
    private String part;
    private String type;
    private String location;
    private boolean available;

    public Classroom(ClassroomDTO classroomDTO){
        if(classroomDTO.getRid()!=null)
            this.rid=Integer.parseInt(classroomDTO.getRid());
        this.roomName=classroomDTO.getRoomName();
        this.capacity=classroomDTO.getCapacity();
        this.multiMedia= Objects.equals(classroomDTO.getMultiMedia(), "1");
        this.AC= Objects.equals(classroomDTO.getAC(), "1");
        this.part=classroomDTO.getPart();

        if(Objects.equals(classroomDTO.getType(), null))
            this.type="普通";
        else this.type = classroomDTO.getType();

        this.location=classroomDTO.getLocation();
        this.available= ! Objects.equals(classroomDTO.getAvailable(), "0");
    }

    public static boolean isSimlar(Classroom a,Classroom b){
        if(a.getCapacity()!=b.getCapacity())
            return false;
        if(a.isMultiMedia()!=b.isMultiMedia())
            return false;
        if(a.isAC()!=b.isAC())
            return false;
        return Objects.equals(a.getType(), b.getType());
    }
}
