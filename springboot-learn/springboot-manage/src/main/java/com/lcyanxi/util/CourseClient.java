package com.lcyanxi.util;

import com.dtflys.forest.annotation.*;

public interface CourseClient {

    @Get(url = "https://zuicool.com/events?type=run", headers = {
            "Content-Type: application/json;charset=UTF-8"})
    String zuicoolList();

    @Get(url = "https://zuicool.com/event/${id}")
    String zuicoolDetail(@Var("id") String id);
}
