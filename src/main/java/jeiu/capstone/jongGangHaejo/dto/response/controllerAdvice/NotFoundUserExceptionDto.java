package jeiu.capstone.jongGangHaejo.dto.response.controllerAdvice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class NotFoundUserExceptionDto {

    private final String message;
}
