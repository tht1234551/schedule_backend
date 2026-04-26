package com.jhj.schedule.hello;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@Profile("dev")
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }

    @GetMapping("/api/hello2")
    public String hello2() {
        return "Hello World!";
    }

    @GetMapping("/api/hello3")
    public void hello3() {
        throw new RuntimeException("hello3");
    }
}
