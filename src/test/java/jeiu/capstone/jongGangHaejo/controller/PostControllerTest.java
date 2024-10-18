package jeiu.capstone.jongGangHaejo.controller;

import io.awspring.cloud.s3.S3Template;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest //Controller 단위 테스트를 위함. 없으면 @Autowired에 "Autowired members must be defined in valid Spring bean" 에러가 난다
@AutoConfigureMockMvc(addFilters = false)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc; //필드 주입

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
        mockMvc.perform(MockMvcRequestBuilders.get("/test")) // /test로 요청시
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

}









