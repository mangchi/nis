package mil.gdl.nis.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mil.gdl.nis.annotation.Page;
import mil.gdl.nis.annotation.Trnc;
import mil.gdl.nis.cmmn.SessionData;
import mil.gdl.nis.cmmn.service.CmmnService;
import mil.gdl.nis.cmmn.util.StringUtil;
import mil.gdl.nis.user.service.UserService;
import mil.gdl.nis.user.vo.Authority;
import mil.gdl.nis.user.vo.UserValidGroups.ChgValid;
import mil.gdl.nis.user.vo.UserValidGroups.ModValid;
import mil.gdl.nis.user.vo.UserValidGroups.PwdValid;
import mil.gdl.nis.user.vo.UserValidGroups.RegValid;
import mil.gdl.nis.user.vo.UserVo;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {
	
	private final SessionData sessionData;
	private final UserService userService;
	private final BCryptPasswordEncoder bPasswordEncoder;
	private final PasswordEncoder passwordEncoder;
	//private final CacheService cacheService;
	private final MessageSourceAccessor messageSource;
	private final AuthenticationManager authenticationManager;
	private final CmmnService cmmnService;
    private final SessionRegistry sessionRegistry;

	@Value("${user.pwFailKey}")
	private String pwFailKey;
	
	@Value("${code.codeKey}")
    private String codeKey;
    
	@GetMapping("/register")
	public ModelAndView register(Model model) {
		UserVo vo = new UserVo();
		vo.setReadonly("N");
		vo.setHiddenPwd("N");
		vo.setAuthority(Authority.ROLE_USER);
		ModelAndView mv = new ModelAndView("user/register","user",vo);
		mv.addObject("authorities", Authority.values());
		mv.addObject("loginAuth", sessionData.getUserVo().getAuthority().name());
		mv.addObject("rankCds", cmmnService.getSelectCode("001"));
		return  mv;
	}
	
	
	@Trnc
	@PostMapping("/register")
    public @ResponseBody Map<String,String> register(@RequestBody @Validated(RegValid.class) UserVo param, Errors errors,HttpServletRequest req) throws Exception{
		Map<String,String> rtnMap = new HashMap<>();
		try {
			
			if (errors.hasErrors()) {
				// 유효성 통과 못한 필드와 메시지를 핸들링
				this.validateHandling(rtnMap,errors);
				if(!rtnMap.containsKey("valid_userPw")) {
					validPw(param,rtnMap,"valid_userPw");
				}
				return rtnMap;
				
			}
			if(!validPw(param,rtnMap,"valid_userPw")) {
				return rtnMap;
			}
			if(userService.checkUserId(param.getUserId()) > 0) {
				rtnMap.put("valid_userId", messageSource.getMessage("error.userId.duplicated"));
				return rtnMap;
			}
			String ip = req.getHeader("X-FORWARDED-FOR");
			
	        if (ip == null) {
	            ip = req.getRemoteAddr();
	        }
			UserVo user = UserVo.builder().userId(param.getUserId()).userPw(bPasswordEncoder.encode(param.getUserPw()))
					.userNm(param.getUserNm()).authority(param.getAuthority()).build();
			user.setDeptNm(param.getDeptNm());
			user.setSpecNm(param.getSpecNm());
			user.setUnitId(param.getUnitId());
			user.setRankCd(param.getRankCd());
			user.setIpAddr(ip);
			userService.register(user);
		}catch(Exception e) {
			log.error("error:", e );
			rtnMap.put("fail_msg", messageSource.getMessage("msg.fail"));
			return rtnMap;
		}
		rtnMap.put("success_msg", messageSource.getMessage("msg.success"));
		return rtnMap;
	}
	
	@GetMapping("/chgUser")
	public ModelAndView chgUser(@RequestParam Map<String,Object> param,@AuthenticationPrincipal UserVo sessionUser) throws Exception{
		
		ModelAndView mv = null;
		if(param.containsKey("userId") && !String.valueOf(param.get("userId")).equals("")){
			UserVo paramVo = new UserVo();
			paramVo.setUserId(String.valueOf(param.get("userId")));
			UserVo rtnUser = userService.getUser(paramVo);
			rtnUser.setHiddenPwd("Y");
			rtnUser.setReadonly("Y");
            mv = new ModelAndView("user/register","user",rtnUser);
		}
		else {
			sessionUser.setReadonly("Y");
			sessionUser.setHiddenPwd("N");
			//sessionUser.setShowPwd("Y");
			mv = new ModelAndView("user/register","user",sessionUser);
		}
		mv.addObject("authorities", Authority.values());
		mv.addObject("rankCds", cmmnService.getSelectCode("001"));
		mv.addObject("loginAuth", sessionData.getUserVo().getAuthority().name());
		return  mv;
	}
	
	@Trnc
	@PostMapping("/chgUser")
	public @ResponseBody Map<String,String> chgUser(@RequestBody  @Validated(ChgValid.class) UserVo param, Errors errors){
		Map<String,String> rtnMap = new HashMap<>();
		
		try {
		if (errors.hasErrors()) {
			// 유효성 통과 못한 필드와 메시지를 핸들링
			this.validateHandling(rtnMap,errors);
			return rtnMap;
		}
		param.setId(sessionData.getUserVo().getId());
		if (!passwordEncoder.matches(param.getUserPw(),sessionData.getUserVo().getUserPw())) {
			rtnMap.put("valid_userPw", messageSource.getMessage("error.userPw.invalid5"));
			return rtnMap;
 		}
		userService.modify(param);
		log.debug("chgUser sessionUser:{}",sessionData.getUserVo());

		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(sessionData.getUserVo().getUsername(), param.getUserPw()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
		
		
		}catch(Exception e) {
			log.error("error:", e );
			rtnMap.put("fail_msg", messageSource.getMessage("msg.fail"));
			return rtnMap;
			
		}
		rtnMap.put("success_msg", messageSource.getMessage("msg.success"));
		return rtnMap;
	}
	
	@Trnc
	@PostMapping("/modUser")
	public @ResponseBody Map<String,String> modUser(@RequestBody  @Validated(ModValid.class) UserVo param, Errors errors){
		Map<String,String> rtnMap = new HashMap<>();
		
		try {
		if (errors.hasErrors()) {
			// 유효성 통과 못한 필드와 메시지를 핸들링
			this.validateHandling(rtnMap,errors);
			return rtnMap;
		}
		userService.modify(param);
		
		}catch(Exception e) {
			log.error("error:", e );
			rtnMap.put("fail_msg", messageSource.getMessage("msg.fail"));
			return rtnMap;
			
		}
		rtnMap.put("success_msg", messageSource.getMessage("msg.success"));
		return rtnMap;
	}
	
	@Trnc
	@PostMapping("/pwdInit")
	public @ResponseBody Map<String,String> pwdInit(@RequestBody Map<String,Object> param,HttpServletRequest req) throws Exception{
		Map<String,String> rtnMap = new HashMap<>();
		String initPwd = "1234";
		try {
		  // userService.pwdInit(param);
			
			UserVo vo = new UserVo();
			vo.setUserId(String.valueOf(param.get("userId")));
			vo.setUserPw(bPasswordEncoder.encode(initPwd));
		    if(userService.initPwd(vo) == 0) {
		    	rtnMap.put("fail_msg", messageSource.getMessage("msg.fail"));
				return rtnMap;
		    }
		}catch(Exception e) {
			log.error("error:", e );
			rtnMap.put("fail_msg", messageSource.getMessage("msg.fail"));
			return rtnMap;
			
		}
		String[] replaceValuesKr = new String[] {initPwd};
		rtnMap.put("success_msg", messageSource.getMessage("msg.pwdInit",replaceValuesKr));
		return rtnMap;
	}
	
	@Trnc
	@PostMapping("/chgPwd")
	public @ResponseBody Map<String,String> chgPwd(@RequestBody @Validated(PwdValid.class) UserVo param, Errors errors) throws Exception{
		Map<String,String> rtnMap = new HashMap<>();
		try {
			UserVo sessionUser = sessionData.getUserVo();
			param.setUserId(sessionUser.getUserId());
			if (errors.hasErrors()) {
				// 유효성 통과 못한 필드와 메시지를 핸들링
				this.validateHandling(rtnMap,errors);
				return rtnMap;
			}
	 		if (!passwordEncoder.matches(param.getOldPw(),sessionUser.getUserPw())) {
	 			rtnMap.put("valid_oldPw", messageSource.getMessage("error.userPw.invalid5"));
				return rtnMap;
	 		}
	 		if(!param.getNewPw().equals(param.getNewPwConfirm())) {
	 			rtnMap.put("valid_newPwConfirm", messageSource.getMessage("error.userPw.invalid6"));
	         	return rtnMap;
	 		}
	 		if (passwordEncoder.matches(param.getNewPw(),sessionUser.getUserPw())) {
	 			rtnMap.put("valid_newPwConfirm", messageSource.getMessage("error.userPw.invalid7"));
				return rtnMap;
	 		}
	 		if(!validPw(param,rtnMap,"valid_newPw")) {
	 			return rtnMap;
			}

	 		param.setUserPw(param.getNewPw());
	 		param.setUserId(sessionUser.getUserId());
	 		if(!StringUtil.checkPwdStr(param.getUserPw())) {
	 			rtnMap.put("valid_userPw", messageSource.getMessage("error.userPw.invalid1"));
				return rtnMap;
			}
	 		
	 		
	 		
			param.setId(sessionUser.getId());
			param.setUserPw(bPasswordEncoder.encode(param.getNewPw()));
			userService.changePwd(param);
			Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(sessionData.getUserVo().getUsername(), param.getNewPw()));
	        SecurityContextHolder.getContext().setAuthentication(authentication);
			}catch(Exception e) {
				log.error("error:", e );
				rtnMap.put("fail_msg", messageSource.getMessage("msg.fail"));
				return rtnMap;
			}
		rtnMap.put("success_msg", messageSource.getMessage("msg.success"));
		return rtnMap;
	}
	
    private boolean validPw(UserVo param,Map<String,String> map,String key) {
    	String pwd = param.getUserPw();
    	if(key.equals("valid_newPw")) {
    		pwd = param.getNewPw();
    	}
		if(StringUtil.checkSerialDupStr(pwd,3)) {
			map.put(key, messageSource.getMessage("error.userPw.invalid2"));
			return false;
		}

		if (!pwd.isEmpty() && pwd.contains(param.getUserId())) {
			//map.put(key, key.equals("valid_newPw")?"변경 ":messageSource.getMessage("error.userPw.invalid3"));
			map.put(key, messageSource.getMessage("error.userPw.invalid3"));
			return false;
		}
		
		if(StringUtil.checkOrdStr(pwd,3)) {
			//map.put(key, key.equals("valid_newPw")?"변경 ":""+messageSource.getMessage("error.userPw.invalid4"));
			map.put(key, messageSource.getMessage("error.userPw.invalid4"));
			return false;
		}
		return true;
	}

	
	@GetMapping("/chgPwdLogin")
	public ModelAndView chgPwdLogin(Authentication authentication) {
		log.debug("chgPwdLogin.............");
		UserVo user = (UserVo) authentication.getPrincipal();
		return new ModelAndView("user/changePwdLogin","user",user);
	}
	
	
	@Trnc
	@PostMapping("/chgPwdLogin")
	public @ResponseBody Map<String,String> chgPwdLogin(@RequestBody @Validated(PwdValid.class) UserVo param, Errors errors) throws Exception{
		Map<String,String> rtnMap = new HashMap<>();
		try {
			if (errors.hasErrors()) {
				// 유효성 통과 못한 필드와 메시지를 핸들링
				this.validateHandling(rtnMap,errors);
				return rtnMap;
			}
			UserVo user = userService.getUser(param);
	 		if (!passwordEncoder.matches(param.getOldPw(),user.getUserPw())) {
	 			rtnMap.put("valid_oldPw", messageSource.getMessage("error.userPw.invalid5"));
				return rtnMap;
	 		}
	 		if(!param.getNewPw().equals(param.getNewPwConfirm())) {
	 			rtnMap.put("valid_newPwConfirm", messageSource.getMessage("error.userPw.invalid6"));
	         	return rtnMap;
	 		}

	 		param.setUserPw(param.getNewPw());
	 		//param.setUserId(user.getUserId());
	 		if(!StringUtil.checkPwdStr(param.getUserPw())) {
	 			rtnMap.put("valid_userPw", messageSource.getMessage("error.userPw.invalid1"));
				return rtnMap;
			}
	 		
	 		if(!validPw(param,rtnMap,"valid_newPw")) {
	 			return rtnMap;
			}
	 		
			param.setId(user.getId());
			param.setUserPw(bPasswordEncoder.encode(param.getNewPw()));
			userService.changePwd(param);
			
			}catch(Exception e) {
				log.error("error:", e );
				rtnMap.put("fail_msg", messageSource.getMessage("msg.fail"));
				return rtnMap;
			}
		rtnMap.put("success_msg", messageSource.getMessage("msg.success"));
		return rtnMap;
	}
	
	
	@GetMapping("/chgPwd")
	public ModelAndView chgPwd(@AuthenticationPrincipal UserVo sessionUser) {
		return new ModelAndView("user/changePwd","user",sessionUser);
	}
	
	
	
	@Trnc
	@PostMapping("/deleteUser")
	public  @ResponseBody Map<String,Object> deleteUser(@RequestBody Map<String,Object> param,HttpServletRequest req) throws Exception {
        Map<String,Object> rtnMap = new HashMap<>();
        List<UserVo> loginUserList = userService.getLoginUser(param);
        log.debug("user:{}",loginUserList);

        if(userService.deleteUser(param)> 0) {
        	for(UserVo loginUser:loginUserList) {
        		 List<SessionInformation> allSessions = sessionRegistry.getAllSessions(loginUser, true);
                 for (SessionInformation session: allSessions) {
                     log.debug("강제 로그아웃사용자: {}" ,((UserVo)session.getPrincipal()).getUsername());
                     //log.debug("세션아이디:{}" , session.getSessionId());
                     if(((UserVo)session.getPrincipal()).getUsername().equals(loginUser.getUserId())){
                    	 session.expireNow();
                     }
                     
                 }
        	}
            rtnMap.put("success_msg", messageSource.getMessage("msg.success"));
        }
        else {
        	rtnMap.put("fail_msg", messageSource.getMessage("msg.fail"));
        }

		return rtnMap;
 
	}

	
	// 회원가입 시, 유효성 체크
	private Map<String, String> validateHandling(Map<String, String> validatorResult,Errors errors) {
		for (FieldError error : errors.getFieldErrors()) {
			String validKeyName = String.format("valid_%s", error.getField());
			if(!validatorResult.containsKey(validKeyName)) {
				validatorResult.put(validKeyName, error.getDefaultMessage());
			}
			
		}

		return validatorResult;
	}
    
	/*
	@ModelAttribute("userRoles")
	public UserRole[]  userRoles() {
	    return UserRole.values();
	}
	
	
	@ModelAttribute("rankCds")
	public List<CodeVo> rankCds() {
		List<CodeVo> classList = cacheService.getCodeCacheData(codeKey).stream().filter(c -> "001".equals(c.getGrpCd())).collect(Collectors.toList());
		return classList;
	}
	*/
	
	
	@GetMapping("/userList")
	public  String getUserList(HttpServletRequest req) throws Exception {
		return "user/userList";
	}
	
	@Page
	@PostMapping("/userList")
	public  @ResponseBody Map<String,Object> getUserList(@RequestBody Map<String,Object> param,HttpServletRequest req) throws Exception {
        Map<String,Object> rtnMap = new HashMap<>();
        List<Map<String,Object>> userList = userService.getUserList(param);
        rtnMap.put("list", userList);
		return rtnMap;

	}
	
	


}
