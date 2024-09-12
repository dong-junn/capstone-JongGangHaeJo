package jeiu.capstone.jongGangHaejo.repository;

import jeiu.capstone.jongGangHaejo.domain.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class RepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("게시물 DB에 저장되는지 확인")
    void PostSaveTest() {

        //given -> 이러한 자원이 주어졌을때
        Post post = Post.builder() //Builder 패턴 사용
                .title("제목")
                .content("내용")
                .build(); //new Post(title, content)와 같다
        //when -> 이러한 로직을 수행 후
        Post savedPost = postRepository.save(post); //JPA를 이용해 저장

        //then -> 검증한다
        assertThat(savedPost.getId()).isNotNull(); //Id가 있는지 확인 후
        assertThat(savedPost.getTitle()).isEqualTo("제목"); //title에 제목이 저장됐는지 확인 후
        assertThat(savedPost.getContent()).isEqualTo("내용"); //content에 내용이 저장됐는지 확인한다
    }
}
