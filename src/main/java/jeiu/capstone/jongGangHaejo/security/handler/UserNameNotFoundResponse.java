package jeiu.capstone.jongGangHaejo.security.handler;

import jeiu.capstone.jongGangHaejo.dto.response.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class UserNameNotFoundResponse {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ErrorResponseDto handleUsernameNotFoundException(UsernameNotFoundException e) {

        Map<String, String> validation = new HashMap<>();
        validation.put("원인", e.getMessage());

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.UNAUTHORIZED.value(), "로그인 실패");
        errorResponseDto.setValidation(validation);
        return errorResponseDto;
    }
}
