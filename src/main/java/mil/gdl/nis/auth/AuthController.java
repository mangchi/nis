package mil.gdl.nis.auth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mil.gdl.nis.annotation.Page;
import mil.gdl.nis.annotation.Trnc;
import mil.gdl.nis.auth.service.AuthService;
import mil.gdl.nis.cmmn.SessionData;
import mil.gdl.nis.user.service.UserService;
import mil.gdl.nis.user.vo.Authority;
import mil.gdl.nis.user.vo.UserVo;;

@Slf4j
@RequiredArgsConstructor
@Controller
public class AuthController {
	
	private final AuthService authService;
	private final UserService userService;
	private final SessionRegistry sessionRegistry;
	private final SessionData sessionData;
	private final MessageSourceAccessor messageSource;

	@Value("${spring.profiles.active}") 
	private String activeProfile;
	
	@GetMapping("/equipAuthList")
	public  String getEquipAuthList(HttpServletRequest req) throws Exception {
		return "auth/equipAuthList";
	}
	
	@Page
	@RequestMapping("/equipAuthList")
	public  @ResponseBody Map<String,Object> getEquipAuthList(@RequestBody Map<String,Object> param,HttpServletRequest req) throws Exception {
        Map<String,Object> rtnMap = new HashMap<>();
        List<Map<String,Object>> list = authService.getEquipAuthList(param);
        rtnMap.put("list", list);
		return rtnMap;

	}

	
	@GetMapping("/authMng")
	public  ModelAndView getAuthMng(Authentication authentication,HttpServletRequest req) throws Exception {
		ModelAndView mv = new ModelAndView("auth/authMng");
		UserVo user = (UserVo) authentication.getPrincipal();
		mv.addObject("schUnitId",user.getUnitId() );
		mv.addObject("schUnitNm", user.getUnitNm());
		return mv;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/authMng")
	public  @ResponseBody Map<String,Object> getAuthMng(@RequestBody Map<String,Object> param,HttpServletRequest req) throws Exception {
		Map<String,Object> rtnMap = new HashMap<>();
		List<Map<String,Object>> roleIdList = (List<Map<String,Object>>)param.get("roleIds");
		String unitId =  sessionData.getUserVo().getUnitId();
		if(param.containsKey("unitId") && !param.get("unitId").equals("")) {
			unitId = String.valueOf(param.get("unitId"));
		}
		for(Map<String,Object> map:roleIdList) {
			map.put("unitId", unitId);
			if(map.get("id").equals(Authority.ROLE_ADMIN.name())){
				rtnMap.put("adminList", authService.getAuthViewList(map));
			}
			if(map.get("id").equals(Authority.ROLE_MANAGER.name())){
				rtnMap.put("managerList", authService.getAuthViewList(map));
			}
			if(map.get("id").equals(Authority.ROLE_USER.name())){
				rtnMap.put("userList", authService.getAuthViewList(map));
			}
		}
		rtnMap.put("role", sessionData.getUserVo().getAuthority().name());
		return rtnMap;
	}
	
	@GetMapping("/myAuthView")
	public  @ResponseBody Map<String,Object> getMyAuthView(@RequestParam Map<String,Object> param,HttpServletRequest req) throws Exception {
		Map<String,Object> rtnMap = new HashMap<>();
		param.put("id", sessionData.getUserVo().getAuthority().name());
		param.put("unitId", sessionData.getUserVo().getUnitId());
		
		rtnMap.put("adminList", authService.getAuthViewList(param));
		return rtnMap;
	}
	
	@Trnc
	@PostMapping("/saveAuthView")
	public  @ResponseBody Map<String,Object> saveAuth(@RequestBody Map<String,Object> param,HttpServletRequest req) throws Exception{
		Map<String,Object> rtnMap = new HashMap<>();
	    try {
	    	int rtn = authService.saveAuthView(param);
	    	if(rtn > 0) {
	    		rtnMap.put("success_msg", messageSource.getMessage("msg.success"));
	    	}
	    	else {
	    		rtnMap.put("success_msg", messageSource.getMessage("msg.fail"));
	    	}
		}catch(Exception e) {
			log.error("error:", e );
			rtnMap.put("fail_msg", messageSource.getMessage("msg.fail"));
			return rtnMap;
		}
	    return rtnMap;
		

	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping("/updateEquip")
	public  @ResponseBody Map<String,Object> blockEquip(@RequestBody Map<String,Object> param,HttpServletRequest req) throws Exception {
        Map<String,Object> rtnMap = new HashMap<>();
        List<UserVo> loginUserList = new ArrayList();
        try {
        	if(param.get("blockYn").equals("Y")) {
        		loginUserList = userService.getLoginUser(param);
        	}
        	if(authService.updateEquip(param)> 0) {
        		if(param.get("blockYn").equals("Y")) {
	            	for(UserVo loginUser:loginUserList) {
	            		 List<SessionInformation> allSessions = sessionRegistry.getAllSessions(loginUser, true);
	                     for (SessionInformation session: allSessions) {
	                         log.debug("강제 로그아웃사용자: {}" ,((UserVo)session.getPrincipal()).getUsername());
	                         if(((UserVo)session.getPrincipal()).getUsername().equals(loginUser.getUserId())){
	                        	 session.expireNow();
	                         }
	                         
	                     }
	            	}
        		}
                rtnMap.put("success_msg", messageSource.getMessage("msg.success"));
            }
            else {
            	rtnMap.put("fail_msg", messageSource.getMessage("msg.fail"));
            }
        }catch(Exception e) {
        	log.error("error:", e );
			rtnMap.put("fail_msg", messageSource.getMessage("msg.fail"));
			return rtnMap;
		}
		return rtnMap;
 
	}
	
	
	
	
	

}
