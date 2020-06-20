package trong.lixco.com.jpa.thai;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import trong.lixco.com.jpa.entitykpi.AbstractEntity;
import trong.lixco.com.jpa.entitykpi.FormulaKPI;

@Entity
@Table(name = "department_performance")
public class KPIDepPerformanceJPA extends AbstractEntity{
	private String content;
	private String codeDepart;
	private int year;
	@ManyToOne
	private FormulaKPI formulaKPI;//cong thuc tinh
	private String computation;// cach tinh
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getCodeDepart() {
		return codeDepart;
	}
	public void setCodeDepart(String codeDepart) {
		this.codeDepart = codeDepart;
	}
	public FormulaKPI getFormulaKPI() {
		return formulaKPI;
	}
	public void setFormulaKPI(FormulaKPI formulaKPI) {
		this.formulaKPI = formulaKPI;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getComputation() {
		return computation;
	}
	public void setComputation(String computation) {
		this.computation = computation;
	}
}
