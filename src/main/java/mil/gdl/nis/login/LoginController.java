package mil.gdl.nis.login;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mil.gdl.nis.cmmn.PwFailCacheData;
import mil.gdl.nis.cmmn.service.CacheService;
import mil.gdl.nis.cmmn.service.CmmnService;
import mil.gdl.nis.user.vo.Authority;
import mil.gdl.nis.user.vo.UserVo;;

@Slf4j
@RequiredArgsConstructor
@Controller
public class LoginController {
	
	private final MessageSourceAccessor messageSource;
	private final CacheService cacheService;
	private final CmmnService cmmnService;
	
	@Value("${user.pwFailKey}")
	private String pwFailKey;
	
	@RequestMapping("/login")
	public ModelAndView login(Model model,Authentication authentication,HttpServletRequest req) throws Exception{
		ModelAndView mv = null;
        if(Boolean.getBoolean(req.getParameter("expired"))) {
        	log.debug("session expired.......");
        	req.setAttribute("loginFailMsg", messageSource.getMessage("msg.session.expired"));
        }
        if(Boolean.getBoolean(req.getParameter("invalid"))) {
        	log.debug("session invalid.......");
        	req.setAttribute("loginFailMsg", messageSource.getMessage("msg.session.expired"));
        	
        	
        }
        if(authentication != null && authentication.isAuthenticated()) {
        	return new ModelAndView("cmmn/dynimicContent");
		}
        UserVo vo = new UserVo();
		vo.setAuthority(Authority.ROLE_USER);
        mv = new ModelAndView("user/login","user",vo);
        mv.addObject("authorities", Authority.values());
        mv.addObject("rankCds", cmmnService.getSelectCode("001"));

		return  mv;
		
	}
    /*
	@GetMapping("/logout")
	public String logout(HttpServletRequest req) {
		return null;
	}
	*/
	
	@RequestMapping("/pwFailCnt")
	@ResponseBody
	public Map<String, PwFailCacheData> getPwFailCnt() {
		return cacheService.getPwdFailCacheData(pwFailKey);
	}
	
	
	

}
