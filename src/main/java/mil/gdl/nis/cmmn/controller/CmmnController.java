package mil.gdl.nis.cmmn.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mil.gdl.nis.annotation.Page;
import mil.gdl.nis.cmmn.service.CmmnService;
import mil.gdl.nis.user.vo.UserVo;

@Slf4j
@RequiredArgsConstructor
@Controller
public class CmmnController  {
    

    private final CmmnService cmmnService;
    private final MessageSourceAccessor messageSource;
	
	@GetMapping("/codes")
	public  @ResponseBody Map<String,Object> getList(HttpServletRequest req) throws Exception {
        Map<String,Object> codeMap = new HashMap<>();
        codeMap.put("codes", cmmnService.getSelectCode(null));
		return codeMap;

	}
	
	@GetMapping("/korMap")
	public String gerKorMap(Model model) throws Exception {
		return "popup/korMap";
	}
	
	
	@GetMapping("/aisList")
	public String getAisList(Model model) throws Exception {
		return "popup/aisInfo";
	}
	
	@Page
	@PostMapping("/aisList")
	public @ResponseBody Map<String,Object> getAisList(@RequestBody Map<String,Object> param) throws Exception {
		Map<String,Object> rtnMap = new HashMap<>();
		rtnMap.put("list", cmmnService.getAisList(param));
		return rtnMap;
	}
	
	@PostMapping("/aisInfo")
	public @ResponseBody Map<String,Object> getAisInfo(@RequestBody Map<String,Object> param) throws Exception {
		Map<String,Object> rtnMap = new HashMap<>();
		rtnMap.put("info", cmmnService.getAisInfo(param));
		return rtnMap;
	}
	
	@GetMapping("/aisInfoAdd")
	public String getAisInfoAdd(Model model) throws Exception {
		return "popup/aisInfoAdd";
	}
	
	@GetMapping("/classInfo")
	public @ResponseBody Map<String,Object> getClassInfo(@RequestParam Map<String,Object> param) throws Exception {
		Map<String,Object> rtnMap = new HashMap<>();
		rtnMap.put("list", cmmnService.getClassInfo(param));
		return rtnMap;
	}
	
	@GetMapping("/unitInfo")
	public @ResponseBody Map<String,Object> getUnitInfo(@RequestParam Map<String,Object> param) throws Exception {
		Map<String,Object> rtnMap = new HashMap<>();
		rtnMap.put("list", cmmnService.getUnitInfo(param));
		return rtnMap;
	}
	
	
	
	

	//public @ResponseBody  Map<String,String> save(@RequestBody @Validated ImgVo param,Errors errors) throws Exception {
	@PostMapping("/saveAis")
	public @ResponseBody  Map<String,String> saveAis(@RequestBody Map<String,Object> param,@AuthenticationPrincipal UserVo sessionUser) throws Exception {
		Map<String,String> rtnMap = new HashMap<>();
		try {
			cmmnService.saveAis(param);
			rtnMap.put("success_msg", messageSource.getMessage("msg.success"));
		}catch(Exception e) {
			log.error("error:", e );
			rtnMap.put("fail_msg", messageSource.getMessage("msg.fail"));
			return rtnMap;
		}
		return rtnMap;
	}
	
	@PostMapping("/saveAisFile")
	public @ResponseBody  Map<String,String> saveAisFile(@RequestBody Map<String,Object> param ) throws Exception {
		Map<String,String> rtnMap = new HashMap<>();
		try {
			cmmnService.saveAisFile(param);
			rtnMap.put("success_msg", messageSource.getMessage("msg.success"));
		}catch(Exception e) {
			log.error("error:", e );
			rtnMap.put("fail_msg", messageSource.getMessage("msg.fail"));
			return rtnMap;
		}
		return rtnMap;
	}
	
	@PostMapping("/saveXmlFile")
	public @ResponseBody  Map<String,String> saveXmlFile(@RequestBody Map<String,Object> param ) throws Exception {
		Map<String,String> rtnMap = new HashMap<>();
		try {
			cmmnService.saveXmlFile(param);
			rtnMap.put("success_msg", messageSource.getMessage("msg.success"));
		}catch(Exception e) {
			log.error("error:", e );
			rtnMap.put("fail_msg", messageSource.getMessage("msg.fail"));
			return rtnMap;
		}
		return rtnMap;
	}
	
	@PostMapping("/deleteAis")
	public @ResponseBody  Map<String,String> deleteAis(@RequestBody Map<String,Object> param) throws Exception {
		Map<String,String> rtnMap = new HashMap<>();
		try {
			int rtn = cmmnService.deleteAis(param);
			if(rtn > 0) {
				rtnMap.put("success_msg", messageSource.getMessage("msg.success"));
			}
			else {
				rtnMap.put("fail_msg", messageSource.getMessage("msg.fail"));
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
	
	@GetMapping("/weaponInfo")
	public String getWeaponInfo(Model model) throws Exception {
		return "popup/weaponInfo";
	}
	
	@PostMapping("/weaponInfo")
	public @ResponseBody Map<String,Object> getWeaponInfo(@RequestBody Map<String,Object> param) throws Exception {
		Map<String,Object> rtnMap = new HashMap<>();
		rtnMap.put("list", cmmnService.getClsWeaponList(param));
		rtnMap.put("weaponList", cmmnService.getWeaponAllList(param));
		return rtnMap;
	}
	
	@PostMapping("/weaponList")
	public @ResponseBody Map<String,Object> getWeaponList(@RequestBody Map<String,Object> param) throws Exception {
		Map<String,Object> rtnMap = new HashMap<>();
		rtnMap.put("list", cmmnService.getWeaponList(param));
		return rtnMap;
	}
	
	@GetMapping("/weaponCntList")
	public @ResponseBody Map<String,Object> getWeaponCntList(@RequestParam Map<String,Object> param) throws Exception {
		Map<String,Object> rtnMap = new HashMap<>();
		rtnMap.put("list", cmmnService.getWeaponCntList(param));
		return rtnMap;
	}
	
	@GetMapping("/weaponInfoAdd")
	public String getWeaponInfoAdd(Model model) throws Exception {
		return "popup/weaponInfoAdd";
	}
	
	@PostMapping("/saveWeapon")
	public @ResponseBody  Map<String,String> saveWeapon(@RequestBody Map<String,Object> param) throws Exception {
		Map<String,String> rtnMap = new HashMap<>();
		try {
			List<Map<String,Object>> list = cmmnService.getWeaponList(param);
			for(Map<String,Object> item:list) {
				if(item.get("weaponNm").equals(param.get("weaponNm"))) {
					rtnMap.put("fail_msg", messageSource.getMessage("error.duplicatedWeapon"));
					return rtnMap;
				}
			}
			cmmnService.saveWeapon(param);
			rtnMap.put("success_msg", messageSource.getMessage("msg.success"));
		}catch(Exception e) {
			log.error("error:", e );
			rtnMap.put("fail_msg", messageSource.getMessage("msg.fail"));
			return rtnMap;
		}
		return rtnMap;
	}
	
	@PostMapping("/deleteWeapon")
	public @ResponseBody  Map<String,String> deleteWeapon(@RequestBody Map<String,Object> param) throws Exception {
		Map<String,String> rtnMap = new HashMap<>();
		try {
			int rtn = cmmnService.deleteWeapon(param);
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
}
