package trong.lixco.com.thai.bean.entities;

public class PersonalMonth {
	private String employeeName;
	private String departmentName;
	private double kpiDepartment;
	private double kpiPersonal;
	private double result;
	private String rate;
	
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	public double getKpiDepartment() {
		return kpiDepartment;
	}
	public void setKpiDepartment(double kpiDepartment) {
		this.kpiDepartment = kpiDepartment;
	}
	public double getKpiPersonal() {
		return kpiPersonal;
	}
	public void setKpiPersonal(double kpiPersonal) {
		this.kpiPersonal = kpiPersonal;
	}
	public double getResult() {
		return result;
	}
	public void setResult(double result) {
		this.result = result;
	}
	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}
}
