package mil.gdl.nis.cmmn.job;

import java.util.Map;
import mil.gdl.nis.cmmn.vo.SchedulerVO;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.stereotype.Component;

@Component
public class SchedulerManager {
	public void makeScheduler(Map<String, Object> map) throws Exception {
		Scheduler dynamicSch = SchedulerVO.getDynamicSch();
		String jobGroupName = SchedulerVO.getJobGroupName();
		String classPath = "mil.gdl.nis.cmmn.job.WorkJob";
		String tegerMethod = "gatherObject";

		MethodInvokingJobDetailFactoryBean jobDetailBean = new MethodInvokingJobDetailFactoryBean();

		Class<?> targetClass = Class.forName(classPath);

		jobDetailBean.setTargetObject(targetClass.newInstance());
		jobDetailBean.setTargetMethod(tegerMethod);
		jobDetailBean.setGroup(jobGroupName);
		jobDetailBean.setName(String.valueOf(map.get("id")));
		jobDetailBean.afterPropertiesSet();

		CronTriggerFactoryBean cronTrigger = new CronTriggerFactoryBean();
		cronTrigger.setJobDetail(jobDetailBean.getObject());
		cronTrigger.setCronExpression("0/1 * * * * ?");
		cronTrigger.setName(String.valueOf(map.get("id")));
		cronTrigger.setGroup(jobGroupName);
		cronTrigger.afterPropertiesSet();

		dynamicSch.scheduleJob(jobDetailBean.getObject(), (Trigger) cronTrigger.getObject());
	}

	public void removeScheduler(Map<String, Object> map) throws Exception {
		Scheduler dynamicSch = SchedulerVO.getDynamicSch();
		String jobGroupName = SchedulerVO.getJobGroupName();

		dynamicSch.deleteJob(new JobKey(String.valueOf(map.get("id")), jobGroupName));
	}

	public void changeScheduler(Map<String, Object> map) throws Exception {
		Scheduler dynamicSch = SchedulerVO.getDynamicSch();
		String jobGroupName = SchedulerVO.getJobGroupName();

		CronTriggerImpl cronTriggerImpl = (CronTriggerImpl) dynamicSch
				.getTrigger(new TriggerKey(String.valueOf(map.get("id")), jobGroupName));

		cronTriggerImpl.setCronExpression("0/2 * * * * ?");
		dynamicSch.rescheduleJob(new TriggerKey(String.valueOf(map.get("id")), jobGroupName),
				(Trigger) cronTriggerImpl);
	}
}
