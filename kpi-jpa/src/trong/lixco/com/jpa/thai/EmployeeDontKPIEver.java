package trong.lixco.com.jpa.thai;

import javax.persistence.Entity;
import javax.persistence.Table;

import trong.lixco.com.jpa.entitykpi.AbstractEntity;

@Entity
@Table(name = "employee_dont_kpi_ever")
public class EmployeeDontKPIEver extends AbstractEntity {

	private String employee_code;

	public EmployeeDontKPIEver() {
		super();
	}

	public EmployeeDontKPIEver(String employee_code) {
		super();
		this.employee_code = employee_code;
	}

	public String getEmployee_code() {
		return employee_code;
	}

	public void setEmployee_code(String employee_code) {
		this.employee_code = employee_code;
	}
}