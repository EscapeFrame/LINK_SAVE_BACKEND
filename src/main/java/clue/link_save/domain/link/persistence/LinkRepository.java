package clue.link_save.domain.link.persistence;

import clue.link_save.domain.link.domain.AuthorizationType;
import clue.link_save.domain.link.domain.Link;
import clue.link_save.domain.link.domain.SubjectType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface LinkRepository extends JpaRepository<Link, Long> {
  Page<Link> findByGradeAndClasAndAuthorizationType(int grade, int clas, AuthorizationType authorizationType, Pageable pageable);

  @Query("SELECT l FROM Link l WHERE " +
          "l.grade = :grade AND " +
          "((l.authorizationType = 'PUBLIC') OR " +
          "(l.authorizationType = 'CLASS_ONLY' AND l.clas = :clas) OR " +
          "(l.authorizationType = 'PRIVATE' AND l.userId = :userId)) " +
          "AND (:subjectType IS NULL OR l.subjectType = :subjectType)")
  Page<Link> findAccessibleLinks(@Param("userId") UUID userId, @Param("grade") int grade, @Param("clas") int clas, @Param("subjectType") SubjectType subjectType, Pageable pageable);
}