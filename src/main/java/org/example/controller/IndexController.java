package org.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "home";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/badRef")
    public String badRef() {
        return "badRef";
    }

    @GetMapping("/errorReferer")
    public String errorReferer() {
        return "errorReferer";
    }


}