package com.nhnacademy.inkbridge.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * class: TestContoller.
 *
 * @author minseo
 * @version 2/14/24
 */
@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "test4";
    }
}
