package mil.gdl.nis.movImg.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mil.gdl.nis.dao.DAO;
import mil.gdl.nis.movImg.vo.MovImgVo;

@RequiredArgsConstructor
@Service
public class MovImgServiceImpl implements MovImgService{
	

    private final DAO dao;

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getMovImgList(Map<String, Object> map) throws Exception {
		return (List<Map<String, Object>>)dao.selectList("MovImg.selectList", map);
	}

	@Override
	public Map<String, Object> getMovImgInfo(Map<String, Object> map) throws Exception {
		
		return dao.selectMap("MovImg.select", map).orElseGet(() -> new HashMap<String, Object>());
	}

	@Override
	public MovImgVo getMovImg(Map<String, Object> map) throws Exception {
		
		return (MovImgVo)dao.selectOptionalObject("MovImg.selectVo", map).orElseGet(() -> new MovImgVo());
	}


}
