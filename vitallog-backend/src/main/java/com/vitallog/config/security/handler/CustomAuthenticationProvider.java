package com.vitallog.config.security.handler;

import com.vitallog.config.security.model.DetailsUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 1. 입력받은 ID, PW
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        // 2. DB에서 유저 정보 조회 (UserDetailsServiceImpl 실행됨)
        DetailsUser detailsUser = (DetailsUser) userDetailsService.loadUserByUsername(username);

        // 3. 비밀번호 매칭 확인
        if (!passwordEncoder.matches(password, detailsUser.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        // 4. 인증 토큰 생성해서 반환
        return new UsernamePasswordAuthenticationToken(detailsUser, password, detailsUser.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
