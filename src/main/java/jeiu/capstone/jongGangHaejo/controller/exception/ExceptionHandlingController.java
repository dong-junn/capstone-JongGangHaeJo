package jeiu.capstone.jongGangHaejo.controller.exception;

import jeiu.capstone.jongGangHaejo.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class ExceptionHandlingController {

    @ResponseStatus(HttpStatus.BAD_REQUEST) //http 400으로 응답
    @ExceptionHandler(MethodArgumentNotValidException.class) //@NotBlank등을 통해 예외 발생시 MethodArgumentNotValidException이 발생한다
    @ResponseBody
    public ErrorResponse validationHandler(MethodArgumentNotValidException e) {
        ErrorResponse errorResponse = new ErrorResponse("400", "잘못된 요청 입니다 validation을 참고해주세요");

        for (FieldError fieldError : e.getFieldErrors()) {
            errorResponse.addValidation(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return errorResponse;
    }
}
