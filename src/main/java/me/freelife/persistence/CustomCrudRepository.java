package me.freelife.persistence;

import me.freelife.domain.WebBoard;
import org.springframework.data.repository.CrudRepository;

/**
 * 엔티티의 Repository 인터페이스 설계
 */
public interface CustomCrudRepository extends CrudRepository<WebBoard, Long>, CustomWebBoard {
}
