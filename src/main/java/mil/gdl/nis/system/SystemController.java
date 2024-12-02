package mil.gdl.nis.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mil.gdl.nis.system.service.SystemService;

@Slf4j
@RequiredArgsConstructor
@Controller
public class SystemController {
	
	

	private final SystemService systemService;
	
	@GetMapping("/dbList")
	public  String getDbList(HttpServletRequest req) throws Exception {

		return "system/dbMng";

	}
	
	@PostMapping("/dbList")
	public  @ResponseBody Map<String,Object> getDbList(@RequestBody Map<String,Object> param,HttpServletRequest req) throws Exception {
        Map<String,Object> rtnMap = new HashMap<>();
		log.debug("getDbList param:{}",param);
        List<Map<String,Object>> dbList = systemService.getDbList(param);
        rtnMap.put("dbList", dbList);
		return rtnMap;

	}
	
	
	
	
	

}
