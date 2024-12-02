package mil.gdl.nis.movImg;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mil.gdl.nis.movImg.service.MovImgService;
import mil.gdl.nis.movImg.vo.MovImgVo;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MovImgController {
	
	private final MovImgService movImgService;

	@Value("${spring.profiles.active}") 
	private String activeProfile;

	
	@RequestMapping("/movImgList")
	public  @ResponseBody Map<String,Object> getMovImgList(@RequestBody Map<String,Object> param,HttpServletRequest req) throws Exception {
        Map<String,Object> rtnMap = new HashMap<>();
		log.debug("getMovImgList param:{}",param);
        List<Map<String,Object>> list = movImgService.getMovImgList(param);
        rtnMap.put("list", list);
		return rtnMap;

	}
	
	/*
	@RequestMapping("movImgInfo")
	public @ResponseBody Map<String,Object> geMovImgInfo(@RequestBody Map<String,Object> param,HttpServletRequest req) throws Exception {
		Map<String,Object> rtnMap = new HashMap<>();
		log.debug("geMovImgInfo param:{}",param);
        rtnMap.put("map", movImgService.getMovImgInfo(param));
		return rtnMap;
	}
	
	*/

	@PostMapping("/movImgInfo")
	public ModelAndView geMovImgInfo(@RequestBody Map<String,Object> param) throws Exception {
		return new ModelAndView("movImg/movImgInfo","movImg",movImgService.getMovImg(param));
	}
	
	
	@GetMapping("/movImgInfo")
	public String geMovImgInfo(Model model) throws Exception {
		model.addAttribute("movImg", new MovImgVo());
		return "movImg/movImgInfo";
	}
	
	
	
	
	
	

}
