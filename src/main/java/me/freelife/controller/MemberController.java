package me.freelife.controller;

import lombok.extern.java.Log;
import me.freelife.domain.Member;
import me.freelife.persistence.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by freejava1191@gmail.com on 2019-04-26
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 *
 * PasswordEncoder 암호화를 테스트 하기 위한 컨트롤러
 */
@Log
@Controller
@RequestMapping("/member/")
public class MemberController {

    @Autowired
    PasswordEncoder pwEncoder;

    @Autowired
    MemberRepository repo;

    @GetMapping("/join")
    public void join() {

    }

    @PostMapping("/join")
    public String joinPost(@ModelAttribute("member") Member member) {
        log.info("MEMBER: " + member);

        // PasswordEncoder를 이용해서 upw를 처리
        String encryptPw = pwEncoder.encode(member.getUpw());
        log.info("en: " + encryptPw);
        member.setUpw(encryptPw);
        repo.save(member);

        return "/member/joinResult";
    }
}

