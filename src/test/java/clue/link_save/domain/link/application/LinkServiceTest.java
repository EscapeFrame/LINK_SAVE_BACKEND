package clue.link_save.domain.link.application;

import clue.link_save.domain.link.domain.AuthorizationType;
import clue.link_save.domain.link.domain.Link;
import clue.link_save.domain.link.domain.SubjectType;
import clue.link_save.domain.link.persistence.LinkRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LinkService 단위 테스트")
class LinkServiceTest {
  @Mock
  private LinkRepository linkRepository;

  @InjectMocks
  private LinkService linkService;

  private Link testLink;

  @BeforeEach
  void setUp() {
    testLink = Link.create('1', '1', "test1", "findById Test",
            "https://google.com", AuthorizationType.PUBLIC, SubjectType.General);

    try {
      java.lang.reflect.Field idField = Link.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(testLink, 1L);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  @DisplayName("링크 ID로 조회 성공")
  void findById_Success() {
    // given
    given(linkRepository.findById(1L)).willReturn(Optional.of(testLink));

    // when
    Link result = linkService.findByIdOrElseThrow(1L);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(testLink.getId());
    assertThat(result.getGrade()).isEqualTo(testLink.getGrade());
    assertThat(result.getClas()).isEqualTo(testLink.getClas());
    assertThat(result.getLink()).isEqualTo(testLink.getLink());
    assertThat(result.getAuthorizationType()).isEqualTo(testLink.getAuthorizationType());
    assertThat(result.getSubjectType()).isEqualTo(testLink.getSubjectType());

    verify(linkRepository, times(1)).findById(1L);
  }

  @Test
  @DisplayName("링크 ID로 조회 실패 시 예외 발생")
  void findById_Fail() throws IllegalStateException {
    // given
    given(linkRepository.findById(999L)).willReturn(Optional.empty());

    // then
    assertThatThrownBy(() -> linkService.findByIdOrElseThrow(999L))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("존재하지 않는 링크입니다.");

    verify(linkRepository, times(1)).findById(999L);
  }

  @Test
  @DisplayName("링크 생성 성공 테스트")
  void create_Success() throws IllegalAccessException, NoSuchFieldException {
    // given
    Link link = Link.create('1','1',"create_test","this link is test for create link","https://google.com",AuthorizationType.PUBLIC,SubjectType.General);
    Link savedLink = Link.create('1','1',"create_test","this link is test for create link","https://google.com",AuthorizationType.PUBLIC,SubjectType.General);

    // Mocking save 시 ID를 가진 객체 반환
    java.lang.reflect.Field idField = Link.class.getDeclaredField("id");
    idField.setAccessible(true);
    idField.set(savedLink, 2L);

    given(linkRepository.save(link)).willReturn(savedLink);
    given(linkRepository.findById(2L)).willReturn(Optional.of(savedLink));

    // when
    Link link1 = linkService.createLink(link);
    Link link2 = linkService.findByIdOrElseThrow(2L);

    // then
    assertThat(link2.getId()).isEqualTo(link1.getId());
    assertThat(link2.getTitle()).isEqualTo(link1.getTitle());

    verify(linkRepository, times(1)).save(link);
    verify(linkRepository, times(1)).findById(2L);
  }

  @Test
  @DisplayName("링크 삭제 테스트")
  void delete_Success() {
    // given
    given(linkRepository.findById(1L)).willReturn(Optional.of(testLink));

    doAnswer(invocation -> {
      given(linkRepository.findById(testLink.getId())).willReturn(Optional.empty());
      return null;
    }).when(linkRepository).delete(testLink);

    // when
    linkService.deleteLink(testLink.getId());

    // then
    verify(linkRepository, times(1)).delete(testLink);

    assertThatThrownBy(() -> linkService.findByIdOrElseThrow(testLink.getId()))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("존재하지 않는 링크입니다.");
  }
}