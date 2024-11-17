package jeiu.capstone.jongGangHaejo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubeUrlValidator implements ConstraintValidator<ValidYoutubeUrl, String> {

    // 유튜브 링크를 확인하기 위한 정규표현식 패턴
    private static final String YOUTUBE_URL_REGEX =
            "^(https?://)?(www\\.)?(youtube\\.com/watch\\?v=|youtu\\.be/)([a-zA-Z0-9_-]{11})$";

    private Pattern pattern = Pattern.compile(YOUTUBE_URL_REGEX);

    @Override
    public boolean isValid(String youtubeLink, ConstraintValidatorContext context) {
        // 유튜브 링크가 null이거나 비어있으면 검증 통과 (링크가 필수가 아닐 경우)
        if (youtubeLink == null || youtubeLink.isEmpty()) {
            return true;
        }

        // 이미 embed 형식이면 그대로 통과
        if (youtubeLink.contains("youtube.com/embed/")) {
            return true;
        }

        return pattern.matcher(youtubeLink).matches();
    }

    // URL을 embed 형식으로 변환하는 정적 메서드 추가
    public static String convertToEmbedUrl(String youtubeLink) {
        if (youtubeLink == null || youtubeLink.isEmpty()) {
            return youtubeLink;
        }

        // 이미 embed 형식이면 그대로 반환
        if (youtubeLink.contains("youtube.com/embed/")) {
            return youtubeLink;
        }

        Pattern pattern = Pattern.compile(YOUTUBE_URL_REGEX);
        Matcher matcher = pattern.matcher(youtubeLink);
        
        if (matcher.find()) {
            String videoId = matcher.group(4);  // 정규식의 4번째 그룹이 video ID
            return "https://www.youtube.com/embed/" + videoId;
        }
        
        return youtubeLink;  // 매칭되지 않으면 원본 반환
    }
}
