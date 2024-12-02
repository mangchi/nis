package mil.gdl.nis.main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mil.gdl.nis.annotation.Page;
import mil.gdl.nis.cmmn.service.CmmnService;
import mil.gdl.nis.dataset.service.DatasetService;
import mil.gdl.nis.main.service.MainService;
import mil.gdl.nis.user.vo.Authority;
import mil.gdl.nis.user.vo.UserVo;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MainController {
	
	private final CmmnService cmmnService;
	private final DatasetService datasetService;
	
	private final MainService mainService;
	//private final MessageSourceAccessor messageSource;

    /*
	@RequestMapping("/")
	public String home(Model model,Authentication authentication,HttpServletRequest req) {
		if(authentication == null || !authentication.isAuthenticated()) {
			model.addAttribute("user", new UserVo());
			return "user/login";
		}
		return "main";
	}
	*/
	@RequestMapping("/")
	public ModelAndView home(Authentication authentication,HttpServletRequest req,HttpServletResponse res) {
		if(authentication == null || !authentication.isAuthenticated()) {
			UserVo vo = new UserVo();
			vo.setAuthority(Authority.ROLE_USER);
			ModelAndView mv = new ModelAndView("user/login","user",vo);
			mv.addObject("authorities", Authority.values());
			mv.addObject("rankCds", cmmnService.getSelectCode("001"));
			return  mv;
		}else {
			UserVo user = (UserVo) authentication.getPrincipal();
			log.debug("isAccountExpired:{}",user.isAccountExpired());
			if(user.isAccountExpired() ) {
				UserVo vo = new UserVo();
				vo.setAuthority(Authority.ROLE_USER);
				ModelAndView mv = new ModelAndView("user/login","user",vo);
				mv.addObject("authorities", Authority.values());
				mv.addObject("rankCds", cmmnService.getSelectCode("001"));
				return  mv;

			}
		}
		return new ModelAndView("cmmn/dynimicContent");
		//return "cmmn/dynimicContent";
	}
	
	@RequestMapping("/main")
	public ModelAndView main(Model model,Authentication authentication,HttpServletRequest req) {
		ModelAndView mv = new ModelAndView("main");
		UserVo user = (UserVo) authentication.getPrincipal();
		mv.addObject("schUnitId",user.getUnitId() );
		mv.addObject("schUnitNm", user.getUnitNm());
		return mv;
	}
	
	@Page
	@RequestMapping("/intro")
	public @ResponseBody Map<String,Object> intro(@RequestBody Map<String,Object> param,HttpServletRequest req) throws Exception {
		
		Map<String,Object> rtnMap = new HashMap<>();
		
        List<Map<String,Object>> list = datasetService.getDatasetList(param);
        rtnMap.put("appendData", mainService.getStatInfo(param));
        rtnMap.put("list", list);
		return rtnMap;
	}

}
