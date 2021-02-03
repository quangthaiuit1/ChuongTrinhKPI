package trong.lixco.com.thai.baocao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.joda.time.LocalDate;

import trong.lixco.com.account.servicepublics.Department;
import trong.lixco.com.account.servicepublics.DepartmentServicePublic;
import trong.lixco.com.account.servicepublics.DepartmentServicePublicProxy;
import trong.lixco.com.account.servicepublics.Member;
import trong.lixco.com.account.servicepublics.MemberServicePublic;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.ejb.thai.kpi.EmployeeDontKPIEverService;
import trong.lixco.com.ejb.thai.kpi.EmployeeDontKPIService;
import trong.lixco.com.ejb.thai.kpi.EmployeeSyncDataCenterService;
import trong.lixco.com.jpa.entitykpi.KPIPerson;
import trong.lixco.com.jpa.thai.EmployeeDontKPI;
import trong.lixco.com.jpa.thai.EmployeeDontKPIEver;
import trong.lixco.com.jpa.thai.EmployeeSyncDataCenter;
import trong.lixco.com.thai.apitrong.EmployeeData;
import trong.lixco.com.thai.apitrong.EmployeeDataService;
import trong.lixco.com.thai.bean.entities.Month;
import trong.lixco.com.thai.bean.staticentity.MessageView;
import trong.lixco.com.util.DepartmentUtil;

@ManagedBean
@ViewScoped
public class CaiDatNgoaiLeBean extends AbstractBean<KPIPerson> {

	private static final long serialVersionUID = 1L;
	private Department departmentSearch;
	private List<Department> departmentSearchs;
	private Member member;
	private List<EmployeeData> emplsByDepart;
	private List<EmployeeSyncDataCenter> allEmpl;
	private List<EmployeeSyncDataCenter> allEmplFilter;
	private String menuExceptionSelected;
	private List<Month> months;
	private boolean renderTableMonth = true;
	private int yearSearch = 0;

	// table member Trong

	DepartmentServicePublic departmentServicePublic;
	MemberServicePublic memberServicePublic;
	@Inject
	private EmployeeSyncDataCenterService EMPLOYEE_SYNC_DATA_CENTER_SERVICE;
	@Inject
	private EmployeeDontKPIService EMPLOYEE_DONT_KPI_SERVICE;
	@Inject
	private EmployeeDontKPIEverService EMPLOYEE_DONT_KPI_EVER_SERVICE;

