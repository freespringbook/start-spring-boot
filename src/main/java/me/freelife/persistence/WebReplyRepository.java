package me.freelife.persistence;

import me.freelife.domain.WebBoard;
import me.freelife.domain.WebReply;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface WebReplyRepository extends CrudRepository<WebReply, Long> {

    @Query("SELECT r FROM WebReply r WHERE r.board = ?1 AND r.rno > 0 ORDER BY r.rno ASC")
    List<WebReply> getRepliesOfBoard(WebBoard board);

}
