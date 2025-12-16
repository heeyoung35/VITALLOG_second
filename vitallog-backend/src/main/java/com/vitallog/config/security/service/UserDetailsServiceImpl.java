package com.vitallog.config.security.service;

import com.vitallog.config.security.model.DetailsUser;
import com.vitallog.user.entity.User;
import com.vitallog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userid) throws UsernameNotFoundException {
        // 1. DB에서 ID로 유저 조회 (Optional 처리)
        User user = userRepository.findByUserId(userid)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다."));

        // 2. DetailsUser(시큐리티 호환 객체)로 변환해서 반환
        return new DetailsUser(user);
    }
}
