package mil.gdl.nis.work.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mil.gdl.nis.cmmn.SessionData;
import mil.gdl.nis.cmmn.util.PythonUtil;
import mil.gdl.nis.cmmn.util.StringUtil;
import mil.gdl.nis.cmmn.util.Uid;
import mil.gdl.nis.dao.DAO;

@RequiredArgsConstructor
@Service
public class WorkServiceImpl implements WorkService{
	
	private final PythonUtil pythonUtil;
	private final SessionData sessionData;
    private final DAO dao;

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getWorkList(Map<String, Object> map) throws Exception {
		return (List<Map<String, Object>>)dao.selectPage("Work.selectList", map);
	}

	@Override
	public Map<String, Object> getWorkInfo(Map<String, Object> map) throws Exception {
		
		return dao.selectMap("Work.select", map).orElseGet(() -> new HashMap<String, Object>());
	}

	
	@Override
	public int saveWork(Map<String, Object> map) throws Exception {
		if(!map.containsKey("id") || StringUtil.isEmpty(String.valueOf(map.get("id")))){
			map.put("id",Uid.makeLongUUID());
		}
		map.put("createUserId",sessionData.getUserVo().getUserId());
		map.put("updateUserId",sessionData.getUserVo().getUserId());

		pythonUtil.callGatheringObject(map);
		//pythonUtil.callTest(map);
	    return dao.update("Work.mergeWork", map);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getWorkLogList(Map<String, Object> map) throws Exception {
		return (List<Map<String, Object>>)dao.selectPage("Work.selectLogList", map);
	}

	@Override
	public int deleteWork(Map<String, Object> map) throws Exception {
		return dao.update("Work.deleteWork", map);
	}



}
