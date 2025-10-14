package clue.link_save.domain.link.persistence;

import clue.link_save.domain.link.domain.Authorization;
import clue.link_save.domain.link.domain.Link;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LinkRepository extends JpaRepository<Link, Long> {
  List<Link> findByGradeAndClas(char grade, char clas);

  Page<Link> findByGradeAndClasAndAuthorization(char grade, char clas, Authorization authorization, Pageable pageable);
}