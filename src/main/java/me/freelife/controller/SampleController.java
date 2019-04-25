package me.freelife.controller;

import lombok.extern.java.Log;
import org.springframework.security.access.annotation.Secured;
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

    /** @Secured - 문자열의 배열이나 문자열을 이용해서 권한을 지정 */
    @Secured({"ROLE_ADMIN"})
    @RequestMapping("/adminSecret")
    public void forAdminSecret() {
        log.info("admin secret");
    }

    /** @Secured - 문자열의 배열이나 문자열을 이용해서 권한을 지정 */
    @Secured("ROLE_MANAGER")
    @RequestMapping("/managerSecret")
    public void forManagerSecret() {
        log.info("manager secret");
    }
}
