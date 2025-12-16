package com.vitallog.user.service;

import com.vitallog.common.UserRole;
import com.vitallog.user.dto.req.*;
import com.vitallog.user.dto.res.LoginResponse;
import com.vitallog.user.dto.res.UserResponse;
import com.vitallog.user.entity.User;
import com.vitallog.user.entity.UserDetail;
import com.vitallog.user.repository.UserDetailRepository;
import com.vitallog.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserDetailRepository userDetailRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final org.springframework.mail.javamail.JavaMailSender mailSender;

    @Transactional
    public void findPassword(FindPwRequest request) {
        // 1. 유저 조회
        User user = userRepository.findByUserIdAndEmail(request.getUserId(), request.getEmail())
                .orElseThrow(() -> new RuntimeException("입력하신 정보와 일치하는 회원이 없습니다."));
        // 2. 임시 비밀번호 생성 및 저장
        String tempPassword = "temp" + (int) (Math.random() * 10000); // 간단한 랜덤 예시
        user.setPwd(passwordEncoder.encode(tempPassword));
        // 3. 이메일 발송 코드 추가
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail()); // 수신자 이메일
        message.setSubject("[VITALLOG] 임시 비밀번호 안내"); // 제목
        message.setText("회원님의 임시 비밀번호는 " + tempPassword + " 입니다."); // 내용
        mailSender.send(message); // 전송!

        System.out.println("메일 발송 완료: " + user.getEmail());
    }

    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        // 1. ID로 회원 조회
        User user = userRepository.findByUserId(loginRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(loginRequest.getPwd(), user.getPwd())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 3. 토큰 생성
        String token = com.vitallog.common.utils.TokenUtils.generateJwtToken(user);

        // 4. 응답 생성
        return LoginResponse.builder()
                .token(token)
                .user(UserResponse.from(user))
                .build();
    }

    public UserResponse getUser(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));
        return UserResponse.from(user);
    }

    @Transactional
    public User signup(SignupRequest signupRequest) {

        // 1. User 테이블 저장 (기본 정보)
        User user = User.builder()
                .userId(signupRequest.getUserId())
                .pwd(passwordEncoder.encode(signupRequest.getPwd())) // 비밀번호 암호화 필수
                .userName(signupRequest.getUserName())
                .email(signupRequest.getEmail())
                .role(UserRole.USER)
                .build();

        User savedUser = userRepository.save(user); // 여기서 userNo가 생성됨(UUID->String)

        // 2. UserDetail 테이블 저장 (상세 정보)
        // SignupRequest에 있는 키, 몸무게 등을 가져와서 저장
        UserDetail userDetail = UserDetail.builder()
                .userNo(savedUser.getUserNo())
                .gender(signupRequest.getGender())
                .age(signupRequest.getAge())
                .height(signupRequest.getHeight())
                .weight(signupRequest.getWeight())
                .isPregnant(signupRequest.isPregnant())
                .build();

        userDetailRepository.save(userDetail);

        return savedUser;
    }

    @Transactional
    public void withdraw(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("회원 정보가 없습니다."));

        // 유저 상세 정보 삭제
        userDetailRepository.deleteByUserNo(user.getUserNo());

        // 유저 삭제
        userRepository.delete(user);
    }

    public boolean checkIdDuplicate(String userId) {
        return userRepository.existsByUserId(userId);
    }

    public boolean checkEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    public String findId(FindIdRequest request) {
        User user = userRepository.findByUserNameAndEmail(request.getUserName(), request.getEmail())
                .orElseThrow(() -> new RuntimeException("일치하는 회원 정보가 없습니다."));
        return user.getUserId();
    }

    @Transactional
    public void changePassword(ChangePwRequest request) {
        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("회원 정보가 없습니다."));

        // 기존 비밀번호 확인
        if (!passwordEncoder.matches(request.getPwd(), user.getPwd())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 새로운 비밀번호 암호화 후 저장
        user.setPwd(passwordEncoder.encode(request.getNewPassword()));
    }

}
