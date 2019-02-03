package me.freelife.persistence;

import me.freelife.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface BoardRepository extends CrudRepository<Board, Long>, QuerydslPredicateExecutor<Board> {
    // Board 엔티티에서 title에 해당하는 값을 조회해서 List 컬렉션 타입으로 리턴
    List<Board> findBoardByTitle(String title);

    // writer가 작성한 모든 데이터를 조회
    Collection<Board> findByWriter(String writer);

    // 작성자에 대한 like % 키워드 %
    Collection<Board> findByWriterContaining(String writer);

    // 작성자에 대한 like 키워드 %
    Collection<Board> findByWriterStartingWith(String writer);

    // 작성자에 대한 like % 키워드
    Collection<Board> findByWriterEndingWith(String writer);

    // OR 조건의 처리
    Collection<Board> findByTitleContainingOrContentContaining(String title, String content);

    // title LIKE % ? % AND BNO > ?
    Collection<Board> findByTitleContainingAndBnoGreaterThan(String keywoard, Long num);

    // bno > ? ORDER BY bno DESC
    Collection<Board> findByBnoGreaterThanOrderByBnoDesc(Long bno);

    // bno > ? ORDER BY bno DESC Paging
    List<Board> findByBnoGreaterThanOrderByBnoDesc(Long bno, Pageable paging);

    Page<Board> findByBnoGreaterThan(Long bno, Pageable paging);

    @Query("SELECT b FROM Board b WHERE b.title LIKE %?1% AND b.bno > 0 ORDER BY b.bno DESC")
    List<Board> findByTitle(String title);

    @Query("SELECT b FROM Board b WHERE b.content LIKE %:content% AND b.bno > 0 ORDER BY b.bno DESC")
    List<Board> findByContent(@Param("content") String content);

    // @Query("SELECT b FROM #{#entityName} WHERE b.content LIKE %?1% AND b.bno > 0 ORDER BY b.bno DESC")
    // List<Board> findByWriter(String writer);

    // content 칼럼을 제외
    @Query("SELECT board.bno, board.title, board.writer, board.regdate FROM Board board WHERE board.title LIKE %?1% AND board.bno > 0 ORDER BY board.bno DESC")
    List<Object[]> findByTitle2(String title);

    //nativeQuery 사용
    @Query(value = "SELECT bno, title, writer FROM tbl_boards WHERE title LIKE CONCAT('%', ?1, '%') AND bno > 0 ORDER BY bno DESC", nativeQuery=true)
    List<Object[]> findByTitle3(String title);

    //@Query와 Paging 처리/정렬
    @Query("SELECT board FROM Board board WHERE board.bno > 0 ORDER BY board.bno DESC")
    List<Board> findBypage(Pageable pageable);
}