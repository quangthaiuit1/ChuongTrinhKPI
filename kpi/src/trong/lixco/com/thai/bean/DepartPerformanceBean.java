package trong.lixco.com.thai.bean;
<<<<<<< HEAD

=======
>>>>>>> kpi_canhan
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;
import org.joda.time.DateTime;
import org.omnifaces.cdi.ViewScoped;
<<<<<<< HEAD
import org.primefaces.context.RequestContext;
=======
>>>>>>> kpi_canhan

import trong.lixco.com.account.servicepublics.DepartmentServicePublic;
import trong.lixco.com.account.servicepublics.DepartmentServicePublicProxy;
import trong.lixco.com.account.servicepublics.Member;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.classInfor.TagetDepartInfo;
<<<<<<< HEAD
import trong.lixco.com.ejb.servicekpi.FormulaKPIService;
import trong.lixco.com.ejb.servicekpi.KPIDepMonthService;
import trong.lixco.com.ejb.thai.kpi.DepPerformanceService;
import trong.lixco.com.jpa.entitykpi.FormulaKPI;
import trong.lixco.com.jpa.entitykpi.KPIDepMonth;
import trong.lixco.com.jpa.entitykpi.KPIDepOfMonth;
=======
import trong.lixco.com.ejb.thai.kpi.DepPerformanceService;
>>>>>>> kpi_canhan
import trong.lixco.com.jpa.thai.KPIDepPerformanceJPA;
import trong.lixco.com.thai.bean.entities.InfoDepPerformance;
import trong.lixco.com.util.Notify;

@Named
@ViewScoped
public class DepartPerformanceBean extends AbstractBean<KPIDepPerformanceJPA> {
	private static final long serialVersionUID = 1L;
	private Notify notify;

	private int year;

	private String codeDepart;

	private Member member;
	private DepartmentServicePublic departmentServicePublic;

	@Inject
	private Logger logger;
<<<<<<< HEAD

=======
>>>>>>> kpi_canhan
	@Override
	protected Logger getLogger() {
		return logger;
	}
<<<<<<< HEAD

	// Thai
=======
	//Thai
>>>>>>> kpi_canhan
	KPIDepPerformanceJPA kpiDepPerformance;

	@Override
	public void initItem() {
		kpiDepPerformance = new KPIDepPerformanceJPA();
		year = new DateTime().getYear();
		try {
			departmentServicePublic = new DepartmentServicePublicProxy();
		} catch (Exception e) {
			e.printStackTrace();
		}
		member = getAccount().getMember();
		codeDepart = member.getDepartment().getCode();

		searchItem();
	}

	public String nameDepart() {
		try {
			return departmentServicePublic.findByCode("code", codeDepart).getName();
		} catch (Exception e) {
			return "";
		}
	}

	public void createOrUpdate() {
		notify = new Notify(FacesContext.getCurrentInstance());
		try {
			if (kpiDepPerformance != null) {
				if (!"".equals(kpiDepPerformance.getYear() != 0)) {
					if (kpiDepPerformance.getId() == null) {
						if (allowSave(null)) {
							if (codeDepart != null) {
								kpiDepPerformance = installSave(kpiDepPerformance);
								kpiDepPerformance.setCodeDepart(codeDepart);
								kpiDepPerformance = DEPARTMENT_PERFORMANCE_SERVICE.create(kpiDepPerformance);
								listDepartPerformance.add(0, kpiDepPerformance);
								writeLogInfo("Tạo mới " + kpiDepPerformance.toString());
								notify.success();
							} else {
								notify.warning("Tài khoản không thuộc phòng ban nào.");
							}
						} else {
							kpiDepPerformanceUpdate = new KPIDepPerformanceJPA();
							notify.warningDetail();
						}
					} else {
						if (allowUpdate(null)) {
							kpiDepPerformance = installUpdate(kpiDepPerformance);
							kpiDepPerformance = DEPARTMENT_PERFORMANCE_SERVICE.update(kpiDepPerformance);
							int index = listDepartPerformance.indexOf(kpiDepPerformance);
							listDepartPerformance.set(index, kpiDepPerformance);
							writeLogInfo("Cập nhật " + kpiDepPerformance.toString());
							notify.success();
						} else {
							notify.warningDetail();
						}
					}
					reset();
				} else {
					notify.warning("Điền đầy đủ thông tin!");
				}
			}
		} catch (Exception e) {
			writeLogError(e.getLocalizedMessage());
			notify.warning("Mã đã tồn tại!");
		}
	}

	public void reset() {
		int year = kpiDepPerformance.getYear();
		kpiDepPerformance = new KPIDepPerformanceJPA();
		kpiDepPerformance.setYear(year);
	}

	public void showEdit() {
		this.kpiDepPerformance = kpiDepPerformanceUpdate;
		codeDepart = this.kpiDepPerformance.getCodeDepart();
	}

	public void delete() {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (kpiDepPerformance.getId() != null) {
			if (allowDelete(null)) {
				boolean status = DEPARTMENT_PERFORMANCE_SERVICE.delete(kpiDepPerformance);
				if (status) {
					listDepartPerformance.remove(kpiDepPerformance);
					writeLogInfo("Xoá " + kpiDepPerformance.toString());
					reset();
					notify.success();
				} else {
					writeLogError("Lỗi khi xoá " + kpiDepPerformance.toString());
					notify.error();
				}
			} else {
				notify.warningDetail();
			}
		} else {
			notify.warning("Chưa chọn trong danh sách!");
		}
	}

