
package mil.gdl.nis.log.service;

import java.util.List;
import java.util.Map;


public interface LogService {
	
	 List<Map<String,Object>> getList(Map<String,Object> map) throws Exception;
	 
	 int insertLog(Map<String,Object> map) throws Exception;
	 
	 int insertUpDownLog(Map<String,Object> map) throws Exception;
	 

}
