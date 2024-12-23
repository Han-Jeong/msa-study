package com.example.user_service.controller;

import com.example.user_service.dto.UserDto;
import com.example.user_service.service.UserService;
import com.example.user_service.vo.Greeting;
import com.example.user_service.vo.RequestUser;
import com.example.user_service.vo.ResponseUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.modelmapper.convention.MatchingStrategies.STRICT;
import static org.springframework.http.HttpStatus.CREATED;

@Slf4j
@RestController
@RequestMapping("/user-service")
public class UserController {

    private Environment env;
    private Greeting greeting;
    private UserService userService;

    @Autowired
    public UserController(Environment env, Greeting greeting, UserService userService) {
        this.env = env;
        this.greeting = greeting;
        this.userService = userService;
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

    @PostMapping("/users")
    public ResponseEntity<ResponseUser> createUser(@RequestBody RequestUser user) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(STRICT);
        UserDto userDto = mapper.map(user, UserDto.class);
        UserDto responseDto = userService.createUser(userDto);
        ResponseUser responseUser = mapper.map(responseDto, ResponseUser.class);
        return ResponseEntity.status(CREATED).body(responseUser);
    }
}