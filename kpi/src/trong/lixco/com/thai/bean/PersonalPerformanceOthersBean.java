package trong.lixco.com.thai.bean;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.jboss.logging.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import trong.lixco.com.account.servicepublics.Department;
import trong.lixco.com.account.servicepublics.DepartmentServicePublic;
import trong.lixco.com.account.servicepublics.DepartmentServicePublicProxy;
import trong.lixco.com.account.servicepublics.Member;
import trong.lixco.com.account.servicepublics.MemberServicePublic;
import trong.lixco.com.account.servicepublics.MemberServicePublicProxy;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.ejb.service.ParamReportDetailService;
import trong.lixco.com.ejb.servicekpi.BehaviourPersonService;
import trong.lixco.com.ejb.servicekpi.FormulaKPIService;
import trong.lixco.com.ejb.servicekpi.KPIPersonService;
import trong.lixco.com.ejb.servicekpi.OrientationPersonService;
import trong.lixco.com.ejb.servicekpi.PositionJobService;
import trong.lixco.com.jpa.entitykpi.BehaviourPerson;
import trong.lixco.com.jpa.entitykpi.FormulaKPI;
import trong.lixco.com.jpa.entitykpi.KPIDepOfMonth;
import trong.lixco.com.jpa.entitykpi.KPIPerson;
import trong.lixco.com.jpa.entitykpi.KPIPersonOfMonth;
import trong.lixco.com.jpa.entitykpi.OrientationPerson;
import trong.lixco.com.jpa.entitykpi.PositionJob;
import trong.lixco.com.kpi.general.ApplicationBean;
import trong.lixco.com.util.DepartmentUtil;
import trong.lixco.com.util.Notify;
@ManagedBean
@ViewScoped
public class PersonalPerformanceOthersBean extends AbstractBean<KPIPerson>{

	private static final long serialVersionUID = 1L;
	private SimpleDateFormat sf;
	private Notify notify;
	private List<KPIPerson> kPIPersons;
	private List<KPIPerson> kPIPersonFilters;

	private boolean orverideData = false;

	private final int TOTALPARAM = 100;

	private KPIPerson kPIPerson;
	private KPIPerson kPIPersonEdit;
	private List<KPIPersonOfMonth> kpiPersonOfMonths;
	private List<KPIPersonOfMonth> kpiPersonOfMonthAdds;
	private List<KPIPersonOfMonth> kpiPersonOfMonthRemoves;

	// private DepartmentParent departmentParent, departmentParentSearch;
	private PositionJob positionJobSearch;
	// private List<DepartmentParent> departmentParents;
	private Department departmentSearch;
	private List<Department> departmentSearchs;
	private boolean isEmp = false;// la nhan vien

	// private List<Employee> employees;
	// private Employee employeeSearch;
	private int monthSearch = 0;
	private int yearSearch = 0;
	private boolean select = false;

	private int monthCopy = 0;
	private int yearCopy = 0;
	
	int totalHV = 100;
	double totalCV = 0;

	private int tabindex;

	private PositionJob positionJobSelect;
	// private List<DepartmentParent> departmentParentSearchs;

	private List<Boolean> list;
	private List<OrientationPerson> orientationPersons;

	private List<KPIDepOfMonth> kpiDepOfMonths;

	private List<BehaviourPerson> behaviourPersons;
	private List<FormulaKPI> formulaKPIs;
	private FormulaKPI formulaKPISelect;

	@Inject
	private FormulaKPIService formulaKPIService;
	@Inject
	private BehaviourPersonService behaviourPersonService;
	@Inject
	private ParamReportDetailService paramReportDetailService;
	@Inject
	private OrientationPersonService orientationPersonService;
	// @Inject
	// private DepartmentParentService departmentParentService;
	@Inject
	private KPIPersonService kPIPersonService;
	@Inject
	private PositionJobService positionJobService;
	// @Inject
	// private EmployeeService employeeService;
	@Inject
	private Logger logger;
	@Inject
	private ApplicationBean applicationBean;

	private Department department;
	private List<Department> departments;
	private Member member;
	private List<Member> members;

	DepartmentServicePublic departmentServicePublic;
	MemberServicePublic memberServicePublic;
	
