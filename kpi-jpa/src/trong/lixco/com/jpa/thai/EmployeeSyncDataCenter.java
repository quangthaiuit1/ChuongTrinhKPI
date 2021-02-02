package trong.lixco.com.jpa.thai;

import javax.persistence.Entity;
import javax.persistence.Table;

import trong.lixco.com.jpa.entitykpi.AbstractEntity;

@Entity
@Table(name = "employee_sync_data_center")
public class EmployeeSyncDataCenter extends AbstractEntity {
	private String employee_code;
	private String employee_name;
	private String department_code;
	private String department_name;

	public EmployeeSyncDataCenter(String employee_code, String employee_name, String department_code,
			String department_name) {
		super();
		this.employee_code = employee_code;
		this.employee_name = employee_name;
		this.department_code = department_code;
		this.department_name = department_name;
	}

	public EmployeeSyncDataCenter() {
		super();
	}

	public String getEmployee_code() {
		return employee_code;
	}

	public void setEmployee_code(String employee_code) {
		this.employee_code = employee_code;
	}

	public String getEmployee_name() {
		return employee_name;
	}

	public void setEmployee_name(String employee_name) {
		this.employee_name = employee_name;
	}

	public String getDepartment_code() {
		return department_code;
	}

	public void setDepartment_code(String department_code) {
		this.department_code = department_code;
	}

	public String getDepartment_name() {
		return department_name;
	}

	public void setDepartment_name(String department_name) {
		this.department_name = department_name;
	}
}