	@Override
	protected void initItem() {
		try {
			LocalDate lc = new LocalDate();
			yearSearch = lc.getYear();
			departmentServicePublic = new DepartmentServicePublicProxy();
			// memberServicePublic = new MemberServicePublicProxy();
			departmentSearchs = new ArrayList<Department>();
			member = getAccount().getMember();
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
			allEmpl = EMPLOYEE_SYNC_DATA_CENTER_SERVICE.findAll();
			if (allEmpl.isEmpty()) {
				allEmpl = new ArrayList<>();
			}
			months = createMonth();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// cap nhat danh sach thai san hoac khong xet kpi
	public void saveOrUpdate() {
		try {
			if (menuExceptionSelected.equals("KHONGLAM")) {
				for (int i = 0; i < allEmpl.size(); i++) {
					if (allEmpl.get(i).isSelect()) {
						List<EmployeeDontKPIEver> empls = EMPLOYEE_DONT_KPI_EVER_SERVICE
								.findByEmpl(allEmpl.get(i).getEmployee_code());
						if (empls.isEmpty()) {
							EmployeeDontKPIEver e = new EmployeeDontKPIEver(allEmpl.get(i).getEmployee_code());
							e.setCreatedUser(member.getCode());
							EMPLOYEE_DONT_KPI_EVER_SERVICE.create(e);
						}
					}
				}
			} else {
				for (int i = 0; i < allEmpl.size(); i++) {
					if (allEmpl.get(i).isSelect()) {
						for (int j = 0; j < months.size(); j++) {
							if (months.get(j).isSelect()) {
								// tim thu co chua
								List<EmployeeDontKPI> epls = EMPLOYEE_DONT_KPI_SERVICE.findByEmplMonthYear(
										allEmpl.get(i).getEmployee_code(), months.get(j).getValue(), yearSearch);
								if (epls.isEmpty()) {
									EmployeeDontKPI e = new EmployeeDontKPI();
									e.setCreatedDate(new Date());
									e.setCreatedUser(member.getCode());
									e.setEmployee_code(allEmpl.get(i).getEmployee_code());
									e.setMonth(months.get(j).getValue());
									e.setYear(yearSearch);
									if (menuExceptionSelected.equals("KHONGXET")) {
										e.setIs_temp(true);
									}
									if (menuExceptionSelected.equals("THAISAN")) {
										e.setIs_thaisan(true);
									}
									if (menuExceptionSelected.equals("NGHIOM")) {
										e.setIs_nghiom(true);
									}
									EMPLOYEE_DONT_KPI_SERVICE.create(e);
								}
							}
						}
					}
				}
			}
			MessageView.INFO("Thành công");
		} catch (Exception e) {
			e.printStackTrace();
			MessageView.ERROR("Lỗi");
		}
	}

	public void ajaxHandleSelectDepartment() {
		try {

			EmployeeData[] allEmployeeArrayNew = EmployeeDataService.timtheophongban(departmentSearch.getCode());
			// emplsByDepart = Arrays.asList(allEmployeeArrayNew);
			List<String> emplsCode = new ArrayList<>();
			for (EmployeeData e : allEmployeeArrayNew) {
				emplsCode.add(e.getCode());
			}
			allEmpl = EMPLOYEE_SYNC_DATA_CENTER_SERVICE.findByListEmplCode(emplsCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void ajaxHandleSelectOneMenu() {
		if (menuExceptionSelected.equals("KHONGXET") || menuExceptionSelected.equals("THAISAN")) {
			renderTableMonth = true;
		}
		if (menuExceptionSelected.equals("KHONGLAM")) {
			renderTableMonth = false;
		}
	}

	public List<Month> createMonth() {
		List<Month> monthTemp = new ArrayList<>();
		long id = 1;
		Month one = new Month(id, "Tháng 1", 1);
		monthTemp.add(one);
		id = id + 1;
		Month one1 = new Month(id, "Tháng 2", 2);
		monthTemp.add(one1);
		id = id + 1;
		Month one2 = new Month(id, "Tháng 3", 3);
		monthTemp.add(one2);
		id = id + 1;
		Month one3 = new Month(id, "Tháng 4", 4);
		monthTemp.add(one3);
		id = id + 1;
		Month one4 = new Month(id, "Tháng 5", 5);
		monthTemp.add(one4);
		id = id + 1;
		Month one5 = new Month(id, "Tháng 6", 6);
		monthTemp.add(one5);
		id = id + 1;
		Month one6 = new Month(id, "Tháng 7", 7);
		monthTemp.add(one6);
		id = id + 1;
		Month one7 = new Month(id, "Tháng 8", 8);
		monthTemp.add(one7);
		id = id + 1;
		Month one8 = new Month(id, "Tháng 9", 9);
		monthTemp.add(one8);
		id = id + 1;
		Month one9 = new Month(id, "Tháng 10", 10);
		monthTemp.add(one9);
		id = id + 1;
		Month one10 = new Month(id, "Tháng 11", 11);
		monthTemp.add(one10);
		id = id + 1;
		Month one11 = new Month(id, "Tháng 12", 12);
		monthTemp.add(one11);
		return monthTemp;
	}

	public void syncDataCenter() {
		try {
			EmployeeData[] allEmployeeCenter = EmployeeDataService.tatcanhanvien();
			for (int i = 0; i < allEmployeeCenter.length; i++) {
				EmployeeSyncDataCenter eTemp = EMPLOYEE_SYNC_DATA_CENTER_SERVICE
						.findByEmplCode(allEmployeeCenter[i].getCode());
				if (eTemp.getId() == null || eTemp.getId() == 0) {
					eTemp.setCreatedUser(member.getCode());
					eTemp.setCreatedDate(new Date());
					eTemp.setEmployee_code(allEmployeeCenter[i].getCode());
					eTemp.setEmployee_name(allEmployeeCenter[i].getName());
					eTemp.setDepartment_code(allEmployeeCenter[i].getCodeDepart());
					eTemp.setDepartment_name(allEmployeeCenter[i].getNameDepart());
					EMPLOYEE_SYNC_DATA_CENTER_SERVICE.create(eTemp);
				}
			}
			allEmpl = EMPLOYEE_SYNC_DATA_CENTER_SERVICE.findAll();
			MessageView.INFO("Thành công");
		} catch (Exception e) {
			MessageView.INFO("Lỗi");
			e.printStackTrace();
		}
	}

	@Override
	protected Logger getLogger() {
		return null;
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

	public List<EmployeeData> getEmplsByDepart() {
		return emplsByDepart;
	}

	public void setEmplsByDepart(List<EmployeeData> emplsByDepart) {
		this.emplsByDepart = emplsByDepart;
	}

	public List<EmployeeSyncDataCenter> getAllEmpl() {
		return allEmpl;
	}

	public void setAllEmpl(List<EmployeeSyncDataCenter> allEmpl) {
		this.allEmpl = allEmpl;
	}

	public List<EmployeeSyncDataCenter> getAllEmplFilter() {
		return allEmplFilter;
	}

	public void setAllEmplFilter(List<EmployeeSyncDataCenter> allEmplFilter) {
		this.allEmplFilter = allEmplFilter;
	}

	public String getMenuExceptionSelected() {
		return menuExceptionSelected;
	}

	public void setMenuExceptionSelected(String menuExceptionSelected) {
		this.menuExceptionSelected = menuExceptionSelected;
	}

	public List<Month> getMonths() {
		return months;
	}

	public void setMonths(List<Month> months) {
		this.months = months;
	}

	public boolean isRenderTableMonth() {
		return renderTableMonth;
	}

	public void setRenderTableMonth(boolean renderTableMonth) {
		this.renderTableMonth = renderTableMonth;
	}

	public int getYearSearch() {
		return yearSearch;
	}

	public void setYearSearch(int yearSearch) {
		this.yearSearch = yearSearch;
	}
}
