package com.vitallog.user.repository;

import com.vitallog.user.entity.User;
import org.hibernate.query.SelectionQuery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUserId(String userId);


    boolean existsByUserId(String userId);
    boolean existsByEmail(String email);

    Optional<User> findByUserNameAndEmail(String userName, String email);
    Optional<User> findByUserIdAndEmail(String userId, String email);

}
