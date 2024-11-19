package jeiu.capstone.jongGangHaejo.validation;

import lombok.extern.slf4j.Slf4j;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class YoutubeUrlValidator implements ConstraintValidator<ValidYoutubeUrl, String> {

    // URL이 null이거나 빈 문자열인 경우도 허용
    @Override
    public boolean isValid(String url, ConstraintValidatorContext context) {
        if (url == null || url.trim().isEmpty()) {
            return true;
        }
        return isValidYoutubeUrl(url);
    }

    private boolean isValidYoutubeUrl(String url) {
        String pattern = "^(https?://)?(www\\.)?(youtube\\.com/watch\\?v=|youtu\\.be/|youtube\\.com/embed/)[A-Za-z0-9_-]+" +
                        "(\\?[^&\\s]*)?(&[^&\\s]*)*$";
        return Pattern.matches(pattern, url);
    }

    public static String convertToEmbedUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return null;
        }

        try {
            String videoId = extractVideoId(url);
            if (videoId != null) {
                return "https://www.youtube.com/embed/" + videoId;
            }
        } catch (Exception e) {
            log.warn("YouTube URL 변환 중 오류 발생: {}", url, e);
        }
        return url;
    }

    private static String extractVideoId(String url) {
        // youtu.be 형식
        Pattern shortPattern = Pattern.compile("youtu\\.be/([A-Za-z0-9_-]+)");
        Matcher shortMatcher = shortPattern.matcher(url);
        if (shortMatcher.find()) {
            return shortMatcher.group(1);
        }

        // youtube.com/watch?v= 형식
        Pattern watchPattern = Pattern.compile("watch\\?v=([A-Za-z0-9_-]+)");
        Matcher watchMatcher = watchPattern.matcher(url);
        if (watchMatcher.find()) {
            return watchMatcher.group(1);
        }

        // youtube.com/embed/ 형식
        Pattern embedPattern = Pattern.compile("embed/([A-Za-z0-9_-]+)");
        Matcher embedMatcher = embedPattern.matcher(url);
        if (embedMatcher.find()) {
            return embedMatcher.group(1);
        }

        // URL에서 추가 파라미터 제거
        String cleanUrl = url.split("[?&]")[0];
        String[] segments = cleanUrl.split("/");
        return segments[segments.length - 1];
    }
}
