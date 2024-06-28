package br.com.fiap.postech.goodbuy.item.helper;

import br.com.fiap.postech.goodbuy.security.JwtService;
import br.com.fiap.postech.goodbuy.security.User;
import br.com.fiap.postech.goodbuy.security.UserDetailsImpl;
import br.com.fiap.postech.goodbuy.security.enums.UserRole;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

public class UserHelper {
    public static User getUser(boolean geraId, UserRole userRole) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        var user = new User(
                "anderson.wagner",
                encoder.encode("123456"),
                userRole
        );
        if (geraId) {
            user.setId(UUID.randomUUID());
        }
        return user;
    }

    public static String getToken(User user) {
        return "Bearer " + new JwtService().generateToken(user);
    }

    public static UserDetails getUserDetails(User user) {
        return new UserDetailsImpl(user);
    }
}
