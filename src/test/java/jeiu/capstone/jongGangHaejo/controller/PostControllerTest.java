package jeiu.capstone.jongGangHaejo.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest //Controller 단위 테스트를 위함. 없으면 @Autowired에 "Autowired members must be defined in valid Spring bean" 에러가 난다
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc; //필드 주입

    @Test
    @DisplayName("GET /test - Hello World")
    void Get_HelloWorld_테스트() throws Exception {
        // expected
        mockMvc.perform(MockMvcRequestBuilders.get("/test")) // /test로 요청시
                .andExpect(status().isOk()) //HTTP 상태코드가 200이여야 하며
                .andExpect(content().string("Hello World")) //Hello World로 내려지는 것을 기대한다
                .andDo(print()); //HTTP요청 정보를 출력되게 함
        //status, content, print등 static import진행 - MockMvcResultMatchers의 메서드들이다.
    }

    @Test
    @DisplayName("POST /test - Hello World")
    void Post_HelloWorld_테스트() throws Exception {
        // expected
        mockMvc.perform(post("/test"))
                .andExpect(status().isOk()) //HTTP 상태코드가 200이여야 하며
                .andExpect(content().string("Hello World")) //Hello World로 내려지는 것을 기대한다
                .andDo(print()); //mockMVC를 통해 요청한 HTTP요청 정보를 출력되게 함
    }

    @Test
    @DisplayName("게시글_요청_JSON_제목 검증")
    void 게시물_제목_검증() throws Exception {
        // expected
        mockMvc.perform(post("/posts")
            .contentType(MediaType.APPLICATION_JSON) //application/json 타입으로
            .content("{\"title\": \"\", \"content\": \"내용\"}") //해당 JSON형식으로 요청
        )

        .andExpect(status().isBadRequest()) //HttpSatus는 400이여야 함

        //ExceptionHanlingController에서 handling 잘 되는지 확인
        .andExpect(jsonPath("$.code").value("400"))
        .andExpect(jsonPath("$.message").value("잘못된 요청 입니다 validation을 참고해주세요"))
        .andDo(print()); //mockMVC를 통해 요청한 HTTP요청 정보를 출력되게 함
    }

    @Test
    @DisplayName("게시글_요청_JSON_내용 검증")
    void 게시물_내용_검증() throws Exception {
        mockMvc.perform(post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"제목\", \"content\": \"\"}")
        )

        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("400"))
        .andExpect(jsonPath("$.message").value("잘못된 요청 입니다 validation을 참고해주세요"))
        .andDo(print());
    }

}









