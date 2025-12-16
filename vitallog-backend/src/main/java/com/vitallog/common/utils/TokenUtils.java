package com.vitallog.common.utils;

import com.vitallog.user.entity.User;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class TokenUtils {

    private static String jwtSecretKey;
    private static Long tokenValidateTime;

    @Value("${jwt.key}")
    public void setJwtSecretKey(String jwtSecretKey) {
        TokenUtils.jwtSecretKey = jwtSecretKey;
    }

    @Value("${jwt.time}")
    public void setTokenValidateTime(Long tokenValidateTime) {
        TokenUtils.tokenValidateTime = tokenValidateTime;
    }

    public static String splitHeader(String header) {

        if (!header.equals("")) {
            String[] split = header.split(" ");

            if (split.length > 1) {
                return split[1];
            }
        }

        return null;
    }

    public static boolean isValidToken(String token) {

        try {
            Claims claims = getClaimsFromToken(token);
            return true;

        } catch (ExpiredJwtException e) {
            e.printStackTrace();
            return false;

        } catch (JwtException e) {
            e.printStackTrace();
            return false;

        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                   .setSigningKey(Base64.getDecoder().decode(jwtSecretKey))
                   .parseClaimsJws(token)
                   .getBody();
    }

    public static String generateJwtToken(User user) {
        Date expireTime = new Date(System.currentTimeMillis() + tokenValidateTime);

        JwtBuilder builder = Jwts.builder()
                .setHeader(createHeader())
                .setClaims(createClaims(user))
                . setSubject("vitallog token : " + user.getUserNo())
                .signWith(SignatureAlgorithm.HS256, createSignature())
                .setExpiration(expireTime);

        return builder.compact();
    }



    private static Map<String, Object> createHeader() {
        Map<String, Object> header = new HashMap<>();

        header.put("type", "jwt");
        header.put("alg", "HS256");
        header.put("date", System.currentTimeMillis());

        return header;
    }

    private static Map<String, Object> createClaims(User user) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("userName", user.getUserName());
        claims.put("Role", user.getRole());
        claims.put("userNo", user.getUserNo());
        claims.put("email", user.getEmail());

        return claims;
    }

    private static Key createSignature() {
        byte[] secretBytes = Base64.getDecoder().decode(jwtSecretKey);
        return new SecretKeySpec(secretBytes, SignatureAlgorithm.HS256.getJcaName());
    }
}

