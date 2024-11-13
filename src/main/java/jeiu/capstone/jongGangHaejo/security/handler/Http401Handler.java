package jeiu.capstone.jongGangHaejo.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jeiu.capstone.jongGangHaejo.dto.response.ErrorResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static jakarta.servlet.http.HttpServletResponse.*;
import static java.nio.charset.StandardCharsets.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

// http:401 코드에 대한 Handler 구현
@Slf4j
@RequiredArgsConstructor
public class Http401Handler implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.error("401 handler 호출");

        ErrorResponseDto errorResponse = new ErrorResponseDto(401, "요청 거부");
        errorResponse.setValidation(Map.of("원인", "로그인 하지 않으셨습니다. 로그인 후 이용해주세요."));

        response.setStatus(SC_UNAUTHORIZED); //401 status 설정
        response.setCharacterEncoding(UTF_8.name()); //인코딩 - utf8로 설정
        response.setContentType(APPLICATION_JSON_VALUE);  // contentType - json으로 설정

        objectMapper.writeValue(response.getWriter(), errorResponse); //errorResponse의 값으로 json세팅
    }
}
