package me.freelife;

import lombok.extern.java.Log;
import me.freelife.domain.FreeBoard;
import me.freelife.domain.FreeBoardReply;
import me.freelife.presistence.FreeBoardReplyRepository;
import me.freelife.presistence.FreeBoardRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@SpringBootTest
@Log
@Commit
public class FreeBoardTests {

    @Autowired
    FreeBoardRepository boardRepo;

    @Autowired
    FreeBoardReplyRepository replyRepo;

    @Test
    public void 더미_데이터_생성() {
        IntStream.rangeClosed(1, 200).forEach(i -> {
            FreeBoard board = FreeBoard.builder()
                    .title("Free Board ... " + i)
                    .content("Free Content.... " + i)
                    .writer("user"+ i%10)
                    .build();
            boardRepo.save(board);
        });
    }

    @Transactional
    @Test
    public void 양방향_댓글등록() {
        Optional<FreeBoard> result = boardRepo.findById(200L);
        result.ifPresent(board -> {
            List<FreeBoardReply> replies = board.getReplies();
            FreeBoardReply reply = FreeBoardReply.builder()
                    .reply("REPLY................")
                    .replayer("replyer00")
                    .board(board)
                    .build();
            replies.add(reply);
            board.setReplies(replies);
            boardRepo.save(board);
        });
    }

    @Test
    public void 단방향_댓글등록() {
        FreeBoard board = new FreeBoard();
        board.setBno(199L);

        FreeBoardReply reply = FreeBoardReply.builder()
                .reply("REPLY................")
                .replayer("replyer00")
                .board(board)
                .build();
        replyRepo.save(reply);
    }

    @Test
    public void 쿼리메소드_게시물페이징() {
        Pageable page = PageRequest.of(0, 10, Sort.Direction.DESC, "bno");
        boardRepo.findByBnoGreaterThan(0L, page).forEach(board -> {
            log.info(board.getBno() +": "+board.getTitle());
        });

    }

    @Transactional
    @Test
    public void 쿼리메소드_게시물옆_댓글수(){
        Pageable page = PageRequest.of(0, 10, Sort.Direction.DESC, "bno");
        boardRepo.findByBnoGreaterThan(0L, page).forEach(board -> {
            log.info(board.getBno() +": "+board.getTitle()+":"+board.getReplies().size());
        });
    }

    @Test
    public void Query와_FETCH_JOIN처리() {
        Pageable page = PageRequest.of(0, 10, Sort.Direction.DESC, "bno");
        boardRepo.getPage(page).forEach(arr -> {
            log.info(Arrays.toString(arr));
        });
    }
}
