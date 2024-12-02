package mil.gdl.nis.work;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mil.gdl.nis.annotation.Page;
import mil.gdl.nis.cmmn.vo.PageInfo;
import mil.gdl.nis.work.service.WorkService;

@Slf4j
@RequiredArgsConstructor
@Controller
public class WorkController {
	
	private final WorkService workService;

	private final MessageSourceAccessor messageSource;

	@Value("${spring.profiles.active}") 
	private String activeProfile;

	
	@GetMapping("/workList")
	public  String getWorkList(HttpServletRequest req) throws Exception {
		return "work/workList";
	}
	
	@Page
	@RequestMapping("/workList")
	public  @ResponseBody Map<String,Object> getWorkList(@RequestBody Map<String,Object> param,HttpServletRequest req) throws Exception {
        Map<String,Object> rtnMap = new HashMap<>();
        List<Map<String,Object>> list = workService.getWorkList(param);
        rtnMap.put("list", list);
        Map<String,Object> appendMap = new HashMap<String,Object>();
        PageInfo pageInfo = (PageInfo)param.get("pageInfo");
        appendMap.put("totalCount", pageInfo.getTotalCount());
        if(list.size() > 0) {
        	appendMap.put("id", list.get(0).get("id"));
        }
        
        rtnMap.put("appendData", appendMap);
		return rtnMap;

	}
	
	@Page
	@RequestMapping("/workLogList")
	public  @ResponseBody Map<String,Object> getWorLogkList(@RequestBody Map<String,Object> param,HttpServletRequest req) throws Exception {
        Map<String,Object> rtnMap = new HashMap<>();
        List<Map<String,Object>> list = workService.getWorkLogList(param);
        rtnMap.put("list", list);
		return rtnMap;

	}


	@PostMapping("/workInfo")
	public @ResponseBody Map<String,Object> getWorkInfo(@RequestBody Map<String,Object> param) throws Exception {
		Map<String,Object> rtnMap = new HashMap<>();
		rtnMap.put("info", workService.getWorkInfo(param));
		return rtnMap;
	}
	
	
	@PostMapping("/deleteWork")
	public @ResponseBody  Map<String,String> deleteWork(@RequestBody Map<String,Object> param) throws Exception {
		Map<String,String> rtnMap = new HashMap<>();
		try {
			int rtn = workService.deleteWork(param);
			if(rtn > 0) {
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
	
	@PostMapping("/saveWork")
	public @ResponseBody  Map<String,String> saveWork(@RequestBody Map<String,Object> param) throws Exception {
		Map<String,String> rtnMap = new HashMap<>();
		try {
			int rtn = workService.saveWork(param);
			if(rtn > 0) {
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
	
	private Map<String, String> validateHandling(Map<String, String> validatorResult,Errors errors) {
		for (FieldError error : errors.getFieldErrors()) {
			String validKeyName = String.format("valid_%s", error.getField());
			if(!validatorResult.containsKey(validKeyName)) {
				validatorResult.put(validKeyName, error.getDefaultMessage());
			}
			
		}
		return validatorResult;
	}
	
	
	
	

}
