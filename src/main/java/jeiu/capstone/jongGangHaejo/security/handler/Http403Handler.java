package jeiu.capstone.jongGangHaejo.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jeiu.capstone.jongGangHaejo.dto.response.ErrorResponseDto;
import jeiu.capstone.jongGangHaejo.security.config.UserConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

// http:403코드에 대한 handler 구현
@Slf4j
@RequiredArgsConstructor
public class Http403Handler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.error("403 handler 호출");

        //로그인 요청한 사용자 찾아오는 코드
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String logginedUser = Optional.ofNullable(authentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getName)
                .orElse("");

        ErrorResponseDto errorResponse = new ErrorResponseDto(403, "요청 거부");
        errorResponse.setValidation(Map.of(
                "원인", "권한이 없습니다.",
                "요청 사용자: ", logginedUser
        ));

        response.setStatus(SC_FORBIDDEN); //403 status 설정
        response.setCharacterEncoding(UTF_8.name()); //인코딩 - utf8로 설정
        response.setContentType(APPLICATION_JSON_VALUE);  // contentType - json으로 설정

        objectMapper.writeValue(response.getWriter(), errorResponse); //errorResponse의 값으로 json세팅
    }
}
