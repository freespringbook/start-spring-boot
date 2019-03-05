package me.freelife.controller;

import lombok.extern.java.Log;
import me.freelife.domain.WebReply;
import me.freelife.persistence.WebReplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/replies")
@Log
public class WebReplyController {

    @Autowired // setter를 만들어서 처리하는 것이 정석이지만..
    private WebReplyRepository replyRepo;

    @PostMapping("/{bno}")
    public ResponseEntity<Void> addReply(@PathVariable("bno") Long bno, @RequestBody WebReply reply){
        log.info("addReply.............................");
        log.info("BNO: " + bno);
        log.info("REPLY: " + reply);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}