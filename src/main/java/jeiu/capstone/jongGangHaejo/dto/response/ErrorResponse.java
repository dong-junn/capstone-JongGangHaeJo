package jeiu.capstone.jongGangHaejo.dto.response;

import lombok.Getter;
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
 * 와 같은 JSON 응답 값을 만들기 위한 Class이다
 */
@Getter
@RequiredArgsConstructor
public class ErrorResponse {

    private final String code;
    private final String message;

    private Map<String, String> validation = new HashMap<>();

    public void addValidation(String fieldName, String errorMessage) {
        this.validation.put(fieldName, errorMessage);
    }
}
