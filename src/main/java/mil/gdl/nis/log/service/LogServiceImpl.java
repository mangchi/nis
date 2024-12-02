package mil.gdl.nis.log.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mil.gdl.nis.dao.DAO;

@RequiredArgsConstructor
@Service
public class LogServiceImpl implements LogService {

	private final DAO dao;

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getList(Map<String, Object> map) throws Exception {
		switch (String.valueOf(map.get("searchType"))) {
		case "CONN":
			return (List<Map<String, Object>>) dao.selectPage("Log.selectConnList", map);
		case "VIEW":
			if (map.containsKey("isExcel")) {
				return (List<Map<String, Object>>) dao.selectList("Log.selectViewList", map);
			} else {
				return (List<Map<String, Object>>) dao.selectPage("Log.selectViewList", map);
			}
		case "UPDOWN":
			if (map.containsKey("isExcel")) {
				return (List<Map<String, Object>>) dao.selectList("Log.selectUpdownList", map);
			} else {
				return (List<Map<String, Object>>) dao.selectPage("Log.selectUpdownList", map);
			}
		default:
			return (List<Map<String, Object>>) dao.selectPage("Log.selectConnList", map);

		}

	}

	@Override
	public int insertLog(Map<String, Object> map) throws Exception {
		return dao.update("Log.insertLog", map);
	}

	@Override
	public int insertUpDownLog(Map<String, Object> map) throws Exception {
		return dao.update("Log.insertUpDownLog", map);
	}

}
