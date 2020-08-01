package trong.lixco.com.thai.bean;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;
import org.primefaces.context.RequestContext;

import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.ejb.thai.kpi.ConfigSendMailService;
import trong.lixco.com.jpa.entitykpi.KPIPerson;
import trong.lixco.com.jpa.thai.ConfigSendMail;
import trong.lixco.com.thai.bean.entities.Reminder;

@Named
@org.omnifaces.cdi.ViewScoped
public class SendMailBean extends AbstractBean<KPIPerson>{
	
	private static final long serialVersionUID = 1L;
	@Inject
	private ConfigSendMailService CONFIG_SEND_MAIL_SERVICE;
	
	private int dateOfMonthSelected = 1;
	private List<ConfigSendMail> configSendMail;
	@Override
	protected void initItem() {
		configSendMail = CONFIG_SEND_MAIL_SERVICE.findAll();
	}
	
	public void startup() {
		try {
			configSendMail = CONFIG_SEND_MAIL_SERVICE.findAll();
			Reminder job = new Reminder();
			job.start(configSendMail.get(0).getDepartmentSignDate(),configSendMail.get(0).getHour(), 24, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//cap nhat ngay
	public void updateDateOfMonth() {
		try {
			ConfigSendMail temp = new ConfigSendMail();
			temp = configSendMail.get(0);
			temp.setDepartmentSignDate(dateOfMonthSelected);
			ConfigSendMail resultUpdate = CONFIG_SEND_MAIL_SERVICE.update(temp);
			RequestContext context = RequestContext.getCurrentInstance();
			if(resultUpdate != null) {
				notice("Thành công");
				context.execute("PF('widgetKPIDepartmentYear').hide();");
			}else {
				noticeError("Không thành công");
				context.execute("PF('widgetKPIDepartmentYear').hide();");
			}
		} catch (Exception e) {
			// TODO: handle exception
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
