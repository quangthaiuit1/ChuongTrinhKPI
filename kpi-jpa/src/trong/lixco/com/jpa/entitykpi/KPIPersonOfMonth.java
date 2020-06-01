package trong.lixco.com.jpa.entitykpi;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
/**
 * @author vantrong
 * @Time 16-04-2018 12:08:21
 * KPI ca nhan theo thang
 */
@Entity
public class KPIPersonOfMonth extends AbstractEntity {

	private int no;//so thu tu
	private double weighted;//trong so
	private String contentAppreciate;//noi dung/tieu chi danh gia
	@ManyToOne
	private OrientationPerson orientationPerson;// dinh huong muc tieu ca nhan
	private String codeFormula;
	private String timeTakeResult;//Thoi gian ghi nhan ket qua
	private String sourceVerify;//nguồn xác nhận/chung minh
	private String unit;//don vi tinh
	private String planKPI;//ke hoach  KPI
	private String performKPI;//thuc hien kpi
	private double ratioComplete;//ti le hoan thanh
	private double ratioCompleteIsWeighted;//ti le da nhan trong so
	private boolean behaviour=false;
	
	@ManyToOne
	private FormulaKPI formulaKPI;
	
	
	private String paramA;
	private String paramB;
	private String paramC;
	private String paramD;
	private String paramE;
	private String paramF;
	
	private String nameAssign;
	
	@ManyToOne
	private KPIPerson kpiPerson;
	

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public double getWeighted() {
		return weighted;
	}

	public void setWeighted(double weighted) {
		this.weighted = weighted;
	}

	public String getContentAppreciate() {
		return contentAppreciate;
	}

	public void setContentAppreciate(String contentAppreciate) {
		this.contentAppreciate = contentAppreciate;
	}

	public OrientationPerson getOrientationPerson() {
		return orientationPerson;
	}

	public void setOrientationPerson(OrientationPerson orientationPerson) {
		this.orientationPerson = orientationPerson;
	}

	public String getTimeTakeResult() {
		return timeTakeResult;
	}

	public void setTimeTakeResult(String timeTakeResult) {
		this.timeTakeResult = timeTakeResult;
	}

	public String getSourceVerify() {
		return sourceVerify;
	}

	public void setSourceVerify(String sourceVerify) {
		this.sourceVerify = sourceVerify;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getPlanKPI() {
		return planKPI;
	}

	public void setPlanKPI(String planKPI) {
		this.planKPI = planKPI;
	}

	public String getPerformKPI() {
		return performKPI;
	}

	public void setPerformKPI(String performKPI) {
		this.performKPI = performKPI;
	}

	public double getRatioComplete() {
		return ratioComplete;
	}

	public void setRatioComplete(double ratioComplete) {
		this.ratioComplete = ratioComplete;
	}

	public double getRatioCompleteIsWeighted() {
		return ratioCompleteIsWeighted;
	}

	public void setRatioCompleteIsWeighted(double ratioCompleteIsWeighted) {
		this.ratioCompleteIsWeighted = ratioCompleteIsWeighted;
	}

	public String getParamA() {
		return paramA;
	}

	public void setParamA(String paramA) {
		this.paramA = paramA;
	}

	public String getParamB() {
		return paramB;
	}

	public void setParamB(String paramB) {
		this.paramB = paramB;
	}

	public String getParamC() {
		return paramC;
	}

	public void setParamC(String paramC) {
		this.paramC = paramC;
	}

	public String getParamD() {
		return paramD;
	}

	public void setParamD(String paramD) {
		this.paramD = paramD;
	}

	public String getParamE() {
		return paramE;
	}

	public void setParamE(String paramE) {
		this.paramE = paramE;
	}

	public String getParamF() {
		return paramF;
	}

	public void setParamF(String paramF) {
		this.paramF = paramF;
	}

	public KPIPerson getKpiPerson() {
		return kpiPerson;
	}

	public void setKpiPerson(KPIPerson kpiPerson) {
		this.kpiPerson = kpiPerson;
	}

	public String getCodeFormula() {
		return codeFormula;
	}

	public void setCodeFormula(String codeFormula) {
		this.codeFormula = codeFormula;
	}

	public boolean isBehaviour() {
		return behaviour;
	}

	public void setBehaviour(boolean behaviour) {
		this.behaviour = behaviour;
	}

	public FormulaKPI getFormulaKPI() {
		return formulaKPI;
	}

	public void setFormulaKPI(FormulaKPI formulaKPI) {
		this.formulaKPI = formulaKPI;
	}

	@Override
	public String toString() {
		return "KPIPersonOfMonth [no=" + no + ", weighted=" + weighted + ", contentAppreciate=" + contentAppreciate
				+ ", orientationPerson=" + orientationPerson + ", codeFormula=" + codeFormula + ", timeTakeResult="
				+ timeTakeResult + ", sourceVerify=" + sourceVerify + ", unit=" + unit + ", planKPI=" + planKPI
				+ ", performKPI=" + performKPI + ", ratioComplete=" + ratioComplete + ", ratioCompleteIsWeighted="
				+ ratioCompleteIsWeighted + ", behaviour=" + behaviour + ", formulaKPI=" + formulaKPI + ", paramA="
				+ paramA + ", paramB=" + paramB + ", paramC=" + paramC + ", paramD=" + paramD + ", paramE=" + paramE
				+ ", paramF=" + paramF + ", kpiPerson=" + kpiPerson + ", id=" + id + ", createdDate=" + createdDate
				+ ", modifiedDate=" + modifiedDate + ", createdUser=" + createdUser + ", note="
				+ note + "]";
	}

	public String getNameAssign() {
		return nameAssign;
	}

	public void setNameAssign(String nameAssign) {
		this.nameAssign = nameAssign;
	}
	
	
}
