package jeiu.capstone.jongGangHaejo.controller;

import jeiu.capstone.jongGangHaejo.config.SecurityTestConfig;
import jeiu.capstone.jongGangHaejo.domain.user.Role;
import jeiu.capstone.jongGangHaejo.domain.user.User;
import jeiu.capstone.jongGangHaejo.security.config.UserConfig;
import jeiu.capstone.jongGangHaejo.service.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LikeController.class)
@Import(SecurityTestConfig.class)
class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LikeService likeService;

    @BeforeEach
    void setUp() {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_USER);

        User user = User.builder()
                .name("testUser")
                .password("password")
                .roles(roles)
                .build();

        UserConfig userConfig = new UserConfig(user);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userConfig,
                null,
                userConfig.getAuthorities()
        );
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }

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