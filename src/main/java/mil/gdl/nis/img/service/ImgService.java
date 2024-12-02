
package mil.gdl.nis.img.service;

import java.util.List;
import java.util.Map;

import mil.gdl.nis.dataset.vo.DatasetVo;

public interface ImgService {
	
	List<Map<String,Object>> getImgList(Map<String,Object> map) throws Exception;
	
	Map<String,Object> getImgInfo(Map<String,Object> map) throws Exception;
	
	List<Map<String,Object>> getObjecList(Map<String, Object> map) throws Exception;
	
	DatasetVo getImg(Map<String,Object> map) throws Exception;
	
	List<Map<String,Object>> getFileList(Map<String,Object> map) throws Exception;
	
	int approve(Map<String,Object> map) throws Exception;
	
	int save(Map<String,Object> map) throws Exception;
	
	int deleteImgs(Map<String,Object> map) throws Exception;

}
