package com.tt.face.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tt.face.pojo.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {


}