
package mil.gdl.nis.dataset.service;

import java.util.List;
import java.util.Map;

import mil.gdl.nis.dataset.vo.DatasetVo;

public interface DatasetService {
	
	List<Map<String,Object>> getDatasetList(Map<String,Object> map) throws Exception;
	
	List<Map<String,Object>> getDataList(Map<String,Object> map) throws Exception;
	
	Map<String,Object> getDatasetInfo(Map<String,Object> map) throws Exception;
	
	DatasetVo getDataset(Map<String,Object> map) throws Exception;
	
	int save(DatasetVo vo) throws Exception;
	
	int delete(Map<String,Object> map) throws Exception;
	
	

}
