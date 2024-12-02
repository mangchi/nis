package mil.gdl.nis.bbs.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mil.gdl.nis.bbs.vo.BbsVo;
import mil.gdl.nis.cmmn.SessionData;
import mil.gdl.nis.cmmn.util.StringUtil;
import mil.gdl.nis.cmmn.util.Uid;
import mil.gdl.nis.dao.DAO;

@RequiredArgsConstructor
@Service
public class BbsServiceImpl implements BbsService{
	
	private final SessionData sessionData;
    private final DAO dao;

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String,Object>> getList(Map<String,Object> map) throws Exception{
		return (List<Map<String,Object>>)dao.selectPage("Bbs.selectList", map);
	}

	@Override
	public BbsVo getBbsInfo(BbsVo vo) throws Exception {
		return (BbsVo)dao.selectOptionalObject("Bbs.select", vo).orElseGet(() -> new BbsVo());
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getBbsNotice(Map<String, Object> map) throws Exception {
		return (Map<String, Object>)dao.selectOne("Bbs.selectNotice",map);
	}
    


	@Override
	public int bbsSave(BbsVo vo) throws Exception {
		vo.setGrpId("notice");
		if(StringUtil.isEmpty(vo.getId())) {
			vo.setId(Uid.makeLongUUID());
			//vo.setCreateUserId(sessionData.getUserVo().getUserId());
		}
		vo.setCreateUserId(sessionData.getUserVo().getUserId());
		vo.setUpdateUserId(sessionData.getUserVo().getUserId());
		return dao.update("Bbs.mergeBbs", vo);
	}


	@Override
	public int bbsDelete(Map<String,Object> map) throws Exception {
		return dao.delete("Bbs.deleteBbs", map);
	}

	
	@Override
	public int getBbsReadCount(Map<String, Object> map) throws Exception {
		return (int)dao.selectOne("Bbs.selectUserReadCount", map);
	}

	@Override
	public int bbsReadSave(Map<String, Object> map) throws Exception {
		return dao.update("Bbs.insertReadBbs", map);
	}




}
