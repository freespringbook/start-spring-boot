package me.freelife.persistence;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import me.freelife.domain.QWebBoard;
import me.freelife.domain.WebBoard;
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
}