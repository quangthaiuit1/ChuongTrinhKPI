package trong.lixco.com.jpa.entitykpi;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * @author vantrong
 * @Time 16-04-2018 12:08:21 KPI cua cong ty/phong trong nam
 */
@Entity
public class KPIDepOfYear extends AbstractEntity {
	private int year;// nam
	private int no;// so thu tu
	@ManyToOne
	private TagetDepart tagetDepart; // chi tieu
	private int weighted;// trong so
	private double weightedParent;// trong so cua nhom
	private double weightedParentRation;// trong so cua nhom da nhan ti le
	private String contentAppreciate;// noi dung
	private String criteriaCheck;// tieu chi danh gia
	@ManyToOne
	private FormulaKPI formulaKPI;
	private String computation;// cach tinh
	private String timeTakeResult;// Thoi gian ghi nhan ket qua
	private String sourceVerify;// nguồn xác nhận/chung minh
	private String unit;// don vi tinh
	private String planKPI;// ke hoach KPI
	private String performKPI;// thuc hien kpi
	private double ratioComplete;// ti le hoan thanh
	private double ratioCompleteIsWeighted;// ti le da nhan trong so

	@ManyToOne
	private KPIDep kpiDep;

	private String paramA;
	private String paramB;
	private String paramC;
	private String paramD;

	private String nameAssign;

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public TagetDepart getTagetDepart() {
		return tagetDepart;
	}

	public void setTagetDepart(TagetDepart tagetDepart) {
		this.tagetDepart = tagetDepart;
	}

	public int getWeighted() {
		return weighted;
	}

	public void setWeighted(int weighted) {
		this.weighted = weighted;
	}

	public double getWeightedParent() {
		return weightedParent;
	}

	public void setWeightedParent(double weightedParent) {
		this.weightedParent = weightedParent;
	}

	public double getWeightedParentRation() {
		return weightedParentRation;
	}

	public void setWeightedParentRation(double weightedParentRation) {
		this.weightedParentRation = weightedParentRation;
	}

	public String getContentAppreciate() {
		return contentAppreciate;
	}

	public void setContentAppreciate(String contentAppreciate) {
		this.contentAppreciate = contentAppreciate;
	}

	public String getCriteriaCheck() {
		return criteriaCheck;
	}

	public void setCriteriaCheck(String criteriaCheck) {
		this.criteriaCheck = criteriaCheck;
	}

	public FormulaKPI getFormulaKPI() {
		return formulaKPI;
	}

	public void setFormulaKPI(FormulaKPI formulaKPI) {
		this.formulaKPI = formulaKPI;
	}

	public String getComputation() {
		return computation;
	}

	public void setComputation(String computation) {
		this.computation = computation;
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

	public KPIDep getKpiDep() {
		return kpiDep;
	}

	public void setKpiDep(KPIDep kpiDep) {
		this.kpiDep = kpiDep;
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

	public String getNameAssign() {
		return nameAssign;
	}

	public void setNameAssign(String nameAssign) {
		this.nameAssign = nameAssign;
	}

	@Override
	public String toString() {
		return "KPIDepOfYear [year=" + year + ", no=" + no + ", weighted=" + weighted + ", weightedParent="
				+ weightedParent + ", weightedParentRation=" + weightedParentRation + ", contentAppreciate="
				+ contentAppreciate + ", criteriaCheck=" + criteriaCheck + ", computation=" + computation
				+ ", timeTakeResult=" + timeTakeResult + ", sourceVerify=" + sourceVerify + ", unit=" + unit
				+ ", planKPI=" + planKPI + ", performKPI=" + performKPI + ", ratioComplete=" + ratioComplete
				+ ", ratioCompleteIsWeighted=" + ratioCompleteIsWeighted + ", paramA=" + paramA + ", paramB=" + paramB
				+ ", paramC=" + paramC + ", paramD=" + paramD + ", id=" + id + ", createdDate=" + createdDate
				+ ", modifiedDate=" + modifiedDate + ", createdUser=" + createdUser + ", note=" + note + "]\n";
	}

}
