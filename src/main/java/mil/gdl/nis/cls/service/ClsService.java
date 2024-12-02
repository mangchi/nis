
package mil.gdl.nis.cls.service;

import java.util.List;
import java.util.Map;


public interface ClsService {
	List<Map<String,Object>> getClsInfo(Map<String,Object> map) throws Exception;
	
	List<Map<String,Object>> getLearningList(Map<String,Object> map) throws Exception;
	
	int saveClass(Map<String,Object> map) throws Exception;
	 
	int callLearning(Map<String,Object> map) throws Exception;
	
	


}
