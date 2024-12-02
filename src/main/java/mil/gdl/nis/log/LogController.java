package mil.gdl.nis.log;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;
import mil.gdl.nis.annotation.Page;
import mil.gdl.nis.log.service.LogService;

@RequiredArgsConstructor
@Controller
public class LogController {

	private final LogService logService;

	@GetMapping("/logList")
	public ModelAndView getLogList(HttpServletRequest req) throws Exception {
		return new ModelAndView("log/logList");
	}
	
    @Page
	@PostMapping("/logList")
	public @ResponseBody Map<String, Object> getLogList(@RequestBody Map<String, Object> param, HttpServletRequest req)
			throws Exception {
		Map<String, Object> rtnMap = new HashMap<>();
		rtnMap.put("list", logService.getList(param));
		return rtnMap;
	}

}
