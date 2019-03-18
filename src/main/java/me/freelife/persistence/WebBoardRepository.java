package me.freelife.persistence;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import me.freelife.domain.QWebBoard;
import me.freelife.domain.WebBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface WebBoardRepository extends CrudRepository<WebBoard, Long>, QuerydslPredicateExecutor<WebBoard> {

    default Predicate makePredicate(String type, String keyword) {
        BooleanBuilder builder = new BooleanBuilder();
        QWebBoard board = QWebBoard.webBoard;

        if(type == null){
            return builder;
        }

        switch (type){
            case "t":
                builder.and(board.title.like("%"+keyword+"%"));
                break;
            case "c":
                builder.and(board.content.like("%"+keyword+"%"));
                break;
            case "w":
                builder.and(board.writer.like("%"+keyword+"%"));
                break;
        }

        return builder;
    }

    // 내용물에 대한 변경이 어렵기 때문에 각 상황에 맞게 다음과 같이 메소드를 미리 작성해서 처리
    // @Query의 내용을 동적으로 변경할 수 없기 때문에 아쉬운 점이 있음

    @Query("SELECT b.bno, b.title, b.writer, b.regdate, count(r) FROM WebBoard b " +
            " LEFT OUTER JOIN b.replies r WHERE b.bno > 0 GROUP BY b")
    Page<Object[]> getListWithAll(Pageable page);

    @Query("SELECT b.bno, b.title, b.writer, b.regdate, count(r) FROM WebBoard b " +
            " LEFT OUTER JOIN b.replies r WHERE b.title like %?1%  AND b.bno > 0 GROUP BY b")
    Page<Object[]> getListWithTitle(String keyword, Pageable page);

    @Query("SELECT b.bno, b.title, b.writer, b.regdate, count(r) FROM WebBoard b " +
            " LEFT OUTER JOIN b.replies r WHERE b.content like %?1% AND b.bno > 0 GROUP BY b")
    Page<Object[]> getListWithContent(String keyword, Pageable page);

    @Query("SELECT b.bno, b.title, b.writer, b.regdate, count(r) FROM WebBoard b " +
            " LEFT OUTER JOIN b.replies r WHERE b.writer like %?1% AND b.bno > 0 GROUP BY b")
    Page<Object[]> getListWithWriter(String keyword, Pageable page);
}