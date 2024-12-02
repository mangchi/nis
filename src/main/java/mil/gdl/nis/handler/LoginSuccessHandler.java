package mil.gdl.nis.handler;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.support.SessionFlashMapManager;

import com.google.gson.JsonObject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mil.gdl.nis.auth.service.AuthService;
import mil.gdl.nis.bbs.service.BbsService;
import mil.gdl.nis.cmmn.PwFailCacheData;
import mil.gdl.nis.cmmn.service.CacheService;
import mil.gdl.nis.cmmn.util.DateUtil;
import mil.gdl.nis.log.service.LogService;
import mil.gdl.nis.user.service.UserService;
import mil.gdl.nis.user.vo.UserVo;

@Slf4j
@RequiredArgsConstructor
@Service
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

	private final CacheService cacheService;
	private final LogService logService;
	private final UserService userService;
	private final AuthService authService;
	private final BbsService bbsService;
	private final MessageSourceAccessor messageSource;
	@Value("${user.pwFailKey}")
	private String pwFailKey;
	@Value("${user.pwFaileCnt}")
	private int pwFailCnt;
	@Value("${user.pwFaileLockMin}")
	private int pwFaileLockMin;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		log.info("Login Success");

		// 인증된 사용자의 정보를 추출
		UserVo user = (UserVo) authentication.getPrincipal();
		log.debug("AuthenticationSuccessHandler user:{}", user);
		/*
		  Timestamp blockTs = user.getBlockTs();
			
			if(null != blockTs) {
				throw new AuthenticationServiceException(messageSource.getMessage("error.userBlock"));
			}
		*/
		try {
			String ip = request.getHeader("X-FORWARDED-FOR");
	        if (ip == null) {
	            ip = request.getRemoteAddr();
	        }
	        user.setIpAddr(ip);
	        String blockYn = authService.getAuthLogin(user);
	        if(blockYn == null) {
	        	authService.insertEquip(user);
	        	blockYn ="Y";
	        }
			if("Y".equals(blockYn)) {
				authentication.setAuthenticated(false);
				//user.setBlockYn(true);
				final FlashMap flashMap = new FlashMap();
				flashMap.put("loginFailMsg",messageSource.getMessage("error.userBlock"));
				//flashMap.put("userId",user.getUserId());
	            final FlashMapManager flashMapManager = new SessionFlashMapManager();
	            flashMapManager.saveOutputFlashMap(flashMap, request, response);
				response.sendRedirect("/login");
				return;
				
			}
		}catch(Exception e) {
			throw new ServletException("Block IP Check Error happend..........");
		}
		
		
		Timestamp pwRegDt = user.getPwRegTs();
		if(!(DateUtil.currentYear() == DateUtil.getYear(pwRegDt) && DateUtil.quarterYear(DateUtil.currentMonth()) == DateUtil.quarterYear(DateUtil.getMonth(pwRegDt)))) {
			//user.setAuthorities(null);
			user.setAccountExpired(true);
			//throw new AuthenticationServiceException("PWD_QUARTER_CHG",new BizException(ErrorCode.PWD_QUARTER_CHG));
			final FlashMap flashMap = new FlashMap();
			flashMap.put("loginFailMsg", messageSource.getMessage("error.userPwChgPerQuarter"));
			flashMap.put("isPwdChange", "Y");
			flashMap.put("userId",user.getUserId());
            final FlashMapManager flashMapManager = new SessionFlashMapManager();
            flashMapManager.saveOutputFlashMap(flashMap, request, response);
			response.sendRedirect("/chgPwdLogin");
			return;
		}
		
		if(user.getPwdInitYn().equals("Y")){
			user.setAccountExpired(true);
			//throw new AuthenticationServiceException("PWD_QUARTER_CHG",new BizException(ErrorCode.PWD_QUARTER_CHG));
			final FlashMap flashMap = new FlashMap();
			flashMap.put("loginFailMsg", messageSource.getMessage("error.pwdInit"));
			flashMap.put("isPwdChange", "Y");
			flashMap.put("userId",user.getUserId());
            final FlashMapManager flashMapManager = new SessionFlashMapManager();
            flashMapManager.saveOutputFlashMap(flashMap, request, response);
			response.sendRedirect("/chgPwdLogin");
			return;
		}
		
        
		Map<String, PwFailCacheData> pwdFailCacheData = cacheService.getPwdFailCacheData(pwFailKey);
	
		//int failCnt = pwdFailCacheData.containsKey(user.getUserId()) ? pwdFailCacheData.get(user.getUserId()).getPwFailCnt() : 0;
		/*
		if (failCnt >= pwFailCnt) {
			
			if(!LocalDateTime.now().isAfter(pwdFailCacheData.get(user.getUserId()).getPwFailDateTime().plusMinutes(pwFaileLockMin))) {
				final FlashMap flashMap = new FlashMap();
				flashMap.put("loginFailMsg", messageSource.getMessage("error.userLockedByError",new String[] { String.valueOf(pwFaileLockMin) }));
	            final FlashMapManager flashMapManager = new SessionFlashMapManager();
	            flashMapManager.saveOutputFlashMap(flashMap, request, response);
				response.sendRedirect("/login");
				return;
			}
		}
		*/
	
		// 권한리스트를 추출GrantedAuthority
		@SuppressWarnings("unchecked")
		Collection<GrantedAuthority> authlist = (Collection<GrantedAuthority>) user.getAuthorities();
		Iterator<GrantedAuthority> authlist_it = authlist.iterator();

		while (authlist_it.hasNext()) {
			GrantedAuthority authority = authlist_it.next();
			log.debug("Auth:{}",authority.getAuthority());
			// 설정되어 있는 권한 중 ROLE_ADMIN이 있다면
			/*
			 * if(authority.getAuthority().equals("admin") |
			 * authority.getAuthority().equals("systemadmin")) {
			 * url="/kimsaemERP/admin/index.do"; }
			 */
		}
		
		if(pwdFailCacheData.containsKey(user.getUserId())) {
			cacheService.setPwdFailCacheData(pwFailKey, user.getUserId(), true);
		}

		String ip = request.getHeader("X-FORWARDED-FOR");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
		Map<String,Object> connectLog = new HashMap<>();
		connectLog.put("userId", user.getUserId());
		connectLog.put("useMenu", "/login");
		connectLog.put("connectIp", ip);
		connectLog.put("contents", "login success");
		user.setLastConnectedIp(ip);
		try {
			logService.insertLog(connectLog);
			userService.updateLoginData(user);
		}catch(Exception e) {
			log.error("error:", e );
		}

		
		final FlashMap flashMap = new FlashMap();
		JsonObject json = new JsonObject();
		json.addProperty("userId", user.getUserId());
		json.addProperty("authoity", user.getAuthority().name());
		flashMap.put("sessionData", json.toString());
        final FlashMapManager flashMapManager = new SessionFlashMapManager();
        flashMapManager.saveOutputFlashMap(flashMap, request, response);
		response.sendRedirect("/");

	}

}
