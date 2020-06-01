package trong.lixco.com.beankpi;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.jboss.logging.Logger;
import org.omnifaces.cdi.ViewScoped;

import trong.lixco.com.account.servicepublics.Account;
import trong.lixco.com.account.servicepublics.Department;
import trong.lixco.com.account.servicepublics.DepartmentServicePublic;
import trong.lixco.com.account.servicepublics.DepartmentServicePublicProxy;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.classInfor.OrienInfoEmpl;
import trong.lixco.com.ejb.servicekpi.EmpPJobService;
import trong.lixco.com.ejb.servicekpi.FormulaKPIService;
import trong.lixco.com.ejb.servicekpi.OrientationPersonService;
import trong.lixco.com.ejb.servicekpi.PositionJobService;
import trong.lixco.com.jpa.entitykpi.EmpPJob;
import trong.lixco.com.jpa.entitykpi.FormulaKPI;
import trong.lixco.com.jpa.entitykpi.OrientationPerson;
import trong.lixco.com.jpa.entitykpi.PositionJob;
import trong.lixco.com.kpi.general.ApplicationBean;
import trong.lixco.com.util.Notify;

@Named
@ViewScoped
// Dinh huong KPI ca nhan
public class OrientationPersonBean extends AbstractBean<OrientationPerson> {
	private static final long serialVersionUID = 1L;
	private Notify notify;
	private List<OrientationPerson> orientationPersons;
	private OrientationPerson orientationPerson;
	private OrientationPerson orientationPersonEdit;

	private List<FormulaKPI> formulaKPIs;
	private FormulaKPI formulaKPISelect;

	@Inject
	private FormulaKPIService formulaKPIService;
	@Inject
	private OrientationPersonService orientationPersonService;
	@Inject
	private PositionJobService positionJobService;
	@Inject
	private Logger logger;

	private Department department;
	private Department departmentSearch;
	private List<Department> departments;
	private List<PositionJob> positionJobs;
	private PositionJob positionJob;

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Inject
	ApplicationBean applicationBean;
	private Account account;

