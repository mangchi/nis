package mil.gdl.nis.cmmn.vo;

import java.io.Serializable;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;


public class SchedulerVO
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private static Scheduler dynamicSch;
  private static String jobGroupName = "dynamicScheduler";
  
  public static Scheduler getDynamicSch() throws SchedulerException {
  if (dynamicSch == null) {
      dynamicSch = (new StdSchedulerFactory()).getScheduler();
    }
   return dynamicSch;
  }
  
  public static String getJobGroupName() {
    return jobGroupName;
  }
}