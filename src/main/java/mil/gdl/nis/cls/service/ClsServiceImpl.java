package mil.gdl.nis.cls.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mil.gdl.nis.cmmn.SessionData;
import mil.gdl.nis.cmmn.error.ErrorCode;
import mil.gdl.nis.cmmn.util.DateUtil;
import mil.gdl.nis.cmmn.util.PythonUtil;
import mil.gdl.nis.cmmn.util.Uid;
import mil.gdl.nis.dao.DAO;
import mil.gdl.nis.exception.BizException;

@Slf4j
@RequiredArgsConstructor
@Service
public class ClsServiceImpl implements ClsService{
	
	private final PythonUtil pythonUtil;
	private final SessionData sessionData;
    private final DAO dao;
    
    @Value("${learning.local}")
    private String localLearningPath;
	
	@Value("${learning.dev}")
    private String devLearningPath;
	
	@Value("${learning.prod}")
    private String prodLearningPath;
	
	@Value("${spring.profiles.active:}") 
	private String activeProfile;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<Map<String,Object>> getClsInfo(Map<String,Object> map) throws Exception{
		List<Map<String,Object>> list = (List<Map<String,Object>>)dao.selectList("Cls.selectClass", map);
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
	public List<Map<String, Object>> getLearningList(Map<String, Object> map) throws Exception {
		return (List<Map<String,Object>>)dao.selectPage("Cls.selectLearningList", map);
	}
    


	@SuppressWarnings("unchecked")
	@Override
	public int saveClass(Map<String, Object> map) throws Exception {
		int rtn = 0;
		List<Map<String,Object>> parseList = (List<Map<String,Object>>)map.get("cls");
		List<Map<String,Object>> list = this.parseList(parseList,"");
		for(Map<String,Object> m:list) {
			if(!m.containsKey("id") || m.get("id").equals("")) {
				m.put("id", Uid.makeLongUUID());
			}
			rtn += dao.update("Cls.mergeClass",m);
		}
		return rtn;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private  List<Map<String,Object>> parseList(List<Map<String,Object>> list,String upperId){
		List<Map<String,Object>> rtnList = new ArrayList();
		for(Map<String,Object> map:list) {
			map.put("id", map.get("id"));
			map.put("classNm", map.get("classNm"));
			map.put("upperId", upperId);
			if(map.containsKey("children")) {
				List<Map<String,Object>> childList = (List<Map<String,Object>>)map.get("children");
				rtnList.addAll(parseList(childList,(String)map.get("id")));
			}
			rtnList.add(map);
		}
		return rtnList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int callLearning(Map<String, Object> map) throws Exception {
		
		
		String learningPath = localLearningPath;
		if(activeProfile.equals("prod")) {
			learningPath = prodLearningPath;
		}
		else if(activeProfile.equals("dev")) {
			learningPath = devLearningPath;
		}
		map.put("id", Uid.makeLongUUID());
		map.put("status", "001");
		map.put("userId", sessionData.getUserVo().getUserId());
		learningPath = learningPath+"/" + DateUtil.currentYear() +"/"+ DateUtil.currentMonth() +"/"+map.get("id");
		map.put("learningPath", learningPath);
		
		int rtn = dao.update("Cls.insertLearning", map);
		List<Map<String,Object>> learningFileList = (List<Map<String,Object>>)dao.selectList("Cls.selectLearningFileList",map);
		if(learningFileList == null || learningFileList.size() == 0) {
			throw new BizException(ErrorCode.NOT_EXIST_LEARNING);
		}
		for(Map<String,Object> learningItem:learningFileList) {
			learningItem.put("destFile", learningPath+"/"+learningItem.get("workFileNm"));
		}
		map.put("learningFileList", learningFileList);
		pythonUtil.callLearning(map);
		return rtn;

	}

	
	
	
	/*
	 * @SuppressWarnings({ "unchecked", "rawtypes" }) private
	 * List<Map<String,Object>> parseJsonList(JSONArray jary,String upperId){
	 * List<Map<String,Object>> jsonList = new ArrayList(); for(int
	 * i=0;i<jary.size();i++) { JSONObject jobj = (JSONObject)jary.get(i);
	 * Map<String,Object> map = new HashMap<String, Object>(); map.put("id",
	 * jobj.get("id")); map.put("classNm", jobj.get("text")); map.put("upperId",
	 * upperId); if(jobj.containsKey("children")) { JSONArray childAry =
	 * (JSONArray)jobj.get("children");
	 * jsonList.addAll(parseJsonList(childAry,(String)jobj.get("id"))); }
	 * jsonList.add(map); } return jsonList; }
	 */

}
