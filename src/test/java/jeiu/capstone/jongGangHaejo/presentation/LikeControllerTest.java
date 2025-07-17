package jeiu.capstone.jongGangHaejo.presentation;

import jeiu.capstone.jongGangHaejo.annotation.WithMockCustomUser;
import jeiu.capstone.jongGangHaejo.config.SecurityTestConfig;
import jeiu.capstone.jongGangHaejo.service.LikeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LikeController.class)
@Import(SecurityTestConfig.class)
@WithMockCustomUser
class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LikeService likeService;

    @Test
    void 좋아요_토글_성공() throws Exception {
        // given
        Long postId = 1L;

        // when & then
        mockMvc.perform(put("/posts/{postId}/like", postId)
                        .with(csrf()))

                .andExpect(status().isOk());

        // verify
        Mockito.verify(likeService, Mockito.times(1)).toggleLike(anyLong());
    }
} 