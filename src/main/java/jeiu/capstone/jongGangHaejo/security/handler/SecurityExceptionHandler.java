package jeiu.capstone.jongGangHaejo.security.handler;

import jeiu.capstone.jongGangHaejo.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class SecurityExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ErrorResponse handleUsernameNotFoundException(UsernameNotFoundException e) {

        Map<String, String> validation = new HashMap<>();
        validation.put("원인", e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "로그인 실패");
        errorResponse.setValidation(validation);
        return errorResponse;
    }
}
