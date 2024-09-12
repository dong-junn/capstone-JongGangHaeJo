package jeiu.capstone.jongGangHaejo.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        mockMvc.perform(MockMvcRequestBuilders.post("/test")
                        .contentType(MediaType.APPLICATION_JSON) //application/json 타입으로
                        .content("{\"title\": \"제목\", \"content\": \"내용\"}") //해당 JSON형식으로 요청
                )
                .andExpect(status().isOk()) //HTTP 상태코드가 200이여야 하며
                .andExpect(content().string("Hello World")) //Hello World로 내려지는 것을 기대한다
                .andDo(print()); //mockMVC를 통해 요청한 HTTP요청 정보를 출력되게 함
        //status, content, print등 static import진행 - MockMvcResultMatchers의 메서드들이다.
    }

    @Test
    @DisplayName("요청 받아온 값 검증")
    void 요청받은_값_검증() throws Exception {
        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/test")
                        .contentType(MediaType.APPLICATION_JSON) //application/json 타입으로
                        .content("{\"title\": \"\", \"content\": \"내용\"}") //해당 JSON형식으로 요청
                )
                .andExpect(status().isOk()) //HTTP 상태코드가 200이여야 하며
                .andExpect(content().string("Hello World")) //Hello World로 내려지는 것을 기대한다
                .andDo(print()); //mockMVC를 통해 요청한 HTTP요청 정보를 출력되게 함
        //status, content, print등 static import진행 - MockMvcResultMatchers의 메서드들이다.
    }

}









