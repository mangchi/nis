package mil.gdl.nis.cmmn.job;


import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NisWorkJob extends QuartzJobBean {

	public NisWorkJob(WorkJob workJob) {
		this.workJob = workJob;
	}

	private final WorkJob workJob;

	protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		log.info("workJob............");
		try {
			this.workJob.gatherAllObject();
		} catch (Exception e) {
			log.error("NisWorkJob executeInternal error:{}", e);
		}
	}
}
