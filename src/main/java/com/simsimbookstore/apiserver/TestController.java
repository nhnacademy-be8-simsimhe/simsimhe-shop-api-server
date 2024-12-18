package com.simsimbookstore.apiserver;


import jakarta.persistence.GeneratedValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shop")
public class TestController {

    @GetMapping("/hello")
    public String hello(){
        return "ㄹㄹㄹㄹㄹ";
    }
}
