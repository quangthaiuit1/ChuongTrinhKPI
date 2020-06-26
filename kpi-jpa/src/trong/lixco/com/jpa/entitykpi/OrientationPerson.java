package trong.lixco.com.jpa.entitykpi;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
/**
 * @author vantrong
 * @Time 16-04-2018 12:08:21
 * Dinh huong KPI ca nhan
 */
@Entity
public class OrientationPerson extends AbstractEntity {
	private String code;//ma
	private String content;//noi dung
	private String codePJob;//Chuc vu (trong KPI theo mo ta cong viec)
	
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
}
