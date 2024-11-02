package jeiu.capstone.jongGangHaejo.controller.exception;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class SecurityExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public Map<String, String> handleUsernameNotFoundException(UsernameNotFoundException e) {

        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        return error;
    }
}
