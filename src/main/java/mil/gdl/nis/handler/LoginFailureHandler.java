package mil.gdl.nis.handler;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mil.gdl.nis.cmmn.PwFailCacheData;
import mil.gdl.nis.cmmn.service.CacheService;


@Slf4j
@RequiredArgsConstructor
@Service
public class LoginFailureHandler implements AuthenticationFailureHandler {

	private final MessageSourceAccessor messageSource;
	private final CacheService cacheService;
	
	@Value("${user.pwFaileCnt}")
    private int pwFailCnt;
	@Value("${user.pwFailKey}")
    private String pwFailKey;
	@Value("${user.pwFaileLockMin}")
    private String pwFaileLockMin;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		Enumeration<String> params = request.getParameterNames();
		while(params.hasMoreElements()) {
			log.debug(params.nextElement());
		}

		log.error("exception:{}", exception.getMessage());
		String userId = request.getParameter("username");

		if (exception instanceof AuthenticationServiceException) {
			request.setAttribute("loginFailMsg", exception.getMessage());

		} else if (exception instanceof BadCredentialsException) {
			cacheService.setPwdFailCacheData(pwFailKey,userId,false);
			Map<String, PwFailCacheData> pwdFailCacheData = cacheService.getPwdFailCacheData(pwFailKey);
			if (pwdFailCacheData.get(userId).getPwFailCnt() > pwFailCnt) {
				request.setAttribute("loginFailMsg", messageSource.getMessage("error.userLockedByError",new String[] { String.valueOf(pwFaileLockMin) }));
			}
			else {
				request.setAttribute("loginFailMsg", messageSource.getMessage("error.userNotMatch"));
			}
			
		} else if (exception instanceof LockedException) {
			request.setAttribute("loginFailMsg", messageSource.getMessage("error.userLocked"));

		} else if (exception instanceof DisabledException) {
			request.setAttribute("loginFailMsg", messageSource.getMessage("error.userDisable"));

		} else if (exception instanceof AccountExpiredException) {
			request.setAttribute("loginFailMsg", messageSource.getMessage("error.userExpired"));

		} else if (exception instanceof CredentialsExpiredException) {
			request.setAttribute("loginFailMsg", messageSource.getMessage("error.pwdExpired"));
		}
		else if(exception instanceof CredentialsExpiredException) {
			request.setAttribute("loginFailMsg", messageSource.getMessage("error.pwdExpired"));
		}
        
		request.getRequestDispatcher("/login").forward(request, response);


	}
   
}
