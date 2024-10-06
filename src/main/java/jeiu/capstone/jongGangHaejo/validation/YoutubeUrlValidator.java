package jeiu.capstone.jongGangHaejo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubeUrlValidator implements ConstraintValidator<ValidYoutubeUrl, String> {

    // 유튜브 링크를 확인하기 위한 정규표현식 패턴
    private static final String YOUTUBE_URL_REGEX =
            "^(https?://)?(www\\.)?(youtube\\.com|youtu\\.be)/(watch\\?v=)?([a-zA-Z0-9_-]{11})$";

    private Pattern pattern = Pattern.compile(YOUTUBE_URL_REGEX);

    @Override
    public boolean isValid(String youtubeLink, ConstraintValidatorContext context) {
        // 유튜브 링크가 null이거나 비어있으면 검증 통과 (링크가 필수가 아닐 경우)
        if (youtubeLink == null || youtubeLink.isEmpty()) {
            return true;
        }

        // 정규 표현식을 사용해 유튜브 링크 검증
        Matcher matcher = pattern.matcher(youtubeLink);
        return matcher.matches();
    }
}
