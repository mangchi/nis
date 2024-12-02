package mil.gdl.nis.cmmn.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mil.gdl.nis.cmmn.util.DateUtil;
import mil.gdl.nis.cmmn.util.PythonUtil;
import mil.gdl.nis.dao.DAO;

@Slf4j
@RequiredArgsConstructor
@Component
public class WorkJob {
	
	private final PythonUtil pythonUtil;
	private final DAO dao;

	@SuppressWarnings("unchecked")
	public void gatherObject(Map<String, Object> param) throws Exception {
		Map<String, Object> workJob = (Map<String, Object>) this.dao.selectList("Work.selectworkJobList", param);
		this.pythonUtil.callGatheringObject(workJob);
	}

	@SuppressWarnings("unchecked")
	public void gatherAllObject() throws Exception {
		log.debug("gatherAllObject...............");
		List<Map<String, Object>> workJobList = (List<Map<String, Object>>) dao.selectList("Work.selectworkJobList", new HashMap<>());
		for (Map<String, Object> workJob : workJobList) {
			log.debug("workJob:{}", workJob);
			if (DateUtil.getToday("yyyyMMddHHmm").equals(String.valueOf(workJob.get("nextWorkDm")))) {
				log.debug("current hm:{}", DateUtil.getToday("yyyyMMddHHmm"));
				log.debug("nextWorkDm hm:{}", String.valueOf(workJob.get("nextWorkDm")));
				log.debug("workProcess", workJob.get("workProcess"));
				if (!workJob.get("workProcess").equals("001")) {
					this.pythonUtil.callGatheringObject(workJob);
					this.dao.update("Work.updateWorkJob", workJob);
				}
			}
		}
	}
}
