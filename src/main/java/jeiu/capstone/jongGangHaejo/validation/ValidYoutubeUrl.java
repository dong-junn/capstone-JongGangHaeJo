package jeiu.capstone.jongGangHaejo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = YoutubeUrlValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidYoutubeUrl {

    String message() default "유효하지 않은 유튜브 링크입니다.";  // 기본 메시지

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
