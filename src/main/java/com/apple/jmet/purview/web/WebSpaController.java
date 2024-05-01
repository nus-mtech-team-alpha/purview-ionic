package com.apple.jmet.purview.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class WebSpaController {
    
    @GetMapping("/admin/**")
    public String adminLinks() {
        return "/";
    }
    
}
