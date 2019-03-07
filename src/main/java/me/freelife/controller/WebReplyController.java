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

    @GetMapping("/{bno}")
    public ResponseEntity<List<WebReply>> getReplies(@PathVariable("bno")Long bno){
        log.info("get All Replies..........................");

        WebBoard board = new WebBoard();
        board.setBno(bno);
        return ResponseEntity.ok(getListByBoard(board));
    }

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

        return ResponseEntity.status(HttpStatus.CREATED).body(getListByBoard(board));
    }

    private List<WebReply> getListByBoard(WebBoard board) throws RuntimeException{
        log.info("getListByBoard...."+ board);
        return replyRepo.getRepliesOfBoard(board);
    }

    @Transactional
    @DeleteMapping("/{bno}/{rno}")
    public ResponseEntity<List<WebReply>> remove(@PathVariable("bno")Long bno, @PathVariable("rno")Long rno){
        log.info("delete reply: "+ rno);

        replyRepo.deleteById(rno);

        WebBoard board = new WebBoard();
        board.setBno(bno);

        return ResponseEntity.ok(getListByBoard(board));
    }

    @Transactional
    @PutMapping("/{bno}")
    public ResponseEntity<List<WebReply>> modify(@PathVariable("bno")Long bno, @RequestBody WebReply reply){
        log.info("modify reply: "+ reply);

        replyRepo.findById(reply.getRno()).ifPresent(origin -> {
            origin.setReplyText(reply.getReplyText());
            replyRepo.save(origin);
        });

        WebBoard board = new WebBoard();
        board.setBno(bno);

        return ResponseEntity.ok(getListByBoard(board));
    }
}