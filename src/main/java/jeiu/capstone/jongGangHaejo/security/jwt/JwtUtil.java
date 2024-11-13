package jeiu.capstone.jongGangHaejo.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Long tokenValidityInMilliseconds;

    private SecretKey key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // 토큰 생성 (로그인 성공 시 사용)
    public String generateToken(String username, String authorities) {
        return Jwts.builder()
                .subject(username)
                .claim("auth", authorities)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + tokenValidityInMilliseconds))
                .signWith(key)  // 알고리즘 명시하지 않음 (기본값 사용)
                .compact();
    }

    // 토큰에서 username 추출
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            return !extractClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // Claims 추출
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
