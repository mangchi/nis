
package mil.gdl.nis.work.service;

import java.util.List;
import java.util.Map;


public interface WorkService {
	
	List<Map<String,Object>> getWorkList(Map<String,Object> map) throws Exception;
	
	List<Map<String,Object>> getWorkLogList(Map<String,Object> map) throws Exception;
	
	Map<String,Object> getWorkInfo(Map<String,Object> map) throws Exception;
	
	int saveWork(Map<String,Object> map) throws Exception;
	
	int deleteWork(Map<String,Object> map) throws Exception;
}
