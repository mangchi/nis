package mil.gdl.nis.sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mil.gdl.nis.cmmn.util.JsonUtil;
import mil.gdl.nis.sample.service.SampleService;


@Slf4j
@RequiredArgsConstructor
//@RestController
@Controller
@RequestMapping(value = "/",method = {RequestMethod.POST, RequestMethod.GET})
public class SampleController {
	
	

	private SampleService sampleService;
	private final MessageSourceAccessor messageSource; 
	
	@Value("${spring.profiles.active}") 
	private String activeProfile;
	

	/*
	 * @PostMapping("sample") public String getSampleList(@RequestBody Map<String,
	 * Object> paramMap,HttpServletRequest req) throws Exception {
	 * log.debug("getSampleList paramMap:{}",paramMap); Gson gson = new Gson();
	 * List<Map<String,Object>> list = sampleService.getSampleList(null); String
	 * jsonStr = gson.toJson(list); return jsonStr;
	 * 
	 * }
	 */
	
	@RequestMapping("sample")
	public String getUser(HttpServletRequest req) throws Exception {
		log.debug("activeProfile:{}",activeProfile);
		log.debug("getUser req:{}",req);
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("id", "1");
		return JsonUtil.toGsonStr(sampleService.getUser(param));

	}
	
	
	
	@GetMapping("sampleMsg")
	@ResponseBody
	public ArrayList<String> test(Locale locale) {
		String[] replaceValuesKr = new String[] { "강동호", "개발자" };
		String[] replaceValuesEn = new String[] { "DonghoKang", "developer" };
		ArrayList<String> msgs = new ArrayList<>();
		msgs.add(messageSource.getMessage("hello"));
		msgs.add(messageSource.getMessage("intro", replaceValuesKr));
		msgs.add(messageSource.getMessage("hello", Locale.ENGLISH));
		msgs.add(messageSource.getMessage("intro", replaceValuesEn, Locale.ENGLISH));

		return msgs;
	}
	
	
	@GetMapping("index")
    public String main(Model model) {
        return "index";
    }
	
	
	@GetMapping("modal1")
    public String modal1() {
        return "modal1";
    }

    @GetMapping("modal2")
    public String modal2(@RequestParam("name") String name, Model model) {
        model.addAttribute("name", name);
        return "modal2";
    }
	
	

}
