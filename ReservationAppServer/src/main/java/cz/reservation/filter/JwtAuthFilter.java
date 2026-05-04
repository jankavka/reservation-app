package cz.reservation.filter;

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

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;

    private final JwtService jwtService;

    private final RefreshTokenService refreshTokenService;


    @Autowired
    @Lazy
    public JwtAuthFilter(
            UserDetailsService userDetailsService,
            JwtService jwtService,
            RefreshTokenService refreshTokenService) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.refreshTokenService = refreshTokenService;
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
        //boolean refreshTokenReached = false;


        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                userName = jwtService.extractUserName(token);
            } catch (ExpiredJwtException e) {
                log.warn("JWT token expired for request: {}", request.getRequestURI());
                handleExpiredAccessTokenException(e, response);
                //refreshTokenReached = true;

            } catch (JwtException e) {
                log.warn("Invalid JWT token: {}", e.getMessage());
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


    void handleExpiredAccessTokenException(ExpiredJwtException e, HttpServletResponse response) throws IOException {
        var claims = e.getClaims();
        var userName = claims.getSubject();

        var refreshToken = refreshTokenService.getRefreshTokenByUsername(userName);
        refreshTokenService.markedAsRevoked(refreshToken);

        if (refreshTokenService.isRefreshTokenNoExpired(refreshToken.getToken())) {
            refreshTokenService.setNewTokenPair(refreshToken, response, userName);
        } else {
            log.error("Refresh token is expired. Please Login.");
            refreshTokenService.refreshTokenExpiredResponse
                    (response, "Refresh token is expired. Please Login.");
        }
    }
}
