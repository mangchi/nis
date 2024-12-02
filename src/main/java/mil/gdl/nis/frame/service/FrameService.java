
package mil.gdl.nis.frame.service;

import java.util.List;
import java.util.Map;

import mil.gdl.nis.dataset.vo.DatasetVo;

public interface FrameService {
	
	List<Map<String,Object>> getFrameList(Map<String,Object> map) throws Exception;
	
	List<Map<String,Object>> getExtractList(Map<String,Object> map) throws Exception;
	
	List<Map<String,Object>> getExtractImageList(Map<String,Object> map) throws Exception;
	
	List<Map<String,Object>> getImageList(Map<String,Object> map) throws Exception;
	
	Map<String,Object> getFrameInfo(Map<String,Object> map) throws Exception;
	
	DatasetVo getFrame(Map<String,Object> map) throws Exception;
	
	int extractMov(Map<String,Object> map) throws Exception;
	
	int deleteFrame(Map<String,Object> map) throws Exception;
	
	int deleteImageByFrame(Map<String,Object> map) throws Exception;

}
