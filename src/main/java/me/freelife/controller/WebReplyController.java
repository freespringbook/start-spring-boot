package me.freelife.controller;

import lombok.extern.java.Log;
import me.freelife.domain.WebBoard;
import me.freelife.domain.WebReply;
import me.freelife.persistence.WebReplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/replies")
@Log
public class WebReplyController {

    @Autowired // setter를 만들어서 처리하는 것이 정석이지만..
    private WebReplyRepository replyRepo;

    @Transactional
    @PostMapping("/{bno}")
    public ResponseEntity<List<WebReply>> addReply(@PathVariable("bno") Long bno, @RequestBody WebReply reply){

        log.info("addReply.............................");
        log.info("BNO: " + bno);
        log.info("REPLY: " + reply);

        WebBoard board = new WebBoard();
        board.setBno(bno);

        reply.setBoard(board);
        replyRepo.save(reply);

        return new ResponseEntity<>(getListByBoard(board), HttpStatus.CREATED);
    }

    private List<WebReply> getListByBoard(WebBoard board) throws RuntimeException{
        log.info("getListByBoard...."+ board);
        return replyRepo.getRepliesOfBoard(board);
    }

}