package mil.gdl.nis.main.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mil.gdl.nis.dao.DAO;


@RequiredArgsConstructor
@Service
public class MainServiceImpl implements MainService{

    private final DAO dao;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<String, Object> getStatInfo(Map<String, Object> map) throws Exception {
		Map<String,Object> rtnMap = new HashMap();
		rtnMap.put("stat",dao.selectMap("Main.selectStat", map).orElseGet(() -> new HashMap<String, Object>()));
		rtnMap.put("statObject",dao.selectList("Main.selectStatObject", map));
		return rtnMap;
	}

	



}
