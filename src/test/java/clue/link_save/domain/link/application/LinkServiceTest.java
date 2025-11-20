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
import java.util.UUID;

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
  private UUID testUserId;

  @BeforeEach
  void setUp() {
    testUserId = UUID.randomUUID();
    testLink = Link.create(testUserId, 1, 1, "test1", "findById Test",
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
    Link link = Link.create(testUserId, 1, 1, "create_test","this link is test for create link","https://google.com",AuthorizationType.PUBLIC,SubjectType.General);
    Link savedLink = Link.create(testUserId, 1, 1, "create_test","this link is test for create link","https://google.com",AuthorizationType.PUBLIC,SubjectType.General);

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
  @DisplayName("링크 삭제 성공 테스트")
  void delete_Success() {
    // given
    given(linkRepository.findById(1L)).willReturn(Optional.of(testLink));

    doAnswer(invocation -> {
      given(linkRepository.findById(testLink.getId())).willReturn(Optional.empty());
      return null;
    }).when(linkRepository).delete(testLink);

    // when
    linkService.deleteLink(testUserId, testLink.getId());

    // then
    verify(linkRepository, times(1)).delete(testLink);

    assertThatThrownBy(() -> linkService.findByIdOrElseThrow(testLink.getId()))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("존재하지 않는 링크입니다.");
  }

  @Test
  @DisplayName("링크 삭제 권한 없음 테스트")
  void delete_Unauthorized() {
    // given
    UUID otherUserId = UUID.randomUUID();
    given(linkRepository.findById(1L)).willReturn(Optional.of(testLink));

    // when & then
    assertThatThrownBy(() -> linkService.deleteLink(otherUserId, testLink.getId()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("링크를 수정/삭제할 권한이 없습니다.");

    verify(linkRepository, never()).delete(testLink);
  }

  @Test
  @DisplayName("링크 수정 권한 없음 테스트")
  void update_Unauthorized() {
    // given
    UUID otherUserId = UUID.randomUUID();
    given(linkRepository.findById(1L)).willReturn(Optional.of(testLink));

    // when & then
    assertThatThrownBy(() -> linkService.updateLink(otherUserId, testLink.getId(), null))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("링크를 수정/삭제할 권한이 없습니다.");
  }

  @Test
  @DisplayName("Link 엔티티 userId 확인 테스트")
  void link_userId_Test() {
    // given & when
    UUID userId = UUID.randomUUID();
    Link link = Link.create(userId, 1, 1, "test", "description", "https://example.com", AuthorizationType.PUBLIC, SubjectType.General);

    // then
    assertThat(link.getUserId()).isEqualTo(userId);
    assertThat(link.isOwner(userId)).isTrue();
    assertThat(link.isOwner(UUID.randomUUID())).isFalse();
  }

  @Test
  @DisplayName("PUBLIC 링크 조회 - 같은 학년 사용자 접근 가능")
  void findById_Public_SameGradeCanAccess() {
    // given
    UUID otherUserId = UUID.randomUUID();
    Link publicLink = Link.create(testUserId, 1, 1, "Public Link", "Public description",
            "https://google.com", AuthorizationType.PUBLIC, SubjectType.General);

    try {
      java.lang.reflect.Field idField = Link.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(publicLink, 2L);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    given(linkRepository.findById(2L)).willReturn(Optional.of(publicLink));

    // when - 같은 1학년, 다른 반(2반)
    Link result = linkService.findByIdWithAccessControl(otherUserId, 1, 2, 2L);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(2L);
  }

  @Test
  @DisplayName("PUBLIC 링크 조회 - 다른 학년 사용자 접근 불가")
  void findById_Public_DifferentGradeCannotAccess() {
    // given
    UUID otherUserId = UUID.randomUUID();
    Link publicLink = Link.create(testUserId, 1, 1, "Public Link", "Public description",
            "https://google.com", AuthorizationType.PUBLIC, SubjectType.General);

    try {
      java.lang.reflect.Field idField = Link.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(publicLink, 2L);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    given(linkRepository.findById(2L)).willReturn(Optional.of(publicLink));

    // when & then - 2학년 사용자는 1학년 링크 조회 불가
    assertThatThrownBy(() -> linkService.findByIdWithAccessControl(otherUserId, 2, 1, 2L))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("링크를 조회할 권한이 없습니다.");
  }

  @Test
  @DisplayName("PRIVATE 링크 조회 - 작성자만 접근 가능")
  void findById_Private_OnlyOwnerCanAccess() {
    // given
    Link privateLink = Link.create(testUserId, 1, 1, "Private Link", "Private description",
            "https://google.com", AuthorizationType.PRIVATE, SubjectType.General);

    try {
      java.lang.reflect.Field idField = Link.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(privateLink, 3L);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    given(linkRepository.findById(3L)).willReturn(Optional.of(privateLink));

    // when
    Link result = linkService.findByIdWithAccessControl(testUserId, 1, 1, 3L);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(3L);
  }

  @Test
  @DisplayName("PRIVATE 링크 조회 - 같은 학년이지만 작성자 아닌 경우 접근 불가")
  void findById_Private_SameGradeButOtherUserCannotAccess() {
    // given
    UUID otherUserId = UUID.randomUUID();
    Link privateLink = Link.create(testUserId, 1, 1, "Private Link", "Private description",
            "https://google.com", AuthorizationType.PRIVATE, SubjectType.General);

    try {
      java.lang.reflect.Field idField = Link.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(privateLink, 3L);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    given(linkRepository.findById(3L)).willReturn(Optional.of(privateLink));

    // when & then - 같은 1학년이지만 작성자가 아님
    assertThatThrownBy(() -> linkService.findByIdWithAccessControl(otherUserId, 1, 1, 3L))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("링크를 조회할 권한이 없습니다.");
  }

  @Test
  @DisplayName("PRIVATE 링크 조회 - 다른 학년인 경우 접근 불가")
  void findById_Private_DifferentGradeCannotAccess() {
    // given
    Link privateLink = Link.create(testUserId, 1, 1, "Private Link", "Private description",
            "https://google.com", AuthorizationType.PRIVATE, SubjectType.General);

    try {
      java.lang.reflect.Field idField = Link.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(privateLink, 3L);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    given(linkRepository.findById(3L)).willReturn(Optional.of(privateLink));

    // when & then - 작성자지만 다른 학년(2학년)으로 조회 시도
    assertThatThrownBy(() -> linkService.findByIdWithAccessControl(testUserId, 2, 1, 3L))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("링크를 조회할 권한이 없습니다.");
  }

  @Test
  @DisplayName("CLASS_ONLY 링크 조회 - 같은 학년/반 학생만 접근 가능")
  void findById_ClassOnly_SameClassCanAccess() {
    // given
    UUID otherUserId = UUID.randomUUID();
    Link classOnlyLink = Link.create(testUserId, 1, 1, "Class Only Link", "Class only description",
            "https://google.com", AuthorizationType.CLASS_ONLY, SubjectType.General);

    try {
      java.lang.reflect.Field idField = Link.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(classOnlyLink, 4L);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    given(linkRepository.findById(4L)).willReturn(Optional.of(classOnlyLink));

    // when - 같은 1학년 1반 학생
    Link result = linkService.findByIdWithAccessControl(otherUserId, 1, 1, 4L);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(4L);
  }

  @Test
  @DisplayName("CLASS_ONLY 링크 조회 - 다른 학년/반 학생은 접근 불가")
  void findById_ClassOnly_DifferentClassCannotAccess() {
    // given
    UUID otherUserId = UUID.randomUUID();
    Link classOnlyLink = Link.create(testUserId, 1, 1, "Class Only Link", "Class only description",
            "https://google.com", AuthorizationType.CLASS_ONLY, SubjectType.General);

    try {
      java.lang.reflect.Field idField = Link.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(classOnlyLink, 4L);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    given(linkRepository.findById(4L)).willReturn(Optional.of(classOnlyLink));

    // when & then - 2학년 1반 학생
    assertThatThrownBy(() -> linkService.findByIdWithAccessControl(otherUserId, 2, 1, 4L))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("링크를 조회할 권한이 없습니다.");
  }
}