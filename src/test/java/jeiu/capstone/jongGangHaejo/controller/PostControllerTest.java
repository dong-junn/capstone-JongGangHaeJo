package jeiu.capstone.jongGangHaejo.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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
    @DisplayName("/test - Hello, World 테스트")
    void test() throws Exception {
        // expected
        mockMvc.perform(MockMvcRequestBuilders.get("/test")) // /test로 요청시
                .andExpect(status().isOk()) //HTTP 상태코드가 200이여야 하며
                .andExpect(content().string("Hello World")) //Hello World로 내려지는 것을 기대한다
                .andDo(print()); //HTTP요청 정보를 출력되게 함
        //status, content, print등 static import진행 - MockMvcResultMatchers의 메서드들이다.
    }

}









