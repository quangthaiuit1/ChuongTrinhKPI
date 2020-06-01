package trong.lixco.com.classInfor;

import java.text.SimpleDateFormat;

import trong.lixco.com.account.servicepublics.Department;
import trong.lixco.com.account.servicepublics.DepartmentServicePublic;
import trong.lixco.com.account.servicepublics.Member;
import trong.lixco.com.account.servicepublics.MemberServicePublic;
import trong.lixco.com.jpa.entitykpi.KPICompOfYear;
import trong.lixco.com.jpa.entitykpi.KPIDepMonth;
import trong.lixco.com.jpa.entitykpi.KPIDepOfMonth;
import trong.lixco.com.jpa.entitykpi.KPIDepOfYear;

public class PrintKPIComp {
	private String hearder = "";
	private int no = 0;
	private String tagetCateNo;
	private String tagetCateContent;
	private double weightedParent;// trong so cua nhom
	private double weightedParentRation;// trong so cua nhom da nhan ti le
	private String contentAppreciate = "";
	private double weighted = 0.0;
	private String criteriaCheck;// tieuchidanhgia
	private String formulaKPICode = "";
	private String timeTakeResult = "";
	private String sourceVerify = "";// nguonchungminh
	private String unit = "";
	private String planKPI = "";
	private String performKPI = "";
	private double ratioComplete = 0.0;
	private double ratioCompleteIsWeighted = 0.0;
	private String note;
	private double result;

	private KPIDepMonth kpiDepMonth;
	private Department department;
	private Member member;

	public PrintKPIComp(KPICompOfYear kpiCompOfYear, SimpleDateFormat sf) {
		try {
			hearder = "KPIs CÔNG TY - NĂM " + kpiCompOfYear.getKpiComp().getYear();
		} catch (Exception e) {
		}
		try {
			no = kpiCompOfYear.getNo();
		} catch (Exception e) {
		}
		try {
			tagetCateNo = kpiCompOfYear.getTaget().getkTagetCate().getCode();
		} catch (Exception e) {
		}
		try {
			tagetCateContent = kpiCompOfYear.getTaget().getkTagetCate().getContent();
		} catch (Exception e) {
		}
		try {
			weightedParent = kpiCompOfYear.getWeightedParent();
		} catch (Exception e) {
		}
		try {
			weightedParentRation = kpiCompOfYear.getWeightedParentRation();
		} catch (Exception e) {
		}
		try {
			contentAppreciate = kpiCompOfYear.getContentAppreciate();
		} catch (Exception e) {
		}
		try {
			weighted = kpiCompOfYear.getWeighted();
		} catch (Exception e) {
		}
		try {
			criteriaCheck = kpiCompOfYear.getCriteriaCheck();
		} catch (Exception e) {
		}
		try {
			formulaKPICode = kpiCompOfYear.getFormulaKPI().getCode();
		} catch (Exception e) {
		}
		try {
			timeTakeResult = kpiCompOfYear.getTimeTakeResult();
		} catch (Exception e) {
		}
		try {
			sourceVerify = kpiCompOfYear.getSourceVerify();
		} catch (Exception e) {
		}
		try {
			unit = kpiCompOfYear.getUnit();
		} catch (Exception e) {
		}
		try {
			planKPI = kpiCompOfYear.getPlanKPI();
		} catch (Exception e) {
		}
		try {
			performKPI = kpiCompOfYear.getPerformKPI();
		} catch (Exception e) {
		}
		try {
			ratioComplete = kpiCompOfYear.getRatioComplete();
		} catch (Exception e) {
		}
		try {
			ratioCompleteIsWeighted = kpiCompOfYear.getRatioCompleteIsWeighted();
		} catch (Exception e) {
		}
		try {
			result = kpiCompOfYear.getKpiComp().getResult();
		} catch (Exception e) {
		}
	}

	public PrintKPIComp(KPIDepOfYear kpiCompOfYear, SimpleDateFormat sf,
			DepartmentServicePublic departmentServicePublic, MemberServicePublic memberServicePublic) {
		try {
			department = departmentServicePublic.findByCode("code", kpiCompOfYear.getKpiDep().getCodeDepart());
			member = memberServicePublic.findByCode(department.getCodeMem());
		} catch (Exception e) {
		}
		try {
			hearder = "KPIs "
					+ departmentServicePublic.findByCode("code", kpiCompOfYear.getKpiDep().getCodeDepart()).getName()
					+ " - NĂM " + kpiCompOfYear.getKpiDep().getYear();
		} catch (Exception e) {
		}
		try {
			no = kpiCompOfYear.getNo();
		} catch (Exception e) {
		}
		try {
			tagetCateNo = kpiCompOfYear.getTagetDepart().getkTagetDepartCate().getCode();
		} catch (Exception e) {
		}
		try {
			tagetCateContent = kpiCompOfYear.getTagetDepart().getkTagetDepartCate().getContent();
		} catch (Exception e) {
		}
		try {
			weightedParent = kpiCompOfYear.getWeightedParent();
		} catch (Exception e) {
		}
		try {
			weightedParentRation = kpiCompOfYear.getWeightedParentRation();
		} catch (Exception e) {
		}
		try {
			contentAppreciate = kpiCompOfYear.getContentAppreciate();
		} catch (Exception e) {
		}
		try {
			weighted = kpiCompOfYear.getWeighted();
		} catch (Exception e) {
		}
		try {
			criteriaCheck = kpiCompOfYear.getCriteriaCheck();
		} catch (Exception e) {
		}
		try {
			formulaKPICode = kpiCompOfYear.getFormulaKPI().getCode();
		} catch (Exception e) {
		}
		try {
			timeTakeResult = kpiCompOfYear.getTimeTakeResult();
		} catch (Exception e) {
		}
		try {
			sourceVerify = kpiCompOfYear.getSourceVerify();
		} catch (Exception e) {
		}
		try {
			unit = kpiCompOfYear.getUnit();
		} catch (Exception e) {
		}
		try {
			planKPI = kpiCompOfYear.getPlanKPI();
		} catch (Exception e) {
		}
		try {
			performKPI = kpiCompOfYear.getPerformKPI();
		} catch (Exception e) {
		}
		try {
			ratioComplete = kpiCompOfYear.getRatioComplete();
		} catch (Exception e) {
		}
		try {
			ratioCompleteIsWeighted = kpiCompOfYear.getRatioCompleteIsWeighted();
		} catch (Exception e) {
		}
		try {
			result = kpiCompOfYear.getKpiDep().getResult();
		} catch (Exception e) {
		}
	}

