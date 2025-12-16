package com.vitallog.user.service;

import com.vitallog.user.dto.req.*;
import com.vitallog.user.dto.res.LoginResponse;

import com.vitallog.user.entity.User;
import io.milvus.v2.client.MilvusClientV2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

// 1. properties 설정 제거 (이전의 잘못된 exclude 설정 삭제)
@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private com.vitallog.user.repository.UserRepository userRepository;

    @Autowired
    private com.vitallog.user.repository.UserDetailRepository userDetailRepository;

    // 2. [핵심] MilvusClientV2를 MockBean으로 등록
    // 이렇게 하면 ZillizDBConfig의 milvusClient() 메서드가 호출되지 않고,
    // 대신 빈 껍데기(Mock) 객체가 Context에 등록되어 연결 오류가 발생하지 않습니다.
    @MockitoBean
    private MilvusClientV2 milvusClientV2;

    @Test
    @DisplayName("회원가입 후 로그인이 정상적으로 동작하는지 확인")
    void login_success() {
        // ... 기존 테스트 코드 그대로 유지 ...
        // (Given, When, Then 로직 변경 없음)

        // given
        String uniqueId = "testUser_" + System.currentTimeMillis();
        String userId = uniqueId;
        String pwd = "password123!";
        String userName = "테스트유저";
        String email = uniqueId + "@example.com";

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUserId(userId);
        signupRequest.setPwd(pwd);
        signupRequest.setUserName(userName);
        signupRequest.setEmail(email);
        signupRequest.setGender("M");
        signupRequest.setAge(25);
        signupRequest.setHeight(175.0);
        signupRequest.setWeight(70.0);
        signupRequest.setPregnant(false);

        userService.signup(signupRequest);

        // when
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUserId(userId);
        loginRequest.setPwd(pwd);

        LoginResponse loginResponse = userService.login(loginRequest);

        // then
        assertNotNull(loginResponse);
        assertNotNull(loginResponse.getToken());
        assertThat(loginResponse.getUser().getUserId()).isEqualTo(userId);
    }


    @MockitoBean private JavaMailSender mailSender;
    @Test
    @DisplayName("비밀번호 찾기(임시 비밀번호 발급) 및 임시 비밀번호 로그인 확인")
    void findPassword_success() {
        // given
        String uniqueId = "testUserMake_" + System.currentTimeMillis();
        String userId = uniqueId;
        String pwd = "password123!";
        String userName = "테스트유저";
        String email = uniqueId + "@example.com";

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUserId(userId);
        signupRequest.setPwd(pwd);
        signupRequest.setUserName(userName);
        signupRequest.setEmail(email);
        signupRequest.setGender("M");
        signupRequest.setAge(25);
        signupRequest.setHeight(175.0);
        signupRequest.setWeight(70.0);
        signupRequest.setPregnant(false);

        userService.signup(signupRequest);

        // when
        FindPwRequest findPwRequest = new FindPwRequest();
        // Setters are now available in FindPwRequest!
        findPwRequest.setUserId(userId);
        findPwRequest.setEmail(email);

        userService.findPassword(findPwRequest);

        // then
        // then: 기존 비밀번호로 로그인이 안 되는지 확인 (비밀번호가 변경되었으므로)
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUserId(userId);
        loginRequest.setPwd(pwd); // 기존 비밀번호 ("password123!")

        // 변경된 비밀번호를 모르므로(랜덤생성), 기존 비밀번호로 로그인 시 실패해야 정상
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            userService.login(loginRequest);
        });

        System.out.println("=========================================");
        System.out.println("비밀번호 변경 확인 완료 (기존 비밀번호로 로그인 불가)");
        System.out.println("=========================================");
    }

        @Test
        @DisplayName("아이디 찾기 테스트")
        void findId_success() {
            // given
            String uniqueId = "testUser_" + System.currentTimeMillis();
            String userId = uniqueId;
            String pwd = "password123!";
            String userName = "테스트유저";
            String email = uniqueId + "@example.com";

            SignupRequest signupRequest = new SignupRequest();
            signupRequest.setUserId(userId);
            signupRequest.setPwd(pwd);
            signupRequest.setUserName(userName);
            signupRequest.setEmail(email);
            signupRequest.setGender("M");
            signupRequest.setAge(25);
            signupRequest.setHeight(175.0);
            signupRequest.setWeight(70.0);
            signupRequest.setPregnant(false);

            userService.signup(signupRequest);

            // when
            FindIdRequest findIdRequest = new FindIdRequest();
            findIdRequest.setUserName(userName);
            findIdRequest.setEmail(email);

            String foundId = userService.findId(findIdRequest);

            // then
            System.out.println("=========================================");
            System.out.println("아이디 찾기 성공!");
            System.out.println("찾은 아이디: " + foundId);
            System.out.println("=========================================");

            assertThat(foundId).isEqualTo(userId);
        }

        @Test
        @DisplayName("아이디 중복 확인 테스트")
        void checkIdDuplicate_success() {
            // given
            String uniqueId = "dupUser_" + System.currentTimeMillis();
            String userId = uniqueId;
            String pwd = "password123!";
            String userName = "중복체크유저";
            String email = uniqueId + "@example.com";

            SignupRequest signupRequest = new SignupRequest();
            signupRequest.setUserId(userId);
            signupRequest.setPwd(pwd);
            signupRequest.setUserName(userName);
            signupRequest.setEmail(email);
            signupRequest.setGender("M");
            signupRequest.setAge(30);
            signupRequest.setHeight(180.0);
            signupRequest.setWeight(75.0);
            signupRequest.setPregnant(false);

            userService.signup(signupRequest);

            // when & then
            // 1. 방금 가입한 아이디로 중복 체크 -> true (중복됨) 나와야 함
            boolean isDuplicate = userService.checkIdDuplicate(userId);
            assertThat(isDuplicate).isTrue();

            // 2. 가입하지 않은 새로운 아이디로 중복 체크 -> false (사용 가능) 나와야 함
            boolean isNotDuplicate = userService.checkIdDuplicate(userId + "_new");
            assertThat(isNotDuplicate).isFalse();

            System.out.println("=========================================");
            System.out.println("아이디 중복 확인 결과: " + (isDuplicate ? "중복됨(정상)" : "오류"));
            System.out.println("=========================================");
        }

        @Test
        @DisplayName("이메일 중복 확인 테스트")
        void checkEmailDuplicate_success() {
            // given
            String uniqueId = "dupEmailUser_" + System.currentTimeMillis();
            String userId = uniqueId;
            String pwd = "password123!";
            String userName = "이메일중복유저";
            String email = uniqueId + "@example.com";

            SignupRequest signupRequest = new SignupRequest();
            signupRequest.setUserId(userId);
            signupRequest.setPwd(pwd);
            signupRequest.setUserName(userName);
            signupRequest.setEmail(email);
            signupRequest.setGender("F");
            signupRequest.setAge(28);
            signupRequest.setHeight(165.0);
            signupRequest.setWeight(55.0);
            signupRequest.setPregnant(false);

            userService.signup(signupRequest);

            // when & then
            // 1. 방금 가입한 이메일로 중복 체크 -> true (중복됨)
            boolean isDuplicate = userService.checkEmailDuplicate(email);
            assertThat(isDuplicate).isTrue();

            // 2. 새로운 이메일 -> false (사용 가능)
            boolean isNotDuplicate = userService.checkEmailDuplicate("new_" + email);
            assertThat(isNotDuplicate).isFalse();

            System.out.println("=========================================");
            System.out.println("이메일 중복 확인 결과: " + (isDuplicate ? "중복됨(정상)" : "오류"));
            System.out.println("=========================================");
        }

        @Test
        @DisplayName("비밀번호 변경 테스트 (기존 비밀번호 확인 포함)")
        void changePassword_success() {
            // given
            String uniqueId = "pwChangeUser_" + System.currentTimeMillis();
            String userId = uniqueId;
            String oldPwd = "password123!";
            String newPwd = "newPassword!@#";
            String userName = "비번변경유저";
            String email = uniqueId + "@example.com";

            SignupRequest signupRequest = new SignupRequest();
            signupRequest.setUserId(userId);
            signupRequest.setPwd(oldPwd);
            signupRequest.setUserName(userName);
            signupRequest.setEmail(email);
            signupRequest.setGender("M");
            signupRequest.setAge(30);
            signupRequest.setHeight(180.0);
            signupRequest.setWeight(75.0);
            signupRequest.setPregnant(false);

            userService.signup(signupRequest);

            // check: 기존 비밀번호로 로그인 성공 확인
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setUserId(userId);
            loginRequest.setPwd(oldPwd);
            userService.login(loginRequest);

            // when: 비밀번호 변경 요청
            ChangePwRequest changePwRequest = new ChangePwRequest();
            changePwRequest.setUserId(userId);
            changePwRequest.setPwd(oldPwd); // 올바른 기존 비밀번호
            changePwRequest.setNewPassword(newPwd);

            userService.changePassword(changePwRequest);

            // then
            // 1. 기존 비밀번호로 로그인 시도 -> 실패해야 함
            LoginRequest oldLoginRequest = new LoginRequest();
            oldLoginRequest.setUserId(userId);
            oldLoginRequest.setPwd(oldPwd);

            org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
                userService.login(oldLoginRequest);
            });

            // 2. 새로운 비밀번호로 로그인 시도 -> 성공해야 함
            LoginRequest newLoginRequest = new LoginRequest();
            newLoginRequest.setUserId(userId);
            newLoginRequest.setPwd(newPwd);

            LoginResponse response = userService.login(newLoginRequest);

            System.out.println("=========================================");
            System.out.println("비밀번호 변경 및 새 비밀번호 로그인 성공!");
            System.out.println("=========================================");

            assertThat(response.getToken()).isNotNull();
        }

        @Test
        @DisplayName("회원 탈퇴 테스트 (계정 및 상세정보 삭제 확인)")
        void withdraw_success() {
            // given
            String uniqueId = "withdrawUser_" + System.currentTimeMillis();
            String userId = uniqueId;
            String pwd = "password123!";
            String userName = "탈퇴유저";
            String email = uniqueId + "@example.com";

            SignupRequest signupRequest = new SignupRequest();
            signupRequest.setUserId(userId);
            signupRequest.setPwd(pwd);
            signupRequest.setUserName(userName);
            signupRequest.setEmail(email);
            signupRequest.setGender("F");
            signupRequest.setAge(22);
            signupRequest.setHeight(160.0);
            signupRequest.setWeight(50.0);
            signupRequest.setPregnant(false);

            User savedUser = userService.signup(signupRequest);
            String userNo = savedUser.getUserNo(); // 삭제 확인용 userNo

            // when: 회원 탈퇴
            userService.withdraw(userId);

            // then
            // 1. User 테이블에서 삭제되었는지 확인
            assertThat(userRepository.findByUserId(userId)).isEmpty();

            // 2. UserDetail 테이블에서 삭제되었는지 확인
            assertThat(userDetailRepository.findByUserNo(userNo)).isEmpty();

            System.out.println("=========================================");
            System.out.println("회원 탈퇴 성공 (User, UserDetail 삭제 확인)");
            System.out.println("=========================================");
        }
}

