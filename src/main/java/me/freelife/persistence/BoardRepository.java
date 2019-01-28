package me.freelife.persistence;

import me.freelife.domain.Board;
import org.springframework.data.repository.CrudRepository;

public interface BoardRepository extends CrudRepository<Board, Long> {
}
