
package mil.gdl.nis.movImg.service;

import java.util.List;
import java.util.Map;

import mil.gdl.nis.movImg.vo.MovImgVo;

public interface MovImgService {
	
	List<Map<String,Object>> getMovImgList(Map<String,Object> map) throws Exception;
	
	Map<String,Object> getMovImgInfo(Map<String,Object> map) throws Exception;
	
	MovImgVo getMovImg(Map<String,Object> map) throws Exception;

}
