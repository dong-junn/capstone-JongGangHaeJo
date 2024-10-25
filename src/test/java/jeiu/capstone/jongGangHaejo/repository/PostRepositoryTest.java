package jeiu.capstone.jongGangHaejo.repository;

import jeiu.capstone.jongGangHaejo.domain.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("게시물 DB에 저장되는지 확인")
    void PostSaveTest() {
        //given -> 이러한 자원이 주어졌을때
        Post post = Post.builder() //Builder 패턴 사용
                .username("test")
                .title("제목")
                .content("내용")
                .team("team A")
                .youtubelink("https://youtube.com/example")
                .build(); //new Post(title, content)와 같다
        //when -> 이러한 로직을 수행 후
        Post savedPost = postRepository.save(post); //JPA를 이용해 저장

        //then -> 검증한다
        assertThat(savedPost.getPostid()).isNotNull(); //Id가 있는지 확인 후
        assertThat(savedPost.getTitle()).isEqualTo("제목"); //title에 제목이 저장됐는지 확인 후
        assertThat(savedPost.getContent()).isEqualTo("내용"); //content에 내용이 저장됐는지 확인한다
        assertThat(savedPost.getUsername()).isEqualTo("test");
        assertThat(savedPost.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("제목으로 게시물 검색 테스트")
    void findByTitleContainingTest() {
        // given
        List<Post> posts = Arrays.asList(
                Post.builder()
                        .title("스프링 게시물")
                        .content("스프링 내용")
                        .team("팀A")
                        .build(),
                Post.builder()
                        .title("리액트 게시물")
                        .content("리액트 내용")
                        .team("팀B")
                        .build(),
                Post.builder()
                        .title("스프링부트 게시물")
                        .content("스프링부트 내용")
                        .team("팀C")
                        .build()
        );
        postRepository.saveAll(posts);

        // when
        Page<Post> foundPosts = postRepository.findByTitleContaining(
                "스프링",
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"))
        );

        // then
        assertThat(foundPosts.getContent()).hasSize(2);
        assertThat(foundPosts.getContent())
                .extracting("title")
                .containsExactlyInAnyOrder("스프링 게시물", "스프링부트 게시물");
    }
}
