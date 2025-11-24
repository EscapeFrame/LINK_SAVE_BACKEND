package clue.link_save.domain.link.application;

import clue.link_save.domain.link.domain.AuthorizationType;
import clue.link_save.domain.link.domain.Link;
import clue.link_save.domain.link.domain.SubjectType;
import clue.link_save.domain.link.persistence.LinkRepository;
import clue.link_save.domain.link.presentation.dto.request.LinkRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class LinkService {
  private final LinkRepository linkRepository;

  public Link findByIdOrElseThrow(Long id) {
    return linkRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 링크입니다."));
  }

  public Link findByIdWithAccessControl(UUID userId, int userGrade, int userClas, Long linkId) {
    Link link = findByIdOrElseThrow(linkId);
    validateReadAccess(link, userId, userGrade, userClas);
    return link;
  }

  public Page<Link> findAll(int grade, int clas, AuthorizationType authorizationType, SubjectType subjectType, int size, int offset) {
    Pageable pageable = PageRequest.of(offset, size);
    return linkRepository.findByGradeAndClasAndAuthorizationType(grade, clas, authorizationType, pageable);
  }

  public Page<Link> findAllAccessibleLinks(UUID userId, int grade, int clas, SubjectType subjectType, int size, int offset) {
    Pageable pageable = PageRequest.of(offset, size);
    return linkRepository.findAccessibleLinks(userId, grade, clas, subjectType, pageable);
  }

  @Transactional
  public Link createLink(Link link){
    return linkRepository.save(link);
  }

  @Transactional
  public void deleteLink(UUID userId, Long id) {
    Link link = findByIdOrElseThrow(id);
    validateOwnership(link, userId);
    linkRepository.delete(link);
  }

  @Transactional
  public Link updateLink(UUID userId, Long linkId, LinkRequest linkRequest) {
    Link link = findByIdOrElseThrow(linkId);
    validateOwnership(link, userId);
    link.update(linkRequest.getTitle(),linkRequest.getDescription(),linkRequest.getLink(),linkRequest.getAuthorizationType(), linkRequest.getSubjectType());
    return link;
  }

  private void validateOwnership(Link link, UUID userId) {
    if (!link.isOwner(userId)) {
      throw new IllegalStateException("링크를 수정/삭제할 권한이 없습니다.");
    }
  }

  private void validateReadAccess(Link link, UUID userId, int userGrade, int userClas) {
    // 모든 경우에 같은 학년만 조회 가능
    if (link.getGrade() != userGrade) {
      throw new IllegalStateException("링크를 조회할 권한이 없습니다.");
    }

    AuthorizationType authType = link.getAuthorizationType();

    switch (authType) {
      case PUBLIC:
        // 같은 학년이면 누구나 접근 가능
        return;
      case PRIVATE:
        // 같은 학년이면서 작성자만 접근 가능
        if (!link.isOwner(userId)) {
          throw new IllegalStateException("링크를 조회할 권한이 없습니다.");
        }
        break;
      case CLASS_ONLY:
        // 같은 학년/반 학생만 접근 가능
        if (link.getClas() != userClas) {
          throw new IllegalStateException("링크를 조회할 권한이 없습니다.");
        }
        break;
    }
  }
}
