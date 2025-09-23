package com.todayter.global.security;

import com.todayter.global.exception.CustomException;
import com.todayter.global.jwt.JwtProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;

    public JwtAuthorizationFilter(JwtProvider jwtProvider, UserDetailsService userDetailsService) {
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String tokenValue = jwtProvider.getAccessTokenFromHeader(request);
        //System.out.println("[JWT FILTER] token value = " + tokenValue);

        // 토큰이 있으면 → 인증 처리
        if (StringUtils.hasText(tokenValue)) {
            try {
                Claims info = jwtProvider.getClaimsFromToken(tokenValue);
                setAuthentication(info.getSubject());
            } catch (CustomException e) {
                addCorsHeadersForError(request, response);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"status\":401,\"message\":\"토큰이 만료되었습니다.\"}");
                return;
            } catch (Exception e) {
                filterChain.doFilter(request, response);
                return;
            }
        }
        // 토큰이 없으면, 혹은 permitAll 경로면 → 그냥 다음 필터로
        filterChain.doFilter(request, response);
    }


    // 현재 인증된 사용자의 정보 저장
    public void setAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private void addCorsHeadersForError(HttpServletRequest req, HttpServletResponse res) {
        String origin = req.getHeader("Origin");
        if ("https://todayter.store".equals(origin) || "https://www.todayter.store".equals(origin)
                || (origin != null && origin.startsWith("http://localhost:"))) { // 로컬 디버그용
            res.setHeader("Access-Control-Allow-Origin", origin);
            res.setHeader("Vary", "Origin");
            res.setHeader("Access-Control-Allow-Credentials", "true");
            // 필요한 경우 노출 헤더들도 추가 가능
            // res.setHeader("Access-Control-Expose-Headers", "Authorization,ETag,Location,x-amz-request-id,x-amz-version-id");
        }
    }

}