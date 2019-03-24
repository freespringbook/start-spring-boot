package me.freelife.controller;

import lombok.extern.java.Log;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Log
@Controller
public class SampleController {

    @GetMapping("/")
    public String index() {

        log.info("index");
        return "index";
    }

    @RequestMapping("/guest")
    public void forGuest() {

        log.info("guest");

    }

    @RequestMapping("/manager")
    public void forManager() {

        log.info("manager");
    }

    @RequestMapping("/admin")
    public void forAdmin() {

        log.info("admin");

    }
}
