
package mil.gdl.nis.auth.service;

import java.util.List;
import java.util.Map;

import mil.gdl.nis.user.vo.UserVo;

public interface AuthService {
	
	List<Map<String,Object>> getEquipAuthList(Map<String,Object> map) throws Exception;
	
	List<Map<String, Object>> getAuthViewList(Map<String,Object> map) throws Exception;
	
	String getAuthLogin(UserVo vo) throws Exception;
	
	int saveAuthView(Map<String, Object> map) throws Exception;
	
	int insertEquip(UserVo vo) throws Exception;
	
	int updateEquip(Map<String,Object> map) throws Exception;

}
