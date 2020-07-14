package trong.lixco.com.jpa.thai;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import trong.lixco.com.jpa.entitykpi.AbstractEntity;
import trong.lixco.com.jpa.entitykpi.FormulaKPI;

@Entity
@Table(name = "personal_performance")
public class KPIPersonalPerformance extends AbstractEntity {
	private String code;//ma
	private String content;//noi dung
	private String codePJob;//Chuc vu (trong KPI theo mo ta cong viec)
	private String computation;
	private double minuspoint = 0;// so diem bi tru
	private int quantity = 1; //so luong
	
	@ManyToOne
	private FormulaKPI formulaKPI;//cong thuc tinh
	
	private boolean isHeader=false;// tieu de
	private String codeHeader;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public boolean isHeader() {
		return isHeader;
	}
	public void setHeader(boolean isHeader) {
		this.isHeader = isHeader;
	}
	
	public String getCodePJob() {
		return codePJob;
	}
	public void setCodePJob(String codePJob) {
		this.codePJob = codePJob;
	}
	public FormulaKPI getFormulaKPI() {
		return formulaKPI;
	}
	public void setFormulaKPI(FormulaKPI formulaKPI) {
		this.formulaKPI = formulaKPI;
	}
	public String getCodeHeader() {
		return codeHeader;
	}
	public void setCodeHeader(String codeHeader) {
		this.codeHeader = codeHeader;
	}
	public String getComputation() {
		return computation;
	}
	public void setComputation(String computation) {
		this.computation = computation;
	}
	public double getMinuspoint() {
		return minuspoint;
	}
	public void setMinuspoint(double minuspoint) {
		this.minuspoint = minuspoint;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}
