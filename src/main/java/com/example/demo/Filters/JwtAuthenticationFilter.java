package com.example.demo.Filters;

import Messages.ResponseMsg;
import com.example.demo.Helpers.Helper;
import com.example.demo.SecurityProvider.JwtTokenProvider;
import com.example.demo.Service.UserService;
import com.example.demo.Service.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserServiceImpl userService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        if (!Helper.getInstance().getUnauthenticatedEndpoints().contains(httpServletRequest.getServletPath())) {
            try {
                String jwt = getJwtFromRequest(httpServletRequest);

                if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {

                    int userId = tokenProvider.getUserIdFromJWT(jwt);

                    UserDetails userDetails = userService.loadUserById(userId);

                    if (userDetails != null) {
                        UsernamePasswordAuthenticationToken
                                authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } else {
                    Map<String, ArrayList<String>> responseBody = new HashMap<>();

                    responseBody.put("errors", new ArrayList<>(List.of(ResponseMsg.Authentication.SignIn.fail.toString())));

                    httpServletResponse.setStatus(UNAUTHORIZED.value());
                    httpServletResponse.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(httpServletResponse.getOutputStream(), responseBody);
                }
            } catch (Exception ex) {
                log.info(ex.getMessage());
                Map<String, ArrayList<String>> responseBody = new HashMap<>();

                responseBody.put("errors", new ArrayList<>(List.of(ex.getMessage())));

                httpServletResponse.setStatus(UNAUTHORIZED.value());
                httpServletResponse.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(httpServletResponse.getOutputStream(), responseBody);
            }

        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);

    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}
