package trong.lixco.com.thai.bean.entities;

import java.util.List;

import trong.lixco.com.jpa.thai.KPIDepPerformanceJPA;

public class InfoDepPerformance {
	private String nameDepart;
	private List<KPIDepPerformanceJPA> listDepPerformance;
	public String getNameDepart() {
		return nameDepart;
	}
	public void setNameDepart(String nameDepart) {
		this.nameDepart = nameDepart;
	}
	public List<KPIDepPerformanceJPA> getListDepPerformance() {
		return listDepPerformance;
	}
	public void setListDepPerformance(List<KPIDepPerformanceJPA> listDepPerformance) {
		this.listDepPerformance = listDepPerformance;
	}
}
