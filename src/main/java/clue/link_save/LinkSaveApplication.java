package clue.link_save;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LinkSaveApplication {

  public static void main(String[] args) {
    // .env 파일 로드
    Dotenv dotenv = Dotenv.configure()
        .directory("./")
        .ignoreIfMissing()
        .load();

    // .env 파일의 모든 변수를 시스템 환경 변수로 설정
    dotenv.entries().forEach(entry ->
        System.setProperty(entry.getKey(), entry.getValue())
    );

    SpringApplication.run(LinkSaveApplication.class, args);
  }

}
