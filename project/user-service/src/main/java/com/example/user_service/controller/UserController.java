package com.example.user_service.controller;

import com.example.user_service.vo.Greeting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user-service")
public class UserController {

    private Environment env;
    private Greeting greeting;

    @Autowired
    public UserController(Environment env, Greeting greeting) {
        this.env = env;
        this.greeting = greeting;
    }

    @GetMapping("health_check")
    public String status() {
        return "It's working in UserService";
    }
    @GetMapping("welcome")
    public String welcome() {
//        return env.getProperty("greeting.message");
        return greeting.getMessage();
    }
}
