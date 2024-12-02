package mil.gdl.nis.user.service;



import java.util.List;
import java.util.Map;

import mil.gdl.nis.user.vo.UserVo;

public interface UserService {
	
	
	int register(UserVo user) throws Exception;
	
	int modify(UserVo user) throws Exception;
	
	int changePwd(UserVo user) throws Exception;
	
	int initPwd(UserVo user) throws Exception;
	
	int checkUserId(String userId) throws Exception;
	
	int updatePwFailCnt(String userId) throws Exception;
	
	int deleteUser(Map<String,Object> map) throws Exception;
	
	int updateLoginData(UserVo user) throws Exception;
	
	UserVo getUser(UserVo user) throws Exception;
	
	List<UserVo> getLoginUser(Map<String, Object> map) throws Exception;
	
	List<Map<String,Object>> getUserList(Map<String,Object> map) throws Exception;

}
