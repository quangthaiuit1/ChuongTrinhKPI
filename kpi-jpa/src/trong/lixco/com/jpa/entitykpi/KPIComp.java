package trong.lixco.com.jpa.entitykpi;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
/**
 * @author vantrong
 * @Time 16-04-2018 12:08:21
 * KPI cua cong ty
 */
@Entity
public class KPIComp extends AbstractEntity {
	private int year;//nam
	private double result;
	private boolean isSignKPI=false;//Duyet cac tieu chi KPI
	private boolean isSignResultKPI=false;//Duyet ket quan KPI
	
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "kpiComp")
	private List<KPICompOfYear> kpiCompOfYears;
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
	public List<KPICompOfYear> getKpiCompOfYears() {
		return kpiCompOfYears;
	}
	public void setKpiCompOfYears(List<KPICompOfYear> kpiCompOfYears) {
		this.kpiCompOfYears = kpiCompOfYears;
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
	
}
