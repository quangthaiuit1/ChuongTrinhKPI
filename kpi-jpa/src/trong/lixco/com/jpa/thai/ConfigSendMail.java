package trong.lixco.com.jpa.thai;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import trong.lixco.com.jpa.entitykpi.AbstractEntity;

@Entity
@Table(name = "config_send_mail")
public class ConfigSendMail extends AbstractEntity{
	private int hour;
	@Column(name = "department_sign_date")
	private int departmentSignDate;
	@Column(name = "result_evaluation_department_date")
	private int resultEvaluationDepartmentDate;
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public int getDepartmentSignDate() {
		return departmentSignDate;
	}
	public void setDepartmentSignDate(int departmentSignDate) {
		this.departmentSignDate = departmentSignDate;
	}
	public int getResultEvaluationDepartmentDate() {
		return resultEvaluationDepartmentDate;
	}
	public void setResultEvaluationDepartmentDate(int resultEvaluationDepartmentDate) {
		this.resultEvaluationDepartmentDate = resultEvaluationDepartmentDate;
	}
}
