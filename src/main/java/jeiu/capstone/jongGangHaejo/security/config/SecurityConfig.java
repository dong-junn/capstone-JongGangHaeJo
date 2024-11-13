package jeiu.capstone.jongGangHaejo.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jeiu.capstone.jongGangHaejo.domain.user.User;
import jeiu.capstone.jongGangHaejo.repository.UserRepository;
import jeiu.capstone.jongGangHaejo.security.handler.Http401Handler;
import jeiu.capstone.jongGangHaejo.security.handler.Http403Handler;
import jeiu.capstone.jongGangHaejo.security.jwt.JwtAuthenticationFilter;
import jeiu.capstone.jongGangHaejo.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        JwtAuthenticationFilter jwtAuthFilter = new JwtAuthenticationFilter(jwtUtil, userDetailsService());

        http
                //권한이 없어도 아래 사항에 대하여서는 허용한다
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/error", "/favicon.ico").permitAll() //기본적으로 필요
                        .requestMatchers("sign-up").permitAll() //회원가입
                        .requestMatchers("sign-in").permitAll() //로그인
                        .requestMatchers("/user").hasRole("USER")
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )

                .formLogin(AbstractHttpConfigurer::disable)

                //로그아웃 설정
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                        .permitAll()
                )

                //REST API 관련 설정
                .csrf(AbstractHttpConfigurer::disable)

                //JWT관련 설정
                .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                //401, 403 custom handler 등록
                .exceptionHandling(e -> {
                    e.accessDeniedHandler(new Http403Handler(new ObjectMapper()));
                    e.authenticationEntryPoint(new Http401Handler(new ObjectMapper()));
                });

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return userId -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException(userId + "을 찾을 수 없습니다."));

            return new UserConfig(user);
        };
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, userDetailsService());
    }
}
