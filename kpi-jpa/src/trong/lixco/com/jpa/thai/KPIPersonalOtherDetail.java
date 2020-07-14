package trong.lixco.com.jpa.thai;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import trong.lixco.com.jpa.entitykpi.AbstractEntity;

@Entity
@Table(name = "personal_other_detail")
public class KPIPersonalOtherDetail extends AbstractEntity{
	
	private String content;
	private double minuspoint;
	private int quantity;
	
	@ManyToOne
	private KPIPersonalOther kpiPersonalOther;
	
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public double getMinuspoint() {
		return minuspoint;
	}
	public void setMinuspoint(double minuspoint) {
		this.minuspoint = minuspoint;
	}
	public KPIPersonalOther getKpiPersonalOther() {
		return kpiPersonalOther;
	}
	public void setKpiPersonalOther(KPIPersonalOther kpiPersonalOther) {
		this.kpiPersonalOther = kpiPersonalOther;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}
