package mil.gdl.nis.mov;

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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mil.gdl.nis.annotation.Page;
import mil.gdl.nis.cmmn.service.CmmnService;
import mil.gdl.nis.dataset.vo.DatasetVo;
import mil.gdl.nis.mov.service.MovService;


@Slf4j
@RequiredArgsConstructor
@Controller
public class MovController {
	
	private final MovService movService;
	private final CmmnService cmmnService;

	private final MessageSourceAccessor messageSource;

	@Value("${spring.profiles.active}") 
	private String activeProfile;

	@PostMapping("/movInfo")
	public ModelAndView geMovInfo(@RequestBody Map<String,Object> param,HttpServletRequest req) throws Exception {
		
		ModelAndView mv = new ModelAndView("dataset/imgMovInfo","imgMov",movService.getMov(param));
		mv.addObject("collectionDeviceCds", cmmnService.getSelectCode("006"));
		mv.addObject("sourceCds", cmmnService.getSelectCode("008"));
		mv.addObject("dataCds", cmmnService.getSelectCode("010"));
		mv.addObject("nationCds", cmmnService.getSelectCode("011"));
		mv.addObject("dataTypes", cmmnService.getSelectCode("033"));
		mv.addObject("shipClasss", cmmnService.getSelectCode("033"));
		return  mv;
	}
	
	
	@GetMapping("/movInfo")
	public ModelAndView geMovInfo(Model model) throws Exception {
		DatasetVo vo = new DatasetVo();
		return new ModelAndView("dataset/imgMovInfo","imgMov",vo);
	}
	
	@Page
	@RequestMapping("/movFileList")
	public @ResponseBody Map<String, Object> getFileList(@RequestBody Map<String, Object> param, HttpServletRequest req)
			throws Exception {
		Map<String, Object> rtnMap = new HashMap<>();
		List<Map<String, Object>> list = movService.getFileList(param);
		rtnMap.put("list", list);
		if(list.size() > 0) {
			for(Map<String,Object> itemMap : list) {
				if(itemMap.containsKey("fileType") && String.valueOf(itemMap.get("fileType")).indexOf("text") == -1) {
					rtnMap.put("appendData", itemMap);
					break;
				}
			}
			
		}
		
		return rtnMap;
	}
	
	
	
	@PostMapping("/movSave")
	public @ResponseBody  Map<String,String> save(@RequestBody @Validated DatasetVo param,Errors errors) throws Exception {
		Map<String,String> rtnMap = new HashMap<>();
		try {
			if (errors.hasErrors()) {
				// 유효성 통과 못한 필드와 메시지를 핸들링
				this.validateHandling(rtnMap,errors);
				return rtnMap;
			}
			rtnMap.put("success_msg", messageSource.getMessage("msg.success"));
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
