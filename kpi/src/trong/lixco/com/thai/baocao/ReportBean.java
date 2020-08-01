package trong.lixco.com.thai.baocao;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.ArrayUtils;
import org.docx4j.org.xhtmlrenderer.pdf.ITextRenderer;
import org.jboss.logging.Logger;
import org.joda.time.LocalDate;
import org.quartz.SchedulerException;

import com.ibm.icu.text.SimpleDateFormat;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import trong.lixco.com.account.servicepublics.Department;
import trong.lixco.com.account.servicepublics.DepartmentServicePublic;
import trong.lixco.com.account.servicepublics.DepartmentServicePublicProxy;
import trong.lixco.com.account.servicepublics.Member;
import trong.lixco.com.account.servicepublics.MemberServicePublic;
import trong.lixco.com.account.servicepublics.MemberServicePublicProxy;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.ejb.servicekpi.KPIDepMonthService;
import trong.lixco.com.ejb.servicekpi.KPIDepService;
import trong.lixco.com.ejb.servicekpi.KPIPersonService;
import trong.lixco.com.jpa.entitykpi.KPIDep;
import trong.lixco.com.jpa.entitykpi.KPIDepMonth;
import trong.lixco.com.jpa.entitykpi.KPIPerson;
import trong.lixco.com.servicepublic.DepartmentDTO;
import trong.lixco.com.servicepublic.EmployeeDTO;
import trong.lixco.com.servicepublic.EmployeeServicePublic;
import trong.lixco.com.servicepublic.EmployeeServicePublicProxy;
import trong.lixco.com.thai.bean.entities.DepartmentTotalMonth;
import trong.lixco.com.thai.bean.entities.PersonalMonth;
import trong.lixco.com.thai.bean.entities.PersonalQuy;
import trong.lixco.com.thai.bean.entities.PersonalYear;
import trong.lixco.com.thai.bean.entities.Reminder;
import trong.lixco.com.thai.mail.CommonService;
import trong.lixco.com.util.DepartmentUtil;
import trong.lixco.com.util.Notify;

@ManagedBean
@ViewScoped
public class ReportBean extends AbstractBean<KPIPerson> {
	private static final long serialVersionUID = 1L;

	private Notify notify;
	private JasperPrint jasperPrint;

	DepartmentServicePublic DEPARTMENT_SERVICE_PUBLIC;
	EmployeeServicePublic EMPLOYEE_SERVICE_PUBLIC;

	@Inject
	private KPIDepMonthService KPI_DEPARTMENT_MONTH;
	@Inject
	private KPIPersonService KPI_PERSON_SERVICE;
	@Inject
	private KPIDepService KPI_DEPARTMENT_SERVICE;

	// nam KPI phong
	private int yearSelectedDepartment;

	// thang nam quy KPI ca nhan
	private int monthSelectedPersonal;
	private int quySelectedPersonal = 2;
	private int yearSelectedPersonal1;
	private int yearSelectedPersonal2;
	private int yearSelectedPersonal3;

	private List<Department> departments;

	private SimpleDateFormat sf;
	private List<Department> allDepartmentList;
	private int[] quys = { 1, 2, 3, 4 };
	private int[] months = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
	//
	private List<Department> allDepartment;
	private List<String> allCodeDepartment;
	private List<String> allCodeEmployee;

	// Tao list code department de test
	private List<EmployeeDTO> allEmloyee;
	private Department departmentSelectedPersonalYear;
	private Department departmentSelectedPersonalQuy;
	private Department departmentSelectedPersonalMonth;
	private String[] allCodeDepartmentArray;

