package trong.lixco.com.thai.bean.entities;

import java.util.List;

import trong.lixco.com.jpa.thai.KPIPersonalPerformance;

public class InfoPersonalPerformance {
	private String positionJobName;
	private List<KPIPersonalPerformance> personalPerformances;
	
	public String getPositionJobName() {
		return positionJobName;
	}
	public void setPositionJobName(String positionJobName) {
		this.positionJobName = positionJobName;
	}
	public List<KPIPersonalPerformance> getPersonalPerformances() {
		return personalPerformances;
	}
	public void setPersonalPerformances(List<KPIPersonalPerformance> personalPerformances) {
		this.personalPerformances = personalPerformances;
	}
}
