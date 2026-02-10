package com.HTT.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {

    @GetMapping("/home")
    public ResponseEntity<?> getMethodName() {
        return new ResponseEntity("you are home",HttpStatus.OK);
    }
    @GetMapping("/loginsuccess")
    public ResponseEntity<?> getLoginSuccess() {
        return new ResponseEntity("you are logged in successfully",HttpStatus.OK);
    }
    
}
