package mil.gdl.nis.frame;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mil.gdl.nis.annotation.Page;
import mil.gdl.nis.cmmn.vo.PageInfo;
import mil.gdl.nis.dataset.vo.DatasetVo;
import mil.gdl.nis.frame.service.FrameService;

@Slf4j
@RequiredArgsConstructor
@Controller
public class FrameController {
	
	private final FrameService frameService;

	private final MessageSourceAccessor messageSource;

	@Value("${spring.profiles.active}") 
	private String activeProfile;

	
	@GetMapping("/frameList")
	public  String getDataList(HttpServletRequest req) throws Exception {
		return "frame/frameList";
	}
	
	@Page
	@RequestMapping("/frameList")
	public  @ResponseBody Map<String,Object> getFrameList(@RequestBody Map<String,Object> param,HttpServletRequest req) throws Exception {
        Map<String,Object> rtnMap = new HashMap<>();
        List<Map<String,Object>> list = frameService.getFrameList(param);
        Map<String,Object> appendMap = new HashMap<String,Object>();
        PageInfo pageInfo = (PageInfo)param.get("pageInfo");
        appendMap.put("totalCount", pageInfo.getTotalCount());
        rtnMap.put("appendData", appendMap);
        rtnMap.put("list", list);
		return rtnMap;

	}
	
	@GetMapping("/extractList")
	public  String getExtractList(HttpServletRequest req) throws Exception {
		return "frame/extractList";
	}
	
	@Page
	@RequestMapping("/extractList")
	public  @ResponseBody Map<String,Object> getExtractList(@RequestBody Map<String,Object> param,HttpServletRequest req) throws Exception {
        Map<String,Object> rtnMap = new HashMap<>();
        List<Map<String,Object>> list = frameService.getExtractList(param);
        Map<String,Object> appendMap = new HashMap<String,Object>();
        PageInfo pageInfo = (PageInfo)param.get("pageInfo");
        appendMap.put("totalCount", pageInfo.getTotalCount());
        if(list.size() > 0) {
        	appendMap.put("movId", list.get(0).get("movId"));
        }
        rtnMap.put("appendData", appendMap);
        rtnMap.put("list", list);
		return rtnMap;

	}
	
	@Page
	@RequestMapping("/extractImageList")
	public  @ResponseBody Map<String,Object> getExtractIMageList(@RequestBody Map<String,Object> param,HttpServletRequest req) throws Exception {
        Map<String,Object> rtnMap = new HashMap<>();
        List<Map<String,Object>> list = frameService.getExtractImageList(param);
        Map<String,Object> appendMap = new HashMap<String,Object>();
        if(list.size() > 0) {
        	appendMap.put("id", list.get(0).get("movId"));
        	List<Map<String,Object>> imgList = frameService.getImageList(appendMap);
        	rtnMap.put("appendData", imgList);
        }
        
        rtnMap.put("list", list);
		return rtnMap;

	}

	@PostMapping("/frameInfo")
	public ModelAndView getFrameInfo(@RequestBody Map<String,Object> param) throws Exception {
		return new ModelAndView("frame/frameInfo","img",frameService.getFrameInfo(param));
	}
	
	
	@GetMapping("/frameInfo")
	public ModelAndView getFrameInfo(Model model) throws Exception {
		DatasetVo vo = new DatasetVo();
		return new ModelAndView("frame/frameInfo","img",vo);
	}
	
	
	@PostMapping("/extractMov")
	public @ResponseBody  Map<String,String> extractMov(@RequestBody   Map<String,Object> param) throws Exception {
		Map<String,String> rtnMap = new HashMap<>();
		try {
			int rtn =frameService.extractMov(param);
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
	
	@PostMapping("/deleteFrame")
	public @ResponseBody  Map<String,String> deleteFrame(@RequestBody   Map<String,Object> param) throws Exception {
		Map<String,String> rtnMap = new HashMap<>();
		try {
			int rtn = frameService.deleteFrame(param);
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
	
	@PostMapping("/deleteImageByFrame")
	public @ResponseBody  Map<String,String> deleteImageByFrame(@RequestBody   Map<String,Object> param) throws Exception {
		Map<String,String> rtnMap = new HashMap<>();
		try {
			int rtn = frameService.deleteImageByFrame(param);
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
