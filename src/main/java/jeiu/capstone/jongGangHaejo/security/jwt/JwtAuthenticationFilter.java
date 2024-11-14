package jeiu.capstone.jongGangHaejo.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jeiu.capstone.jongGangHaejo.security.config.UserConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 헤더에서 JWT 토큰 추출
        getTokenFromRequest(request).ifPresent(token -> {
            if (jwtUtil.validateToken(token)) {
                String userId = jwtUtil.getUserIdFromToken(token);
                log.debug("User ID from token: {}", userId);

                try {
                    // UserDetailsService를 통해 UserConfig 객체 생성
                    UserConfig userConfig = (UserConfig) userDetailsService.loadUserByUsername(userId);

                    // Authentication 객체 생성 및 SecurityContext에 설정
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userConfig,               // principal
                                    token,                   // credentials (토큰을 credentials로 사용)
                                    userConfig.getAuthorities()  // authorities
                            );

                    // 추가 세부 정보 설정
                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("SecurityContextHolder {}", SecurityContextHolder.getContext());
                } catch (Exception e) {
                    log.error("인증 세팅이 실패했습니다", e);
                }
            }
        });

        filterChain.doFilter(request, response);
    }

    private Optional<String> getTokenFromRequest(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter(StringUtils::hasText)
                .filter(header -> header.startsWith("Bearer "))
                .map(header -> header.substring(7));
    }
}
