package jeiu.capstone.jongGangHaejo.controller.exception;

import jeiu.capstone.jongGangHaejo.dto.response.ErrorResponse;
import jeiu.capstone.jongGangHaejo.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@ControllerAdvice
public class ExceptionHandlingController {

    /**
     * 유효성 검증 실패 시 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException e) {
        ErrorResponse errorResponse = new ErrorResponse("400", "잘못된 요청입니다. 검증 오류를 확인해주세요.");

        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errorResponse.addValidation(fieldError.getField(), fieldError.getDefaultMessage());
        }

        log.error("Validation error: {}", errorResponse);
        return errorResponse;
    }

    /**
     * FileServiceException 처리
     */
    @ExceptionHandler(FileServiceException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleFileServiceException(FileServiceException e) {
        HttpStatus status;
        String code;
        String message;

        if (e instanceof InvalidFileNameException) {
            status = HttpStatus.BAD_REQUEST;
            code = "400";
            message = e.getMessage();
        } else if (e instanceof S3UploadException || e instanceof AwsSdkException || e instanceof FileUploadException) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            code = "500";
            message = e.getMessage();
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            code = "500";
            message = "파일 서비스 오류가 발생했습니다.";
        }

        ErrorResponse errorResponse = new ErrorResponse(code, message);
        log.error("FileServiceException: {}", e.getMessage(), e);
        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * 기타 예외 처리 (선택 사항)
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleGeneralException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse("500", "서버에서 오류가 발생했습니다.");
        log.error("Unhandled exception: {}", e.getMessage(), e);
        return errorResponse;
    }
}
