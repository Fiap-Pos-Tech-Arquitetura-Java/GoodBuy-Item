package br.com.fiap.postech.goodbuy.item.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Value("${REMOTE_BASE_URI:http://localhost:8080/goodbuy/user/findByLogin/{userName}}")
	String baseURI;

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		RestClient restClient = RestClient.create();
		User user = restClient.get()
				.uri(baseURI, userName)
				.retrieve()
				.body(User.class);

		if (user != null) {
			return new UserDetailsImpl(user);
		} else {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
	}
}