	List<TagetDepartInfo> tagetDepartInfos;
<<<<<<< HEAD

	// Thai
=======
	
	//Thai
>>>>>>> kpi_canhan
	@Inject
	DepPerformanceService DEPARTMENT_PERFORMANCE_SERVICE;
	List<InfoDepPerformance> listInfoDepartPerformance;
	List<KPIDepPerformanceJPA> listDepartPerformance;
	private KPIDepPerformanceJPA kpiDepPerformanceUpdate;
<<<<<<< HEAD

=======
>>>>>>> kpi_canhan
	public void searchItem() {
		if (getAccount().isAdmin())
			listDepartPerformance = DEPARTMENT_PERFORMANCE_SERVICE.find(year, null);
		else
			listDepartPerformance = DEPARTMENT_PERFORMANCE_SERVICE.find(year, member.getDepartment().getCode());

<<<<<<< HEAD
		Map<String, List<KPIDepPerformanceJPA>> datagroups1 = listDepartPerformance.stream()
				.collect(Collectors.groupingBy(p -> p.getCodeDepart(), Collectors.toList()));
=======
		Map<String, List<KPIDepPerformanceJPA>> datagroups1 = listDepartPerformance.stream().collect(
				Collectors.groupingBy(p -> p.getCodeDepart(), Collectors.toList()));
>>>>>>> kpi_canhan

		listInfoDepartPerformance = new ArrayList<InfoDepPerformance>();
		for (String key : datagroups1.keySet()) {
			List<KPIDepPerformanceJPA> invs = datagroups1.get(key);
			try {
				InfoDepPerformance tgi = new InfoDepPerformance();
				tgi.setNameDepart(departmentServicePublic.findByCode("code", key).getName());
				tgi.setListDepPerformance(invs);
				listInfoDepartPerformance.add(tgi);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

	}
<<<<<<< HEAD

	private List<FormulaKPI> formulaKPIs;
	private FormulaKPI formulaKPISelect;
	private KPIDepPerformanceJPA kpy;
	@Inject
	private FormulaKPIService formulaKPIService;

	public void showListFormula(KPIDepPerformanceJPA param) {
		notify = new Notify(FacesContext.getCurrentInstance());
		formulaKPIs = formulaKPIService.findAll();
		formulaKPISelect = param.getFormulaKPI();
		kpy = param;
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('dialogFormula1').show();");
	}

	@Inject
	private KPIDepMonthService kpiDepMonthService;

	public void updateFormula() {
		notify = new Notify(FacesContext.getCurrentInstance());
		try {
			if (formulaKPISelect == null) {
				notify.warning("Chưa chọn công thức!");
			} else {
				for (int i = 0; i < listDepartPerformance.size(); i++) {
					KPIDepPerformanceJPA kp = listDepartPerformance.get(i);
					if (listDepartPerformance.get(i).getIndex() == kpy.getIndex()) {
						kpy.setFormulaKPI(formulaKPISelect);
						kpy.setComputation(formulaKPISelect.getCode());
						listDepartPerformance.set(i, kpy);
						notify.success();
						break;
					}
				}
			}

		} catch (Exception e) {
		}
	}

=======
>>>>>>> kpi_canhan
	public List<InfoDepPerformance> getListInfoDepartPerformance() {
		return listInfoDepartPerformance;
	}

	public void setListInfoDepartPerformance(List<InfoDepPerformance> listInfoDepartPerformance) {
		this.listInfoDepartPerformance = listInfoDepartPerformance;
	}

	public List<KPIDepPerformanceJPA> getListDepartPerformance() {
		return listDepartPerformance;
	}

	public void setListDepartPerformance(List<KPIDepPerformanceJPA> listDepartPerformance) {
		this.listDepartPerformance = listDepartPerformance;
	}
<<<<<<< HEAD

	// End Thai
=======
	
	//End Thai
>>>>>>> kpi_canhan

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public List<TagetDepartInfo> getTagetDepartInfos() {
		return tagetDepartInfos;
	}

	public void setTagetDepartInfos(List<TagetDepartInfo> tagetDepartInfos) {
		this.tagetDepartInfos = tagetDepartInfos;
	}

	public String getCodeDepart() {
		return codeDepart;
	}

	public void setCodeDepart(String codeDepart) {
		this.codeDepart = codeDepart;
	}

	public KPIDepPerformanceJPA getKpiDepPerformanceUpdate() {
		return kpiDepPerformanceUpdate;
	}

	public void setKpiDepPerformanceUpdate(KPIDepPerformanceJPA kpiDepPerformanceUpdate) {
		this.kpiDepPerformanceUpdate = kpiDepPerformanceUpdate;
	}

	public KPIDepPerformanceJPA getKpiDepPerformance() {
		return kpiDepPerformance;
	}

	public void setKpiDepPerformance(KPIDepPerformanceJPA kpiDepPerformance) {
		this.kpiDepPerformance = kpiDepPerformance;
	}

<<<<<<< HEAD
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

	public KPIDepPerformanceJPA getKpy() {
		return kpy;
	}

	public void setKpy(KPIDepPerformanceJPA kpy) {
		this.kpy = kpy;
	}

	public FormulaKPIService getFormulaKPIService() {
		return formulaKPIService;
	}

	public void setFormulaKPIService(FormulaKPIService formulaKPIService) {
		this.formulaKPIService = formulaKPIService;
	}

=======
>>>>>>> kpi_canhan
}
