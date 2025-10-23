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

@Service
@RequiredArgsConstructor
@Transactional
public class LinkService {
  private final LinkRepository linkRepository;

  public Link findByIdOrElseThrow(Long id) {
    return linkRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 링크입니다."));
  }

  public Page<Link> findAll(char grade, char clas, AuthorizationType authorizationType, SubjectType subjectType, int size, int offset) {
    Pageable pageable = PageRequest.of(offset, size);
    return linkRepository.findByGradeAndClasAndAuthorizationType(grade, clas, authorizationType, pageable);
  }

  @Transactional
  public Link createLink(Link link){
    return linkRepository.save(link);
  }

  @Transactional
  public void deleteLink(Long id) {
    Link link = findByIdOrElseThrow(id);
    linkRepository.delete(link);
  }

  @Transactional
  public Link updateLink(Long linkId, LinkRequest linkRequest) {
    Link link = findByIdOrElseThrow(linkId);
    link.update(linkRequest.getTitle(),linkRequest.getLink(),linkRequest.getDescription(),linkRequest.getAuthorizationType(), linkRequest.getSubjectType());
    return link;
  }
}
