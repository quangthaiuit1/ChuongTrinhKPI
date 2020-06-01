package trong.lixco.com.classInfor;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.inject.Inject;

import trong.lixco.com.account.servicepublics.Member;
import trong.lixco.com.account.servicepublics.MemberServicePublic;
import trong.lixco.com.ejb.servicekpi.EmpPJobService;
import trong.lixco.com.ejb.servicekpi.PositionJobService;
import trong.lixco.com.jpa.entitykpi.EmpPJob;
import trong.lixco.com.jpa.entitykpi.KPIPersonOfMonth;
import trong.lixco.com.jpa.entitykpi.PositionJob;

public class PrintKPI {
	private String departmentName = "";
	private String hearder = "";
	private String employeeName = "";
	private String employeeNameManager = "";
	private String positionJobName = "";
	private double total = 0.0;

	private String headerGroupCode = "";
	private String headerGroupName = "";
	private double headerGroupWeighted = 0.0;
	private double headerGroupTotal = 0.0;

	private int no = 0;
	private String contentAppreciate = "";
	private double weighted = 0.0;
	private String formulaKPICode = "";
	private String dateRecei = "";
	private String dateAssignResult = "";
	private String planKPI = "";
	private String performKPI = "";
	private double ratioComplete = 0.0;
	private double ratioCompleteIsWeighted = 0.0;
	private String unit = "";
	private String sourceVerify = "";

