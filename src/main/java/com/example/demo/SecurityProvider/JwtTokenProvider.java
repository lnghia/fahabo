package com.example.demo.SecurityProvider;

import com.example.demo.User.Entity.CustomUserDetails;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {
    @Value("${JWT_SECRET}")
    private String JWT_SECRET;

    @Value("${ACCESS_TOKEN_EXPIRATION}")
    private long ACCESS_JWT_EXPIRATION;

    @Value("${REFRESH_TOKEN_EXPIRATION}")
    private long REFRESH_JWT_EXPIRATION;

    public String generateAccessToken(CustomUserDetails userDetails) {
//        log.debug(Long.toString(ACCESS_JWT_EXPIRATION));
//        log.debug(Long.toString(REFRESH_JWT_EXPIRATION));

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + ACCESS_JWT_EXPIRATION);

        return Jwts.builder()
                .setSubject(Long.toString(userDetails.getUser().getId()))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
    }

    public String generateTokenValidWithin(CustomUserDetails userDetails, int min){
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + min * 60 * 1000);

        return Jwts.builder()
                .setSubject(Long.toString(userDetails.getUser().getId()))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
    }

    public String generateRefreshToken(CustomUserDetails userDetails){
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + REFRESH_JWT_EXPIRATION);

        return Jwts.builder()
                .setSubject(Long.toString(userDetails.getUser().getId()))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
    }

    public int getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();

        return Integer.parseInt(claims.getSubject());
    }

    public boolean validateToken(String token){
        try{
            Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token);

            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }

        return false;
    }
}
