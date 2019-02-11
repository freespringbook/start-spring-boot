package me.freelife.persistence;

import me.freelife.domain.WebReply;
import org.springframework.data.repository.CrudRepository;

public interface WebReplyRepository extends CrudRepository<WebReply, Long> {
}