	public PrintKPI(KPIPersonOfMonth kpiPersonOfMonth, boolean add, EmpPJobService empPJobService,
			MemberServicePublic memberServicePublic, PositionJobService positionJobService) {
		Member mem = null;
		try {
			mem = memberServicePublic.findByCode(kpiPersonOfMonth.getKpiPerson().getCodeEmp());
			departmentName = mem.getDepartment().getName();
		} catch (Exception e) {
		}
		try {
			hearder = "KPIs CÁ NHÂN THÁNG " + kpiPersonOfMonth.getKpiPerson().getKmonth() + "/"
					+ kpiPersonOfMonth.getKpiPerson().getKyear();
		} catch (Exception e) {
		}
		try {
			employeeName = mem.getName();
		} catch (Exception e) {
		}
		try {
			employeeNameManager = memberServicePublic.findByCode(mem.getDepartment().getCodeMem()).getName();
		} catch (Exception e) {
		}
		try {
			List<EmpPJob> empPJobs = empPJobService.findByEmployee(mem.getCode());
			for (int i = 0; i < empPJobs.size(); i++) {
				try {
					positionJobName += positionJobService.findByCode(empPJobs.get(i).getCodePJob()).getName() + "\r\n";
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
		}
		try {
			total = kpiPersonOfMonth.getKpiPerson().getTotal();
		} catch (Exception e) {
		}
		try {
			headerGroupCode = "I";
			headerGroupName = "Phẩm chất - thái độ - hành vi";
			headerGroupWeighted = 30.0;
			headerGroupTotal = kpiPersonOfMonth.getKpiPerson().getTotalHV();

		} catch (Exception e) {
		}
	}

	public PrintKPI(KPIPersonOfMonth kpiPersonOfMonth, SimpleDateFormat sf, EmpPJobService empPJobService,
			MemberServicePublic memberServicePublic, PositionJobService positionJobService) {
		Member mem = null;
		try {
			mem = memberServicePublic.findByCode(kpiPersonOfMonth.getKpiPerson().getCodeEmp());
			departmentName = mem.getDepartment().getName();
		} catch (Exception e) {
		}
		try {
			hearder = "KPIs CÁ NHÂN THÁNG " + kpiPersonOfMonth.getKpiPerson().getKmonth() + "/"
					+ kpiPersonOfMonth.getKpiPerson().getKyear();
		} catch (Exception e) {
		}
		try {
			employeeName = mem.getName();
		} catch (Exception e) {
		}
		try {
			employeeNameManager = memberServicePublic.findByCode(mem.getDepartment().getCodeMem()).getName();
		} catch (Exception e) {
		}
		try {
			List<EmpPJob> empPJobs = empPJobService.findByEmployee(mem.getCode());
			for (int i = 0; i < empPJobs.size(); i++) {
				try {
					positionJobName += positionJobService.findByCode(empPJobs.get(i).getCodePJob()).getName() + "\r\n";
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			total = kpiPersonOfMonth.getKpiPerson().getTotal();
		} catch (Exception e) {
		}
		try {
			if (kpiPersonOfMonth.isBehaviour()) {
				headerGroupCode = "I";
				headerGroupName = "Phẩm chất - thái độ - hành vi";
				headerGroupWeighted = 30.0;
				headerGroupTotal = kpiPersonOfMonth.getKpiPerson().getTotalHV();
			} else {
				headerGroupCode = "II";
				headerGroupName = "Khả năng giải quyết công việc";
				headerGroupWeighted = 70.0;
				headerGroupTotal = kpiPersonOfMonth.getKpiPerson().getTotalCV();
			}
		} catch (Exception e) {
		}
		try {
			no = kpiPersonOfMonth.getNo();
		} catch (Exception e) {
		}
		try {
			contentAppreciate = kpiPersonOfMonth.getContentAppreciate();
		} catch (Exception e) {
		}
		try {
			weighted = kpiPersonOfMonth.getWeighted();
		} catch (Exception e) {
		}
		try {
			formulaKPICode = kpiPersonOfMonth.getCodeFormula();
		} catch (Exception e) {
		}
		try {
			dateRecei = sf.format(kpiPersonOfMonth.getKpiPerson().getDateRecei());
		} catch (Exception e) {
		}
		try {
			dateAssignResult = sf.format(kpiPersonOfMonth.getKpiPerson().getDateAssignResult());
		} catch (Exception e) {
		}
		try {
			planKPI = kpiPersonOfMonth.getPlanKPI();
		} catch (Exception e) {
		}
		try {
			performKPI = kpiPersonOfMonth.getPerformKPI();
		} catch (Exception e) {
		}
		try {
			ratioComplete = kpiPersonOfMonth.getRatioComplete();
		} catch (Exception e) {
		}
		try {
			ratioCompleteIsWeighted = kpiPersonOfMonth.getRatioCompleteIsWeighted();
		} catch (Exception e) {
		}
		try {
			unit = kpiPersonOfMonth.getUnit();
		} catch (Exception e) {
		}
		try {
			sourceVerify = kpiPersonOfMonth.getSourceVerify();
		} catch (Exception e) {
		}
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getHearder() {
		return hearder;
	}

	public void setHearder(String hearder) {
		this.hearder = hearder;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getPositionJobName() {
		return positionJobName;
	}

	public void setPositionJobName(String positionJobName) {
		this.positionJobName = positionJobName;
	}

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
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

	public String getFormulaKPICode() {
		return formulaKPICode;
	}

	public void setFormulaKPICode(String formulaKPICode) {
		this.formulaKPICode = formulaKPICode;
	}

	public String getDateRecei() {
		return dateRecei;
	}

	public void setDateRecei(String dateRecei) {
		this.dateRecei = dateRecei;
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

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getSourceVerify() {
		return sourceVerify;
	}

	public void setSourceVerify(String sourceVerify) {
		this.sourceVerify = sourceVerify;
	}

	public String getEmployeeNameManager() {
		return employeeNameManager;
	}

	public void setEmployeeNameManager(String employeeNameManager) {
		this.employeeNameManager = employeeNameManager;
	}

	public String getHeaderGroupCode() {
		return headerGroupCode;
	}

	public void setHeaderGroupCode(String headerGroupCode) {
		this.headerGroupCode = headerGroupCode;
	}

	public String getHeaderGroupName() {
		return headerGroupName;
	}

	public void setHeaderGroupName(String headerGroupName) {
		this.headerGroupName = headerGroupName;
	}

	public String getDateAssignResult() {
		return dateAssignResult;
	}

	public void setDateAssignResult(String dateAssignResult) {
		this.dateAssignResult = dateAssignResult;
	}

	public double getHeaderGroupWeighted() {
		return headerGroupWeighted;
	}

	public void setHeaderGroupWeighted(double headerGroupWeighted) {
		this.headerGroupWeighted = headerGroupWeighted;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public double getHeaderGroupTotal() {
		return headerGroupTotal;
	}

	public void setHeaderGroupTotal(double headerGroupTotal) {
		this.headerGroupTotal = headerGroupTotal;
	}

}
