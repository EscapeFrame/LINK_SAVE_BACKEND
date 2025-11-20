package clue.link_save.domain.link.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "link")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 외부에서 직접 호출 불가
public class Link {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private UUID userId; // 링크 작성자 ID

  private int grade; // 학년
  private int clas;  // 반

  private String title;       // 제목
  private String description; // 설명
  private String link;        // 실제 링크

  @CreationTimestamp
  private Timestamp createdAt;

  @Enumerated(EnumType.STRING)
  private AuthorizationType authorizationType; // 읽기 권한

  @Enumerated(EnumType.STRING)
  private SubjectType subjectType; // 과목 종류

  public static Link create(UUID userId, int grade, int clas, String title, String description, String link, AuthorizationType authorization, SubjectType subjectType) {
    Link newLink = new Link();
    newLink.userId = userId;
    newLink.grade = grade;
    newLink.clas = clas;
    newLink.title = title;
    newLink.description = description;
    newLink.link = link;
    newLink.authorizationType = authorization;
    newLink.subjectType = subjectType;
    return newLink;
  }

  public void update(String title, String description, String link, AuthorizationType authorization, SubjectType subjectType) {
    this.title = title;
    this.description = description;
    this.link = link;
    this.authorizationType = authorization;
    this.subjectType = subjectType;
  }

  public boolean isOwner(UUID userId) {
    return this.userId.equals(userId);
  }
}