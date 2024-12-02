package mil.gdl.nis.system.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mil.gdl.nis.dao.DAO;

@RequiredArgsConstructor
@Service
public class SystemServiceImpl implements SystemService{
	

    private final DAO dao;

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String,Object>> getDbList(Map<String,Object> map) throws Exception{
		return (List<Map<String,Object>>)dao.selectList("Db.selectList", map);
	}

}
