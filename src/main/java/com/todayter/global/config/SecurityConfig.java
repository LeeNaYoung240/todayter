package com.todayter.global.config;

import com.todayter.domain.user.repository.UserRepository;
import com.todayter.global.jwt.JwtProvider;
import com.todayter.global.oauth2.handler.OAuth2FailureHandler;
import com.todayter.global.oauth2.handler.OAuth2SuccessHandler;
import com.todayter.global.oauth2.service.CustomOAuth2UserService;
import com.todayter.global.security.JwtAuthenticationFilter;
import com.todayter.global.security.JwtAuthorizationFilter;
import com.todayter.global.security.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Bean
    public CustomOAuth2UserService customOAuth2UserService(PasswordEncoder passwordEncoder) {
        return new CustomOAuth2UserService(userRepository, passwordEncoder);
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(jwtProvider, userRepository);
    }

    @Bean
    public OAuth2FailureHandler oAuth2FailureHandler() {
        return new OAuth2FailureHandler();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtProvider, userRepository);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtProvider, userDetailsService);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .exceptionHandling(ex -> ex.authenticationEntryPoint((req, res, e) -> {
                    String origin = req.getHeader("Origin");
                    if ("https://todayter.store".equals(origin) || "https://www.todayter.store".equals(origin)) {
                        res.setHeader("Access-Control-Allow-Origin", origin);
                        res.setHeader("Vary", "Origin");
                        res.setHeader("Access-Control-Allow-Credentials", "true");
                    }
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.setCharacterEncoding("UTF-8");
                    res.setContentType("application/json;charset=UTF-8");
                    res.getWriter().write("{\"status\":401,\"message\":\"인증이 필요합니다.\"}");
                }))


                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/",
                                "/login", "/login/**",
                                "/oauth2/**",
                                "/login/oauth2/code/**",
                                "/oauth2/authorization/**",
                                "/oauth2/redirect",
                                "/api/cheers/count/**",
                                "/api/follows/count",
                                "/api/users/signup",
                                "/api/boards/**",
                                "/api/users/user-cnt",
                                "/api/users/login",
                                "/api/users/check-existence",
                                "/api/users/send-Email",
                                "/api/users/signup",
                                "/api/users/verify/**",
                                "/api/users/check-nickname",
                                "/api/users/check-username",
                                "/login/oauth2/code/**",
                                "/oauth2/authorization/**",
                                "/api/follows/**"
                        ).permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .requestMatchers("/api/users/*/block").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService(bCryptPasswordEncoder()))
                        )
                        .successHandler(oAuth2SuccessHandler())
                        .failureHandler(oAuth2FailureHandler()));


        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);

        return http.build();
    }

    // Cors 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();

        cfg.setAllowedOrigins(List.of(
                "https://todayter.store",
                "https://www.todayter.store"
        ));
        cfg.setAllowedOriginPatterns(List.of(
                "http://localhost:*"
        ));

        cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setExposedHeaders(List.of(
                "Authorization","ETag","Location","x-amz-request-id","x-amz-version-id"
        ));
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(Duration.ofHours(1));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

}
