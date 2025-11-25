package clue.link_save.domain.link.presentation.dto.response;

import clue.link_save.domain.link.domain.AuthorizationType;
import clue.link_save.domain.link.domain.Link;
import clue.link_save.domain.link.domain.SubjectType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LinkResponse {
  private Long id;
  private int grade;
  private int clas;
  private String title;
  private String description;
  private String link;
  private AuthorizationType authorizationType;
  private SubjectType subjectType;
  private boolean isMine;

  public static LinkResponse from(Link link){
    LinkResponse response = new LinkResponse();
    response.id = link.getId();
    response.grade = link.getGrade();
    response.clas = link.getClas();
    response.title = link.getTitle();
    response.description = link.getDescription();
    response.link = link.getLink();
    response.authorizationType = link.getAuthorizationType();
    response.subjectType = link.getSubjectType();
    return response;
  }
}
