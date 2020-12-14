package com.lcyanxi.dto;

import com.lcyanxi.model.User1;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface User1Mapper {


    int insert(User1 record);

    User1 selectByPrimaryKey(@Param("id") Integer id);
}
