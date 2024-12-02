package mil.gdl.nis.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mil.gdl.nis.log.service.LogService;

@RequiredArgsConstructor
@Slf4j
@Component
public class SessionDestoryedHandler implements ApplicationListener<SessionDestroyedEvent> {
	
	private final LogService logService;
    
	@Override
	public void onApplicationEvent(SessionDestroyedEvent event) {
		
		List<SecurityContext> securityContexts = event.getSecurityContexts();
		

		for (SecurityContext securityContext : securityContexts) {
			Authentication authentication = securityContext.getAuthentication();
			String userName = authentication.getName();
			log.debug("session destoryed userName:{}",userName);
			/*
			String ip = request.getHeader("X-FORWARDED-FOR");
			if (ip == null) {
				ip = request.getRemoteAddr();
		    }
		    */
		    Map<String,Object> logMap = new HashMap<>();
		    logMap.put("userId", userName);
		    logMap.put("useMenu", "/logout");
		    logMap.put("connectIp", "");
		    logMap.put("contents", "session destoryed");
		    logMap.put("viewDataId", "");
			try {
				logService.insertLog(logMap);
			}catch(Exception e) {
				log.error("error:", e );
			}


		}

	}

}

