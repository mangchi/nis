package mil.gdl.nis.config;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import mil.gdl.nis.cmmn.job.NisWorkJob;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class JobConfig {

	private final Scheduler scheduler;

	@PostConstruct
	public void start() {
		JobDetail jobDetail = buildJobDetail(NisWorkJob.class, new HashMap<>());

		try {
			this.scheduler.scheduleJob(jobDetail, buildJobTrigger("0 0/1 * * * ?"));
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}


	public Trigger buildJobTrigger(String scheduleExp) {
		return TriggerBuilder.newTrigger().withSchedule((ScheduleBuilder<?>) CronScheduleBuilder.cronSchedule(scheduleExp))
				.build();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public JobDetail buildJobDetail(Class job, Map params) {
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.putAll(params);

		return JobBuilder.newJob(job).usingJobData(jobDataMap).build();
	}
}
