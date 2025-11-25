package clue.link_save.domain.link.presentation;

import clue.link_save.domain.link.application.LinkService;
import clue.link_save.domain.link.domain.AuthorizationType;
import clue.link_save.domain.link.domain.Link;
import clue.link_save.domain.link.domain.SubjectType;
import clue.link_save.domain.link.presentation.dto.request.LinkRequest;
import clue.link_save.domain.link.presentation.dto.response.LinkResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class LinkController {
  private final LinkService linkService;

  @GetMapping("/linksave")
  public ResponseEntity<List<LinkResponse>> getAll(
          @RequestParam UUID userId,
          @RequestParam int grade,
          @RequestParam int clas,
          @RequestParam(required = false) SubjectType subjectType,
          @RequestParam(required = false)AuthorizationType authorizationType,
          @RequestParam(defaultValue = "40") int size,
          @RequestParam(defaultValue = "0") int offset
  ){
    Page<Link> links = linkService.findAllAccessibleLinks(userId, grade, clas, subjectType, authorizationType, size, offset);
    List<LinkResponse> linkResponses = links.stream()
            .map(link -> {
              LinkResponse linkResponse = LinkResponse.from(link);
              linkResponse.setMine(link.getUserId().equals(userId));
              return linkResponse;
            })
            .toList();
    return ResponseEntity.ok(linkResponses);
  }

  @GetMapping("/linksave/{link_id}")
  public ResponseEntity<LinkResponse> getAllLink(
          @RequestParam UUID userId,
          @RequestParam int grade,
          @RequestParam int clas,
          @PathVariable Long link_id
  ){
    Link link = linkService.findByIdWithAccessControl(userId, grade, clas, link_id);
    return ResponseEntity.ok(LinkResponse.from(link));
  }

  @PostMapping("/linksave") // 링크 저장
  public ResponseEntity<LinkResponse> getLink(
          @RequestParam UUID userId,
          @RequestBody LinkRequest linkRequest
  ) {
    Link link = Link.create(
            userId,
            linkRequest.getGrade(),
            linkRequest.getClas(),
            linkRequest.getTitle(),
            linkRequest.getDescription(),
            linkRequest.getLink(),
            linkRequest.getAuthorizationType(),
            linkRequest.getSubjectType()
    );
    linkService.createLink(link);
    return ResponseEntity.ok(LinkResponse.from(link));
  }

  @DeleteMapping("/linksave/{link_id}")
  public ResponseEntity<?> deleteLink(
          @RequestParam UUID userId,
          @PathVariable Long link_id
  ){
    try {
      linkService.deleteLink(userId, link_id);
      return ResponseEntity.status(HttpStatus.OK).build();
    } catch(EntityNotFoundException ne) {
      return ResponseEntity.notFound().build();
    } catch(IllegalStateException ise) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @PatchMapping("/linksave/{link_id}")
  public ResponseEntity<LinkResponse> updateLink(
          @RequestParam UUID userId,
          @PathVariable Long link_id,
          @RequestBody LinkRequest linkRequest
  ){
    try {
      Link link = linkService.updateLink(userId, link_id, linkRequest);
      return ResponseEntity.ok(LinkResponse.from(link));
    } catch(EntityNotFoundException ne) {
      return ResponseEntity.notFound().build();
    } catch(IllegalStateException ise) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }
}