	@Override
	protected void initItem() {

		sf = new SimpleDateFormat("dd/MM/yyyy");
		LocalDate lc = new LocalDate();
		monthSelectedPersonal = lc.getMonthOfYear();
		yearSelectedDepartment = lc.getYear();
		yearSelectedPersonal1 = lc.getYear();
		yearSelectedPersonal2 = lc.getYear();
		yearSelectedPersonal3 = lc.getYear();
		DEPARTMENT_SERVICE_PUBLIC = new DepartmentServicePublicProxy();
		EMPLOYEE_SERVICE_PUBLIC = new EmployeeServicePublicProxy();
		departmentSelectedPersonalYear = new Department();
		departmentSelectedPersonalQuy = new Department();
		departmentSelectedPersonalMonth = new Department();

		allCodeDepartment = new ArrayList<>();
		allCodeEmployee = new ArrayList<>();
		departments = new ArrayList<Department>();

		// handle get list department from Department table // lam the nao de lay duoc
		// danh sach phong ban tai ho chi minh
		try {
			Department[] allDepartmentArray = DEPARTMENT_SERVICE_PUBLIC.findAll();
			allDepartmentArray = CommonService.findAll();
			allDepartmentList = Arrays.asList(allDepartmentArray);
			allDepartment = new ArrayList<>();
			for (int i = 0; i < allDepartmentArray.length; i++) {
				if (allDepartmentArray[i].getDepartment() != null) {
					if (allDepartmentArray[i].getDepartment().getId() == 191
							&& allDepartmentArray[i].getLevelDep().getLevel() == 2) {
						allDepartment.add(allDepartmentArray[i]);
						// lap de tao list code cua toan bo nhan vien
						allCodeDepartment.add(allDepartmentArray[i].getCode());
					}
				}
			}

			allCodeDepartmentArray = new String[allCodeDepartment.size()];
			allCodeDepartmentArray = allCodeDepartment.toArray(allCodeDepartmentArray);

			EmployeeDTO[] allEmployeeArray = EMPLOYEE_SERVICE_PUBLIC.findByDep(allCodeDepartmentArray);
			for (EmployeeDTO e : allEmployeeArray) {
				allCodeEmployee.add(e.getCode());
			}

			// Get phong ban cua a Trong
			Department[] deps = DEPARTMENT_SERVICE_PUBLIC.getAllDepartSubByParent("10001");
			for (int i = 0; i < deps.length; i++) {
				if (deps[i].getLevelDep().getLevel() <= 2)
					departments.add(deps[i]);
			}
			if (departments.size() != 0) {
				departments = DepartmentUtil.sort(departments);
			}

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// end
	}

	public void showReportPersonalMonth() throws JRException, IOException {
		String departmentName ="";
		departmentName = getDepartmentName(departmentSelectedPersonalMonth);
		List<PersonalMonth> dataReportPersonalMonth = createDataReportKPIPersonalMonth(this.monthSelectedPersonal,
				this.yearSelectedPersonal1,departmentSelectedPersonalMonth);
		if (!dataReportPersonalMonth.isEmpty()) {
			String reportPath = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/thaireports/kpi/personalMonth.jasper");
			JRDataSource beanDataSource = new JRBeanCollectionDataSource(dataReportPersonalMonth);
			Map<String, Object> importParam = new HashMap<String, Object>();

			String image = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/gfx/lixco_logo.png");
			importParam.put("logo", image);
			importParam.put("year", yearSelectedPersonal1);
			importParam.put("month", monthSelectedPersonal);
			importParam.put("department", departmentName);
			importParam.put("listKPIDepartmentYear", beanDataSource);
			JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, importParam, beanDataSource);
			FacesContext facesContext = FacesContext.getCurrentInstance();
			OutputStream outputStream;
			outputStream = facesContext.getExternalContext().getResponseOutputStream();
			JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
			facesContext.responseComplete();
		} else {
			noticeError("Không có dữ liệu");
		}
	}

//
	public void showReportPersonalQuy() throws JRException, IOException {
		String reportPath = FacesContext.getCurrentInstance().getExternalContext()
				.getRealPath("/resources/thaireports/kpi/personalQuy.jasper");
		String departmentName = "";
		departmentName = getDepartmentName(departmentSelectedPersonalQuy);
		List<PersonalQuy> dataReportPersonalQuy = createDataReportKPIPersonalQuy(this.quySelectedPersonal,
				this.yearSelectedPersonal2, departmentSelectedPersonalQuy);
		// check neu list rong~
		if (!dataReportPersonalQuy.isEmpty()) {
			JRDataSource beanDataSource = new JRBeanCollectionDataSource(dataReportPersonalQuy);
			Map<String, Object> importParam = new HashMap<String, Object>();

			String image = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/gfx/lixco_logo.png");
			importParam.put("logo", image);
			importParam.put("year", yearSelectedPersonal2);
			importParam.put("quy", quySelectedPersonal);
			importParam.put("department", departmentName);
			importParam.put("firstMonth", dataReportPersonalQuy.get(0).getFirstMonth());
			importParam.put("secondMonth", dataReportPersonalQuy.get(0).getSecondndMonth());
			importParam.put("thirdMonth", dataReportPersonalQuy.get(0).getThirdMonth());
			importParam.put("listKPIDepartmentYear", beanDataSource);
			JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, importParam, beanDataSource);
			FacesContext facesContext = FacesContext.getCurrentInstance();
			OutputStream outputStream;
			outputStream = facesContext.getExternalContext().getResponseOutputStream();
			JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
			facesContext.responseComplete();
		} else {
			noticeError("Không có dữ liệu");
		}
	}

	public void showReportPersonalYear() throws JRException, IOException {
		String reportPath = FacesContext.getCurrentInstance().getExternalContext()
				.getRealPath("/resources/thaireports/kpi/personalYear.jasper");
		String departmentString = "";
		// check phong ban nao
		departmentString = getDepartmentName(departmentSelectedPersonalYear);

		List<PersonalYear> dataReportPersonalYear = createDataReportKPIPersonalYear(this.departmentSelectedPersonalYear,
				this.yearSelectedPersonal3);
		if (!dataReportPersonalYear.isEmpty()) {
			JRDataSource beanDataSource = new JRBeanCollectionDataSource(dataReportPersonalYear);
			Map<String, Object> importParam = new HashMap<String, Object>();

			String image = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/gfx/lixco_logo.png");
			importParam.put("logo", image);
			importParam.put("year", yearSelectedPersonal3);
			importParam.put("department", departmentString);
			importParam.put("listKPIDepartmentYear", beanDataSource);
			JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, importParam, beanDataSource);
			FacesContext facesContext = FacesContext.getCurrentInstance();
			OutputStream outputStream;
			outputStream = facesContext.getExternalContext().getResponseOutputStream();
			JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
			facesContext.responseComplete();
		} else {
			noticeError("Không có dữ liệu");
		}
	}

	public void showReportDepartmentYear() throws JRException, IOException {
		String reportPath = FacesContext.getCurrentInstance().getExternalContext()
				.getRealPath("/resources/thaireports/kpi/departmentYear.jasper");
		List<DepartmentTotalMonth> dataReportDepartmentYear = createDataReportKPIDepartmentYear(yearSelectedDepartment);
		if (!dataReportDepartmentYear.isEmpty()) {
			JRDataSource beanDataSource = new JRBeanCollectionDataSource(dataReportDepartmentYear);
			Map<String, Object> importParam = new HashMap<String, Object>();

			String image = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/gfx/lixco_logo.png");
			importParam.put("logo", image);
			importParam.put("year", yearSelectedDepartment);
			importParam.put("listKPIDepartmentYear", beanDataSource);
			JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, importParam, beanDataSource);
			FacesContext facesContext = FacesContext.getCurrentInstance();
			OutputStream outputStream;
			outputStream = facesContext.getExternalContext().getResponseOutputStream();
			JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
//			//print excel
//			JRXlsxExporter exporter = new JRXlsxExporter();
//			exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
//			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput("D:\\demo\\sample_report.xlsx"));
//			//Set configuration as you like it!!
//			SimpleXlsxExporterConfiguration configuration = null;
//	        configuration = new SimpleXlsxExporterConfiguration();
//	        configuration.setKeepWorkbookTemplateSheets(true);
//	        exporter.setConfiguration(configuration);
//	        exporter.exportReport();
//			//end excel
			facesContext.responseComplete();
		} else {
			noticeError("Không có dữ liệu");
		}

	}

	// Entity bao cao KPI ca nhan nam
	public List<PersonalYear> createDataReportKPIPersonalYear(Department department, int yearSearch)
			throws RemoteException {
		List<PersonalYear> personalYears = new ArrayList<>();
		// search by code Emp and year -> all KPIPerson 2019

		// ****cho nay lam sao lay duoc list nhan vien (- nhan vien thang do chua vo lam
		// thang sau vo)

		// truong hop khong co phong ban
		allCodeEmployee = new ArrayList<>();
		if (department == null) {
			EmployeeDTO[] allEmployeeArray = EMPLOYEE_SERVICE_PUBLIC.findByDep(allCodeDepartmentArray);
			for (EmployeeDTO e : allEmployeeArray) {
				allCodeEmployee.add(e.getCode());
			}
		}
		if (department != null) {
			String[] tempDepartmentArr = { department.getCode() };
			EmployeeDTO[] allEmployeeArray = EMPLOYEE_SERVICE_PUBLIC.findByDep(tempDepartmentArr);
			for (EmployeeDTO e : allEmployeeArray) {
				allCodeEmployee.add(e.getCode());
			}
		}

		for (String k : allCodeEmployee) {
			// tao 1 item personalYear
			PersonalYear personalYearTemp = new PersonalYear();
			// Tim nhan vien theo codeEmp KPIPerson -> nameEmp, nameDepart
			EmployeeDTO memberTemp = EMPLOYEE_SERVICE_PUBLIC.findByCode(k);
			personalYearTemp.setNameEmp(memberTemp.getName());
			personalYearTemp.setNameDepartment(memberTemp.getNameDepart());
			// list KPIPerson by Employee and year
			List<KPIPerson> kpiPersonByCodeEmp = KPI_PERSON_SERVICE.findRange(memberTemp.getCode(), yearSearch);
			// check employee do da co duoi db chua
			if (!kpiPersonByCodeEmp.isEmpty()) {
				for (int i = 0; i < kpiPersonByCodeEmp.size(); i++) {
					int monthTemp = kpiPersonByCodeEmp.get(i).getKmonth();
					switch (monthTemp) {
					case 1:
						personalYearTemp.setThang1(kpiPersonByCodeEmp.get(i).getTotal());
						break;
					case 2:
						personalYearTemp.setThang2(kpiPersonByCodeEmp.get(i).getTotal());
						break;
					case 3:
						personalYearTemp.setThang3(kpiPersonByCodeEmp.get(i).getTotal());
						break;
					case 4:
						personalYearTemp.setThang4(kpiPersonByCodeEmp.get(i).getTotal());
						break;
					case 5:
						personalYearTemp.setThang5(kpiPersonByCodeEmp.get(i).getTotal());
						break;
					case 6:
						personalYearTemp.setThang6(kpiPersonByCodeEmp.get(i).getTotal());
						break;
					case 7:
						personalYearTemp.setThang7(kpiPersonByCodeEmp.get(i).getTotal());
						break;
					case 8:
						personalYearTemp.setThang8(kpiPersonByCodeEmp.get(i).getTotal());
						break;
					case 9:
						personalYearTemp.setThang9(kpiPersonByCodeEmp.get(i).getTotal());
						break;
					case 10:
						personalYearTemp.setThang10(kpiPersonByCodeEmp.get(i).getTotal());
						break;
					case 11:
						personalYearTemp.setThang11(kpiPersonByCodeEmp.get(i).getTotal());
						break;
					case 12:
						personalYearTemp.setThang12(kpiPersonByCodeEmp.get(i).getTotal());
						break;

					default:
						break;
					}

				}
				personalYears.add(personalYearTemp);
			}
		}
		return personalYears;
	}

	// Entity bao cao KPI ca nhan quy
	public List<PersonalQuy> createDataReportKPIPersonalQuy(int quy, int year, Department department)
			throws RemoteException {
		List<PersonalQuy> dataPersonalQuy = new ArrayList<>();
		// Tim list nhan vien theo phong ban
		allCodeEmployee = new ArrayList<>();
		if (department == null) {
			EmployeeDTO[] allEmployeeArray = EMPLOYEE_SERVICE_PUBLIC.findByDep(allCodeDepartmentArray);
			for (EmployeeDTO e : allEmployeeArray) {
				allCodeEmployee.add(e.getCode());
			}
		}
		if (department != null) {
			String[] tempDepartmentArr = { department.getCode() };
			EmployeeDTO[] allEmployeeArray = EMPLOYEE_SERVICE_PUBLIC.findByDep(tempDepartmentArr);
			for (EmployeeDTO e : allEmployeeArray) {
				allCodeEmployee.add(e.getCode());
			}
		}
		for (String k : allCodeEmployee) {
			// tao 1 item personalYear
			PersonalQuy personalQuyTemp = new PersonalQuy();
			// Tim nhan vien theo codeEmp KPIPerson -> nameEmp, nameDepart
			EmployeeDTO memberTemp = EMPLOYEE_SERVICE_PUBLIC.findByCode(k);
			personalQuyTemp.setNameEmployee(memberTemp.getName());
			personalQuyTemp.setNameDepartment(memberTemp.getNameDepart());
			// list KPIPerson by Employee and year
			List<KPIPerson> kpiPersonByCodeEmp = KPI_PERSON_SERVICE.findRange(memberTemp.getCode(), year);
			// kiem tra thang do nhan vien da vao lam hay chua
			if (!kpiPersonByCodeEmp.isEmpty()) {
				for (int i = 0; i < kpiPersonByCodeEmp.size(); i++) {
					int monthTemp = kpiPersonByCodeEmp.get(i).getKmonth();
					switch (quySelectedPersonal) {
					case 1:
						personalQuyTemp.setFirstMonth("Tháng 1");
						personalQuyTemp.setSecondndMonth("Tháng 2");
						personalQuyTemp.setThirdMonth("Tháng 3");
						if (monthTemp == 1) {
							personalQuyTemp.setResult1(kpiPersonByCodeEmp.get(i).getTotal());
						}
						if (monthTemp == 2) {
							personalQuyTemp.setResult2(kpiPersonByCodeEmp.get(i).getTotal());
						}
						if (monthTemp == 3) {
							personalQuyTemp.setResult3(kpiPersonByCodeEmp.get(i).getTotal());
						}
						break;
					case 2:
						personalQuyTemp.setFirstMonth("Tháng 4");
						personalQuyTemp.setSecondndMonth("Tháng 5");
						personalQuyTemp.setThirdMonth("Tháng 6");
						if (monthTemp == 4) {
							personalQuyTemp.setResult1(kpiPersonByCodeEmp.get(i).getTotal());
						}
						if (monthTemp == 5) {
							personalQuyTemp.setResult2(kpiPersonByCodeEmp.get(i).getTotal());
						}
						if (monthTemp == 6) {
							personalQuyTemp.setResult3(kpiPersonByCodeEmp.get(i).getTotal());
						}
						break;
					case 3:
						personalQuyTemp.setFirstMonth("Tháng 7");
						personalQuyTemp.setSecondndMonth("Tháng 8");
						personalQuyTemp.setThirdMonth("Tháng 9");
						if (monthTemp == 7) {
							personalQuyTemp.setResult1(kpiPersonByCodeEmp.get(i).getTotal());
						}
						if (monthTemp == 8) {
							personalQuyTemp.setResult2(kpiPersonByCodeEmp.get(i).getTotal());
						}
						if (monthTemp == 9) {
							personalQuyTemp.setResult3(kpiPersonByCodeEmp.get(i).getTotal());
						}
						break;
					case 4:
						personalQuyTemp.setFirstMonth("Tháng 10");
						personalQuyTemp.setSecondndMonth("Tháng 11");
						personalQuyTemp.setThirdMonth("Tháng 12");
						if (monthTemp == 10) {
							personalQuyTemp.setResult1(kpiPersonByCodeEmp.get(i).getTotal());
						}
						if (monthTemp == 11) {
							personalQuyTemp.setResult2(kpiPersonByCodeEmp.get(i).getTotal());
						}
						if (monthTemp == 12) {
							personalQuyTemp.setResult3(kpiPersonByCodeEmp.get(i).getTotal());
						}
						break;

					default:
						break;
					}
				}
				double kpiAvgQuy = Math.round(
						((personalQuyTemp.getResult1() + personalQuyTemp.getResult2() + personalQuyTemp.getResult3())
								/ 3) * 100.0)
						/ 100.0;
				personalQuyTemp.setAvgQuy(kpiAvgQuy);
				if (90 <= kpiAvgQuy && kpiAvgQuy <= 200) {
					personalQuyTemp.setRate("A");
				}
				if (80 <= kpiAvgQuy && kpiAvgQuy < 90) {
					personalQuyTemp.setRate("B");
				}
				if (70 <= kpiAvgQuy && kpiAvgQuy < 80) {
					personalQuyTemp.setRate("C");
				}
				if (0 <= kpiAvgQuy && kpiAvgQuy < 70) {
					personalQuyTemp.setRate("D");
				}
				dataPersonalQuy.add(personalQuyTemp);
			}
		}
		return dataPersonalQuy;
	}

	// Entity bao cao KPI ca nhan thang
	public List<PersonalMonth> createDataReportKPIPersonalMonth(int month, int year, Department department) {
		try {
			List<PersonalMonth> dataPersonalMonth = new ArrayList<>();
			allCodeEmployee = new ArrayList<>();
			if (department == null) {
				EmployeeDTO[] allEmployeeArray = EMPLOYEE_SERVICE_PUBLIC.findByDep(allCodeDepartmentArray);
				for (EmployeeDTO e : allEmployeeArray) {
					allCodeEmployee.add(e.getCode());
				}
			}
			if (department != null) {
				String[] tempDepartmentArr = { department.getCode() };
				EmployeeDTO[] allEmployeeArray = EMPLOYEE_SERVICE_PUBLIC.findByDep(tempDepartmentArr);
				for (EmployeeDTO e : allEmployeeArray) {
					allCodeEmployee.add(e.getCode());
				}
			}
			for (String k : allCodeEmployee) {
				// tao 1 item personalYear
				PersonalMonth personalMonthTemp = new PersonalMonth();
				// Tim nhan vien theo codeEmp KPIPerson -> nameEmp, nameDepart
				EmployeeDTO memberTemp = EMPLOYEE_SERVICE_PUBLIC.findByCode(k);
				personalMonthTemp.setEmployeeName(memberTemp.getName());
				personalMonthTemp.setDepartmentName(memberTemp.getNameDepart());
				// list KPIPerson by Employee and year
				List<KPIPerson> kpiPersonByEmpCode = KPI_PERSON_SERVICE.findRange(k, month, year);
				// kiem tra thang do nhan vien da vao lam hay chua
				if (!kpiPersonByEmpCode.isEmpty()) {
					// code phong ban
					List<KPIDepMonth> kpiDepartmentByEmpCode = KPI_DEPARTMENT_MONTH.findKPIDepMonth(month, year,
							memberTemp.getCodeDepart());

					double result = 0;
					// kiem tra duoi bang kpi ca nhan co null hay khong
					if (kpiPersonByEmpCode.isEmpty()) {
						personalMonthTemp.setKpiPersonal(0);
						// kpi ca nhan va phong deu null
						if (kpiDepartmentByEmpCode.isEmpty()) {
							result = (double) ((0 * 40) / 100) + (double) ((0 * 60) / 100);
							// xem lai doan nay
							// tinh tong kpiPersonal * 60 + kpiDeparment * 40
							personalMonthTemp.setResult(result);

							// Cho nay se set xep loai -> bo sung sau
							personalMonthTemp.setRate("A");
							dataPersonalMonth.add(personalMonthTemp);
						} else {
							// set gia tri cho bien
							personalMonthTemp.setKpiDepartment(kpiDepartmentByEmpCode.get(0).getResult());
							result = (double) ((kpiDepartmentByEmpCode.get(0).getResult() * 40) / 100)
									+ (double) ((0 * 60) / 100);
							// xem lai doan nay
							// tinh tong kpiPersonal * 60 + kpiDeparment * 40
							personalMonthTemp.setResult(result);

							// Cho nay se set xep loai -> bo sung sau
							personalMonthTemp.setRate("A");
							dataPersonalMonth.add(personalMonthTemp);
						}
					} else {
						personalMonthTemp.setKpiPersonal(kpiPersonByEmpCode.get(0).getTotal());
						if (kpiDepartmentByEmpCode.isEmpty()) {
							result = (double) ((0 * 40) / 100)
									+ (double) ((kpiPersonByEmpCode.get(0).getTotal() * 60) / 100);

							// xem lai doan nay
							// tinh tong kpiPersonal * 60 + kpiDeparment * 40
							personalMonthTemp.setResult(result);

							// Cho nay se set xep loai -> bo sung sau
							personalMonthTemp.setRate("A");
							dataPersonalMonth.add(personalMonthTemp);
						} else {
							// set gia tri cho bien
							personalMonthTemp.setKpiDepartment(kpiDepartmentByEmpCode.get(0).getResult());
							result = (double) ((kpiDepartmentByEmpCode.get(0).getResult() * 40) / 100)
									+ (double) ((kpiPersonByEmpCode.get(0).getTotal() * 60) / 100);

							// xem lai doan nay
							// tinh tong kpiPersonal * 60 + kpiDeparment * 40
							personalMonthTemp.setResult(result);

							// Cho nay se set xep loai -> bo sung sau
							personalMonthTemp.setRate("A");
							dataPersonalMonth.add(personalMonthTemp);
						}
					}
				}
			}
			if (dataPersonalMonth.size() != 0) {
				return dataPersonalMonth;
			} else {
				List<PersonalMonth> abc = new ArrayList<>();
				return abc;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// Entity bao cao phong nam
	// *** lam sao lay duoc danh sach phong ban dang hoat dong tai ho chi minh
	public List<DepartmentTotalMonth> createDataReportKPIDepartmentYear(int yearSelectedDepartment) {
		Department departmentTemp = new Department();
		// Danh sach diem phong thang
		List<DepartmentTotalMonth> departmentTotalMonth = new ArrayList<>();
//		for (Department d : allDepartment) {
		for (String d : allCodeDepartment) {
			// danh sach kpi phong nam -> 12 thang
			List<KPIDepMonth> temp = KPI_DEPARTMENT_MONTH.find(d, yearSelectedDepartment);
			// kpi phong nam -> 1 phong 1 row
			List<KPIDep> listKPIYear = KPI_DEPARTMENT_SERVICE.findKPIDep(d, yearSelectedDepartment);
			// Select department -> nameDepart
			try {
				departmentTemp = DEPARTMENT_SERVICE_PUBLIC.findByCode("code", d);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DepartmentTotalMonth departmentTotalTemp = new DepartmentTotalMonth();
			departmentTotalTemp.setNameDepart(departmentTemp.getName());
			if (!listKPIYear.isEmpty()) {
				departmentTotalTemp.setKpiYear(listKPIYear.get(0).getResult());
			}
			for (int i = 0; i < temp.size(); i++) {
				int monthTemp = temp.get(i).getMonth();
				switch (monthTemp) {
				case 1:
					departmentTotalTemp.setThang1(temp.get(i).getResult());
					break;
				case 2:
					departmentTotalTemp.setThang2(temp.get(i).getResult());
					break;
				case 3:
					departmentTotalTemp.setThang3(temp.get(i).getResult());
					break;
				case 4:
					departmentTotalTemp.setThang4(temp.get(i).getResult());
					break;
				case 5:
					departmentTotalTemp.setThang5(temp.get(i).getResult());
					break;
				case 6:
					departmentTotalTemp.setThang6(temp.get(i).getResult());
					break;
				case 7:
					departmentTotalTemp.setThang7(temp.get(i).getResult());
					break;
				case 8:
					departmentTotalTemp.setThang8(temp.get(i).getResult());
					break;
				case 9:
					departmentTotalTemp.setThang9(temp.get(i).getResult());
					break;
				case 10:
					departmentTotalTemp.setThang10(temp.get(i).getResult());
					break;
				case 11:
					departmentTotalTemp.setThang11(temp.get(i).getResult());
					break;
				case 12:
					departmentTotalTemp.setThang12(temp.get(i).getResult());
					break;

				default:
					break;
				}

			}
			double kpiAvgYear = Math
					.round(((departmentTotalTemp.getThang1() + departmentTotalTemp.getThang2()
							+ departmentTotalTemp.getThang3() + departmentTotalTemp.getThang4()
							+ departmentTotalTemp.getThang5() + departmentTotalTemp.getThang6()
							+ departmentTotalTemp.getThang7() + departmentTotalTemp.getThang8()
							+ departmentTotalTemp.getThang9() + departmentTotalTemp.getThang10()
							+ departmentTotalTemp.getThang11() + departmentTotalTemp.getThang12()) / 12) * 100.0)
					/ 100.0;
			departmentTotalTemp.setKpiAvgYear(kpiAvgYear);
			departmentTotalMonth.add(departmentTotalTemp);
		}
		return departmentTotalMonth;
	}
//	public void printOnly() {
//		notify = new Notify(FacesContext.getCurrentInstance());
//		try {
//			String pathre = "/resources/reports/kpis/kpiperson.jasper";
//			List<KPIPersonOfMonth> expds = kPIPersonService.findKPIPerson(kPIPerson);
//			String reportPath = null;
//			Map<String, Object> importParam = null;
//			importParam = installConfigPersonReport();
//			List<ParamReportDetail> paramnhaps = paramReportDetailService
//					.findByParamReports_param_name("kpicanhanthang");
//			for (ParamReportDetail pd : paramnhaps) {
//				importParam.put(pd.getKey(), pd.getValue());
//			}
//			reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath(pathre);
//			importParam.put("REPORT_LOCALE", new Locale("vi", "VN"));
//
//			List<PrintKPI> printKPIs = new ArrayList<PrintKPI>();
//			SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
//			for (int i = 0; i < expds.size(); i++) {
//				PrintKPI pr = new PrintKPI(expds.get(i), sf, empPJobService);
//				printKPIs.add(pr);
//			}
//			boolean status = true;
//			for (int i = 0; i < printKPIs.size(); i++) {
//				if ("I".equals(printKPIs.get(i).getHeaderGroupCode())) {
//					status = false;
//					break;
//				}
//			}
//			if (status && expds.size() != 0) {
//				PrintKPI pk = new PrintKPI(expds.get(0), true, empPJobService);
//				pk.setHeaderGroupCode("I");
//				pk.setHeaderGroupName("Phẩm chất - thái độ - hành vi");
//				pk.setHeaderGroupWeighted(30.0);
//				printKPIs.add(pk);
//			}
//			printKPIs.sort(Comparator.comparing(PrintKPI::getHeaderGroupCode).thenComparing(PrintKPI::getNo));
//
//			JRDataSource beanDataSource = new JRBeanCollectionDataSource(printKPIs);
//			jasperPrint = JasperFillManager.fillReport(reportPath, importParam, beanDataSource);
//			data = JasperExportManager.exportReportToPdf(jasperPrint);
//			mediaBean.setData(data);
//			RequestContext context = RequestContext.getCurrentInstance();
//			context.execute("PF('showpdfreport').show();");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}

	public String getDepartmentName(Department department) {
		if (department != null) {
			return department.getName();
		} else {
			return "Tất cả";
		}
	}

	public void showPDF() throws JRException, IOException {
		/* Convert List to JRBeanCollectionDataSource */

		List<KPIDepMonth> dataSource = new ArrayList<>();
		dataSource = KPI_DEPARTMENT_MONTH.findKPIDepMonth(4, 2020, null);
		JRBeanCollectionDataSource postJRBean = new JRBeanCollectionDataSource(dataSource);

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("listKPIDepartMonthByYearDataSource", postJRBean);
		parameters.put("title", "BÁO CÁO");
		parameters.put("REPORT_LOCALE", new Locale("vi", "VN"));
		// pass logo
//				BufferedImage image = ImageIO.read(getClass().getResource("resource/images/logo-lixco.png"));
//				parameters.put("logo", image );

//				JRDataSource beanDataSource = new JRBeanCollectionDataSource(dataSource);

		String jasperPath = FacesContext.getCurrentInstance().getExternalContext()
				.getRealPath("/resources/thaireports/kpi/personalMonth.jasper");
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperPath, parameters, new JREmptyDataSource());
//		FacesContext facesContext = FacesContext.getCurrentInstance();
//		// Tao file PDF
//		OutputStream outputStream;
//		outputStream = facesContext.getExternalContext().getResponseOutputStream();
		// Viet noi dung vao file PDF

		try {
//			JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
			byte[] data = JasperExportManager.exportReportToPdf(jasperPrint);
			showreport(data);
//			JasperExportManager.exportReportToPdfFile(jasperPrint, "D:\\demo\\reportPersonalMonth.pdf");
//			JRXlsxExporter exporter = new JRXlsxExporter();
//			exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
//			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput("D:\\demo\\sample_report.xlsx"));
//			//Set configuration as you like it!!
//			SimpleXlsxExporterConfiguration configuration = null;
//	        configuration = new SimpleXlsxExporterConfiguration();
//	        configuration.setKeepWorkbookTemplateSheets(true);
//	        exporter.setConfiguration(configuration);
//	        exporter.exportReport();
//			facesContext.responseComplete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showreport(byte[] kpm) throws IOException {
		if (kpm != null) {
			notify = new Notify(FacesContext.getCurrentInstance());
			try {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				ExternalContext externalContext = facesContext.getExternalContext();
				try {
					HttpSession session = (HttpSession) externalContext.getSession(true);
					HttpServletRequest ht = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
							.getRequest();
					ITextRenderer renderer = new ITextRenderer();
					String url = "http://" + ht.getServerName() + ":" + ht.getServerPort()
							+ "/kpi/showdata.xhtml;jsessionid=" + session.getId() + "?pdf=true";
					renderer.setDocument(new URL(url).toString());
					renderer.layout();

					HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
					response.reset();
					response.setContentType("application/pdf");
					response.addHeader("Content-disposition", "inline;filename=report.pdf");

					response.setContentLength(kpm.length);
					response.getOutputStream().write(kpm, 0, kpm.length);
					response.getOutputStream().flush();

					OutputStream browserStream = response.getOutputStream();
					renderer.createPDF(browserStream);
				} finally {
					facesContext.responseComplete();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			noticeError("Không có dữ liệu chứng minh.");
		}
	}

	public void createPDF(List<?> dataSource) throws JRException, IOException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		// compiler report
//		JasperReport reportPersonalMonth = JasperCompileManager.compileReport(FacesContext.getCurrentInstance()
//				.getExternalContext().getRealPath("/resources/thaireports/kpi/personalMonth.jrxml"));
		/* Convert List to JRBeanCollectionDataSource */
		JRBeanCollectionDataSource postJRBean = new JRBeanCollectionDataSource(dataSource);

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("personalMonthDatasource", postJRBean);
		parameters.put("titleCompanyName", "CÔNG TY CỔ PHẦN BỘT GIẶT LIX");
		parameters.put("year", "2020");
		parameters.put("REPORT_LOCALE", new Locale("vi", "VN"));
//		// pass logo
//			BufferedImage image = ImageIO.read(getClass().getResource("resources/gfx/lixco_logo.png"));
//			parameters.put("logo", image );

//			JRDataSource beanDataSource = new JRBeanCollectionDataSource(dataSource);

		String jasperPath = FacesContext.getCurrentInstance().getExternalContext()
				.getRealPath("/resources/thaireports/kpi/personalMonth.jasper");
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperPath, parameters, new JREmptyDataSource());
		// Tao file PDF
		OutputStream outputStream;
		outputStream = facesContext.getExternalContext().getResponseOutputStream();
		// Viet noi dung vao file PDF

		try {
			JasperExportManager.exportReportToPdfFile(jasperPrint, "D:\\demo\\reportPersonalMonth.pdf");
//			JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
			facesContext.responseComplete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Logger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getYearSelectedDepartment() {
		return yearSelectedDepartment;
	}

	public void setYearSelectedDepartment(int yearSelectedDepartment) {
		this.yearSelectedDepartment = yearSelectedDepartment;
	}

	public int getMonthSelectedPersonal() {
		return monthSelectedPersonal;
	}

	public void setMonthSelectedPersonal(int monthSelectedPersonal) {
		this.monthSelectedPersonal = monthSelectedPersonal;
	}

	public int getQuySelectedPersonal() {
		return quySelectedPersonal;
	}

	public void setQuySelectedPersonal(int quySelectedPersonal) {
		this.quySelectedPersonal = quySelectedPersonal;
	}

	public int getYearSelectedPersonal1() {
		return yearSelectedPersonal1;
	}

	public void setYearSelectedPersonal1(int yearSelectedPersonal1) {
		this.yearSelectedPersonal1 = yearSelectedPersonal1;
	}

	public int getYearSelectedPersonal2() {
		return yearSelectedPersonal2;
	}

	public void setYearSelectedPersonal2(int yearSelectedPersonal2) {
		this.yearSelectedPersonal2 = yearSelectedPersonal2;
	}

	public int getYearSelectedPersonal3() {
		return yearSelectedPersonal3;
	}

	public void setYearSelectedPersonal3(int yearSelectedPersonal3) {
		this.yearSelectedPersonal3 = yearSelectedPersonal3;
	}

	public int[] getQuys() {
		return quys;
	}

	public void setQuys(int[] quys) {
		this.quys = quys;
	}

	public int[] getMonths() {
		return months;
	}

	public void setMonths(int[] months) {
		this.months = months;
	}

	public List<Department> getAllDepartment() {
		return allDepartment;
	}

	public void setAllDepartment(List<Department> allDepartment) {
		this.allDepartment = allDepartment;
	}

	public Department getDepartmentSelectedPersonalYear() {
		return departmentSelectedPersonalYear;
	}

	public void setDepartmentSelectedPersonalYear(Department departmentSelectedPersonalYear) {
		this.departmentSelectedPersonalYear = departmentSelectedPersonalYear;
	}

	public Department getDepartmentSelectedPersonalQuy() {
		return departmentSelectedPersonalQuy;
	}

	public void setDepartmentSelectedPersonalQuy(Department departmentSelectedPersonalQuy) {
		this.departmentSelectedPersonalQuy = departmentSelectedPersonalQuy;
	}

	public Department getDepartmentSelectedPersonalMonth() {
		return departmentSelectedPersonalMonth;
	}

	public void setDepartmentSelectedPersonalMonth(Department departmentSelectedPersonalMonth) {
		this.departmentSelectedPersonalMonth = departmentSelectedPersonalMonth;
	}

	public List<Department> getDepartments() {
		return departments;
	}

	public void setDepartments(List<Department> departments) {
		this.departments = departments;
	}

}
