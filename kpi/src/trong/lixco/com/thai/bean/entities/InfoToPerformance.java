package trong.lixco.com.thai.bean.entities;

import java.util.List;

import trong.lixco.com.jpa.thai.KPIToPerformance;

public class InfoToPerformance {
	private String nameDepart;
	private List<KPIToPerformance> listToPerformance;

	public String getNameDepart() {
		return nameDepart;
	}

	public void setNameDepart(String nameDepart) {
		this.nameDepart = nameDepart;
	}

	public List<KPIToPerformance> getListToPerformance() {
		return listToPerformance;
	}

	public void setListToPerformance(List<KPIToPerformance> listToPerformance) {
		this.listToPerformance = listToPerformance;
	}

}
