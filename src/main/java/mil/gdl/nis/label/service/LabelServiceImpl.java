package mil.gdl.nis.label.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mil.gdl.nis.cmmn.SessionData;
import mil.gdl.nis.cmmn.util.PythonUtil;
import mil.gdl.nis.cmmn.util.Uid;
import mil.gdl.nis.dao.DAO;
import mil.gdl.nis.dataset.vo.DatasetVo;

@Slf4j
@RequiredArgsConstructor
@Service
public class LabelServiceImpl implements LabelService{
	
	private final PythonUtil pythonUtil;
	private final SessionData sessionData;
    private final DAO dao;

	@Override
	public Map<String,Object> getLabelInfo(Map<String, Object> map) throws Exception {
		return dao.selectMap("Label.selectLabel", map).orElseGet(() -> new HashMap<String, Object>());
	}


	@SuppressWarnings({ "unchecked"})
	@Override
	public int save(Map<String, Object> map) throws Exception {
		int rtn = 0;
		List<Map<String,Object>> objectList = (List<Map<String,Object>>)map.get("objectList");
		for(Map<String,Object> objectItem:objectList) {
			objectItem.put("id", Uid.makeLongUUID());
		}
		//List<Map<String,Object>> weaponList = (List<Map<String,Object>>)map.get("weaponList");
		map.put("createUserId",sessionData.getUserVo().getUserId());
		map.put("updateUserId",sessionData.getUserVo().getUserId());
		dao.delete("Label.deleteObject", map);
		
		rtn += dao.update("Label.updateImgLabel", map);
		rtn += dao.update("Cmmn.insertObjectInfo", map);
		if(map.containsKey("weaponList")) {
			List<Map<String,Object>> weaponList = (List<Map<String,Object>>)map.get("weaponList");
			if(weaponList.size() > 0) {
				dao.delete("Label.deleteClassWeapon", map);
				rtn += dao.update("Label.insertClassWeapon", map);
			}
		}
		
		return rtn;
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getLabelList(Map<String, Object> map) throws Exception {
		return (List<Map<String, Object>>)dao.selectPage("Label.selectList",map);
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<DatasetVo> getLabelVoList(Map<String, Object> map) throws Exception {
		return (List<DatasetVo>)dao.selectList("Label.selectVoList",map);
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getLabelImgList(Map<String, Object> map) throws Exception {
		return (List<Map<String, Object>>)dao.selectList("Label.selectImgList",map);
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getObjectList(Map<String, Object> map) throws Exception {
		return (List<Map<String, Object>>)dao.selectList("Label.selectObjectList", map);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getClassWeaponList(Map<String, Object> map) throws Exception {
		return (List<Map<String, Object>>)dao.selectList("Label.selectClassWeaponList", map);
	}

	
	@Override
	public List<Map<String, Object>> callAi(Map<String, Object> map) throws Exception {
		return pythonUtil.calllObjectList(map);
	}



}
