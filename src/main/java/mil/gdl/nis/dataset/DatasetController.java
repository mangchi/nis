package mil.gdl.nis.dataset;

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
import mil.gdl.nis.annotation.Trnc;
import mil.gdl.nis.cmmn.service.CmmnService;
import mil.gdl.nis.cmmn.service.FileStorageService;
import mil.gdl.nis.cmmn.vo.PageInfo;
import mil.gdl.nis.dataset.service.DatasetService;
import mil.gdl.nis.dataset.vo.DataValidGroups.AirValid;
import mil.gdl.nis.dataset.vo.DataValidGroups.BaseValid;
import mil.gdl.nis.dataset.vo.DatasetVo;;

@Slf4j
@RequiredArgsConstructor
@Controller
public class DatasetController {
	
	private final DatasetService datasetService;
	private final CmmnService cmmnService;
	private final FileStorageService fileStorageService;

	private final MessageSourceAccessor messageSource;

	@Value("${spring.profiles.active}") 
	private String activeProfile;
	
	@GetMapping("/dataList")
	public  String getDataList(HttpServletRequest req) throws Exception {
		return "dataset/dataList";
	}
	
	@Page
	@RequestMapping("/dataList")
	public @ResponseBody Map<String,Object> getDataList(@RequestBody Map<String,Object> param,HttpServletRequest req) throws Exception {
        Map<String,Object> rtnMap = new HashMap<>();
        List<Map<String,Object>> list = datasetService.getDataList(param);
        Map<String,Object> appendMap = new HashMap<String,Object>();
        PageInfo pageInfo = (PageInfo)param.get("pageInfo");
        appendMap.put("totalCount", pageInfo.getTotalCount());
        rtnMap.put("appendData", appendMap);
        rtnMap.put("list", list);
		return rtnMap;

	}

	
	@GetMapping("/datasetList")
	public  String getDatasetList(HttpServletRequest req) throws Exception {
		return "dataset/datasetList";
	}
	
	@PostMapping("/dataDetail")
	public  ModelAndView getdataDetail(@RequestBody Map<String,Object> param,HttpServletRequest req) throws Exception {

		ModelAndView mv = new ModelAndView("dataset/dataDetail","dataSet",datasetService.getDataset(param));
		mv.addObject("collectionDeviceCds", cmmnService.getSelectCode("006"));
		mv.addObject("sourceCds", cmmnService.getSelectCode("008"));
		mv.addObject("dataCds", cmmnService.getSelectCode("010"));
		mv.addObject("nationCds", cmmnService.getSelectCode("011"));
		mv.addObject("dataTypes", cmmnService.getSelectCode("033"));
		mv.addObject("shipClasss", cmmnService.getSelectCode("033"));
		return  mv;
		
	}
	
	
	@Page
	@RequestMapping("/datasetList")
	public @ResponseBody Map<String,Object> getDatasetList(@RequestBody Map<String,Object> param,HttpServletRequest req) throws Exception {
        Map<String,Object> rtnMap = new HashMap<>();
        List<Map<String,Object>> list = datasetService.getDatasetList(param);
        Map<String,Object> appendMap = new HashMap<String,Object>();
        PageInfo pageInfo = (PageInfo)param.get("pageInfo");
        appendMap.put("totalCount", pageInfo.getTotalCount());
        rtnMap.put("appendData", appendMap);
        rtnMap.put("list", list);
		return rtnMap;

	}
	
	@PostMapping("/deleteDataset")
	public @ResponseBody Map<String,Object> deleteDataset(@RequestBody Map<String,Object> param,HttpServletRequest req) throws Exception {
        Map<String,Object> rtnMap = new HashMap<>();
        if(datasetService.delete(param) > 0) {
        	rtnMap.put("success_msg", messageSource.getMessage("msg.success"));
        }
        else {
        	rtnMap.put("fail_msg", messageSource.getMessage("msg.fail"));
        }
		return rtnMap;
 
	}
	
	@Page
	@PostMapping("/datasetMng")
	public  @ResponseBody Map<String,Object> getDatasetMng(@RequestBody Map<String,Object> param) throws Exception {
		Map<String,Object> rtnMap = new HashMap<>();
	    List<Map<String,Object>> list = datasetService.getDataList(param);
        Map<String,Object> appendMap = new HashMap<String,Object>();
        PageInfo pageInfo = (PageInfo)param.get("pageInfo");
        appendMap.put("totalCount", pageInfo.getTotalCount());
        rtnMap.put("appendData", appendMap);
	    rtnMap.put("list", list);
	    return rtnMap;
	}
	
	
	@GetMapping("/datasetMng")
	public String getDatasetMng(Model model) throws Exception {
		return "dataset/datasetMng";
	}
	
	@GetMapping("/datasetInfo")
	public String getDatasetInfo(Model model) throws Exception {
		return "dataset/datasetInfo";
	}

	@PostMapping("/datasetInfo")
	public @ResponseBody Map<String,Object>  getDatasetInfo(@RequestBody Map<String,Object> param) throws Exception {
		Map<String,Object> rtnMap = new HashMap<>();
		rtnMap.put("info", datasetService.getDatasetInfo(param));
		rtnMap.put("fileList", fileStorageService.selectList(String.valueOf(param.get("id"))));

		return  rtnMap;
	}
	
	@Trnc
	@PostMapping("/datasetSave")
	public @ResponseBody  Map<String,String> save(@RequestBody @Validated(BaseValid.class) DatasetVo param,Errors errors) throws Exception {
		Map<String,String> rtnMap = new HashMap<>();
		try {
			if (errors.hasErrors()) {
				// 유효성 통과 못한 필드와 메시지를 핸들링
				this.validateHandling(rtnMap,errors);
				return rtnMap;
			}
			datasetService.save(param);
			rtnMap.put("success_msg", messageSource.getMessage("msg.success"));
		}catch(Exception e) {
			log.error("error:", e );
			rtnMap.put("fail_msg", messageSource.getMessage("msg.fail"));
			return rtnMap;
		}
		return rtnMap;
	}
	
	@Trnc
	@PostMapping("/datasetSaveAir")
	public @ResponseBody  Map<String,String> saveAir(@RequestBody @Validated(AirValid.class) DatasetVo param,Errors errors) throws Exception {
		Map<String,String> rtnMap = new HashMap<>();
		try {
			if (errors.hasErrors()) {
				// 유효성 통과 못한 필드와 메시지를 핸들링
				this.validateHandling(rtnMap,errors);
				return rtnMap;
			}
			param.setStillOrMovie("002"); //항공영상
			datasetService.save(param);
			rtnMap.put("success_msg", messageSource.getMessage("msg.success"));
		}catch(Exception e) {
			log.error("error:", e );
			rtnMap.put("fail_msg", messageSource.getMessage("msg.fail"));
			return rtnMap;
		}
		return rtnMap;
	}
	
	@Trnc
	@PostMapping("/datasetDelete")
	public  @ResponseBody Map<String,Object> datasetDelete(@RequestBody Map<String,Object> param,HttpServletRequest req) throws Exception {
        Map<String,Object> rtnMap = new HashMap<>();
        if(datasetService.delete(param)> 0) {
        	rtnMap.put("success_msg", messageSource.getMessage("msg.success"));
        }
        else {
        	rtnMap.put("fail_msg", messageSource.getMessage("msg.fail"));
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
