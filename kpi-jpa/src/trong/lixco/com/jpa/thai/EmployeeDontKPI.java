package trong.lixco.com.jpa.thai;

import javax.persistence.Entity;
import javax.persistence.Table;

import trong.lixco.com.jpa.entitykpi.AbstractEntity;

@Entity
@Table(name = "employee_dont_kpi")
public class EmployeeDontKPI extends AbstractEntity {

	private String employee_code;
	private boolean is_temp = false;
	private boolean is_thaisan = false;
	private int month;
	private int year;

	public String getEmployee_code() {
		return employee_code;
	}

	public void setEmployee_code(String employee_code) {
		this.employee_code = employee_code;
	}

	public boolean isIs_temp() {
		return is_temp;
	}

	public void setIs_temp(boolean is_temp) {
		this.is_temp = is_temp;
	}

	public boolean isIs_thaisan() {
		return is_thaisan;
	}

	public void setIs_thaisan(boolean is_thaisan) {
		this.is_thaisan = is_thaisan;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}
}
