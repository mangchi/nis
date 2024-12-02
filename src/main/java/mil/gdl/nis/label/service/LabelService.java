
package mil.gdl.nis.label.service;

import java.util.List;
import java.util.Map;

import mil.gdl.nis.dataset.vo.DatasetVo;

public interface LabelService {
	
	List<Map<String,Object>> getLabelList(Map<String,Object> map) throws Exception;
	
	List<Map<String,Object>> getLabelImgList(Map<String,Object> map) throws Exception;
	
	List<DatasetVo> getLabelVoList(Map<String,Object> map) throws Exception;
	
	List<Map<String,Object>> getObjectList(Map<String,Object> map) throws Exception;
	
	List<Map<String,Object>> callAi(Map<String,Object> map) throws Exception;
	
	List<Map<String, Object>> getClassWeaponList(Map<String, Object> map) throws Exception;
	
	Map<String,Object> getLabelInfo(Map<String,Object> map) throws Exception;

	
	int save(Map<String, Object> map) throws Exception;

}
