package jeiu.capstone.jongGangHaejo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 예시 JSON
 * {
 *     "code": "400",
 *     "message": "Client 에러",
 *     "validation" {
 *         "title": "값이 비었습니다 값은 필수 값 입니다"
 *     }
 * }
 * 와 같은 JSON 응답 값을 만들기 위한 Class이다. ExceptionHandling Controller에서 사용한다.
 */
/**
 * 오류 응답을 위한 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String code;
    private String message;
    private Map<String, String> validations = new HashMap<>();

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 검증 오류를 추가하는 메서드
     *
     * @param field   오류가 발생한 필드 이름
     * @param message 오류 메시지
     */
    public void addValidation(String field, String message) {
        this.validations.put(field, message);
    }
}
