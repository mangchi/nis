package mil.gdl.nis.cls;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;
import mil.gdl.nis.annotation.Page;
import mil.gdl.nis.cls.service.ClsService;
import mil.gdl.nis.exception.BizException;

@RequiredArgsConstructor
@Controller
public class ClsController {
	private static final Logger log = LoggerFactory.getLogger(ClsController.class);
	private final ClsService clsService;
	private final MessageSourceAccessor messageSource;

	@GetMapping({ "/clsInfo" })
	public ModelAndView getClsInfo(HttpServletRequest req) throws Exception {
		return new ModelAndView("cls/clsMng");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping({ "/clsInfo" })
	@ResponseBody
	public Map<String, Object> getClsInfo(@RequestBody Map<String, Object> param) throws Exception {
		Map<String,Object> rtnMap = new HashMap();
		rtnMap.put("list", this.clsService.getClsInfo(param));
		return rtnMap;
	}
	
	@Page
	@PostMapping("/learningList")
	public  @ResponseBody Map<String,Object> getLearningList(@RequestBody Map<String,Object> param,HttpServletRequest req) throws Exception {
        Map<String,Object> rtnMap = new HashMap<>();
        List<Map<String,Object>> list = clsService.getLearningList(param);
        rtnMap.put("list", list);
		return rtnMap;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping({ "/saveClass" })
	@ResponseBody
	public Map<String, Object> saveClass(@RequestBody Map<String, Object> param) throws Exception {
		Map<String,Object> rtnMap = new HashMap();
		try {
			int rtn = this.clsService.saveClass(param);
			if (rtn > 0)
				rtnMap.put("success_msg", this.messageSource.getMessage("msg.success"));
			else
				rtnMap.put("fail_msg", this.messageSource.getMessage("msg.fail"));
		} catch (Exception e) {
			log.error("error:", e);
			rtnMap.put("fail_msg", this.messageSource.getMessage("msg.fail"));
			return rtnMap;
		}
		return rtnMap;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping({ "/callLearning" })
	@ResponseBody
	public Map<String, Object> callLearning(@RequestBody Map<String, Object> param) throws Exception {
		Map<String,Object> rtnMap = new HashMap();
		try {
			int rtn = this.clsService.callLearning(param);
			if (rtn > 0)
				rtnMap.put("success_msg", this.messageSource.getMessage("msg.success"));
			else
				rtnMap.put("fail_msg", this.messageSource.getMessage("msg.fail"));
		} catch (Exception e) {
			log.error("error:", e);
			if(e instanceof BizException){
				
				rtnMap.put("fail_msg", this.messageSource.getMessage(e.getMessage()));
			}else {
				rtnMap.put("fail_msg", this.messageSource.getMessage("msg.fail"));
			}
			
			return rtnMap;
		}
		return rtnMap;
	}

}