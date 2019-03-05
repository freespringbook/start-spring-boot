package me.freelife.persistence;

import lombok.extern.java.Log;
import me.freelife.domain.WebBoard;
import me.freelife.domain.WebReply;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@SpringBootTest
@Log
@Commit
public class WebReplyRepositoryTest {

    @Autowired
    WebReplyRepository repo;

    @Test
    public void 댓글_더미데이터_생성() {
        Long[] arr = {302L, 301L, 296L};

        Arrays.stream(arr).forEach(num ->{

            WebBoard board = new WebBoard();
            board.setBno(num);

            IntStream.range(0, 10).forEach(i -> {

                WebReply reply = new WebReply();
                reply.setReplyText("REPLY ..." + i);
                reply.setReplyer("replyer" + i);
                reply.setBoard(board);

                repo.save(reply);
            });
        });
    }
}