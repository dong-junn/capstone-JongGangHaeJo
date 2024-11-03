package jeiu.capstone.jongGangHaejo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.CustomAutowireConfigurer;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //권한이 없어도 아래 사항에 대하여서는 허용한다
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/error", "/favicon.ico").permitAll() //기본적으로 필요
                        .requestMatchers(HttpMethod.POST, "/member/sign-in").permitAll() //회원가입
                        .requestMatchers(HttpMethod.POST, "/member/sign-up").permitAll() //로그인
                        .anyRequest().authenticated()
                )

                //로그아웃 설정
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                        .permitAll()
                )

                //REST API 관련 설정
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
