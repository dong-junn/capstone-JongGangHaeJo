package jeiu.capstone.jongGangHaejo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(RestDocumentationExtension.class)
public class PostDocTest {


    private MockMvc mockMvc;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    @DisplayName("게시물 스니펫 생성")
    void generateDocSnippet() throws Exception {
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
                .andExpect(jsonPath("$.message").value("게시물이 성공적으로 생성되었습니다."))
                .andExpect(status().isOk())
                .andDo(document("index", preprocessRequest(prettyPrint())))
                .andDo(print());


    }

}
