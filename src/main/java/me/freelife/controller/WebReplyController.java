package me.freelife.controller;

import me.freelife.persistence.WebReplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/replies")
public class WebReplyController {

    @Autowired // setter를 만들어서 처리하는 것이 정석이지만..
    private WebReplyRepository replyRepo;

}