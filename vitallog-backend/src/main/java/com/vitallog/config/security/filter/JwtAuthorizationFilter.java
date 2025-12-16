package com.vitallog.config.security.filter;

import com.vitallog.common.AuthConstants;
import com.vitallog.common.UserRole;
import com.vitallog.common.utils.TokenUtils;
import com.vitallog.config.security.model.DetailsUser;
import com.vitallog.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import tools.jackson.databind.util.JSONPObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {

        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        List<String> roleLessList = Arrays.asList(
                "/api/user/signup",
                "/api/user/login",
                "/api/user/reissue",
                "/api/user/find-id",
                "/api/user/reset-password",
                "/api/user/check-id",
                "/api/user/check-email"
        );

        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String uri = requestURI;

        if (contextPath != null && requestURI.startsWith(contextPath)) {
            uri = requestURI.substring(contextPath.length());
        }

        if (roleLessList.contains(uri)) {
            chain.doFilter(request, response);
            return;
        }

        String header = request.getHeader(AuthConstants.AUTH_HEADER);

        try {
            if (header != null && !header.equalsIgnoreCase("")) {
                String token = TokenUtils.splitHeader(header);

                if (TokenUtils.isValidToken(token)) {

                    Claims claims = TokenUtils.getClaimsFromToken(token);

                    DetailsUser authentication = new DetailsUser();
                    User user = new User();

                    user.setUserName(claims.get("userName").toString());
                    user.setUserNo(claims.get("userNo").toString());
                    user.setEmail(claims.get("email").toString());
                    user.setRole(UserRole.valueOf(claims.get("Role").toString()));
                    authentication.setUser(user);

                    AbstractAuthenticationToken authenticationToken = UsernamePasswordAuthenticationToken
                            .authenticated(authentication, token, authentication.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    chain.doFilter(request, response);
                } else {
                    throw new RemoteException("token이 유효하지 않습니다.");
                }
            } else {
                throw new RemoteException("token이 존재하지 않습니다.");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            PrintWriter printWriter = response.getWriter();
            JSONObject jsonObject = jsonResponseWrapper(e);
            printWriter.print(jsonObject);
            printWriter.flush();
            printWriter.close();
        }
    }

    private JSONObject jsonResponseWrapper(Exception e) {
        String resultMsg = "";

        if (e instanceof ExpiredJwtException) {
            resultMsg = "Token Expired";
        } else if (e instanceof SignatureException) {
            resultMsg = "Token SignatureException";
        } else if (e instanceof JwtException) {
            resultMsg = "Token Parsing JwtException";
        } else {
            resultMsg = "other Token error";
        }

        HashMap<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("status", 401);
        jsonMap.put("message", resultMsg);
        jsonMap.put("reason", e.getMessage());

        return new JSONObject(jsonMap);
    }
}
