package me.freelife.persistence;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.java.Log;
import me.freelife.domain.QWebBoard;
import me.freelife.domain.QWebReply;
import me.freelife.domain.WebBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.ArrayList;
import java.util.List;

/**
 * 사용자 정의 인터페이스의 구현
 * 실제로 JPQL을 코드로 처리하는 작업은 '엔티티 Repository 이름'+'Impl'로 작성
 *
 * 클래스를 만들 때 주의할 점은 클래스의 이름과 QuerydslRepositorySupport를 부모 클래스로 지정
 * QuerydslRepositorySupport 클래스는 생성자를 구현
 */
@Log
public class CustomCrudRepositoryImpl extends QuerydslRepositorySupport implements CustomWebBoard {

    public CustomCrudRepositoryImpl() {
        super(WebBoard.class);
    }

    @Override
    public Page<Object[]> getCustomPage(String type, String keyword, Pageable page) {
        log.info("====================================");
        log.info("TYPE: " + type);
        log.info("KEYWORD: " + keyword);
        log.info("PAGE: " + page);
        log.info("====================================");

        QWebBoard b = QWebBoard.webBoard;
        QWebReply r = QWebReply.webReply;

        JPQLQuery<WebBoard> query = from(b);

        JPQLQuery<Tuple> tuple = query.select(b.bno, b.title, r.count(), b.writer, b.regdate);

        tuple.leftJoin(r);
        tuple.on(b.bno.eq(r.board.bno));
        tuple.where(b.bno.gt(0L));

        if(type != null){

            switch (type.toLowerCase()){
                case "t":
                    tuple.where(b.title.like("%" + keyword + "%"));
                    break;
                case "c":
                    tuple.where(b.content.like("%" + keyword + "%"));
                    break;
                case "w":
                    tuple.where(b.writer.like("%" + keyword + "%"));
                    break;
            }
        }

        tuple.groupBy(b.bno);
        tuple.orderBy(b.bno.desc());

        tuple.offset(page.getOffset());
        tuple.limit(page.getPageSize());

        List<Tuple> list = tuple.fetch();

        List<Object[]> resultList = new ArrayList<>();

        list.forEach(t -> {
            resultList.add(t.toArray());
        });

        long total = tuple.fetchCount();

        return new PageImpl<>(resultList, page, total);
    }
}
