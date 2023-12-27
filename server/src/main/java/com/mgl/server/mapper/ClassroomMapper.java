package com.mgl.server.mapper;

import com.mgl.server.entity.Classroom;
import com.mgl.server.entity.Student;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@Mapper
public interface ClassroomMapper
{
    List<Classroom> findPage(Integer pageNum,
                             Integer pageSize,
                             String roomName,
                             Integer capacity,
                             Integer multiMedia,
                             Integer AC,
                             String type);

    int countTotal(String roomName,
                   Integer capacity,
                   Integer multiMedia,
                   Integer AC,
                   String type);

    @Insert("insert into classroom(roomname,capacity,multimedia,AC,part,type,location,available)" +
            "values(#{roomName},#{capacity},#{multiMedia},#{AC},#{part},#{type},#{location},#{available})")
    public int save(Classroom classroom);

    @Select("select roomname from classroom where rid = #{id}")
    public String getNameById(int id);

    @Select("select 1 from classroom where roomname=#{name}")
    public String findByName(String name);

    public int update(Classroom classroom);

    @Delete("delete from classroom where rid = #{rid}")
    public Integer deleteByRid(@Param("rid") int rid);

    @Select("select * from classroom")
    List<Classroom> list();
}
