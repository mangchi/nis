package mil.gdl.nis.cmmn.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mil.gdl.nis.cmmn.SessionData;
import mil.gdl.nis.cmmn.util.StringUtil;
import mil.gdl.nis.cmmn.util.Uid;
import mil.gdl.nis.cmmn.util.XmlUtil;
import mil.gdl.nis.cmmn.vo.CodeVo;
import mil.gdl.nis.dao.DAO;

@Slf4j
@RequiredArgsConstructor
@Service
public class CmmnServiceImpl implements CmmnService{
    
	private final DAO dao;
	private final CacheService cacheService;
	private final SessionData sessionData;
	@Value("${code.codeKey}")
    private String codeKey;
	
	@Value("${spring.servlet.multipart.location}")
	private String uploadPath;
	

	@Override
	@CacheEvict(value = "codeCacheData", allEntries = true)
	public CodeVo saveCode(CodeVo vo) {
		dao.update("Cmmn.mergeCode",vo);
		return vo;
	}

	@Override
	public List<CodeVo> getSelectCode(String grpCd) {
		List<CodeVo> codeList = cacheService.getCodeCacheData(codeKey);
		if(grpCd == null || grpCd.equals("")) {
			return codeList;
		}
		return codeList.stream().filter(c -> grpCd.equals(c.getGrpCd())).collect(Collectors.toList());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getWeaponList(Map<String, Object> map) {
		return (List<Map<String, Object>>)dao.selectList("Cmmn.selectWeaponList", map);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getWeaponAllList(Map<String, Object> map) {
		return (List<Map<String, Object>>)dao.selectList("Cmmn.selectWeaponAllList", map);
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getWeaponCntList(Map<String, Object> map) {
		return ((List<Map<String, Object>>)dao.selectList("Cmmn.selectWeaponCnt", map));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getClsWeaponList(Map<String, Object> map) {
		return (List<Map<String, Object>>)dao.selectList("Cmmn.selectClsWeaponList", map);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<Map<String, Object>> getUnitInfo(Map<String, Object> map) throws Exception {
		/*
		if(!map.containsKey("schUnitId")) {
			if(SessionData.getUserVo() == null) {
				map.put("schUnitId", "0");
			}
			else {
				map.put("schUnitId", SessionData.getUserVo().getUnitId());
			}
			
		}
		*/
		List<Map<String,Object>> list = (List<Map<String,Object>>)dao.selectList("Cmmn.selectUnit", map);
		if(list == null || list.size() == 0) {
			throw new Exception("부대정보가 없습니다.");
		}
		int size = list.size();
		//if(size > 0 && !list.get(size - 1).get("id").equals("0")) {
		if(sessionData.getUserVo() != null) {
			Map<String,Object> headMap = new HashMap<String,Object>();
			headMap.put("id", list.get(size-1).get("upperId"));
			headMap.put("upperId", "");
			headMap.put("classNm", "부대정보");
			list.add(headMap);
		}
		
		List<Map<String,Object>> toList = new ArrayList();
		for(int i=list.size()-1;i>=0;i--) {
			if(!list.get(i).containsKey("upperId") || list.get(i).get("upperId").equals("")) {
				toList.add(list.get(i));
				list.remove(i);
			}	
		}
		toList = toJsonMap(list,toList);
		return toList;
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<Map<String,Object>> toJsonMap(List<Map<String,Object>> fromList,List<Map<String,Object>> toList){
		List<Map<String,Object>> childList = null;
			for(Map<String,Object> toMap:toList) {
			childList = new ArrayList();
			for(int i=fromList.size()-1;i>=0;i--) {
				if(toMap.get("id").equals(fromList.get(i).get("upperId"))) {
					childList.add(fromList.get(i));
					fromList.remove(i);
				}					
			}
			if(childList.size() > 0) {
				toMap.put("children", childList);
			}
			
			if(fromList.size() > 0) {
				toJsonMap(fromList,childList);
			}
		}
		
		return toList;
	}

	

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getAisList(Map<String, Object> map) throws Exception{
		return (List<Map<String, Object>>)dao.selectPage("Cmmn.selectAisList", map);
	}
	
	
	
	@Override
	public Map<String, Object> getWeaponInfo(Map<String, Object> map) throws Exception {
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getObjectWeaponList(Map<String, Object> map) throws Exception {
		return (List<Map<String, Object>>)dao.selectList("Cmmn.selectObjectWeponList", map);
	}


	@Override
	public int saveAis(Map<String, Object> map) throws Exception {
		if(StringUtil.isEmpty((String)map.get("id"))) {
			map.put("id", Uid.makeLongUUID());
		}
		map.put("createUserId",sessionData.getUserVo().getUserId());
		map.put("updateUserId",sessionData.getUserVo().getUserId());
		return dao.update("Cmmn.mergeAis", map);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getAisInfo(Map<String, Object> map) throws Exception {
		return (Map<String, Object>)dao.selectObject("Cmmn.selectAis", map);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getClassInfo(Map<String, Object> map) {
		return (List<Map<String, Object>>)dao.selectList("Cmmn.selectClassInfo", map);
	}

	@Override
	public int deleteAis(Map<String, Object> map) throws Exception {
		int rtn = 0;
		rtn += dao.delete("Cmmn.deleteAis", map);
		rtn += dao.delete("Cmmn.deleteAisObject", map);
		return rtn;
	}

	@Override
	public int saveWeapon(Map<String, Object> map) throws Exception {
		if(StringUtil.isEmpty((String)map.get("weaponId"))) {
			map.put("weaponId", Uid.makeLongUUID());
		}
		map.put("createUserId",sessionData.getUserVo().getUserId());
		map.put("updateUserId",sessionData.getUserVo().getUserId());
		return dao.update("Cmmn.mergeWeapon", map);
	}

	@Override
	public int deleteWeapon(Map<String, Object> map) throws Exception {
		int rtn = dao.delete("Cmmn.deleteWeapon", map);
		rtn += dao.delete("Cmmn.deleteWeaponMapping", map);
		return rtn;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int saveAisFile(Map<String, Object> map) throws Exception {
		int rtn = 0;
		map.put("seq", 1);
		map.put("id", map.get("fileId"));
		List<Map<String,Object>> fileList = (List<Map<String,Object>>)dao.selectList("File.selectFile", map);
		if(fileList.size() > 0) {
			for(Map<String,Object> fileMap:fileList) {
				List<Map<String,Object>> aisList = (List<Map<String,Object>>)XmlUtil
						.getAisXmlData(uploadPath+fileMap.get("filePath")+File.separator+fileMap.get("fileNm")).get("objectList");
				for(Map<String,Object> aisMap:aisList) {
					aisMap.put("id", Uid.makeLongUUID());
					aisMap.put("createUserId",sessionData.getUserVo().getUserId());
					aisMap.put("updateUserId",sessionData.getUserVo().getUserId());
					log.debug("aisMap:{}",aisMap);
					rtn += dao.update("Cmmn.mergeAis", aisMap);
				}
			}
		}
		return rtn;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public int saveXmlFile(Map<String, Object> map) throws Exception {
		log.debug("saveXml map:{}",map);
		int rtn = 0;
		map.put("seq", 1);
		map.put("id", map.get("fileId"));
	    Map<String,Object> fileMap = (Map<String,Object>)dao.selectObject("File.selectFile", map);
		log.debug("fileMap:{}",fileMap);
		Map<String,Object> objectMap = XmlUtil.getObjectXmlData(uploadPath+fileMap.get("filePath")+File.separator+fileMap.get("fileNm"));
		log.debug("objectMap:{}",objectMap);
		List<Map<String,Object>> objectList = (List<Map<String,Object>>)objectMap.get("objectList");
		for(Map<String,Object> objectItem:objectList) {
			log.debug("objectItem:{}",objectItem);
			String classId  = (String)dao.selectOne("Cmmn.SelectClasIdByName", objectItem);
			if(classId == null) {
				objectItem.put("id", Uid.makeLongUUID());   ////TB_CLASS_INFO id 생성
				objectItem.put("classInfoId", objectItem.get("id"));
				objectItem.put("upperId", "0");
				rtn += dao.update("Cls.insertClassByXml",objectItem);
			}else {
				objectItem.put("classInfoId", classId);
			}

		}
		for(Map<String,Object> objectItem:objectList) {
			objectItem.put("id", Uid.makeLongUUID());  //TB_OBJECT_INFO id 생성
		}
		Map<String,Object> xmlMap = new HashMap();
		xmlMap.put("imageId", map.get("imageId"));
		xmlMap.put("createUserId",sessionData.getUserVo().getUserId());
		xmlMap.put("objectList", objectList);
		log.debug("xmlMap:{}",xmlMap);
		rtn += dao.update("Label.deleteObject",xmlMap);
		rtn += dao.update("Cmmn.insertObjectInfo",xmlMap);

		return rtn;
	}


	
	

	

	

	

	




}
