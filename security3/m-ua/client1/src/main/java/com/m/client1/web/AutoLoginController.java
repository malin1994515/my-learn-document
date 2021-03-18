package com.m.client1.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/autologin")
public class AutoLoginController {

    @GetMapping
    public void autoLogin(@RequestParam String code) {
        System.out.println("autologin: " + code);
    }
}
