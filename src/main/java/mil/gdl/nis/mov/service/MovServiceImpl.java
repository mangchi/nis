package mil.gdl.nis.mov.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mil.gdl.nis.cmmn.SessionData;
import mil.gdl.nis.cmmn.util.Uid;
import mil.gdl.nis.dao.DAO;
import mil.gdl.nis.dataset.vo.DatasetVo;


@RequiredArgsConstructor
@Service
public class MovServiceImpl implements MovService{
	

    private final DAO dao;
    private final SessionData sessionData;

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getMovList(Map<String, Object> map) throws Exception {
		return (List<Map<String, Object>>)dao.selectPage("Mov.selectList", map);
	}

	@Override
	public Map<String, Object> getMovInfo(Map<String, Object> map) throws Exception {
		
		return dao.selectMap("Mov.select", map).orElseGet(() -> new HashMap<String, Object>());
	}

	@Override
	public DatasetVo getMov(Map<String, Object> map) throws Exception {
		
		return (DatasetVo)dao.selectOptionalObject("Mov.selectVo", map).orElseGet(() -> new DatasetVo());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String,Object>> getFileList(Map<String, Object> map) throws Exception {
		return (List<Map<String,Object>>)dao.selectPage("Mov.selectFileList", map);
	}
   
	@SuppressWarnings("unchecked")
	@Override
	public int save(Map<String, Object> map) throws Exception {
		int rtn = 0;
		if(map.containsKey("list")) {
			List<Map<String,Object>> list = (List<Map<String,Object>>)map.get("list");
			for(Map<String,Object> item:list) {
				item.put("parentId",item.get("id"));
				item.put("fileSeq",item.get("seq"));
				item.put("createUserId",sessionData.getUserVo().getUserId());
				item.put("updateUserId",sessionData.getUserVo().getUserId());
				item.put("id",Uid.makeLongUUID());
				rtn += dao.update("Mov.mergeMov",  item);
			}
			
		}
		else {
			throw new Exception("첨부파일이 없습니다.");
		}
		return rtn;
	}

	




}
