
package mil.gdl.nis.system.service;

import java.util.List;
import java.util.Map;


public interface SystemService {
	
	 List<Map<String,Object>> getDbList(Map<String,Object> map) throws Exception;

}
