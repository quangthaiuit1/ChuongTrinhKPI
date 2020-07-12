package trong.lixco.com.jpa.thai;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import trong.lixco.com.jpa.entitykpi.AbstractEntity;

@Entity
@Table(name = "personal_other")
public class KPIPersonalOther extends AbstractEntity{
	@Column(name = "employee_code")
	private String codeEmp;
	private String nameEmp;
	private String nameDepart;
	private int kMonth;
	private int kYear;
	private double total;
	
	@OneToMany(cascade = CascadeType.ALL,mappedBy = "kpiPersonalOther")
	private List<KPIPersonalOtherDetail> kpiPersonalOtherDetails;
	
	public List<KPIPersonalOtherDetail> getKpiPersonalOtherDetails() {
		return kpiPersonalOtherDetails;
	}
	public void setKpiPersonalOtherDetails(List<KPIPersonalOtherDetail> kpiPersonalOtherDetails) {
		this.kpiPersonalOtherDetails = kpiPersonalOtherDetails;
	}
	public String getCodeEmp() {
		return codeEmp;
	}
	public void setCodeEmp(String codeEmp) {
		this.codeEmp = codeEmp;
	}
	public int getkMonth() {
		return kMonth;
	}
	public void setkMonth(int kMonth) {
		this.kMonth = kMonth;
	}
	public int getkYear() {
		return kYear;
	}
	public void setkYear(int kYear) {
		this.kYear = kYear;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public String getNameEmp() {
		return nameEmp;
	}
	public void setNameEmp(String nameEmp) {
		this.nameEmp = nameEmp;
	}
	public String getNameDepart() {
		return nameDepart;
	}
	public void setNameDepart(String nameDepart) {
		this.nameDepart = nameDepart;
	}
}