	@Override
	public void initItem() {
		sf = new SimpleDateFormat("dd/MM/yyyy");
		kPIPersons = new ArrayList<KPIPerson>();
		kpiPersonOfMonths = new ArrayList<KPIPersonOfMonth>();
		kpiPersonOfMonthAdds = new ArrayList<KPIPersonOfMonth>();
		kpiPersonOfMonthRemoves = new ArrayList<KPIPersonOfMonth>();
		orientationPersons = new ArrayList<OrientationPerson>();

		try {
			departmentServicePublic = new DepartmentServicePublicProxy();
			memberServicePublic = new MemberServicePublicProxy();
			departments = new ArrayList<Department>();
			members = new ArrayList<Member>();
			member = getAccount().getMember();
			departmentSearchs = new ArrayList<Department>();
			if (getAccount().isAdmin()) {
				Department[] deps = departmentServicePublic.findAll();
				for (int i = 0; i < deps.length; i++) {
					if (deps[i].getLevelDep() != null)
						if (deps[i].getLevelDep().getLevel() > 1)
							departmentSearchs.add(deps[i]);
				}

			} else {
				departmentSearchs.add(member.getDepartment());
			}
			if (departmentSearchs.size() != 0) {
				departmentSearchs = DepartmentUtil.sort(departmentSearchs);
				departmentSearch = departmentSearchs.get(0);
			}
		} catch (Exception e) {
		}

		LocalDate lc = new LocalDate();
		monthSearch = lc.getMonthOfYear();
		yearSearch = lc.getYear();

		list = Arrays.asList(true, true, true, true, true, false, false, false, false, false, false, true, true, true,
				true, true, true, false, true);
		tabindex = 0;
		reset();
		searchItem();
	}
	public void showEdit() {
		kpiPersonOfMonthRemoves.clear();
		kpiPersonOfMonthAdds.clear();
		kpiPersonOfMonths.clear();
		if (kPIPersonEdit != null && kPIPersonEdit.getId() != null) {
			KPIPerson od = kPIPersonService.findByIdAll(kPIPersonEdit.getId());
			if (od != null) {
				kPIPerson = od;
				try {
					member = memberServicePublic.findByCode(kPIPerson.getCodeEmp());
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				for (int i = 0; i < od.getKpiPersonOfMonths().size(); i++) {
					if (od.getKpiPersonOfMonths().get(i).isBehaviour()) {
						kpiPersonOfMonthAdds.add(od.getKpiPersonOfMonths().get(i));
					} else {
						kpiPersonOfMonths.add(od.getKpiPersonOfMonths().get(i));
					}
				}
				for (int i = 0; i < kpiPersonOfMonthAdds.size(); i++) {
					kpiPersonOfMonthAdds.get(i).setNo(i + 1);
				}
				for (int i = 0; i < kpiPersonOfMonths.size(); i++) {
					kpiPersonOfMonths.get(i).setIndex(i + 1);
				}
			}
		}

	}
	public void showBehaviour() {
		behaviourPersons = behaviourPersonService.findAll();
		System.out.println(behaviourPersons.size());
	}

	public void addBehaviour() {
		for (int i = 0; i < behaviourPersons.size(); i++) {
			if (behaviourPersons.get(i).isSelect()) {
				KPIPersonOfMonth item = new KPIPersonOfMonth();
				item.setBehaviour(true);
				item.setRatioComplete(-behaviourPersons.get(i).getMinusPoint());
				item.setRatioCompleteIsWeighted((-behaviourPersons.get(i).getMinusPoint() * 10) / 100);
				item.setContentAppreciate(behaviourPersons.get(i).getContent());
				kpiPersonOfMonthAdds.add(item);
			}

		}
		for (int i = 0; i < kpiPersonOfMonthAdds.size(); i++) {
			kpiPersonOfMonthAdds.get(i).setNo(i + 1);
		}
	}
	//Tao moi
	public void createNew() {
		
	}
	public void caculatorResult() {
		totalCV = 0;
		totalHV = 100;
		for (int i = 0; i < kpiPersonOfMonths.size(); i++) {
			KPIPersonOfMonth item = kpiPersonOfMonths.get(i);
			// Lay bo cong thuc xem tinh theo sua dung cong thuc nao
			String KH = item.getPlanKPI();// Ke hoach
			String TH = item.getPerformKPI();// thuc hien
			// Khai bao bo script de tinh bieu thuc
			ScriptEngineManager mgr = new ScriptEngineManager();
			ScriptEngine engine = mgr.getEngineByName("JavaScript");
			// kiem tra bieu thuc dung
			boolean iscacul = true;
			boolean isdate = false;
			Date THd = null;
			Date KHd = null;
			try {
				THd = sf.parse(TH);
			} catch (Exception e) {
			}
			try {
				KHd = sf.parse(KH);
			} catch (Exception e) {
			}
			if (THd != null && KHd != null) {
				try {
					engine.eval("var TH = " + "new Date(" + (THd.getYear() + 1900) + ", " + (THd.getMonth()) + ", "
							+ (THd.getDate() + 1) + ");");
					engine.eval("var KH = " + "new Date(" + (KHd.getYear() + 1900) + ", " + (KHd.getMonth()) + ", "
							+ (KHd.getDate() + 1) + ");");
					isdate = true;
				} catch (Exception e) {
					iscacul = false;
					kpiPersonOfMonths.get(i).setRatioComplete(0);
					kpiPersonOfMonths.get(i).setRatioCompleteIsWeighted(0);
					totalCV += 0;
					e.printStackTrace();
				}

			} else {
				try {
					engine.eval("var TH = " + TH);
					engine.eval("var KH = " + KH);
				} catch (Exception e) {
					iscacul = false;
					kpiPersonOfMonths.get(i).setRatioComplete(0);
					kpiPersonOfMonths.get(i).setRatioCompleteIsWeighted(0);
					totalCV += 0;
				}
			}
			if (iscacul) {
				try {
					String KQ = "";
					if (item.getFormulaKPI() != null) {
						KQ = item.getFormulaKPI().getCodeVSGood();
					} else {
						KQ = item.getOrientationPerson().getFormulaKPI().getCodeVSGood();
					}
					engine.eval("var KQ = " + KQ);
					if (!"".equals(item.getParamA()))
						engine.eval("var A = " + item.getParamA());
					if (!"".equals(item.getParamB()))
						engine.eval("var B = " + item.getParamB());
					if (!"".equals(item.getParamC()))
						engine.eval("var C = " + item.getParamC());
					if (!"".equals(item.getParamD()))
						engine.eval("var D = " + item.getParamD());
					if (!"".equals(item.getParamE()))
						engine.eval("var E = " + item.getParamE());
					if (!"".equals(item.getParamF()))
						engine.eval("var F = " + item.getParamF());
					if ((boolean) engine.get("KQ")) {
						// ket qua thu hien theo tinh dung
						String RESULT = "";
						if (item.getFormulaKPI() != null) {
							RESULT = item.getFormulaKPI().getCodeGood();
						} else {
							RESULT = item.getOrientationPerson().getFormulaKPI().getCodeGood();
						}
						engine.eval("var RESULT = " + RESULT);
					} else {
						// ket qua false
						String code = "";
						if (item.getFormulaKPI() != null) {
							code = item.getFormulaKPI().getCodeNotGood();
						} else {
							code = item.getOrientationPerson().getFormulaKPI().getCodeNotGood();
						}
						if (isdate) {
							if (code.contains("TH-KH") || code.contains("KH-TH")) {
								engine.eval("var KQ = Math.abs((TH.getTime() - KH.getTime()) / (24 * 60 * 60 * 1000))");
								code = code.replaceAll("TH-KH", "KQ");
								code = code.replaceAll("KH-TH", "KQ");
							}
						} else {
							if (code.contains("TH-KH") || code.contains("KH-TH")) {
								engine.eval("var KQ = Math.abs((TH - KH))");
								code = code.replaceAll("TH-KH", "KQ");
								code = code.replaceAll("KH-TH", "KQ");
							}
						}

						engine.eval("var RESULT = " + code);
					}
					Double result = (double) Math.round(Double.parseDouble(engine.get("RESULT").toString()));
					if (result < 0)
						result = 0.0;
					try {
						if (item.getFormulaKPI().isMax100())
							if (result > 100)
								result = 100.0;
					} catch (Exception e) {
					}

					kpiPersonOfMonths.get(i).setRatioComplete(Math.round(result));
					kpiPersonOfMonths.get(i).setRatioCompleteIsWeighted(
							Math.round((result * kpiPersonOfMonths.get(i).getWeighted()) / 100));
					totalCV += kpiPersonOfMonths.get(i).getRatioCompleteIsWeighted();

				} catch (Exception e) {
					kpiPersonOfMonths.get(i).setRatioComplete(0);
					kpiPersonOfMonths.get(i).setRatioCompleteIsWeighted(0);
					totalCV += 0;
				}

			}
		}
		totalCV = 0;
		for (int i = 0; i < kpiPersonOfMonthAdds.size(); i++) {
			if (kpiPersonOfMonthAdds.get(i).getContentAppreciate() != null
					&& !"".equals(kpiPersonOfMonthAdds.get(i).getContentAppreciate().trim()))
				totalHV += kpiPersonOfMonthAdds.get(i).getRatioCompleteIsWeighted();
		}
		// if (totalHV < 0)
		// totalHV = 0;
		kPIPerson.setTotalHV(Math.round(totalHV));
//		double a = (double) Math.round(totalCV * 10) / 10;
//		kPIPerson.setTotalCV(Math.round(a));
		kPIPerson.setTotal(Math.round(totalHV));
	}
	public void reset() {
		DateTime dt = new DateTime();
		kPIPerson = new KPIPerson();
		monthCopy = dt.getMonthOfYear();
		yearCopy = dt.getYear();
		kPIPerson.setDateRecei(new DateTime().withDayOfMonth(1).minusDays(1).toDate());
		kPIPerson.setDateAssignResult(new DateTime().plusMonths(1).withDayOfMonth(1).minusDays(1).toDate());
		kpiPersonOfMonths.clear();
		kpiPersonOfMonthAdds.clear();
		orientationPersons.clear();
		kpiPersonOfMonthRemoves.clear();
		try {
		} catch (Exception e) {
		}
	}

	public void searchItem() {
		try {
			kPIPersons = kPIPersonService.findRange(member.getCode(), 0, yearSearch);
		} catch (Exception e) {

		}

		try {
			List<String> members = new ArrayList<String>();
			Member mems[] = null;
			if (departmentSearch != null) {
				if (getAccount().isAdmin()) {
					mems = memberServicePublic.findByCodeDepart(departmentSearch.getCode());
					if (mems != null)
						for (int i = 0; i < mems.length; i++) {
							members.add(mems[i].getCode());
						}
					kPIPersons = kPIPersonService.findRange(null, monthSearch, yearSearch, members);
				} else {
					kPIPersons = kPIPersonService.findRange(member.getCode(), monthSearch, yearSearch);
				}

				for (int i = 0; i < kPIPersons.size(); i++) {
					try {
						kPIPersons.get(i)
								.setNameEmp(memberServicePublic.findByCode(kPIPersons.get(i).getCodeEmp()).getName());
					} catch (Exception e) {
					}
				}
			}
		} catch (Exception e) {

		}

	}
	@Override
	protected Logger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}
	public Notify getNotify() {
		return notify;
	}
	public void setNotify(Notify notify) {
		this.notify = notify;
	}
	public List<KPIPerson> getkPIPersons() {
		return kPIPersons;
	}
	public void setkPIPersons(List<KPIPerson> kPIPersons) {
		this.kPIPersons = kPIPersons;
	}
	public List<KPIPerson> getkPIPersonFilters() {
		return kPIPersonFilters;
	}
	public void setkPIPersonFilters(List<KPIPerson> kPIPersonFilters) {
		this.kPIPersonFilters = kPIPersonFilters;
	}
	public boolean isOrverideData() {
		return orverideData;
	}
	public void setOrverideData(boolean orverideData) {
		this.orverideData = orverideData;
	}
	public KPIPerson getkPIPerson() {
		return kPIPerson;
	}
	public void setkPIPerson(KPIPerson kPIPerson) {
		this.kPIPerson = kPIPerson;
	}
	public KPIPerson getkPIPersonEdit() {
		return kPIPersonEdit;
	}
	public void setkPIPersonEdit(KPIPerson kPIPersonEdit) {
		this.kPIPersonEdit = kPIPersonEdit;
	}
	public List<KPIPersonOfMonth> getKpiPersonOfMonths() {
		return kpiPersonOfMonths;
	}
	public void setKpiPersonOfMonths(List<KPIPersonOfMonth> kpiPersonOfMonths) {
		this.kpiPersonOfMonths = kpiPersonOfMonths;
	}
	public List<KPIPersonOfMonth> getKpiPersonOfMonthAdds() {
		return kpiPersonOfMonthAdds;
	}
	public void setKpiPersonOfMonthAdds(List<KPIPersonOfMonth> kpiPersonOfMonthAdds) {
		this.kpiPersonOfMonthAdds = kpiPersonOfMonthAdds;
	}
	public List<KPIPersonOfMonth> getKpiPersonOfMonthRemoves() {
		return kpiPersonOfMonthRemoves;
	}
	public void setKpiPersonOfMonthRemoves(List<KPIPersonOfMonth> kpiPersonOfMonthRemoves) {
		this.kpiPersonOfMonthRemoves = kpiPersonOfMonthRemoves;
	}
	public PositionJob getPositionJobSearch() {
		return positionJobSearch;
	}
	public void setPositionJobSearch(PositionJob positionJobSearch) {
		this.positionJobSearch = positionJobSearch;
	}
	public Department getDepartmentSearch() {
		return departmentSearch;
	}
	public void setDepartmentSearch(Department departmentSearch) {
		this.departmentSearch = departmentSearch;
	}
	public List<Department> getDepartmentSearchs() {
		return departmentSearchs;
	}
	public void setDepartmentSearchs(List<Department> departmentSearchs) {
		this.departmentSearchs = departmentSearchs;
	}
	public boolean isEmp() {
		return isEmp;
	}
	public void setEmp(boolean isEmp) {
		this.isEmp = isEmp;
	}
	public int getMonthSearch() {
		return monthSearch;
	}
	public void setMonthSearch(int monthSearch) {
		this.monthSearch = monthSearch;
	}
	public int getYearSearch() {
		return yearSearch;
	}
	public void setYearSearch(int yearSearch) {
		this.yearSearch = yearSearch;
	}
	public boolean isSelect() {
		return select;
	}
	public void setSelect(boolean select) {
		this.select = select;
	}
	public int getMonthCopy() {
		return monthCopy;
	}
	public void setMonthCopy(int monthCopy) {
		this.monthCopy = monthCopy;
	}
	public int getYearCopy() {
		return yearCopy;
	}
	public void setYearCopy(int yearCopy) {
		this.yearCopy = yearCopy;
	}
	public int getTabindex() {
		return tabindex;
	}
	public void setTabindex(int tabindex) {
		this.tabindex = tabindex;
	}
	public PositionJob getPositionJobSelect() {
		return positionJobSelect;
	}
	public void setPositionJobSelect(PositionJob positionJobSelect) {
		this.positionJobSelect = positionJobSelect;
	}
	public List<Boolean> getList() {
		return list;
	}
	public void setList(List<Boolean> list) {
		this.list = list;
	}
	public List<OrientationPerson> getOrientationPersons() {
		return orientationPersons;
	}
	public void setOrientationPersons(List<OrientationPerson> orientationPersons) {
		this.orientationPersons = orientationPersons;
	}
	public List<KPIDepOfMonth> getKpiDepOfMonths() {
		return kpiDepOfMonths;
	}
	public void setKpiDepOfMonths(List<KPIDepOfMonth> kpiDepOfMonths) {
		this.kpiDepOfMonths = kpiDepOfMonths;
	}
	public List<BehaviourPerson> getBehaviourPersons() {
		return behaviourPersons;
	}
	public void setBehaviourPersons(List<BehaviourPerson> behaviourPersons) {
		this.behaviourPersons = behaviourPersons;
	}
	public List<FormulaKPI> getFormulaKPIs() {
		return formulaKPIs;
	}
	public void setFormulaKPIs(List<FormulaKPI> formulaKPIs) {
		this.formulaKPIs = formulaKPIs;
	}
	public FormulaKPI getFormulaKPISelect() {
		return formulaKPISelect;
	}
	public void setFormulaKPISelect(FormulaKPI formulaKPISelect) {
		this.formulaKPISelect = formulaKPISelect;
	}
	public Department getDepartment() {
		return department;
	}
	public void setDepartment(Department department) {
		this.department = department;
	}
	public List<Department> getDepartments() {
		return departments;
	}
	public void setDepartments(List<Department> departments) {
		this.departments = departments;
	}
	public Member getMember() {
		return member;
	}
	public void setMember(Member member) {
		this.member = member;
	}
	public List<Member> getMembers() {
		return members;
	}
	public void setMembers(List<Member> members) {
		this.members = members;
	}
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	

}
