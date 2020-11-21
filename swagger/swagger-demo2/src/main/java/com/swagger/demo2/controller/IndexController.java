package com.swagger.demo2.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "测试API")
public class IndexController {

    @ApiOperation(value = "index", notes = "测试API")
    @GetMapping("/")
    public String index() {
        return "index";
    }
}