	public PrintKPIComp(KPIDepOfMonth kpiCompOfMonth, SimpleDateFormat sf,
			DepartmentServicePublic departmentServicePublic, MemberServicePublic memberServicePublic) {
		try {
			department = departmentServicePublic.findByCode("code", kpiCompOfMonth.getKpiDepMonth().getCodeDepart());
			member = memberServicePublic.findByCode(department.getCodeMem());
		} catch (Exception e) {
		}
		try {
			department = departmentServicePublic.findByCode("code", kpiCompOfMonth.getKpiDepMonth().getCodeDepart());
		} catch (Exception e) {
		}
		try {
			kpiDepMonth = kpiCompOfMonth.getKpiDepMonth();
		} catch (Exception e) {
		}
		try {
			hearder = "KPIs "
					+ departmentServicePublic.findByCode("code", kpiCompOfMonth.getKpiDepMonth().getCodeDepart())
							.getName() + " THÁNG " + kpiCompOfMonth.getKpiDepMonth().getMonth() + "/"
					+ kpiCompOfMonth.getKpiDepMonth().getYear();
		} catch (Exception e) {
		}
		try {
			no = kpiCompOfMonth.getNo();
		} catch (Exception e) {
		}
		try {
			tagetCateNo = kpiCompOfMonth.getTaget().getkTagetCate().getCode();
		} catch (Exception e) {
		}
		try {
			tagetCateContent = kpiCompOfMonth.getTaget().getkTagetCate().getContent();
		} catch (Exception e) {
		}
		try {
			weightedParent = kpiCompOfMonth.getWeightedParent();
		} catch (Exception e) {
		}
		try {
			weightedParentRation = kpiCompOfMonth.getWeightedParentRation();
		} catch (Exception e) {
		}
		try {
			contentAppreciate = kpiCompOfMonth.getContentAppreciate();
		} catch (Exception e) {
		}
		try {
			weighted = kpiCompOfMonth.getWeighted();
		} catch (Exception e) {
		}
		try {
			criteriaCheck = kpiCompOfMonth.getCriteriaCheck();
		} catch (Exception e) {
		}
		try {
			formulaKPICode = kpiCompOfMonth.getFormulaKPI().getCode();
		} catch (Exception e) {
		}
		try {
			timeTakeResult = kpiCompOfMonth.getTimeTakeResult();
		} catch (Exception e) {
		}
		try {
			sourceVerify = kpiCompOfMonth.getSourceVerify();
		} catch (Exception e) {
		}
		try {
			unit = kpiCompOfMonth.getUnit();
		} catch (Exception e) {
		}
		try {
			planKPI = kpiCompOfMonth.getPlanKPI();
		} catch (Exception e) {
		}
		try {
			performKPI = kpiCompOfMonth.getPerformKPI();
		} catch (Exception e) {
		}
		try {
			ratioComplete = kpiCompOfMonth.getRatioComplete();
		} catch (Exception e) {
		}
		try {
			ratioCompleteIsWeighted = kpiCompOfMonth.getRatioCompleteIsWeighted();
		} catch (Exception e) {
		}
		try {
			result = kpiCompOfMonth.getKpiDepMonth().getResult();
		} catch (Exception e) {
		}
	}

	public String getHearder() {
		return hearder;
	}

	public void setHearder(String hearder) {
		this.hearder = hearder;
	}

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public String getTagetCateNo() {
		return tagetCateNo;
	}

	public void setTagetCateNo(String tagetCateNo) {
		this.tagetCateNo = tagetCateNo;
	}

	public String getTagetCateContent() {
		return tagetCateContent;
	}

	public void setTagetCateContent(String tagetCateContent) {
		this.tagetCateContent = tagetCateContent;
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

	public double getWeighted() {
		return weighted;
	}

	public void setWeighted(double weighted) {
		this.weighted = weighted;
	}

	public String getCriteriaCheck() {
		return criteriaCheck;
	}

	public void setCriteriaCheck(String criteriaCheck) {
		this.criteriaCheck = criteriaCheck;
	}

	public String getFormulaKPICode() {
		return formulaKPICode;
	}

	public void setFormulaKPICode(String formulaKPICode) {
		this.formulaKPICode = formulaKPICode;
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

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public double getResult() {
		return result;
	}

	public void setResult(double result) {
		this.result = result;
	}

	public KPIDepMonth getKpiDepMonth() {
		return kpiDepMonth;
	}

	public void setKpiDepMonth(KPIDepMonth kpiDepMonth) {
		this.kpiDepMonth = kpiDepMonth;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

}
