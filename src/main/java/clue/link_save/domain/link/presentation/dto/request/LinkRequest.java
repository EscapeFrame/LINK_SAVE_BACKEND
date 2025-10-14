package clue.link_save.domain.link.presentation.dto.request;

import clue.link_save.domain.link.domain.Authorization;
import clue.link_save.domain.link.domain.SubjectType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkRequest {
  private char grade;
  private char clas;
  private String title;
  private String description;
  private String link;
  private Authorization authorization;
  private SubjectType subjectType;
}
