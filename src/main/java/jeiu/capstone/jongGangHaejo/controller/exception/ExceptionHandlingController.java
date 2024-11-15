package jeiu.capstone.jongGangHaejo.controller.exception;

import jakarta.persistence.EntityNotFoundException;
import jeiu.capstone.jongGangHaejo.dto.response.ErrorResponseDto;
import jeiu.capstone.jongGangHaejo.exception.common.CommonErrorCode;
import jeiu.capstone.jongGangHaejo.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.FieldError;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

/**
 * 전역 예외 처리 컨트롤러
 * 애플리케이션 전반에서 발생하는 예외를 일관된 형식으로 처리하여 클라이언트에게 반환합니다.
 */
@Slf4j
@RestControllerAdvice // @ControllerAdvice + @ResponseBody
public class ExceptionHandlingController {

    /**
     * 유효성 검증 실패 시 처리
     *
     * @param e MethodArgumentNotValidException 예외 객체
     * @return ErrorResponse를 담은 ResponseEntity
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException e) {
        CommonErrorCode errorCode = CommonErrorCode.INVALID_ARGUMENT_ERROR;
        Map<String, String> validationErrors = new HashMap<>();

        // 각 필드의 유효성 검증 오류 메시지를 맵에 저장
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        log.error("Validation error: {}", validationErrors);

        // ErrorResponse 객체 생성 및 validation 정보 추가
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(errorCode.getCode(), errorCode.getMessage());
        errorResponseDto.setValidation(validationErrors);

        return new ResponseEntity<>(errorResponseDto, errorCode.getHttpStatus());
    }

    /**
     * 커스텀 파일 서비스 예외 처리
     *
     * @param e FileServiceException 예외 객체
     * @return ErrorResponse를 담은 ResponseEntity
     */
    @ExceptionHandler(FileServiceException.class)
    public ResponseEntity<ErrorResponseDto> handleFileServiceException(FileServiceException e) {
        CommonErrorCode errorCode;

        // 예외 타입에 따라 적절한 오류 코드 선택
        if (e instanceof InvalidFileNameException) {
            errorCode = CommonErrorCode.INVALID_ARGUMENT_ERROR; // 잘못된 파일 이름
        } else if (e instanceof S3UploadException || e instanceof AwsSdkException || e instanceof FileUploadException) {
            errorCode = CommonErrorCode.FILE_UPLOAD_ERROR; // 파일 업로드 관련 오류
        } else {
            errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR; // 기타 파일 서비스 오류
        }

        log.error("FileServiceException: {}", e.getMessage(), e);

        // ErrorResponse 객체 생성
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(errorCode.getCode(), errorCode.getMessage());

        return new ResponseEntity<>(errorResponseDto, errorCode.getHttpStatus());
    }

    /**
     * 리소스 찾기 실패 시 처리
     *
     * @param e ResourceNotFoundException 예외 객체
     * @return ErrorResponse를 담은 ResponseEntity
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(ResourceNotFoundException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.error("ResourceNotFoundException: {}", e.getMessage(), e);

        // ErrorResponse 객체 생성 (메시지에 동적 데이터 포함 가능)
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(errorCode.getCode(), e.getMessage());

        return new ResponseEntity<>(errorResponseDto, errorCode.getHttpStatus());
    }

    /**
     * 데이터 접근 관련 예외 처리 (스프링의 DataAccessException)
     *
     * @param e DataAccessException 예외 객체
     * @return ErrorResponse를 담은 ResponseEntity
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponseDto> handleDataAccessException(DataAccessException e) {
        CommonErrorCode errorCode = CommonErrorCode.DATABASE_ERROR;
        log.error("DataAccessException: {}", e.getMessage(), e);

        // ErrorResponse 객체 생성
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(errorCode.getCode(), errorCode.getMessage());

        return new ResponseEntity<>(errorResponseDto, errorCode.getHttpStatus());
    }

    /**
     * 기타 모든 예외 처리
     *
     * @param e Exception 예외 객체
     * @return ErrorResponse를 담은 ResponseEntity
     */
    //@ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneralException(Exception e) {
        CommonErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
        log.error("Unhandled exception: {}", e.getMessage(), e);

        // ErrorResponse 객체 생성
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(errorCode.getCode(), errorCode.getMessage());

        return new ResponseEntity<>(errorResponseDto, errorCode.getHttpStatus());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponseDto> handleUnauthorizedException(UnauthorizedException e) {
        CommonErrorCode errorCode = e.getErrorCode();
        log.error("UnauthorizedException: {}", e.getMessage(), e);

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(errorCode.getCode(), e.getMessage());

        return new ResponseEntity<>(errorResponseDto, errorCode.getHttpStatus());
    }

    /**
     * 메서드 인자 타입 불일치 예외 처리
     *
     * @param e MethodArgumentTypeMismatchException 예외 객체
     * @return ErrorResponse를 담은 ResponseEntity
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        CommonErrorCode errorCode = CommonErrorCode.INVALID_ARGUMENT_ERROR;

        String message = String.format("'%s' 파라미터의 타입이 잘못되었습니다. 예상 타입: %s", e.getName(), e.getRequiredType().getSimpleName());

        log.error("MethodArgumentTypeMismatchException: {}", message, e);

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(errorCode.getCode(), message);

        return new ResponseEntity<>(errorResponseDto, errorCode.getHttpStatus());
    }


    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalStateException(IllegalStateException e) {
        CommonErrorCode errorCode = CommonErrorCode.INVALID_ARGUMENT_ERROR;
        log.error("IllegalStateException: {}", e.getMessage(), e);
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(errorCode.getCode(), e.getMessage());
        return new ResponseEntity<>(errorResponseDto, errorCode.getHttpStatus());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleEntityNotFoundException(EntityNotFoundException e) {
        CommonErrorCode errorCode = CommonErrorCode.ADMIN_USERNAME_NOT_FOUND_ERROR;
        log.error("EntityNotFoundException: {}", e.getMessage(), e);
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(errorCode.getCode(), e.getMessage());
        return new ResponseEntity<>(errorResponseDto, errorCode.getHttpStatus());
    }
}
