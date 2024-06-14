package br.com.fiap.postech.goodbuy.item.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtServiceHelper extends JwtService {

    private String createToken(Map<String, Object> claims, User user) {
        return Jwts.builder().setClaims(claims)
                .setSubject(user.getLogin())
                .claim("role", user.getRole())
                .claim("idUser", user.getId())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, user);
    }
}
