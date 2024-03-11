package com.lcyanxi.controller;

import com.lcyanxi.finish.FinishPageReq;
import com.lcyanxi.finish.TrainingType;
import com.lcyanxi.serviceImpl.finish.FinishPageService;
import com.lcyanxi.serviceImpl.finish.FinishPageServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/03/10/11:21 上午
 */
@RestController
@RequestMapping("/my/finish")
public class FinishPageController {
    @Autowired
    private FinishPageServiceFactory finishPageServiceFactory;

    @GetMapping(value = {"/{logId}"}, produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity finishPage(@PathVariable String logId) {
        TrainingType type;
        if (logId.contains("rn")){
            type = TrainingType.Run;
        }else if (logId.contains("cy")){
            type = TrainingType.Cyc;
        }else {
            type = TrainingType.Hik;
        }
        FinishPageService finishPageService = finishPageServiceFactory.getPageServiceByTrainingType(type);
        if (Objects.isNull(finishPageService)) {
            return new ResponseEntity("type not find", HttpStatus.OK);
        }
        FinishPageReq build = FinishPageReq.builder().logId(logId).build();
        return new ResponseEntity(finishPageService.getLogPage(build), HttpStatus.OK);
    }
}
