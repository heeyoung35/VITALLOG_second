package com.vitallog.config.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

@Component
public class CustomAuthFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        JSONObject jsonObject;
        String failMsg;

        if (exception instanceof BadCredentialsException || exception instanceof InternalAuthenticationServiceException) {

            failMsg = "아이디 또는 비밀번호가 일치하지 않습니다.";

        } else if (exception instanceof DisabledException) {

            failMsg = "비활성화(탈퇴)된 계정입니다. 관리자에게 문의하세요.";

        } else {

            failMsg = "로그인 요청을 처리할 수 없습니다. 관리자에게 문의하세요.";
        }

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("failType", failMsg);

        jsonObject = new JSONObject(resultMap);

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println(jsonObject);
        out.flush();
        out.close();

    }
}
