
package mil.gdl.nis.mov.service;

import java.util.List;
import java.util.Map;

import mil.gdl.nis.dataset.vo.DatasetVo;

public interface MovService {
	
	List<Map<String,Object>> getMovList(Map<String,Object> map) throws Exception;
	
	Map<String,Object> getMovInfo(Map<String,Object> map) throws Exception;
	
	DatasetVo getMov(Map<String,Object> map) throws Exception;
	
	List<Map<String,Object>> getFileList(Map<String,Object> map) throws Exception;
	
	//int save(DatasetVo vo) throws Exception;
	
	int save(Map<String,Object> map) throws Exception;

}
