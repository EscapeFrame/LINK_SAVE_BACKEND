package clue.link_save.domain.link.presentation;

import clue.link_save.domain.link.application.LinkService;
import clue.link_save.domain.link.domain.AuthorizationType;
import clue.link_save.domain.link.domain.Link;
import clue.link_save.domain.link.domain.SubjectType;
import clue.link_save.domain.link.presentation.dto.request.LinkRequest;
import clue.link_save.domain.link.presentation.dto.response.LinkResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class LinkController {
  private final LinkService linkService;

  @GetMapping("/linksave") // 링크 전체 조회
  public ResponseEntity<List<LinkResponse>> getAll(
          @RequestParam char grade,
          @RequestParam char clas,
          @RequestParam AuthorizationType authorization,
          @RequestParam()SubjectType subjectType,
          @RequestParam(defaultValue = "40") int size,
          @RequestParam(defaultValue = "0") int offset
  ){
    Page<Link> links = linkService.findAll(grade, clas, authorization, subjectType, size, offset);
    List<LinkResponse> linkResponses = links.stream()
            .map(link -> LinkResponse.from(link))
            .toList();
    return ResponseEntity.ok(linkResponses);
  }

  @GetMapping("/linksave/{link_id}") // 링크 단일 조회
  public ResponseEntity<LinkResponse> getAllLink(@PathVariable Long link_id){
    Link link = linkService.findByIdOrElseThrow(link_id);
    return ResponseEntity.ok(LinkResponse.from(link));
  }

  @PostMapping("/linksave") // 링크 저장
  public ResponseEntity<LinkResponse> getLink(@RequestBody LinkRequest linkRequest){
    Link link = Link.create(
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

  @DeleteMapping("/linksave/{link_id}") // 링크 삭제
  public ResponseEntity<?> deleteLink(@PathVariable Long link_id){
    linkService.deleteLink(link_id);
    return ResponseEntity.ok(true);
  }

  @PatchMapping("/linksave/{link_id}") // 링크 수정
  public ResponseEntity<LinkResponse> updateLink(@PathVariable Long link_id, @RequestBody LinkRequest linkRequest){
    Link link = linkService.updateLink(link_id,linkRequest);
    return ResponseEntity.ok(LinkResponse.from(link));
  }
}
