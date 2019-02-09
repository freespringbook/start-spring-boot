package me.freelife.persistence;

import lombok.extern.java.Log;
import me.freelife.domain.WebBoard;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@SpringBootTest
@Log
@Commit
public class WebBoardRepositoryTest {

    @Autowired
    WebBoardRepository repo;

    @Test
    public void insertBoardDummies() {
        IntStream.range(0, 300).forEach(i -> {
            WebBoard board = WebBoard.builder()
                    .title("Sample Board Title " + i)
                    .content("Content Sample ..." + i + " of Board ")
                    .writer("user0" + (i % 10))
                    .build();
            repo.save(board);
        });
    }
}