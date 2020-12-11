package trong.lixco.com.thai.bean;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.context.RequestContext;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.ejb.thai.kpi.ConfigSendMailService;
import trong.lixco.com.jpa.entitykpi.KPIPerson;
import trong.lixco.com.jpa.thai.ConfigSendMail;
import trong.lixco.com.thai.bean.entities.Reminder;
import trong.lixco.com.thai.mail.JobSendMail;

@Singleton
@Startup

// @Named
// @ViewScoped
public class SendMailBean extends AbstractBean<KPIPerson> {

	private static final long serialVersionUID = 1L;
	@Inject
	private ConfigSendMailService CONFIG_SEND_MAIL_SERVICE;

	private int dateOfMonthSelected = 1;
	private List<ConfigSendMail> configSendMail;
	Connection con = null;
	PreparedStatement preStatement = null;
	private ConfigSendMail config;

	// @Override
	// protected void initItem() {
	// configSendMail = CONFIG_SEND_MAIL_SERVICE.findAll();
	//
	// config = new ConfigSendMail();
	// String queryLinkConfigMail = "SELECT * FROM kpi.config_send_mail";
	// try {
	// con = SendMailBean
	// .getConnectionMySQL("jdbc:mysql://192.168.0.132:3306/kpi?useUnicode=yes&characterEncoding=UTF-8");
	// preStatement = con.prepareStatement(queryLinkConfigMail);
	// ResultSet resultSet = preStatement.executeQuery();
	// while (resultSet.next()) {
	// config.setDepartmentSignDate(resultSet.getInt("department_sign_date"));
	// config.setHour(resultSet.getInt("hour"));
	// }
	// Reminder job = new Reminder();
	// job.start(configSendMail.get(0).getDepartmentSignDate(),
	// configSendMail.get(0).getHour(), 40, false);
	// } catch (
	//
	// Exception e) {
	// e.printStackTrace();
	// }
	// }

	@Override
	protected void initItem() {
		//chạy vào lúc 13h3 ngày 10 hàng tháng
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity("triggerName", "group1")
				.withSchedule(CronScheduleBuilder.cronSchedule("0 24 9 11 * ?")).build();
		JobDetail job = JobBuilder.newJob(JobSendMail.class).withIdentity("jobName", "group1").build();
		Scheduler scheduler;
		try {
			scheduler = new StdSchedulerFactory().getScheduler();
			scheduler.start();
			scheduler.scheduleJob(job, trigger);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	public void startup() {
		try {
			configSendMail = CONFIG_SEND_MAIL_SERVICE.findAll();
			Reminder job = new Reminder();
			job.start(configSendMail.get(0).getDepartmentSignDate(), configSendMail.get(0).getHour(), 8, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// cap nhat ngay
	public void updateDateOfMonth() {
		try {
			ConfigSendMail temp = new ConfigSendMail();
			temp = configSendMail.get(0);
			temp.setDepartmentSignDate(dateOfMonthSelected);
			ConfigSendMail resultUpdate = CONFIG_SEND_MAIL_SERVICE.update(temp);
			RequestContext context = RequestContext.getCurrentInstance();
			if (resultUpdate != null) {
				notice("Thành công");
				context.execute("PF('widgetKPIDepartmentYear').hide();");
			} else {
				noticeError("Không thành công");
				context.execute("PF('widgetKPIDepartmentYear').hide();");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static Connection getConnectionMySQL(String username, String password, String url) throws ClassNotFoundException {
		// Load driver mysql
		try {
			// Class.forName("com.mysql.jdbc.Driver");
			// handle connect mysql
			// String url =
			// "jdbc:mysql://localhost:3306/quanlydatcom?useUnicode=yes&characterEncoding=UTF-8";
			// String user = "remote";
			// String password = "Voquangthai1901";

			return DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected Logger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getDateOfMonthSelected() {
		return dateOfMonthSelected;
	}

	public void setDateOfMonthSelected(int dateOfMonthSelected) {
		this.dateOfMonthSelected = dateOfMonthSelected;
	}

}
