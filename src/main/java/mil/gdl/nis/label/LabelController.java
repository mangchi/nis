package mil.gdl.nis.label;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mil.gdl.nis.annotation.Page;
import mil.gdl.nis.cmmn.service.CmmnService;
import mil.gdl.nis.cmmn.vo.PageInfo;
import mil.gdl.nis.dataset.service.DatasetService;
import mil.gdl.nis.label.service.LabelService;

@Slf4j
@RequiredArgsConstructor
@Controller
public class LabelController {
	
	private final LabelService labelService;
	private final DatasetService datasetService;

	private final CmmnService cmmnService;

	private final MessageSourceAccessor messageSource;
	
	@Value("${spring.profiles.active}") 
	private String activeProfile;

	@PostMapping("/labelInfo")
	public @ResponseBody Map<String,Object> getLabelInfo(@RequestBody Map<String,Object> param,HttpServletRequest req) throws Exception {
		Map<String,Object> rtnMap = new HashMap<>();
		rtnMap.put("info", labelService.getLabelInfo(param));
		rtnMap.put("list", labelService.getObjectList(param));
		rtnMap.put("weaponList", labelService.getClassWeaponList(param));
		/*
		ModelAndView mv = new ModelAndView("label/dataLabel");
		mv.addObject("img",labelService.getLabelInfo(param));
		mv.addObject("dataCds", cmmnService.getSelectCode("010"));
		return mv;
		*/
		return rtnMap;
	}
	
	
	@GetMapping(value="/labelInfo")
	public ModelAndView getLabelInfo(Model mode,HttpServletRequest reql) throws Exception {
		//ImgVo vo = new ImgVo();
		ModelAndView mv = new ModelAndView("label/dataLabel");
		//mv.addObject("img",vo);
		//mv.addObject("dataCds", cmmnService.getSelectCode("010"));
		return mv;
	}
	
	@Page
	@PostMapping("/labelList")
	public  @ResponseBody Map<String,Object> getDatasetMng(@RequestBody Map<String,Object> param,HttpServletRequest req) throws Exception {
		Map<String,Object> rtnMap = new HashMap<>();
	    List<Map<String,Object>> list = labelService.getLabelList(param);
	    Map<String,Object> appendMap = new HashMap<String,Object>();
        PageInfo pageInfo = (PageInfo)param.get("pageInfo");
        appendMap.put("totalCount", pageInfo.getTotalCount());
        
	    if(list.size() > 0) {
	    	appendMap.put("imgList", labelService.getLabelImgList(list.get(0)));
	    }
	    rtnMap.put("appendData", appendMap);
	    rtnMap.put("list", list);
	    return rtnMap;
	}
	
	
	@GetMapping("/labelList")
	public String getDatasetMng(Model model) throws Exception {
		return "label/dataLabelList";
	}
	
	@PostMapping("/labelImgList")
	public  @ResponseBody Map<String,Object> getLabelImgList(@RequestBody Map<String,Object> param,HttpServletRequest req) throws Exception {
        Map<String,Object> rtnMap = new HashMap<>();
        List<Map<String,Object>> labelList = labelService.getLabelImgList(param);
        rtnMap.put("list", labelList);
        
		return rtnMap;
	}
	
	@PostMapping("/objectList")
	public  @ResponseBody Map<String,Object> getObjecgtList(@RequestBody Map<String,Object> param,HttpServletRequest req) throws Exception {
        Map<String,Object> rtnMap = new HashMap<>();
        List<Map<String,Object>> list = labelService.getObjectList(param);
        rtnMap.put("list", list);
        
		return rtnMap;
	}
	

	@PostMapping("/callAi")
	public  @ResponseBody Map<String,Object> callAi(@RequestBody Map<String,Object> param,HttpServletRequest req) throws Exception {
        Map<String,Object> rtnMap = new HashMap<>();
        List<Map<String,Object>> list = labelService.callAi(param);
        rtnMap.put("list", list);
        
		return rtnMap;
	}
	
	/*
	@GetMapping("/labelList")
	public  @ResponseBody Map<String,Object> getLabelList(@RequestParam Map<String,Object> param,HttpServletRequest req) throws Exception {
        Map<String,Object> rtnMap = new HashMap<>();
        List<Map<String,Object>> labelList = labelService.getLabelList(param);
        rtnMap.put("list", labelList);
        
		return rtnMap;
	}
	*/
	
	@PostMapping("/labelSave")
	public @ResponseBody  Map<String,String> save(@RequestBody Map<String,Object> param) throws Exception {
		Map<String,String> rtnMap = new HashMap<>();
		int rtn = 0;
		try {
			rtn = labelService.save(param);
			if(rtn > 0) {
				rtnMap.put("success_msg", messageSource.getMessage("msg.success"));
			}else {
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
