package mil.gdl.nis.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import mil.gdl.nis.exception.BizException;
import mil.gdl.nis.user.vo.UserVo;

public class CustomAuthProvider implements AuthenticationProvider {

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException,BizException {

		String username = authentication.getName();
		String password = (String) authentication.getCredentials();

		UserVo user = (UserVo) userDetailsService.loadUserByUsername(username);

		// password 일치하지 않으면!
		if (!passwordEncoder.matches(password, user.getUserPw())) {
			throw new BadCredentialsException("BadCredentialsException");
		}

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null,
				user.getAuthorities());

		return authenticationToken;
	}

	// 토큰 타입과 일치할 때 인증
	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}
}