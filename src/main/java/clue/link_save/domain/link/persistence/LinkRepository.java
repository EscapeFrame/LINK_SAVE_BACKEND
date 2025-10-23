package clue.link_save.domain.link.persistence;

import clue.link_save.domain.link.domain.AuthorizationType;
import clue.link_save.domain.link.domain.Link;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LinkRepository extends JpaRepository<Link, Long> {
  Page<Link> findByGradeAndClasAndAuthorizationType(char grade, char clas, AuthorizationType authorizationType, Pageable pageable);
}