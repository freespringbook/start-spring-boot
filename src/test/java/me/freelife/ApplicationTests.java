package me.freelife;

import com.querydsl.core.BooleanBuilder;
import me.freelife.common.TestDescription;
import me.freelife.domain.Board;
import me.freelife.domain.QBoard;
import me.freelife.persistence.BoardRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

    @Autowired
    private BoardRepository repo;

    @Test
    @TestDescription("더미데이터 생성")
    public void testInsert200() {
        IntStream.rangeClosed(1, 200).forEach(i -> {
            Board board = Board.builder()
                    .title("제목.." + i)
                    .content("내용 ...." + i + " 채우기 ")
                    .writer("user0" + (i % 10))
                    .build();
            repo.save(board);
        });
    }

    @Test
    @TestDescription("쿼리메소드 테스트")
    public void testByTitle() {
        getBoardList(repo.findBoardByTitle("제목..177"));
    }

    @Test
    @TestDescription("게시물에서 user00 이라는 작성자의 모든 데이터를 구한다")
    public void testByWriter() {
        Collection<Board> results = repo.findByWriter("user00");

        getBoardList(results);
    }

    @Test
    @TestDescription("키워드 % 검색 테스트")
    public void testByWriterStartingWith() {
        Collection<Board> results = repo.findByWriterStartingWith("user");
        getBoardList(results);
    }

    @Test
    @TestDescription(" % 키워드 검색 테스트")
    public void testByWriterEndingWith() {
        Collection<Board> results = repo.findByWriterEndingWith("00");
        getBoardList(results);
    }

    @Test
    @TestDescription(" % 키워드 % 검색 테스트")
    public void testByWriterContaining() {
        Collection<Board> results = repo.findByWriterContaining("05");
        getBoardList(results);
    }

    @Test
    @TestDescription(" And Or 조건 테스트")
    public void findByTitleContainingOrContentContaining() {
        Collection<Board> results = repo.findByTitleContainingOrContentContaining("09", "09");
        getBoardList(results);
    }

    @Test
    @TestDescription("title LIKE % ? % AND BNO > ? 부등호 처리 테스트")
    public void testByTitleAndBno() {
        Collection<Board> results = repo.findByTitleContainingAndBnoGreaterThan("5", 50L);
        getBoardList(results);
    }

    @Test
    @TestDescription("bno > ? ORDER BY bno DESC")
    public void testBnoOrderBy() {
        Collection<Board> results = repo.findByBnoGreaterThanOrderByBnoDesc(90L);
        getBoardList(results);
    }

    @Test
    @TestDescription("bno > ? ORDER BY bno DESC Paging")
    public void testBnoOrderByPaging() {
        // 첫 번째 페이지(인덱스 번호는 0부터 시작)이고 10건의 데이터를 가져오도록 설정
        Pageable paging = PageRequest.of(0, 10);
        Collection<Board> results = repo.findByBnoGreaterThanOrderByBnoDesc(0L, paging);
        getBoardList(results);
    }

    @Test
    @TestDescription("PageRequst 생성자를 이용한 정렬")
    public void testBnoPagingSort() {
        Pageable paging = PageRequest.of(0, 10, Sort.Direction.ASC, "bno");
        Page<Board> result = repo.findByBnoGreaterThan(0L, paging);

        System.out.println("PAGE SIZE: " + result.getSize());
        System.out.println("TOTAL PAGES: " + result.getTotalPages());
        System.out.println("TOTAL COUNT: " + result.getTotalElements());
        System.out.println("NEXT: " + result.nextPageable());

        List<Board> list = result.getContent();

        getBoardList(list);
    }

    @Test
    @TestDescription("@Query 를 사용해 JPQL로 title 조회")
    public void testByTitle2() {
        repo.findByTitle("17").forEach(board -> System.out.println(board));
    }

    @Test
    @TestDescription("content 칼럼을 제외")
    public void testByTitle17() {
        repo.findByTitle2("17").forEach(arr -> System.out.println(Arrays.toString(arr)));
    }

    @Test
    @TestDescription("nativeQuery 사용")
    public void testByTitle3() {
        repo.findByTitle3("17").forEach(arr -> System.out.println(Arrays.toString(arr)));
    }

    @Test
    @TestDescription("@Query와 Paging 처리/정렬")
    public void testByPaging() {
        Pageable pageable = PageRequest.of(0, 10);
        repo.findBypage(pageable).forEach(board -> System.out.println(board));
    }

    @Test
    @TestDescription("@Querydsl를 이용한 Predicate 생성 및 테스트")
    public void testPredicate() {
        String type = "t";
        String keyword = "17";

        BooleanBuilder builder = new BooleanBuilder();

        QBoard board = QBoard.board;
        if (type.equals("t")) {
            builder.and(board.title.like("%" + keyword + "%"));
        }

        // bno > 0
        builder.and(board.bno.gt(0L));

        Pageable pageable = PageRequest.of(0, 10);

        Page<Board> result = repo.findAll(builder, pageable);

        System.out.println("PAGE SIZE: " + result.getSize());
        System.out.println("TOTAL PAGES: " + result.getTotalPages());
        System.out.println("TOTAL COUNT: " + result.getTotalElements());
        System.out.println("NEXT: " + result.nextPageable());

        List<Board> list = result.getContent();

        getBoardList(list);


    }

    // BoardList 공통함수
    private void getBoardList(Collection<Board> results) {
        results.forEach(board -> System.out.println(board));
    }


    @Test
    public void contextLoads() {
    }

}
