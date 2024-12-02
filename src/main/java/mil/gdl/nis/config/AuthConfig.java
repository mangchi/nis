package mil.gdl.nis.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;
import mil.gdl.nis.handler.LoginFailureHandler;
import mil.gdl.nis.handler.LoginSuccessHandler;
import mil.gdl.nis.handler.TaskLogoutHandler;
import mil.gdl.nis.log.service.LogService;


@EnableWebSecurity
@RequiredArgsConstructor
public class AuthConfig extends WebSecurityConfigurerAdapter {
	
	private final UserDetailsService userDetailsService;
	private final LoginSuccessHandler loginSuccessHandler;
	private final LoginFailureHandler loginFailureHandler;
	private final LogService logService;
	
	//private final SessionRegistry sessionRegistry;
	
	@Value("${server.servlet.session.cookie.name}")
    private String sessionId;
	@Value("${rememberMe.parameter}")
    private String rememberMeParameter;
	@Value("${rememberMe.tokenValiditySeconds}")
    private String tokenValiditySeconds;
	@Value("${rememberMe.alwaysRemember}")
    private String alwaysRemember;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// 넓은 범위의 URL을 아래에 배치한다.
		http.csrf().disable()
		    .authorizeRequests()
		        .antMatchers("/admin").hasRole("ADMIN")
		        .antMatchers("/","/js/**","/css/**","/font/**","/img/**","/register","/chgPwdLogin","/unitInfo").permitAll() 
				.anyRequest().authenticated()
				.and()
				.formLogin()
				.loginPage("/login")
				.defaultSuccessUrl("/")
				.successHandler(loginSuccessHandler)
				.failureHandler(loginFailureHandler)
				//.failureUrl("/login?error")
				.permitAll()
				.and()
				.logout()
				.deleteCookies(sessionId)
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
	    		.addLogoutHandler(new TaskLogoutHandler(logService)).permitAll().logoutSuccessUrl("/")
		    .and()
		    .sessionManagement()
		    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)		
		    .sessionFixation(sf ->sf.migrateSession())
		    .invalidSessionUrl("/login?invalid=true")
		    .maximumSessions(1)
		    .maxSessionsPreventsLogin(true)   //동시 세션 차단, false:기존 세션 만료
		    .expiredUrl("/login?expired=true")
		    .sessionRegistry(sessionRegistry());
		
		
	    http 
		    .rememberMe()
		    .rememberMeParameter(rememberMeParameter)
		    .tokenValiditySeconds(Integer.parseInt(tokenValiditySeconds))  //default:14일
		    .alwaysRemember(Boolean.getBoolean(alwaysRemember))    // rememberme 기능이 활성화 되어 있지 않아도 항상 실행, default:false
		    .userDetailsService(userDetailsService);


	}
	


	
    @Bean 
    public AuthenticationProvider authenticationProvider() { 
    	return new CustomAuthProvider(); 
    }
	 
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		 auth.userDetailsService(userDetailsService);
		 //auth.authenticationProvider(authenticationProvider());
		 //auth.eraseCredentials(false);

	}

	// passwordEncoder() 추가
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
	@Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:8080");
        configuration.setAllowedMethods(Arrays.asList("GET","POST", "OPTIONS", "PUT","DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
	

	
	@Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }


	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	public static ServletListenerRegistrationBean httpSessionEventPublisher() {
	    return new ServletListenerRegistrationBean(new HttpSessionEventPublisher());
	}
	
	@Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}