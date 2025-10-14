package clue.link_save.domain.link.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "link")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 외부에서 직접 호출 불가
public class Link {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private char grade; // 학년
  private char clas;  // 반

  private String title;       // 제목
  private String description; // 설명
  private String link;        // 실제 링크

  @CreationTimestamp
  private Timestamp createdAt;

  @Enumerated(EnumType.STRING)
  private Authorization authorization; // 읽기 권한
  private SubjectType subjectType; // 과목 종류

  public static Link create(char grade, char clas, String title, String description, String link, Authorization authorization, SubjectType subjectType) {
    Link newLink = new Link();
    newLink.grade = grade;
    newLink.clas = clas;
    newLink.title = title;
    newLink.description = description;
    newLink.link = link;
    newLink.authorization = authorization;
    newLink.subjectType = subjectType;
    return newLink;
  }

  public void update(String title, String description, String link, Authorization authorization, SubjectType subjectType) {
    this.title = title;
    this.description = description;
    this.link = link;
    this.authorization = authorization;
    this.subjectType = subjectType;
  }
}