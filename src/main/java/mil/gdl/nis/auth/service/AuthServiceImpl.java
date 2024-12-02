package mil.gdl.nis.auth.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mil.gdl.nis.cmmn.SessionData;
import mil.gdl.nis.cmmn.util.Uid;
import mil.gdl.nis.dao.DAO;
import mil.gdl.nis.user.vo.Authority;
import mil.gdl.nis.user.vo.UserVo;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService{
	

    private final SessionData sessionData;
    private final DAO dao;

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getEquipAuthList(Map<String, Object> map) throws Exception {
		return (List<Map<String, Object>>)dao.selectPage("Auth.selectEquipAuthList", map);
	}
    
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getAuthViewList(Map<String, Object> map) throws Exception {
		return (List<Map<String, Object>>)dao.selectList("Auth.selectAuthViewList", map);
	}
	
	@Override
	public String getAuthLogin(UserVo vo) throws Exception {
		return (String)dao.selectOne("Auth.selectAuthLogin", vo);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public int saveAuthView(Map<String, Object> map) throws Exception {
		int rtn = 0;
		Set<String> keySet = map.keySet();
		Map<String,Object> paramMap = null;
		String myRole = sessionData.getUserVo().getAuthority().name();
    	for(String key:keySet) {
    		log.debug("key:{}",key);
    		if(map.get(key) instanceof List) {
    			List<Map<String,Object>> list = (List<Map<String,Object>>)map.get(key);
        		paramMap = new HashMap<String,Object>();
        		for(Map<String,Object> m:list) {
    	    		switch(key){
    	    		case "adminIds": m.put("id", Authority.ROLE_ADMIN.name());
    	    		                 if(!paramMap.containsKey("id")){
    	    		                	 paramMap.put("id",Authority.ROLE_ADMIN.name());
    	    		                 }
    	    		                 break;
    	    		case "managerIds": m.put("id", Authority.ROLE_MANAGER.name());
    	    			               if(!paramMap.containsKey("id")){
    	    			            	   paramMap.put("id",Authority.ROLE_MANAGER.name());
    		    		               }
    	    			               break;
    	    		case "userIds" : m.put("id", Authority.ROLE_USER.name());
    	    		                 if(!paramMap.containsKey("id")){
    	    		                	 paramMap.put("id",Authority.ROLE_USER.name());
    	    		                 }
    	    		                 break;
    	    		default : throw new Exception("key is not defined");
    	    		}
        		}
        		paramMap.put("list", list);
        		paramMap.put("role", sessionData.getUserVo().getAuthority().name());
        		Map<String,Object> unitMap = new HashMap();
        		log.debug("viewUnitId:{}",map.get("viewUnitId"));
        		if(map.get("viewUnitId").equals("All") || map.get("viewUnitId").equals("")) {
        			unitMap.put("schUnitId", "01");
				}
        		else {
        			unitMap.put("schUnitId", map.get("viewUnitId"));
        		}
        		List<Map<String,Object>> unitList = (List<Map<String,Object>>)dao.selectList("Cmmn.selectUnit",unitMap);
        		if(myRole.equals(Authority.ROLE_ADMIN.name())) {
        			if(key.equals("adminIds")) {
        				paramMap.put("unitId", "");
        				rtn += dao.update("Auth.deleteAuthView", paramMap);
                		rtn += dao.update("Auth.saveAuthView", paramMap);
        			}
        			else {
        				for(Map<String,Object> unit:unitList) {
        					paramMap.put("unitId", unit.get("id"));
        					rtn += dao.update("Auth.deleteAuthView", paramMap);
        					rtn += dao.update("Auth.saveAuthView", paramMap);
        				}
        			}
        			
        		}
        		else if(myRole.equals(Authority.ROLE_MANAGER.name())) {
        			if(!key.equals("adminIds")) {
        				for(Map<String,Object> unit:unitList) {
        					paramMap.put("unitId", unit.get("id"));
        					rtn += dao.update("Auth.deleteAuthView", paramMap);
        					rtn += dao.update("Auth.saveAuthView", paramMap);
        				}
        			}
        		}
        		
        	}
    	}
    		
    	
		return rtn;
	}



	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public int insertEquip(UserVo vo) throws Exception {
		Map<String,Object> map = new HashMap();
		map.put("id", Uid.makeLongUUID());
		map.put("userId", vo.getUserId());
		map.put("ipAddr", vo.getIpAddr());
		map.put("blockYn", "Y");
		return dao.update("Auth.insertEquip",map);
	}

	@Override
	public int updateEquip(Map<String, Object> map) throws Exception {
		map.put("userId", sessionData.getUserVo().getUserId());
		return dao.update("Auth.updateEquip",map);
	}

	
	


}