	@Override
	public void initItem() {
		orientationPerson = new OrientationPerson();
		orientationPersons = new ArrayList<OrientationPerson>();
		departments = new ArrayList<Department>();
		try {
			DepartmentServicePublic departmentServicePublic = new DepartmentServicePublicProxy();
			if (getAccount().isAdmin()) {
				Department[] deps = departmentServicePublic.findAll();
				for (int i = 0; i < deps.length; i++) {
					departments.add(deps[i]);
				}
			} else {
				Department dep = departmentServicePublic.findByCode("code", getAccount().getMember().getDepartment()
						.getCode());
				if (dep != null)
					departments.add(dep);
			}
			if (departments.size() != 0){
				department=departments.get(0);
				departmentSearch = departments.get(0);
				ajaxSelectDep();
			}
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
		searchItem();
	}

	public void showListFormula() {
		formulaKPIs = formulaKPIService.findAll();
	}

	public void createOrUpdate() {
		notify = new Notify(FacesContext.getCurrentInstance());
		try {
			if (orientationPerson != null) {
				if (!"".equals(orientationPerson.getCode())) {
					if (orientationPerson.getId() == null) {
						if (allowSave(null)) {
							orientationPerson = installSave(orientationPerson);
							orientationPerson = orientationPersonService.create(orientationPerson);
							orientationPersons.add(0, orientationPerson);
							writeLogInfo("Tạo mới " + orientationPerson.toString());
							notify.success();
							reset();
						} else {
							orientationPersonEdit = new OrientationPerson();
							notify.warningDetail();
						}
					} else {
						if (allowUpdate(null)) {
							orientationPerson = installUpdate(orientationPerson);
							orientationPerson = orientationPersonService.update(orientationPerson);
							int index = orientationPersons.indexOf(orientationPerson);
							orientationPersons.set(index, orientationPerson);
							writeLogInfo("Cập nhật " + orientationPerson.toString());
							notify.success();
						} else {
							notify.warningDetail();
						}
					}
					searchItem();
				} else {
					notify.warning("Điền đầy đủ thông tin!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			writeLogError(e.getLocalizedMessage());
			notify.warning("Xảy ra lỗi không lưu được!");
		}
	}

	public void updateFormula() {
		notify = new Notify(FacesContext.getCurrentInstance());
		try {
			if (orientationPerson.getId() == null) {
				notify.warning("Lưu định hướng cá nhân trước khi cài đặt công thức!");
			} else {
				orientationPerson.setFormulaKPI(formulaKPISelect);
				orientationPerson = installUpdate(orientationPerson);
				orientationPerson = orientationPersonService.update(orientationPerson);
				int index = orientationPersons.indexOf(orientationPerson);
				orientationPersons.set(index, orientationPerson);
				writeLogInfo("Cập nhật " + orientationPerson.toString());
				notify.success();
			}
		} catch (Exception e) {
			e.printStackTrace();
			writeLogError(e.getLocalizedMessage());
			notify.warning("Xảy ra lỗi không lưu được!");
		}
	}

	public void ajaxSelectDep() {
		if (department != null)
			positionJobs = positionJobService.findByDepartmentP(department.getCode());
	}

	public void reset() {
		orientationPerson = new OrientationPerson();

	}

	public void showEdit() {
		this.orientationPerson = orientationPersonEdit;
		positionJob = positionJobService.findByCode(orientationPerson.getCodePJob());
		ajaxSelectDep();
		if (orientationPerson.getFormulaKPI() != null) {
			formulaKPISelect = orientationPerson.getFormulaKPI();
		} else {
			formulaKPISelect = null;
		}

	}

	public void delete() {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (orientationPerson.getId() != null) {
			if (allowDelete(null)) {
				boolean status = orientationPersonService.delete(orientationPerson);
				if (status) {
					orientationPersons.remove(orientationPerson);
					writeLogInfo("Xoá " + orientationPerson.toString());
					reset();
					notify.success();
				} else {
					writeLogError("Lỗi khi xoá " + orientationPerson.toString());
					notify.error();
				}
			} else {
				notify.warningDetail();
			}
		} else {
			notify.warning("Chưa chọn trong danh sách!");
		}
	}

	public static void main(String[] args) throws ScriptException {
		ScriptEngineManager mgr = new ScriptEngineManager();
		ScriptEngine engine = mgr.getEngineByName("JavaScript");
		String foo = "100-((40+2)*5)";
		System.out.println(engine.eval(foo));
	}

	@Inject
	private EmpPJobService empPJobService;
	private List<OrienInfoEmpl> orienInfoEmpls;

	public void searchItem() {
		orienInfoEmpls = new ArrayList<OrienInfoEmpl>();
		List<EmpPJob> empPJobs = null;
		if (departmentSearch != null) {
			List<PositionJob> positionJobs = positionJobService.findByDepartmentP(departmentSearch.getCode());
			List<String> codePojs=new ArrayList<String>();
			for (int i = 0; i < positionJobs.size(); i++) {
				codePojs.add(positionJobs.get(i).getCode());
			}
			empPJobs = empPJobService.findByPositionJob(codePojs);
		}
		if (empPJobs != null)
			for (int i = 0; i < empPJobs.size(); i++) {
				try {
					OrienInfoEmpl orEmpl = new OrienInfoEmpl();
					orEmpl.setNamePos(positionJobService.findByCode(empPJobs.get(i).getCodePJob()).getName());
					orEmpl.setOrientationPersons(orientationPersonService.findSearch(empPJobs.get(i).getCodePJob()));
					orienInfoEmpls.add(orEmpl);
				} catch (Exception e) {
				}

			}
	}

	public List<OrientationPerson> getOrientationPersons() {
		return orientationPersons;
	}

	public void setOrientationPersons(List<OrientationPerson> OrientationPersons) {
		this.orientationPersons = OrientationPersons;
	}

	public OrientationPerson getOrientationPerson() {
		return orientationPerson;
	}

	public void setOrientationPerson(OrientationPerson OrientationPerson) {
		this.orientationPerson = OrientationPerson;
	}

	public OrientationPerson getOrientationPersonEdit() {
		return orientationPersonEdit;
	}

	public void setOrientationPersonEdit(OrientationPerson OrientationPersonEdit) {
		this.orientationPersonEdit = OrientationPersonEdit;
	}

	public List<PositionJob> getPositionJobs() {
		return positionJobs;
	}

	public void setPositionJobs(List<PositionJob> positionJobs) {
		this.positionJobs = positionJobs;
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

	public PositionJob getPositionJob() {
		return positionJob;
	}

	public void setPositionJob(PositionJob positionJob) {
		this.positionJob = positionJob;
	}

	public Department getDepartmentSearch() {
		return departmentSearch;
	}

	public void setDepartmentSearch(Department departmentSearch) {
		this.departmentSearch = departmentSearch;
	}

	public List<OrienInfoEmpl> getOrienInfoEmpls() {
		return orienInfoEmpls;
	}

	public void setOrienInfoEmpls(List<OrienInfoEmpl> orienInfoEmpls) {
		this.orienInfoEmpls = orienInfoEmpls;
	}

}
