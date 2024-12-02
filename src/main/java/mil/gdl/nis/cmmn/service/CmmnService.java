package mil.gdl.nis.cmmn.service;

import java.util.List;
import java.util.Map;

import mil.gdl.nis.cmmn.vo.CodeVo;

public interface CmmnService {
	

	
	CodeVo saveCode(CodeVo vo);
	
	List<CodeVo> getSelectCode(String grpCd);
	
	List<Map<String,Object>> getWeaponList(Map<String, Object> map);
	
	List<Map<String,Object>> getWeaponCntList(Map<String, Object> map);
	
	List<Map<String,Object>> getClassInfo(Map<String, Object> map);
	
	List<Map<String,Object>> getClsWeaponList(Map<String, Object> map);
	
	List<Map<String,Object>> getWeaponAllList(Map<String, Object> map);

	List<Map<String,Object>> getObjectWeaponList(Map<String,Object> map) throws Exception;
	
	List<Map<String,Object>> getAisList(Map<String,Object> map) throws Exception;
	
	List<Map<String,Object>> getUnitInfo(Map<String,Object> map) throws Exception;
	
	
	Map<String,Object> getAisInfo(Map<String,Object> map) throws Exception;
	
	Map<String,Object> getWeaponInfo(Map<String,Object> map) throws Exception;

	
	int saveAis(Map<String,Object> map) throws Exception;
	
	int saveAisFile(Map<String,Object> map) throws Exception;
	
	int saveXmlFile(Map<String,Object> map) throws Exception;
	
	int deleteAis(Map<String,Object> map) throws Exception;
	
    int saveWeapon(Map<String,Object> map) throws Exception;
	
	int deleteWeapon(Map<String,Object> map) throws Exception;

}
 