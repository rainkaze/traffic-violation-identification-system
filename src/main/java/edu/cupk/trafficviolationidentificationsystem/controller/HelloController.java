package edu.cupk.trafficviolationidentificationsystem.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin // 允许所有来源的跨域请求
public class HelloController {

    @RequestMapping("/hello")
    public String hello() {
        return "hello Spring Boot!";
    }

}