package com.lcyanxi.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lcyanxi.serviceImpl.home.HomePageService;
import com.lcyanxi.serviceImpl.home.HomePageServiceFactory;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/03/07/3:00 下午
 */
@RestController
@RequestMapping("/my/home")
public class HomePageController {
    @Autowired
    private HomePageServiceFactory homePageServiceFactory;

    @GetMapping(value = {"/{type}"}, produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity home(@PathVariable String type) {
        HomePageService homePageService = homePageServiceFactory.getHomePageService(type);
        if (Objects.isNull(homePageService)) {
            return new ResponseEntity("type not find", HttpStatus.OK);
        }
        return new ResponseEntity(homePageService.getHomePageInfo(type), HttpStatus.OK);
    }
}
