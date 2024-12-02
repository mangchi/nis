package mil.gdl.nis.bbs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Controller;
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
import mil.gdl.nis.bbs.service.BbsService;
import mil.gdl.nis.bbs.vo.BbsVo;
import mil.gdl.nis.cmmn.SessionData;
import mil.gdl.nis.cmmn.util.StringUtil;

@Slf4j
@RequiredArgsConstructor
@Controller
public class BbsController {
	
	private final BbsService bbsService;
	private final SessionData sessionData;
	private final MessageSourceAccessor messageSource;
	
	@GetMapping("/bbsList")
	public  ModelAndView getList(HttpServletRequest req) throws Exception {
		return new ModelAndView("bbs/bbsList");
	}
	
	@Page
	@PostMapping("/bbsList")
	public  @ResponseBody Map<String,Object> getList(@RequestBody Map<String,Object> param,HttpServletRequest req) throws Exception {
        Map<String,Object> rtnMap = new HashMap<>();
        List<Map<String,Object>> bbsList = bbsService.getList(param);
        rtnMap.put("list", bbsList);
        
		return rtnMap;
	}
	
	@GetMapping("/notice")
	public   @ResponseBody Map<String,Object> notice(@RequestParam Map<String,Object> param,HttpServletRequest req) throws Exception {
		Map<String,Object> rtnMap = new HashMap<>();
		param.put("userId",sessionData.getUserVo().getUserId());
	    rtnMap.put("info", bbsService.getBbsNotice(param));
		return rtnMap;
	}
	
	@PostMapping("/bbsInfo")
	public  ModelAndView getBbsInfo(@RequestBody BbsVo param,HttpServletRequest req) throws Exception {
		ModelAndView mv = new ModelAndView("bbs/bbsInfo");
		if(StringUtil.isEmpty(param.getId())) {
			param.setBbsType("002");  //default: 보통
			mv.addObject("bbsInfo", param);
			return mv;
		}
		mv.addObject("bbsInfo", bbsService.getBbsInfo(param));
		return mv;
	}
	
	@GetMapping("/bbsCount")
	public  @ResponseBody Map<String,Object>  getBbsReadCount(@RequestParam Map<String,Object> param,HttpServletRequest req) throws Exception {
		Map<String,Object> rtnMap = new HashMap<>();
		param.put("userId",sessionData.getUserVo().getUserId());
        log.debug("param:",param);
        int count = bbsService.getBbsReadCount(param);
        
        rtnMap.put("count", count);
		return rtnMap;
	}
	
	@PostMapping("/bbsReadSave")
	public  @ResponseBody Map<String,Object> bbsReadSave(@RequestBody Map<String,Object> param, Errors errors) throws Exception{
		Map<String,Object> rtnMap = new HashMap<>();
		try {
			param.put("userId",sessionData.getUserVo().getUserId());
		    if(bbsService.bbsReadSave(param) > 0) {
		    	rtnMap.put("info", bbsService.getBbsNotice(param));
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
	
	@Trnc
	@PostMapping("/bbsSave")
	public  @ResponseBody Map<String,Object> bbsSave(@RequestBody @Validated BbsVo param, Errors errors) throws Exception{
		Map<String,Object> rtnMap = new HashMap<>();
		try {
		if (errors.hasErrors()) {
			this.validateHandling(rtnMap,errors);
			return rtnMap;
		}
		
		bbsService.bbsSave(param);
		}catch(Exception e) {
			log.error("error:", e );
			rtnMap.put("fail_msg", messageSource.getMessage("msg.fail"));
			return rtnMap;
		}
		rtnMap.put("success_msg", messageSource.getMessage("msg.success"));
		return rtnMap;

	}
	
	@Trnc
	@PostMapping("/bbsDelete")
	public  @ResponseBody Map<String,Object> bbsDelete(@RequestBody Map<String,Object> param,HttpServletRequest req) throws Exception {
        Map<String,Object> rtnMap = new HashMap<>();
        
        if(bbsService.bbsDelete(param)> 0) {
        	rtnMap.put("success_msg", messageSource.getMessage("msg.success"));
        }
        else {
        	rtnMap.put("fail_msg", messageSource.getMessage("msg.fail"));
        }
        
		return rtnMap;
 
	}
	

	private Map<String, Object> validateHandling(Map<String, Object> validatorResult,Errors errors) {
		for (FieldError error : errors.getFieldErrors()) {
			String validKeyName = String.format("valid_%s", error.getField());
			if(!validatorResult.containsKey(validKeyName)) {
				validatorResult.put(validKeyName, error.getDefaultMessage());
			}
			
		}

		return validatorResult;
	}

}
