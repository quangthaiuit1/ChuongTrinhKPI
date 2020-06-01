package trong.lixco.com.jpa.entitykpi;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
/**
 * @author vantrong
 * @Time 16-04-2018 12:08:21
 * KPI nam cua phong
 */
@Entity
public class KPIDep extends AbstractEntity {
	private int year;//nam
	private double result;
	private boolean isSignKPI=false;//Duyet cac tieu chi KPI
	private boolean isSignResultKPI=false;//Duyet ket quan KPI
	private boolean isSignKPIBLD=false;//Duyet cac tieu chi KPI
	private boolean isSignResultKPIBLD=false;//Duyet ket quan KPI
	private String codeDepart;
	@Transient
	private String nameDepart;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "kpiDep")
	private List<KPIDepOfYear> kpiDepOfYears;
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public double getResult() {
		return result;
	}
	public void setResult(double result) {
		this.result = result;
	}
	
	public String getCodeDepart() {
		return codeDepart;
	}
	public void setCodeDepart(String codeDepart) {
		this.codeDepart = codeDepart;
	}
	public List<KPIDepOfYear> getKpiDepOfYears() {
		return kpiDepOfYears;
	}
	public void setKpiDepOfYears(List<KPIDepOfYear> kpiDepOfYears) {
		this.kpiDepOfYears = kpiDepOfYears;
	}
	public boolean isSignKPI() {
		return isSignKPI;
	}
	public void setSignKPI(boolean isSignKPI) {
		this.isSignKPI = isSignKPI;
	}
	public boolean isSignResultKPI() {
		return isSignResultKPI;
	}
	public void setSignResultKPI(boolean isSignResultKPI) {
		this.isSignResultKPI = isSignResultKPI;
	}
	
	public String getNameDepart() {
		return nameDepart;
	}
	public void setNameDepart(String nameDepart) {
		this.nameDepart = nameDepart;
	}
	
	public boolean isSignKPIBLD() {
		return isSignKPIBLD;
	}
	public void setSignKPIBLD(boolean isSignKPIBLD) {
		this.isSignKPIBLD = isSignKPIBLD;
	}
	public boolean isSignResultKPIBLD() {
		return isSignResultKPIBLD;
	}
	public void setSignResultKPIBLD(boolean isSignResultKPIBLD) {
		this.isSignResultKPIBLD = isSignResultKPIBLD;
	}
	@Override
	public String toString() {
		String kpids="";
		try {
			kpids=""+kpiDepOfYears;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "KPIDep [year=" + year + ", result=" + result + ", isSignKPI=" + isSignKPI + ", isSignResultKPI="
				+ isSignResultKPI + ", departmentParent=" + codeDepart + ", kpiDepOfYears=" + kpids
				+ ", id=" + id + ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate + ", createdUser="
				+ createdUser + ", note=" + note + "]\n";
	}
	
}
