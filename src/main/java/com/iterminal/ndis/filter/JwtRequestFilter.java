package com.iterminal.ndis.filter;

import com.iterminal.exception.CustomException;
import com.iterminal.exception.AuthenticationFailedException;
import com.iterminal.ndis.service.impl.CustomUserDetailsService;
import com.iterminal.ndis.util.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private JwtTokenUtil jwtUtil;
    private CustomUserDetailsService userDetailsService;

    @Autowired
    public JwtRequestFilter(
            JwtTokenUtil jwtUtil,
            CustomUserDetailsService userDetailsService
    ) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    // Examine the incoming request for JWT in the header
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        String userName = null;
        final String META_DATA = "/meta-data";

        try {

            String jwtToken;
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwtToken = authorizationHeader.substring(7);

            } else {
                jwtToken = authorizationHeader;
            }

            if (jwtToken != null) {

                try {
                    userName = jwtUtil.getUsernameFromToken(jwtToken);
                } catch (SignatureException e) {
                    throw new CustomException(CustomException.INVALID_JWT_SIGNATURE, "Invalid JWT signature.");
                } catch (MalformedJwtException e) {
                    throw new CustomException(CustomException.INVALID_JWT_TOKEN, "Invalid JWT token.");
                } catch (ExpiredJwtException e) {
                    throw new CustomException(CustomException.EXPIRED_JWT_EXCEPTION, "JWT token is expired.");
                } catch (UnsupportedJwtException e) {
                    throw new CustomException(CustomException.UNSUPPORTED_JWT_EXCEPTION, "JWT token is unsupported.");
                } catch (IllegalArgumentException e) {
                    throw new CustomException(CustomException.INVALID_JWT_TOKEN, "JWT claims string is empty.");
                }
            }

            if (userName == null) {
                throw new CustomException(CustomException.INVALID_JWT_TOKEN, "Invalid JWT token.");
            }
            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = null;
                try {
                    userDetails = userDetailsService.loadUserByUsername(userName);
                } catch (Exception e) {
                    throw new AuthenticationFailedException();
                }

                if (!request.getRequestURI().startsWith(META_DATA)) {
                    userDetailsService.authorization(userName, request.getRequestURI());
                }

                try {
                    if (jwtUtil.validateToken(jwtToken, userDetails)) {
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, userName, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                    } else {
                        throw new AuthenticationFailedException();
                    }
                } catch (Exception e) {
                    throw new AuthenticationFailedException();
                }

            }

        } catch (CustomException ex) {
            response.addHeader("errorCode", ex.getErrorCode() + "");
            response.addHeader("errorMessage", ex.getMessage() + "");
        } catch (Exception ex) {
            response.addHeader("errorCode", HttpServletResponse.SC_UNAUTHORIZED + "");
            response.addHeader("errorMessage", ex.getMessage() + "");

        }

        filterChain.doFilter(request, response);

    }
}
