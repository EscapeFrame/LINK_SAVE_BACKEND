package clue.link_save.domain.link.presentation.dto.request;

import clue.link_save.domain.link.domain.AuthorizationType;
import clue.link_save.domain.link.domain.SubjectType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkRequest {
  private int grade;
  private int clas;
  private String title;
  private String description;
  private String link;
  private AuthorizationType authorizationType;
  private SubjectType subjectType;
}
