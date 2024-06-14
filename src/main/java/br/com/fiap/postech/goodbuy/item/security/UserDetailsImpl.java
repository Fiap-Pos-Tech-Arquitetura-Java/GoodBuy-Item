package br.com.fiap.postech.goodbuy.item.security;

import br.com.fiap.postech.goodbuy.item.security.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class UserDetailsImpl implements UserDetails {

	@Serial
	private static final long serialVersionUID = 0;

	private String username;
	private String password;
	private UserRole role;

	public UserDetailsImpl() {
		super();
	}

	public UserDetailsImpl(User user) {
		this();
		this.username = user.getLogin();
		this.password = user.getPassword();
		this.role = user.getRole();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
        if (Objects.requireNonNull(role) == UserRole.ADMIN) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        }
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}
}