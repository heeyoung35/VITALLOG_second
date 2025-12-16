package com.vitallog.config.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vitallog.common.AuthConstants;
import com.vitallog.common.utils.TokenUtils;
import com.vitallog.config.security.model.DetailsUser;
import com.vitallog.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CustomAuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws SecurityException, IOException {

        User user = ((DetailsUser) authentication.getPrincipal()).getUser();

        String token = TokenUtils.generateJwtToken(user);


        response.addHeader(AuthConstants.AUTH_HEADER, AuthConstants.TOKEN_TYPE + " " + token);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("userInfo", user);
        responseMap.put("message", "로그인 성공입니다.");


        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");


        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        String jsonString = objectMapper.writeValueAsString(responseMap);

        PrintWriter printWriter = response.getWriter();
        printWriter.print(jsonString);
        printWriter.flush();
        printWriter.close();
    }
}
