package cz.reservation.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.reservation.service.serviceinterface.JwtService;
import cz.reservation.service.serviceinterface.RefreshTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;

    private final JwtService jwtService;

    private static final ObjectMapper objectMapper = new ObjectMapper();


    @Lazy
    public JwtAuthFilter(
            UserDetailsService userDetailsService,
            JwtService jwtService) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @Nonnull
            HttpServletResponse response,
            @Nonnull
            FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String userName = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                userName = jwtService.extractUserName(token);
            } catch (ExpiredJwtException e) {
                log.warn("JWT token expired for request: {}", request.getRequestURI());
            } catch (JwtException e) {
                log.warn("Invalid JWT token: {}", e.getMessage());
                if(!request.getRequestURI().equals("/auth/addNewUser")) {
                    ContentCachingResponseWrapper myResponse = new ContentCachingResponseWrapper(response);
                    myResponse.resetBuffer();
                    myResponse
                            .getWriter()
                            .write(objectMapper.writeValueAsString(Map.of("message", "Unexpected mistake")));
                    myResponse.copyBodyToResponse();
                }

            } catch (AuthorizationDeniedException e) {
                log.error("{}, {}", e.getMessage(), e.getClass());
            }
        }

        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
            if (Boolean.TRUE.equals(jwtService.validateToken(token, userDetails))) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }

}
