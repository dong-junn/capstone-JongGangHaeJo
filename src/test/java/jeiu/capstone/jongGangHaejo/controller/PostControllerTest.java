package jeiu.capstone.jongGangHaejo.controller;

import io.awspring.cloud.s3.S3Template;
import jeiu.capstone.jongGangHaejo.domain.Post;
import jeiu.capstone.jongGangHaejo.repository.PostRepository;
import jeiu.capstone.jongGangHaejo.service.FileService;
import jeiu.capstone.jongGangHaejo.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest //Controller 단위 테스트를 위함. 없으면 @Autowired에 "Autowired members must be defined in valid Spring bean" 에러가 난다
@AutoConfigureMockMvc(addFilters = false)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc; //필드 주입

    @Autowired
    private PostRepository postRepository;


    @MockBean
    private FileService fileService;  // MockBean으로 선언

    @MockBean
    private PostService postService;  // MockBean으로 선언

    @MockBean
    private S3Template s3Template;

    @Test
    @DisplayName("GET /test - Hello World")
    void Get_HelloWorld_테스트() throws Exception {
        // expected
        mockMvc.perform(get("/test")) // /test로 요청시
                .andExpect(status().isOk()) //HTTP 상태코드가 200이여야 하며
                .andExpect(content().string("안녕하세요 종강해조 프로젝트입니다")) //Hello World로 내려지는 것을 기대한다
                .andDo(print()); //HTTP요청 정보를 출력되게 함
        //status, content, print등 static import진행 - MockMvcResultMatchers의 메서드들이다.
    }

    @Test
    @DisplayName("파일_업로드_및_게시물_생성_테스트")
    void 파일_업로드_및_게시물_생성_테스트() throws Exception {
        // 가짜 MultipartFile 객체 생성
        MockMultipartFile file1 = new MockMultipartFile("files", "test-image.png", "image/png", "test-image".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "test-document.txt", "text/plain", "test-document".getBytes());

        // 게시물 데이터를 JSON 형식으로 생성 (multipart/form-data 내의 JSON 파트)
        MockMultipartFile post = new MockMultipartFile("post", null,
                "application/json", "{\"title\": \"Test Title\", \"content\": \"Test Content\", \"team\": \"Test Team\", \"youtubelink\": \"https://www.youtube.com/watch?v=czPWLqZP1UQ\"}".getBytes());

        // multipart/form-data 요청으로 게시물과 파일을 함께 전송
        mockMvc.perform(multipart("/post")
                        .file(post)  // 게시물 데이터 전송
                        .file(file1)  // 첫 번째 파일 전송
                        .file(file2)  // 두 번째 파일 전송
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시물이 성공적으로 생성되었습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("전체 게시물 목록 조회")
    void getAllPosts() throws Exception {
        // given
        List<Post> posts = Arrays.asList(
                Post.builder()
                        .title("첫번째 게시물")
                        .content("첫번째 내용")
                        .team("팀A")
                        .username("user1")
                        .build(),
                Post.builder()
                        .title("두번째 게시물")
                        .content("두번째 내용")
                        .team("팀B")
                        .username("user2")
                        .build()
        );
        postRepository.saveAll(posts);

        // when & then
        mockMvc.perform(get("/posts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].title").value("첫번째 게시물"))
                .andExpect(jsonPath("$.content[1].title").value("두번째 게시물"))
                .andDo(print());
    }

    @Test
    @DisplayName("단일 게시물 조회")
    void getPostById() throws Exception {
        // given
        Post post = Post.builder()
                .title("테스트 게시물")
                .content("테스트 내용")
                .team("테스트 팀")
                .username("tester")
                .youtubelink("https://youtube.com/watch?v=test")
                .build();
        Post savedPost = postRepository.save(post);

        // when & then
        mockMvc.perform(get("/posts/{id}", savedPost.getPostid())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("테스트 게시물"))
                .andExpect(jsonPath("$.content").value("테스트 내용"))
                .andExpect(jsonPath("$.team").value("테스트 팀"))
                .andExpect(jsonPath("$.username").value("tester"))
                .andExpect(jsonPath("$.youtubelink").value("https://youtube.com/watch?v=test"))
                .andExpect(jsonPath("$.files").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 게시물 조회시 404 응답")
    void getPostByIdNotFound() throws Exception {
        // when & then
        mockMvc.perform(get("/posts/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("게시물을 찾을 수 없습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("제목으로 게시물 검색")
    void searchPostsByTitle() throws Exception {
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
                        .build()
        );
        postRepository.saveAll(posts);

        // when & then
        mockMvc.perform(get("/posts/search")
                        .param("keyword", "스프링")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title").value("스프링 게시물"))
                .andDo(print());
    }

    @Test
    @DisplayName("페이징 처리된 게시물 목록 조회")
    void getPostsWithPagination() throws Exception {
        // given
        List<Post> posts = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            posts.add(Post.builder()
                    .title("게시물 " + i)
                    .content("내용 " + i)
                    .team("팀 " + i)
                    .build());
        }
        postRepository.saveAll(posts);

        // when & then
        mockMvc.perform(get("/posts")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "createdAt,desc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(10)))
                .andExpect(jsonPath("$.totalElements").value(15))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.first").value(true))
                .andDo(print());
    }

}









