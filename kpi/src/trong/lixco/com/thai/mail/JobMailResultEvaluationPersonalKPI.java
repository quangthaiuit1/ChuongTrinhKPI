package trong.lixco.com.thai.mail;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import trong.lixco.com.thai.bean.Mail;

public class JobMailResultEvaluationPersonalKPI implements Job {
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println("Reminder Notification:- " + new Date());
//		Mail.processSendMailResultEvaluationPersonal("yeucausuachua@lixco.com", "It@2019", "thai-dinhquang@lixco.com");
	}
}