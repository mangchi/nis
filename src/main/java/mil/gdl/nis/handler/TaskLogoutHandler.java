package mil.gdl.nis.handler;


import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mil.gdl.nis.log.service.LogService;
import mil.gdl.nis.user.vo.UserVo;

@Slf4j
@RequiredArgsConstructor
//@AllArgsConstructor
public class TaskLogoutHandler implements LogoutHandler {
	
	private final LogService logService;
	//private final SessionData sessionData;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) {
      log.info("TaskLogoutHandler logout");
      String ip = request.getHeader("X-FORWARDED-FOR");
      if (ip == null) {
          ip = request.getRemoteAddr();
      }
      Map<String,Object> logMap = new HashMap<>();
      logMap.put("userId", ((UserVo)authentication.getPrincipal()).getUserId());
      //logMap.put("userId", sessionData.getUserVo().getUserId());
      logMap.put("useMenu", "/logout");
      logMap.put("connectIp", ip);
      logMap.put("contents", "logout success");
      logMap.put("viewDataId", "");
	  try {
		  logService.insertLog(logMap);
	  }catch(Exception e) {
		  log.error("error:", e );
	  }
		
    }
    

}