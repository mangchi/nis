
package mil.gdl.nis.bbs.service;

import java.util.List;
import java.util.Map;

import mil.gdl.nis.bbs.vo.BbsVo;


public interface BbsService {
	
	 List<Map<String,Object>> getList(Map<String,Object> map) throws Exception;
	 
	 BbsVo getBbsInfo(BbsVo vo) throws Exception;
	 
	 Map<String, Object> getBbsNotice(Map<String, Object> map) throws Exception;
	 
	 int bbsSave(BbsVo vo) throws Exception;
	 
	 int bbsReadSave(Map<String,Object> map) throws Exception;
	 
	 int bbsDelete(Map<String,Object> map) throws Exception;
	 
 
	 int getBbsReadCount(Map<String, Object> map) throws Exception;

}
