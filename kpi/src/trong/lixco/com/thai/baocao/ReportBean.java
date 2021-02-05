package trong.lixco.com.thai.baocao;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.docx4j.org.xhtmlrenderer.pdf.ITextRenderer;
import org.jboss.logging.Logger;
import org.joda.time.LocalDate;

import com.ibm.icu.text.SimpleDateFormat;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import trong.lixco.com.account.servicepublics.Account;
import trong.lixco.com.account.servicepublics.Department;
import trong.lixco.com.account.servicepublics.DepartmentServicePublic;
import trong.lixco.com.account.servicepublics.DepartmentServicePublicProxy;
import trong.lixco.com.account.servicepublics.Member;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.ejb.servicekpi.KPIDepMonthService;
import trong.lixco.com.ejb.servicekpi.KPIDepService;
import trong.lixco.com.ejb.servicekpi.KPIPersonService;
import trong.lixco.com.ejb.thai.kpi.EmployeeDontKPIEverService;
import trong.lixco.com.ejb.thai.kpi.EmployeeDontKPIService;
import trong.lixco.com.ejb.thai.kpi.KPIToService;
import trong.lixco.com.ejb.thai.kpi.PersonalOtherService;
import trong.lixco.com.jpa.entitykpi.KPIDep;
import trong.lixco.com.jpa.entitykpi.KPIDepMonth;
import trong.lixco.com.jpa.entitykpi.KPIPerson;
import trong.lixco.com.jpa.thai.EmployeeDontKPI;
import trong.lixco.com.jpa.thai.EmployeeDontKPIEver;
import trong.lixco.com.jpa.thai.KPIPersonalOther;
import trong.lixco.com.jpa.thai.KPITo;
import trong.lixco.com.servicepublic.EmployeeDTO;
import trong.lixco.com.servicepublic.EmployeeServicePublic;
import trong.lixco.com.servicepublic.EmployeeServicePublicProxy;
import trong.lixco.com.thai.apitrong.DepartmentData;
import trong.lixco.com.thai.apitrong.DepartmentDataService;
import trong.lixco.com.thai.apitrong.EmployeeData;
import trong.lixco.com.thai.apitrong.EmployeeDataService;
import trong.lixco.com.thai.bean.entities.ABCPersonMonth;
import trong.lixco.com.thai.bean.entities.DepartmentTotalMonth;
import trong.lixco.com.thai.bean.entities.PersonalMonth;
import trong.lixco.com.thai.bean.entities.PersonalQuy;
import trong.lixco.com.thai.bean.entities.PersonalYear;
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
	@Inject
	private KPIToService KPI_TO_SERVICE;
	@Inject
	private PersonalOtherService KPI_PERSON_OTHER_SERVICE;
	@Inject
	private EmployeeDontKPIService EMPLOYEE_DONT_KPI_SERVICE;
	@Inject
	private EmployeeDontKPIEverService EMPLOYEE_DONT_KPI_EVER_SERVICE;

	// nam KPI phong
	private int yearSelectedDepartment;

	// thang nam quy KPI ca nhan
	private int monthSelectedPersonal;
	private int quySelectedPersonal = 2;
	private int yearSelectedPersonal1;
	private int yearSelectedPersonal2;
	private int yearSelectedPersonal3;

	private List<Department> departments;
	private List<Department> departmentsLv1;

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
	private Member member;
	private Department departLv1;
	private String nameLocation;

	@Override
	protected void initItem() {
		member = getAccount().getMember();
		sf = new SimpleDateFormat("dd/MM/yyyy");
		LocalDate lc = new LocalDate();
		monthSelectedPersonal = lc.getMonthOfYear();
		yearSelectedDepartment = lc.getYear();
		yearSelectedPersonal1 = lc.getYear();
		yearSelectedPersonal2 = lc.getYear();
		yearSelectedPersonal3 = lc.getYear();
		DEPARTMENT_SERVICE_PUBLIC = new DepartmentServicePublicProxy();
		// EMPLOYEE_SERVICE_PUBLIC = new EmployeeServicePublicProxy();
		departmentSelectedPersonalYear = new Department();
		departmentSelectedPersonalQuy = new Department();
		departmentSelectedPersonalMonth = new Department();

		allCodeDepartment = new ArrayList<>();
		// allCodeEmployee = new ArrayList<>();
		departments = new ArrayList<Department>();
		departmentsLv1 = new ArrayList<>();

		// handle get list department from Department table // lam the nao de
		// lay duoc
		// danh sach phong ban tai ho chi minh
		try {
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
					.getRequest();
			HttpSession session = request.getSession();
			nameLocation = (String) session.getAttribute("database");

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

			// EmployeeDTO[] allEmployeeArray =
			// EMPLOYEE_SERVICE_PUBLIC.findByDep(allCodeDepartmentArray);
			// for (EmployeeDTO e : allEmployeeArray) {
			// allCodeEmployee.add(e.getCode());
			// }

			// Get phong ban cua a Trong
			// Department[] deps =
			// DEPARTMENT_SERVICE_PUBLIC.getAllDepartSubByParent("10001");
			// for (int i = 0; i < deps.length; i++) {
			// if (deps[i].getLevelDep().getLevel() <= 2) {
			// departments.add(deps[i]);
			// }
			// if (deps[i].getLevelDep().getLevel() == 1) {
			// departmentsLv1.add(deps[i]);
			// }
			// }

			Department[] deps = DEPARTMENT_SERVICE_PUBLIC.findAll();
			for (int i = 0; i < deps.length; i++) {
				if (deps[i].getLevelDep() != null) {
					if (deps[i].getLevelDep().getLevel() == 1) {
						departmentsLv1.add(deps[i]);
					}
				}
			}
			if (getAccount().isAdmin()) {

				Department dFirst = new Department();
				dFirst.setName("--Tất cả phòng ban--");
				departments.add(dFirst);
				for (int i = 0; i < deps.length; i++) {
					if (deps[i].getLevelDep() != null) {
						if (deps[i].getLevelDep().getLevel() <= 2) {
							departments.add(deps[i]);
						}
					}
				}

			} else {
				departments.add(member.getDepartment().getDepartment());
			}

			if (departments.size() != 0) {
				departments = DepartmentUtil.sort(departments);
				departmentSelectedPersonalMonth = departments.get(0);
			}

			if (member.getDepartment().getLevelDep().getLevel() == 3) {
				departLv1 = member.getDepartment().getDepartment().getDepartment();
			}
			if (member.getDepartment().getLevelDep().getLevel() == 2) {
				departLv1 = member.getDepartment().getDepartment();
			}
			if (member.getDepartment().getLevelDep().getLevel() == 1) {
				departLv1 = member.getDepartment();
			}
			for (Department d : departmentsLv1) {
				if (d.getCode().equals("20002") && nameLocation.equals("HO CHI MINH")) {
					departLv1 = d;
					nameLocation = "Thủ Đức";
				}
				if (d.getCode().equals("20001") && nameLocation.equals("BINH DUONG")) {
					departLv1 = d;
					nameLocation = "Bình Dương";
				}
				if (d.getCode().equals("20003") && nameLocation.equals("BAC NINH")) {
					departLv1 = d;
					nameLocation = "Bắc Ninh";
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		// end
	}

	// bao cao ds nv chua lam kpi
	// public void showReportEmployeeMissKPI() throws JRException, IOException {
	// String departmentName = "";
	// departmentName = getDepartmentName(departmentSelectedPersonalMonth);
	// List<ABCPersonMonth> dataReportPersonalMonth =
	// createDataReportABCPersonMonth(this.monthSelectedPersonal,
	// this.yearSelectedPersonal1, departmentSelectedPersonalMonth);
	// dataReportPersonalMonth.sort(Comparator.comparing(ABCPersonMonth::getDepartmentName));
	// if (!dataReportPersonalMonth.isEmpty()) {
	// String reportPath =
	// FacesContext.getCurrentInstance().getExternalContext()
	// .getRealPath("/resources/thaireports/kpi/AbcPersonalMonth.jasper");
	// JRDataSource beanDataSource = new
	// JRBeanCollectionDataSource(dataReportPersonalMonth);
	// Map<String, Object> importParam = new HashMap<String, Object>();
	//
	// String image = FacesContext.getCurrentInstance().getExternalContext()
	// .getRealPath("/resources/gfx/lixco_logo.png");
	// importParam.put("logo", image);
	// importParam.put("location", nameLocation);
	// importParam.put("year", yearSelectedPersonal1);
	// importParam.put("month", monthSelectedPersonal);
	// importParam.put("department", departmentName);
	// importParam.put("listAbcPersonMonth", beanDataSource);
	// JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath,
	// importParam, beanDataSource);
	// FacesContext facesContext = FacesContext.getCurrentInstance();
	// OutputStream outputStream;
	// outputStream =
	// facesContext.getExternalContext().getResponseOutputStream();
	// JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
	// facesContext.responseComplete();
	// } else {
	// noticeError("Không có dữ liệu");
	// }
	// }

	// end bao cao ds nv chua lam kpi
	// data bc ds chua lam kpi
	public List<ABCPersonMonth> createDataReportEmployeeMissKPI(int month, int year, Department department) {
		try {
			EMPLOYEE_SERVICE_PUBLIC = new EmployeeServicePublicProxy();
			List<ABCPersonMonth> dataPersonalMonth = new ArrayList<>();
			// tim danh sach nhan vien khong lam kpi
			List<EmployeeDontKPIEver> allEmpDontKPIEver = EMPLOYEE_DONT_KPI_EVER_SERVICE.findAll();

			allCodeEmployee = new ArrayList<>();
			if (department == null || department.getCode() == null) {
				DepartmentData[] allDepartByLv1 = DepartmentDataService.timtheophongquanly(departLv1.getCode());
				if (allDepartByLv1 != null) {
					StringBuilder builder = new StringBuilder();
					for (DepartmentData d : allDepartByLv1) {
						builder.append(d.getCode());
						builder.append(",");
					}
					String s = "";
					if (builder.toString().endsWith(",")) {
						s = builder.toString().substring(0, builder.toString().length() - 1);
					}
					EmployeeData[] allEmployeeArrayNew = EmployeeDataService.timtheophongban(s);
					if (allEmployeeArrayNew != null) {
						for (EmployeeData e : allEmployeeArrayNew) {
							allCodeEmployee.add(e.getCode());
						}
					}
				}
			}
			if (department != null && department.getCode() != null) {
				// String[] tempDepartmentArr = { department.getCode() };
				DepartmentData[] depsNew = null;
				if (department.getLevelDep().getLevel() == 2) {
					depsNew = DepartmentDataService.timtheophongquanly(department.getCode());
				}
				if (department.getLevelDep().getLevel() == 3) {
					depsNew = DepartmentDataService.timtheophongquanly(department.getDepartment().getCode());
				}
				StringBuilder builder = new StringBuilder();
				for (DepartmentData s : depsNew) {
					builder.append(s.getCode());
					builder.append(",");
				}
				String s = "";
				if (builder.toString().endsWith(",")) {
					s = builder.toString().substring(0, builder.toString().length() - 1);
				}

				EmployeeData[] allEmployeeArrayNew = EmployeeDataService.timtheophongbancomail(s);

				for (EmployeeData e : allEmployeeArrayNew) {
					allCodeEmployee.add(e.getCode());
				}
			}
			for (int i = 0; i < allCodeEmployee.size(); i++) {
				boolean isContinue = false;
				// kiem tra thu phai khong lam kpi hay khong
				for (int j = 0; j < allEmpDontKPIEver.size(); j++) {
					if (allEmpDontKPIEver.get(j).getEmployee_code().equals(allCodeEmployee.get(i))) {
						isContinue = true;
					}
				}
				// neu co nhan vien khong lam kpi se khong xuong code doan duoi
				if (isContinue) {
					continue;
				}
				// tao 1 item personalYear
				ABCPersonMonth personalMonthTemp = new ABCPersonMonth();
				// Tim nhan vien theo codeEmp KPIPerson -> nameEmp, nameDepart
				EmployeeDTO memberTemp = EMPLOYEE_SERVICE_PUBLIC.findByCode(allCodeEmployee.get(i));
				personalMonthTemp.setEmployeeName(memberTemp.getName());
				personalMonthTemp.setEmployeeCode(memberTemp.getCode());
				personalMonthTemp.setMonth(month);
				personalMonthTemp.setYear(year);
				personalMonthTemp.setId(i);

				// list KPIPerson by Employee and year
				List<KPIPerson> kpiPersonByEmpCode = KPI_PERSON_SERVICE.findRange(allCodeEmployee.get(i), month, year);
				// kiem tra thang do nhan vien da vao lam hay chua
				// truong hop kpi hang thang

				// 2 truong hop: hoac nhom co dinh, hoac khong lam kpi
				if (kpiPersonByEmpCode.isEmpty()) {
					List<KPIPersonalOther> personOtherTemp = KPI_PERSON_OTHER_SERVICE.find(null, month, year,
							allCodeEmployee.get(i));
					// nhom co dinh
					// nhom khong lam kpi
					if (personOtherTemp.isEmpty()) {
						// tim phong ban
						Department depTemp1 = DEPARTMENT_SERVICE_PUBLIC.findByCode("code", memberTemp.getCodeDepart());
						// neu khong phai truong phong
						if (depTemp1 != null && !memberTemp.getCode().equals(depTemp1.getCodeMem())) {
							// thai san hoac khong xet
							List<EmployeeDontKPI> emplDontKPI = EMPLOYEE_DONT_KPI_SERVICE
									.findByEmplMonthYear(allCodeEmployee.get(i), month, year);
							if (emplDontKPI.isEmpty()) {
								if (depTemp1 != null) {
									personalMonthTemp.setDepartmentName(depTemp1.getDepartment().getName());
									personalMonthTemp.setDepartmentLv3(depTemp1.getName());
								}
								dataPersonalMonth.add(personalMonthTemp);
							}
						}
					}
				}
			}
			if (dataPersonalMonth.size() != 0) {
				return dataPersonalMonth;
			} else {
				List<ABCPersonMonth> abc = new ArrayList<>();
				return abc;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	// end data

	// excel ds chua lam kpi
	public void excelEmployeeMissKPI() throws IOException {
		List<ABCPersonMonth> dataReportPersonalMonth = createDataReportEmployeeMissKPI(this.monthSelectedPersonal,
				this.yearSelectedPersonal1, departmentSelectedPersonalMonth);
		dataReportPersonalMonth.sort(Comparator.comparing(ABCPersonMonth::getDepartmentName)
				.thenComparing(ABCPersonMonth::getDepartmentLv3));

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("DS NV chua lam KPI");

		int rownum = 0;
		Cell cell;
		Row row;
		XSSFCellStyle style = createStyleForTitle(workbook);
		style.setAlignment(CellStyle.ALIGN_CENTER);

		row = sheet.createRow(rownum);

		// EmpNo
		cell = row.createCell(0);
		cell.setCellValue("Họ và tên");
		// EmpNo
		cell = row.createCell(1);
		cell.setCellValue("Mã nhân viên");
		// xep loai// EmpName
		cell = row.createCell(2);
		cell.setCellValue("Đơn vị");
		cell.setCellStyle(style);

		cell = row.createCell(3);
		cell.setCellValue("Bộ phận");
		cell.setCellStyle(style);

		cell = row.createCell(4);
		cell.setCellValue("Tháng");
		cell.setCellStyle(style);

		cell = row.createCell(5);
		cell.setCellValue("Năm");
		cell.setCellStyle(style);

		for (ABCPersonMonth kq : dataReportPersonalMonth) {
			rownum++;
			row = sheet.createRow(rownum);
			// ho ten
			cell = row.createCell(0);
			cell.setCellValue(kq.getEmployeeName());
			// ho ten
			cell = row.createCell(1);
			cell.setCellValue(kq.getEmployeeCode());
			// Don vi (B)
			cell = row.createCell(2);
			cell.setCellValue(kq.getDepartmentName());

			cell = row.createCell(3);
			cell.setCellValue(kq.getDepartmentLv3());

			cell = row.createCell(4);
			cell.setCellValue(kq.getMonth());

			cell = row.createCell(5);
			cell.setCellValue(kq.getYear());
		}
		String filename = "dsnvchualamkpi.xlsx";
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/vnd.ms-excel");
		externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
		workbook.write(externalContext.getResponseOutputStream());
		// cancel progress
		facesContext.responseComplete();
	}
	// end excel ds chua lam kpi

	public void showReportPersonalMonth() throws JRException, IOException {
		String departmentName = "";
		departmentName = getDepartmentName(departmentSelectedPersonalMonth);
		List<PersonalMonth> dataReportPersonalMonth = createDataReportKPIPersonalMonth(this.monthSelectedPersonal,
				this.yearSelectedPersonal1, departmentSelectedPersonalMonth);
		dataReportPersonalMonth.sort(Comparator.comparing(PersonalMonth::getDepartmentName));
		if (!dataReportPersonalMonth.isEmpty()) {
			String reportPath = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/thaireports/kpi/personalMonth.jasper");
			JRDataSource beanDataSource = new JRBeanCollectionDataSource(dataReportPersonalMonth);
			Map<String, Object> importParam = new HashMap<String, Object>();

			String image = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/gfx/lixco_logo.png");
			importParam.put("logo", image);
			importParam.put("location", nameLocation);
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

	// bao cao abc ca nhan thang
	public void showReportABCPersonMonth() throws JRException, IOException {
		String departmentName = "";
		departmentName = getDepartmentName(departmentSelectedPersonalMonth);
		List<ABCPersonMonth> dataReportPersonalMonth = createDataReportABCPersonMonth(this.monthSelectedPersonal,
				this.yearSelectedPersonal1, departmentSelectedPersonalMonth);
		dataReportPersonalMonth.sort(Comparator.comparing(ABCPersonMonth::getDepartmentName)
				.thenComparing(ABCPersonMonth::getDepartmentLv3));
		if (!dataReportPersonalMonth.isEmpty()) {
			String reportPath = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/thaireports/kpi/AbcPersonalMonth.jasper");
			JRDataSource beanDataSource = new JRBeanCollectionDataSource(dataReportPersonalMonth);
			Map<String, Object> importParam = new HashMap<String, Object>();

			String image = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/gfx/lixco_logo.png");
			importParam.put("logo", image);
			importParam.put("location", nameLocation);
			importParam.put("year", yearSelectedPersonal1);
			importParam.put("month", monthSelectedPersonal);
			importParam.put("department", departmentName);
			importParam.put("listAbcPersonMonth", beanDataSource);
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

	// end abc ca nhan thang
	// data abc ca nhan thang
	public List<ABCPersonMonth> createDataReportABCPersonMonth(int month, int year, Department department) {
		try {
			EMPLOYEE_SERVICE_PUBLIC = new EmployeeServicePublicProxy();
			List<ABCPersonMonth> dataPersonalMonth = new ArrayList<>();
			// tim danh sach nhan vien khong lam kpi
			List<EmployeeDontKPIEver> allEmpDontKPIEver = EMPLOYEE_DONT_KPI_EVER_SERVICE.findAll();

			// tim cap duoi xuong san xuat
			DepartmentData[] depsXuongSanXuat = DepartmentDataService.timtheophongquanly("30005");
			DepartmentData[] depsXuongSanXuatBD = DepartmentDataService.timtheophongquanly("30015");
			DepartmentData[] depsXuongSanXuatBN = DepartmentDataService.timtheophongquanly("30018");
			List<DepartmentData> depsXuongSanXuatList = new ArrayList<>();
			for (DepartmentData d : depsXuongSanXuat) {
				if (!d.getCode().equals("40003")) {
					depsXuongSanXuatList.add(d);
				}
			}
			// bd
			for (DepartmentData d : depsXuongSanXuatBD) {
				if (!d.getCode().equals("40026")) {
					depsXuongSanXuatList.add(d);
				}
			}
			// bn
			for (DepartmentData d : depsXuongSanXuatBN) {
				if (!d.getCode().equals("40057")) {
					depsXuongSanXuatList.add(d);
				}
			}

			allCodeEmployee = new ArrayList<>();
			if (department == null || department.getCode() == null) {
				DepartmentData[] allDepartByLv1 = DepartmentDataService.timtheophongquanly(departLv1.getCode());
				if (allDepartByLv1 != null) {
					StringBuilder builder = new StringBuilder();
					for (DepartmentData d : allDepartByLv1) {
						builder.append(d.getCode());
						builder.append(",");
					}
					String s = "";
					if (builder.toString().endsWith(",")) {
						s = builder.toString().substring(0, builder.toString().length() - 1);
					}
					s = s + ";" + month + "," + year;
					// EmployeeData[] allEmployeeArrayNew =
					// EmployeeDataService.timtheophongban(s);
					EmployeeData[] allEmployeeArrayNew = EmployeeDataService.findByListDepartForKPI(s);
					if (allEmployeeArrayNew != null) {
						for (EmployeeData e : allEmployeeArrayNew) {
							allCodeEmployee.add(e.getCode());
						}
					}
				}
			}
			if (department != null && department.getCode() != null) {
				// String[] tempDepartmentArr = { department.getCode() };
				DepartmentData[] depsNew = null;
				if (department.getLevelDep().getLevel() == 2) {
					depsNew = DepartmentDataService.timtheophongquanly(department.getCode());
				}
				if (department.getLevelDep().getLevel() == 3) {
					depsNew = DepartmentDataService.timtheophongquanly(department.getDepartment().getCode());
				}
				StringBuilder builder = new StringBuilder();
				for (DepartmentData s : depsNew) {
					builder.append(s.getCode());
					builder.append(",");
				}
				String s = "";
				if (builder.toString().endsWith(",")) {
					s = builder.toString().substring(0, builder.toString().length() - 1);
				}
				s = s + ";" + month + "," + year;
				EmployeeData[] allEmployeeArrayNew = EmployeeDataService.findByListDepartForKPI(s);
				// EmployeeData[] allEmployeeArrayNew =
				// EmployeeDataService.timtheophongbancomail(s);
				if (allEmployeeArrayNew != null) {
					for (EmployeeData e : allEmployeeArrayNew) {
						allCodeEmployee.add(e.getCode());
					}
				}
			}
			for (int i = 0; i < allCodeEmployee.size(); i++) {
				boolean isContinue = false;
				// kiem tra thu phai khong lam kpi hay khong
				for (int j = 0; j < allEmpDontKPIEver.size(); j++) {
					if (allEmpDontKPIEver.get(j).getEmployee_code().equals(allCodeEmployee.get(i))) {
						isContinue = true;
					}
				}
				// neu co nhan vien khong lam kpi se khong xuong code doan duoi
				if (isContinue) {
					continue;
				}
				// tao 1 item personalYear
				ABCPersonMonth personalMonthTemp = new ABCPersonMonth();
				// Tim nhan vien theo codeEmp KPIPerson -> nameEmp, nameDepart
				EmployeeDTO memberTemp = EMPLOYEE_SERVICE_PUBLIC.findByCode(allCodeEmployee.get(i));
				personalMonthTemp.setEmployeeName(memberTemp.getName());
				personalMonthTemp.setEmployeeCode(memberTemp.getCode());
				personalMonthTemp.setMonth(month);
				personalMonthTemp.setYear(year);
				personalMonthTemp.setId(i);

				// list KPIPerson by Employee and year
				List<KPIPerson> kpiPersonByEmpCode = KPI_PERSON_SERVICE.findRange(allCodeEmployee.get(i), month, year);
				// kiem tra thang do nhan vien da vao lam hay chua
				// truong hop kpi hang thang
				if (!kpiPersonByEmpCode.isEmpty()) {
					// tim phong ban
					Department depTemp = DEPARTMENT_SERVICE_PUBLIC.findByCode("code", memberTemp.getCodeDepart());
					// kpi phong theo nhan vien
					List<KPIDepMonth> kpiDepartmentByEmpCode = new ArrayList<>();
					if (depTemp.getLevelDep().getLevel() == 2) {
						personalMonthTemp.setDepartmentName(depTemp.getName());
					}
					if (depTemp.getLevelDep().getLevel() == 3) {
						personalMonthTemp.setDepartmentName(depTemp.getDepartment().getName());
					}
					personalMonthTemp.setDepartmentLv3(depTemp.getName());
					// kpi phong theo nhan vien
					kpiDepartmentByEmpCode = KPI_DEPARTMENT_MONTH.findKPIDepMonth(month, year,
							depTemp.getDepartment().getCode());
					double result = 0;
					// kiem tra duoi bang kpi ca nhan co null hay khong
					// if (kpiPersonByEmpCode.isEmpty()) {
					personalMonthTemp.setKpiCaNhan(kpiPersonByEmpCode.get(0).getTotal());
					// kpi ca nhan va phong deu null
					if (kpiDepartmentByEmpCode.isEmpty()) {
						// diem kpi to hoac phong
						personalMonthTemp.setKpiTo(0);
						// kpi to 40%, kpi to 60%
						result = (double) ((0 * 40) / 100) + (double) ((personalMonthTemp.getKpiCaNhan() * 60) / 100);
						// tinh tong kpiPersonal * 60 + kpiDeparment * 40
						result = (double) Math.round(result * 100) / 100;
						personalMonthTemp.setTongdiem(result);

						// Cho nay se set xep loai -> bo sung sau
						personalMonthTemp.setXeploai(tinhXepLoai(personalMonthTemp.getTongdiem()));
						dataPersonalMonth.add(personalMonthTemp);
					} else {
						// diem kpi to hoac phong
						personalMonthTemp.setKpiTo(kpiDepartmentByEmpCode.get(0).getResult());
						result = (double) ((kpiDepartmentByEmpCode.get(0).getResult() * 40) / 100)
								+ (double) ((personalMonthTemp.getKpiCaNhan() * 60) / 100);
						result = (double) Math.round(result * 100) / 100;
						// tinh tong kpiPersonal * 60 + kpiDeparment * 40
						personalMonthTemp.setTongdiem(result);

						// Cho nay se set xep loai -> bo sung sau
						personalMonthTemp.setXeploai(tinhXepLoai(personalMonthTemp.getTongdiem()));
						dataPersonalMonth.add(personalMonthTemp);
					}
				}
				// 2 truong hop: hoac nhom co dinh, hoac khong lam kpi
				else {
					double result = 0;
					List<KPIPersonalOther> personOtherTemp = KPI_PERSON_OTHER_SERVICE.find(null, month, year,
							allCodeEmployee.get(i));
					// tim phong ban
					Department depTemp = DEPARTMENT_SERVICE_PUBLIC.findByCode("code", memberTemp.getCodeDepart());
					if (depTemp != null) {
						personalMonthTemp.setDepartmentName(depTemp.getDepartment().getName());
						personalMonthTemp.setDepartmentLv3(depTemp.getName());
					}
					// nhom co dinh
					if (!personOtherTemp.isEmpty()) {
						// kiem tra co phai xuong san xuat khong de lay kpi to
						boolean isTo = false;
						for (int t = 0; t < depsXuongSanXuatList.size(); t++) {
							if (depTemp.getCode().equals(depsXuongSanXuatList.get(t).getCode())) {
								isTo = true;
								break;
							}
						}
						List<KPITo> kpiToTemp = null;
						// neu khong phai xuong san xuat -> kpi phong
						if (!isTo) {
							// kpi phong theo nhan vien
							List<KPIDepMonth> kpiDepartmentByEmpCode = KPI_DEPARTMENT_MONTH.findKPIDepMonth(month, year,
									depTemp.getDepartment().getCode());
							if (kpiDepartmentByEmpCode.isEmpty()) {
								personalMonthTemp.setKpiTo(0);
							} else {
								personalMonthTemp.setKpiTo(kpiDepartmentByEmpCode.get(0).getResult());
							}
						}
						// kpi to
						else {
							kpiToTemp = KPI_TO_SERVICE.findKPIDepMonth(month, year, depTemp.getCode());
							if (kpiToTemp.isEmpty()) {
								personalMonthTemp.setKpiTo(0);
							} else {
								personalMonthTemp.setKpiTo(kpiToTemp.get(0).getResult());
							}
						}
						personalMonthTemp.setKpiCaNhan(personOtherTemp.get(0).getTotal());

						result = (double) ((personalMonthTemp.getKpiTo() * 40) / 100)
								+ (double) ((personalMonthTemp.getKpiCaNhan() * 60) / 100);
						// tinh tong kpiPersonal * 60 + kpiDeparment * 40
						result = (double) Math.round(result * 100) / 100;
						personalMonthTemp.setTongdiem(result);
						personalMonthTemp.setXeploai(tinhXepLoai(personalMonthTemp.getTongdiem()));
						dataPersonalMonth.add(personalMonthTemp);
						// }
					}
					// nhom khong lam kpi
					else {
						// tim phong ban
						Department depTemp1 = DEPARTMENT_SERVICE_PUBLIC.findByCode("code", memberTemp.getCodeDepart());
						// neu la truong phong
						if (depTemp1 != null && memberTemp.getCode().equals(depTemp1.getCodeMem())) {
							// kpi phong theo nhan vien
							List<KPIDepMonth> kpiDepartmentByEmpCode = new ArrayList<>();
							if (depTemp1.getLevelDep().getLevel() == 2) {
								personalMonthTemp.setDepartmentName(depTemp1.getName());
							}
							if (depTemp1.getLevelDep().getLevel() == 3) {
								personalMonthTemp.setDepartmentName(depTemp1.getDepartment().getName());
							}
							personalMonthTemp.setDepartmentLv3(depTemp1.getName());
							// kpi phong theo nhan vien
							kpiDepartmentByEmpCode = KPI_DEPARTMENT_MONTH.findKPIDepMonth(month, year,
									depTemp1.getDepartment().getCode());
							double result1 = 0;
							// kiem tra duoi bang kpi ca nhan co null hay khong

							if (kpiDepartmentByEmpCode.isEmpty()) {
								// diem kpi to hoac phong
								personalMonthTemp.setKpiTo(0);
								personalMonthTemp.setKpiCaNhan(0);
							} else {
								// diem kpi to hoac phong
								personalMonthTemp.setKpiTo(kpiDepartmentByEmpCode.get(0).getResult());
								personalMonthTemp.setKpiCaNhan(kpiDepartmentByEmpCode.get(0).getResult());
							}
							// kpi to 40%, kpi to 60%
							result1 = (double) ((personalMonthTemp.getKpiTo() * 40) / 100)
									+ (double) ((personalMonthTemp.getKpiCaNhan() * 60) / 100);
							// tinh tong kpiPersonal * 60 + kpiDeparment *
							// 40
							// lam tron
							result1 = (double) Math.round(result1 * 100) / 100;
							personalMonthTemp.setTongdiem(result1);
							// Cho nay se set xep loai -> bo sung sau
							personalMonthTemp.setXeploai(tinhXepLoai(personalMonthTemp.getTongdiem()));
							dataPersonalMonth.add(personalMonthTemp);
						}
						// khong phai truong phong
						else {
							// thai san hoac khong xet
							List<EmployeeDontKPI> emplDontKPI = EMPLOYEE_DONT_KPI_SERVICE
									.findByEmplMonthYear(allCodeEmployee.get(i), month, year);
							if (!emplDontKPI.isEmpty()) {
								if (depTemp1 != null) {
									personalMonthTemp.setDepartmentName(depTemp1.getDepartment().getName());
									personalMonthTemp.setDepartmentLv3(depTemp1.getName());
								}
								if (emplDontKPI.get(0).isIs_temp()) {
									personalMonthTemp.setNote("Không xét");
									personalMonthTemp.setXeploai("");
									dataPersonalMonth.add(personalMonthTemp);
								}
								if (emplDontKPI.get(0).isIs_thaisan()) {
									personalMonthTemp.setXeploai("B");
									personalMonthTemp.setNote("Thai sản");
									dataPersonalMonth.add(personalMonthTemp);
								}
								if (emplDontKPI.get(0).isIs_nghiom()) {
									personalMonthTemp.setXeploai("B");
									personalMonthTemp.setNote("Nghỉ ốm");
									dataPersonalMonth.add(personalMonthTemp);
								}
							}
						}
					}
				}
			}
			if (dataPersonalMonth.size() != 0) {
				return dataPersonalMonth;
			} else {
				List<ABCPersonMonth> abc = new ArrayList<>();
				return abc;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// end data
	// excel abc thang
	public void excelABCPersonalMonth() throws IOException {
		List<ABCPersonMonth> dataReportPersonalMonth = createDataReportABCPersonMonth(this.monthSelectedPersonal,
				this.yearSelectedPersonal1, departmentSelectedPersonalMonth);
		dataReportPersonalMonth.sort(Comparator.comparing(ABCPersonMonth::getDepartmentName)
				.thenComparing(ABCPersonMonth::getDepartmentLv3));

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("ABC CÁ NHÂN THÁNG");

		int rownum = 0;
		Cell cell;
		Row row;
		XSSFCellStyle style = createStyleForTitle(workbook);
		style.setAlignment(CellStyle.ALIGN_CENTER);

		row = sheet.createRow(rownum);

		// EmpNo
		cell = row.createCell(0);
		cell.setCellValue("Họ và tên");
		// EmpNo
		cell = row.createCell(1);
		cell.setCellValue("Mã nhân viên");
		// xep loai// EmpName
		cell = row.createCell(2);
		cell.setCellValue("Đơn vị");
		cell.setCellStyle(style);

		cell = row.createCell(3);
		cell.setCellValue("Bộ phận");
		cell.setCellStyle(style);

		cell = row.createCell(4);
		cell.setCellValue("KPI Phòng/Tổ 40%");
		cell.setCellStyle(style);
		// Grade
		cell = row.createCell(5);
		cell.setCellValue("KPI cá nhân 60%");
		cell.setCellStyle(style);
		// Bonus
		cell = row.createCell(6);
		cell.setCellValue("Tổng điểm");
		cell.setCellStyle(style);

		cell = row.createCell(7);
		cell.setCellValue("Xếp loại");
		cell.setCellStyle(style);

		cell = row.createCell(8);
		cell.setCellValue("Ghi chú");
		cell.setCellStyle(style);

		cell = row.createCell(9);
		cell.setCellValue("Tháng");
		cell.setCellStyle(style);

		cell = row.createCell(10);
		cell.setCellValue("Năm");
		cell.setCellStyle(style);

		for (ABCPersonMonth kq : dataReportPersonalMonth) {
			rownum++;
			row = sheet.createRow(rownum);
			// ho ten
			cell = row.createCell(0);
			cell.setCellValue(kq.getEmployeeName());
			// ho ten
			cell = row.createCell(1);
			cell.setCellValue(kq.getEmployeeCode());
			// Don vi (B)
			cell = row.createCell(2);
			cell.setCellValue(kq.getDepartmentName());

			cell = row.createCell(3);
			cell.setCellValue(kq.getDepartmentLv3());

			cell = row.createCell(4);
			cell.setCellValue(kq.getKpiTo());

			cell = row.createCell(5);
			cell.setCellValue(kq.getKpiCaNhan());

			cell = row.createCell(6);
			cell.setCellValue(kq.getTongdiem());

			cell = row.createCell(7);
			cell.setCellValue(kq.getXeploai());

			if (StringUtils.isNotEmpty(kq.getNote())) {
				cell = row.createCell(8);
				cell.setCellValue(kq.getNote());
			}

			cell = row.createCell(9);
			cell.setCellValue(kq.getMonth());

			cell = row.createCell(10);
			cell.setCellValue(kq.getYear());
		}
		String filename = "abcthang.xlsx";
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/vnd.ms-excel");
		externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
		workbook.write(externalContext.getResponseOutputStream());
		// cancel progress
		facesContext.responseComplete();
	}
	// end excel

	public String tinhXepLoai(double tongdiem) {
		if (tongdiem >= 90) {
			return "A";
		}
		if (tongdiem >= 80 && tongdiem < 90) {
			return "B";
		}
		if (tongdiem >= 70 && tongdiem < 80) {
			return "C";
		}
		if (tongdiem < 70) {
			return "D";
		}
		return "";
	}

	public void showReportPersonalQuy() throws JRException, IOException {
		String reportPath = FacesContext.getCurrentInstance().getExternalContext()
				.getRealPath("/resources/thaireports/kpi/personalQuy.jasper");
		String departmentName = "";
		departmentName = getDepartmentName(departmentSelectedPersonalQuy);
		List<PersonalQuy> dataReportPersonalQuy = createDataReportKPIPersonalQuy(this.quySelectedPersonal,
				this.yearSelectedPersonal2, departmentSelectedPersonalQuy);
		dataReportPersonalQuy.sort(Comparator.comparing(PersonalQuy::getNameDepartment));
		// check neu list rong~
		if (!dataReportPersonalQuy.isEmpty()) {
			JRDataSource beanDataSource = new JRBeanCollectionDataSource(dataReportPersonalQuy);
			Map<String, Object> importParam = new HashMap<String, Object>();

			String image = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/gfx/lixco_logo.png");
			importParam.put("logo", image);
			importParam.put("location", nameLocation);
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
		dataReportPersonalYear.sort(Comparator.comparing(PersonalYear::getNameDepartment));
		if (!dataReportPersonalYear.isEmpty()) {
			JRDataSource beanDataSource = new JRBeanCollectionDataSource(dataReportPersonalYear);
			Map<String, Object> importParam = new HashMap<String, Object>();

			String image = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/gfx/lixco_logo.png");
			importParam.put("logo", image);
			importParam.put("location", nameLocation);
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
		dataReportDepartmentYear.sort(Comparator.comparing(DepartmentTotalMonth::getNameDepart));
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
			// //print excel
			// JRXlsxExporter exporter = new JRXlsxExporter();
			// exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			// exporter.setExporterOutput(new
			// SimpleOutputStreamExporterOutput("D:\\demo\\sample_report.xlsx"));
			// //Set configuration as you like it!!
			// SimpleXlsxExporterConfiguration configuration = null;
			// configuration = new SimpleXlsxExporterConfiguration();
			// configuration.setKeepWorkbookTemplateSheets(true);
			// exporter.setConfiguration(configuration);
			// exporter.exportReport();
			// //end excel
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

		// ****cho nay lam sao lay duoc list nhan vien (- nhan vien thang do
		// chua vo lam
		// thang sau vo)

		// truong hop khong co phong ban
		allCodeEmployee = new ArrayList<>();
		if (department == null || department.getCode() == null) {
			DepartmentData[] allDepartByLv1 = DepartmentDataService.timtheophongquanly(departLv1.getCode());
			if (allDepartByLv1 != null) {
				StringBuilder builder = new StringBuilder();
				for (DepartmentData d : allDepartByLv1) {
					builder.append(d.getCode());
					builder.append(",");
				}
				String s = "";
				if (builder.toString().endsWith(",")) {
					s = builder.toString().substring(0, builder.toString().length() - 1);
				}
				EmployeeData[] allEmployeeArrayNew = EmployeeDataService.timtheophongbancomail(s);
				// EmployeeDTO[] allEmployeeArray =
				// EMPLOYEE_SERVICE_PUBLIC.findByDep(allCodeDepartmentArray);
				if (allEmployeeArrayNew != null) {
					for (EmployeeData e : allEmployeeArrayNew) {
						allCodeEmployee.add(e.getCode());
					}
				}
			}
		}
		if (department != null && department.getCode() != null) {
			DepartmentData[] depsNew = null;
			if (department.getLevelDep().getLevel() == 2) {
				depsNew = DepartmentDataService.timtheophongquanly(department.getCode());
			}
			if (department.getLevelDep().getLevel() == 3) {
				depsNew = DepartmentDataService.timtheophongquanly(department.getDepartment().getCode());
			}
			// EmployeeDTO[] allEmployeeArray =
			// EMPLOYEE_SERVICE_PUBLIC.findByDep(tempDepartmentArr);
			StringBuilder builder = new StringBuilder();
			for (DepartmentData s : depsNew) {
				builder.append(s.getCode());
				builder.append(",");
			}
			String s = "";
			if (builder.toString().endsWith(",")) {
				s = builder.toString().substring(0, builder.toString().length() - 1);
			}

			EmployeeData[] allEmployeeArrayNew = EmployeeDataService.timtheophongbancomail(s);

			for (EmployeeData e : allEmployeeArrayNew) {
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
		if (department == null || department.getCode() == null) {
			// EmployeeDTO[] allEmployeeArray =
			// EMPLOYEE_SERVICE_PUBLIC.findByDep(allCodeDepartmentArray);
			// if (allEmployeeArray != null) {
			// for (EmployeeDTO e : allEmployeeArray) {
			// allCodeEmployee.add(e.getCode());
			// }
			// }
			DepartmentData[] allDepartByLv1 = DepartmentDataService.timtheophongquanly(departLv1.getCode());
			if (allDepartByLv1 != null) {
				StringBuilder builder = new StringBuilder();
				for (DepartmentData d : allDepartByLv1) {
					builder.append(d.getCode());
					builder.append(",");
				}
				String s = "";
				if (builder.toString().endsWith(",")) {
					s = builder.toString().substring(0, builder.toString().length() - 1);
				}
				EmployeeData[] allEmployeeArrayNew = EmployeeDataService.timtheophongbancomail(s);
				// EmployeeDTO[] allEmployeeArray =
				// EMPLOYEE_SERVICE_PUBLIC.findByDep(allCodeDepartmentArray);
				if (allEmployeeArrayNew != null) {
					for (EmployeeData e : allEmployeeArrayNew) {
						allCodeEmployee.add(e.getCode());
					}
				}
			}
		}
		if (department != null && department.getCode() != null) {
			DepartmentData[] depsNew = null;
			if (department.getLevelDep().getLevel() == 2) {
				depsNew = DepartmentDataService.timtheophongquanly(department.getCode());
			}
			if (department.getLevelDep().getLevel() == 3) {
				depsNew = DepartmentDataService.timtheophongquanly(department.getDepartment().getCode());
			}
			// EmployeeDTO[] allEmployeeArray =
			// EMPLOYEE_SERVICE_PUBLIC.findByDep(tempDepartmentArr);
			StringBuilder builder = new StringBuilder();
			for (DepartmentData s : depsNew) {
				builder.append(s.getCode());
				builder.append(",");
			}
			String s = "";
			if (builder.toString().endsWith(",")) {
				s = builder.toString().substring(0, builder.toString().length() - 1);
			}

			EmployeeData[] allEmployeeArrayNew = EmployeeDataService.timtheophongbancomail(s);

			for (EmployeeData e : allEmployeeArrayNew) {
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
			if (department == null || department.getCode() == null) {
				DepartmentData[] allDepartByLv1 = DepartmentDataService.timtheophongquanly(departLv1.getCode());
				if (allDepartByLv1 != null) {
					StringBuilder builder = new StringBuilder();
					for (DepartmentData d : allDepartByLv1) {
						builder.append(d.getCode());
						builder.append(",");
					}
					String s = "";
					if (builder.toString().endsWith(",")) {
						s = builder.toString().substring(0, builder.toString().length() - 1);
					}
					EmployeeData[] allEmployeeArrayNew = EmployeeDataService.timtheophongbancomail(s);
					// EmployeeDTO[] allEmployeeArray =
					// EMPLOYEE_SERVICE_PUBLIC.findByDep(allCodeDepartmentArray);
					if (allEmployeeArrayNew != null) {
						for (EmployeeData e : allEmployeeArrayNew) {
							allCodeEmployee.add(e.getCode());
						}
					}
				}

				// List<KPIPerson> kpiPersonByCodeEmp =
				// KPI_PERSON_SERVICE.findRange(month, year);
				// if (kpiPersonByCodeEmp != null &&
				// !kpiPersonByCodeEmp.isEmpty()) {
				// for (KPIPerson k : kpiPersonByCodeEmp) {
				// if (k.getCodeEmp() != null) {
				// allCodeEmployee.add(k.getCodeEmp());
				// }
				// }
				// }
			}
			if (department != null && department.getCode() != null) {
				// String[] tempDepartmentArr = { department.getCode() };
				DepartmentData[] depsNew = null;
				if (department.getLevelDep().getLevel() == 2) {
					depsNew = DepartmentDataService.timtheophongquanly(department.getCode());
				}
				if (department.getLevelDep().getLevel() == 3) {
					depsNew = DepartmentDataService.timtheophongquanly(department.getDepartment().getCode());
				}
				// EmployeeDTO[] allEmployeeArray =
				// EMPLOYEE_SERVICE_PUBLIC.findByDep(tempDepartmentArr);
				StringBuilder builder = new StringBuilder();
				for (DepartmentData s : depsNew) {
					builder.append(s.getCode());
					builder.append(",");
				}
				String s = "";
				if (builder.toString().endsWith(",")) {
					s = builder.toString().substring(0, builder.toString().length() - 1);
				}

				EmployeeData[] allEmployeeArrayNew = EmployeeDataService.timtheophongbancomail(s);

				for (EmployeeData e : allEmployeeArrayNew) {
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
		// for (Department d : allDepartment) {
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

	public String getDepartmentName(Department department) {
		if (department != null) {
			return department.getName();
		} else {
			return "Tất cả";
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

	// EXCEL
	private static XSSFCellStyle createStyleForTitle(XSSFWorkbook workbook) {
		XSSFFont font = workbook.createFont();
		font.setBold(true);
		XSSFCellStyle style = workbook.createCellStyle();
		style.setFont(font);
		return style;
	}

	public void excelDepartmentYear() throws IOException {
		List<DepartmentTotalMonth> dataReportDepartmentYear = createDataReportKPIDepartmentYear(yearSelectedDepartment);
		dataReportDepartmentYear.sort(Comparator.comparing(DepartmentTotalMonth::getNameDepart));
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("KPI PHÒNG NĂM");

		int rownum = 0;
		Cell cell;
		Row row;
		XSSFCellStyle style = createStyleForTitle(workbook);
		style.setAlignment(CellStyle.ALIGN_CENTER);

		CellStyle styleContent = workbook.createCellStyle();
		row = sheet.createRow(rownum);

		// EmpNo
		cell = row.createCell(0);
		cell.setCellValue("Phòng ban");
		// Salary
		cell = row.createCell(1);
		cell.setCellValue("Tháng 1");
		cell.setCellStyle(style);
		// Grade
		cell = row.createCell(2);
		cell.setCellValue("Tháng 2");
		cell.setCellStyle(style);
		// Bonus
		cell = row.createCell(3);
		cell.setCellValue("Tháng 3");
		cell.setCellStyle(style);
		cell = row.createCell(4);
		cell.setCellValue("Tháng 4");
		cell.setCellStyle(style);
		// xep loai
		cell = row.createCell(5);
		cell.setCellValue("Tháng 5");
		cell.setCellStyle(style);
		// xep loai
		cell = row.createCell(6);
		cell.setCellValue("Tháng 6");
		cell.setCellStyle(style);
		// xep loai
		cell = row.createCell(7);
		cell.setCellValue("Tháng 7");
		cell.setCellStyle(style);
		// xep loai
		cell = row.createCell(8);
		cell.setCellValue("Tháng 8");
		cell.setCellStyle(style);

		cell = row.createCell(9);
		cell.setCellValue("Tháng 9");
		cell.setCellStyle(style);

		cell = row.createCell(10);
		cell.setCellValue("Tháng 10");
		cell.setCellStyle(style);

		cell = row.createCell(11);
		cell.setCellValue("Tháng 11");
		cell.setCellStyle(style);

		cell = row.createCell(12);
		cell.setCellValue("Tháng 12");
		cell.setCellStyle(style);

		cell = row.createCell(13);
		cell.setCellValue("KPI TB năm");
		cell.setCellStyle(style);

		cell = row.createCell(14);
		cell.setCellValue("KPI năm");
		cell.setCellStyle(style);
		// Data
		for (DepartmentTotalMonth kq : dataReportDepartmentYear) {
			rownum++;
			row = sheet.createRow(rownum);
			// ho ten
			cell = row.createCell(0);
			cell.setCellValue(kq.getNameDepart());
			// Don vi (B)
			cell = row.createCell(1);
			cell.setCellValue(kq.getThang1());

			cell = row.createCell(2);
			cell.setCellValue(kq.getThang2());

			cell = row.createCell(3);
			cell.setCellValue(kq.getThang3());

			cell = row.createCell(4);
			cell.setCellValue(kq.getThang4());

			cell = row.createCell(5);
			cell.setCellValue(kq.getThang5());

			cell = row.createCell(6);
			cell.setCellValue(kq.getThang6());

			cell = row.createCell(7);
			cell.setCellValue(kq.getThang7());

			cell = row.createCell(8);
			cell.setCellValue(kq.getThang8());

			cell = row.createCell(9);
			cell.setCellValue(kq.getThang9());

			cell = row.createCell(10);
			cell.setCellValue(kq.getThang10());

			cell = row.createCell(11);
			cell.setCellValue(kq.getThang11());

			cell = row.createCell(12);
			cell.setCellValue(kq.getThang12());

			cell = row.createCell(13);
			cell.setCellValue(kq.getKpiAvgYear());

			cell = row.createCell(14);
			cell.setCellValue(kq.getKpiYear());

		}

		String filename = "kpiphongnam.xlsx";
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/vnd.ms-excel");
		externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
		workbook.write(externalContext.getResponseOutputStream());
		// cancel progress
		facesContext.responseComplete();
	}

	// ca nhan nam
	public void excelPersonalYear() throws IOException {
		List<PersonalYear> dataReportPersonalYear = createDataReportKPIPersonalYear(this.departmentSelectedPersonalYear,
				this.yearSelectedPersonal3);
		dataReportPersonalYear.sort(Comparator.comparing(PersonalYear::getNameDepartment));

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("KPI CÁ NHÂN NĂM");

		int rownum = 0;
		Cell cell;
		Row row;
		XSSFCellStyle style = createStyleForTitle(workbook);
		style.setAlignment(CellStyle.ALIGN_CENTER);

		row = sheet.createRow(rownum);

		// EmpNo
		cell = row.createCell(0);
		cell.setCellValue("Họ và tên");
		// xep loai// EmpName
		cell = row.createCell(1);
		cell.setCellValue("Đơn vị");
		cell.setCellStyle(style);
		// Salary
		cell = row.createCell(2);
		cell.setCellValue("Tháng 1");
		cell.setCellStyle(style);
		// Grade
		cell = row.createCell(3);
		cell.setCellValue("Tháng 2");
		cell.setCellStyle(style);
		// Bonus
		cell = row.createCell(4);
		cell.setCellValue("Tháng 3");
		cell.setCellStyle(style);
		cell = row.createCell(5);
		cell.setCellValue("Tháng 4");
		cell.setCellStyle(style);
		// xep loai
		cell = row.createCell(6);
		cell.setCellValue("Tháng 5");
		cell.setCellStyle(style);
		// xep loai
		cell = row.createCell(7);
		cell.setCellValue("Tháng 6");
		cell.setCellStyle(style);
		// xep loai
		cell = row.createCell(8);
		cell.setCellValue("Tháng 7");
		cell.setCellStyle(style);
		// xep loai
		cell = row.createCell(9);
		cell.setCellValue("Tháng 8");
		cell.setCellStyle(style);

		cell = row.createCell(10);
		cell.setCellValue("Tháng 9");
		cell.setCellStyle(style);

		cell = row.createCell(11);
		cell.setCellValue("Tháng 10");
		cell.setCellStyle(style);

		cell = row.createCell(12);
		cell.setCellValue("Tháng 11");
		cell.setCellStyle(style);

		cell = row.createCell(13);
		cell.setCellValue("Tháng 12");
		cell.setCellStyle(style);

		cell = row.createCell(14);
		cell.setCellValue("Năm");
		cell.setCellStyle(style);

		cell = row.createCell(15);
		cell.setCellValue("Xếp loại");
		cell.setCellStyle(style);

		for (PersonalYear kq : dataReportPersonalYear) {
			rownum++;
			row = sheet.createRow(rownum);
			// ho ten
			cell = row.createCell(0);
			cell.setCellValue(kq.getNameEmp());
			// Don vi (B)
			cell = row.createCell(1);
			cell.setCellValue(kq.getNameDepartment());

			cell = row.createCell(2);
			cell.setCellValue(kq.getThang1());

			cell = row.createCell(3);
			cell.setCellValue(kq.getThang2());

			cell = row.createCell(4);
			cell.setCellValue(kq.getThang3());

			cell = row.createCell(5);
			cell.setCellValue(kq.getThang4());

			cell = row.createCell(6);
			cell.setCellValue(kq.getThang5());

			cell = row.createCell(7);
			cell.setCellValue(kq.getThang6());

			cell = row.createCell(8);
			cell.setCellValue(kq.getThang7());

			cell = row.createCell(9);
			cell.setCellValue(kq.getThang8());

			cell = row.createCell(10);
			cell.setCellValue(kq.getThang9());

			cell = row.createCell(11);
			cell.setCellValue(kq.getThang10());

			cell = row.createCell(12);
			cell.setCellValue(kq.getThang11());

			cell = row.createCell(13);
			cell.setCellValue(kq.getThang12());

			cell = row.createCell(14);
			cell.setCellValue("");

			cell = row.createCell(15);
			cell.setCellValue("");

		}
		String filename = "kpicanhannam.xlsx";
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/vnd.ms-excel");
		externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
		workbook.write(externalContext.getResponseOutputStream());
		// cancel progress
		facesContext.responseComplete();
	}

	// ca nhan quy
	public void excelPersonalQuy() throws IOException {
		List<PersonalQuy> dataReportPersonalQuy = createDataReportKPIPersonalQuy(this.quySelectedPersonal,
				this.yearSelectedPersonal2, departmentSelectedPersonalQuy);

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("KPI CÁ NHÂN NĂM");

		int rownum = 0;
		Cell cell;
		Row row;
		XSSFCellStyle style = createStyleForTitle(workbook);
		style.setAlignment(CellStyle.ALIGN_CENTER);

		row = sheet.createRow(rownum);

		// EmpNo
		cell = row.createCell(0);
		cell.setCellValue("Họ và tên");
		// xep loai// EmpName
		cell = row.createCell(1);
		cell.setCellValue("Đơn vị");
		cell.setCellStyle(style);
		// Salary
		cell = row.createCell(2);
		cell.setCellValue(dataReportPersonalQuy.get(0).getFirstMonth());
		cell.setCellStyle(style);
		// Grade
		cell = row.createCell(3);
		cell.setCellValue(dataReportPersonalQuy.get(1).getSecondndMonth());
		cell.setCellStyle(style);
		// Bonus
		cell = row.createCell(4);
		cell.setCellValue(dataReportPersonalQuy.get(2).getThirdMonth());
		cell.setCellStyle(style);

		cell = row.createCell(5);
		cell.setCellValue("Quý " + this.quySelectedPersonal);
		cell.setCellStyle(style);

		cell = row.createCell(6);
		cell.setCellValue("Xếp loại");
		cell.setCellStyle(style);

		for (PersonalQuy kq : dataReportPersonalQuy) {
			rownum++;
			row = sheet.createRow(rownum);
			// ho ten
			cell = row.createCell(0);
			cell.setCellValue(kq.getNameEmployee());
			// Don vi (B)
			cell = row.createCell(1);
			cell.setCellValue(kq.getNameDepartment());

			cell = row.createCell(2);
			cell.setCellValue(kq.getResult1());

			cell = row.createCell(3);
			cell.setCellValue(kq.getResult2());

			cell = row.createCell(4);
			cell.setCellValue(kq.getResult3());

			cell = row.createCell(5);
			cell.setCellValue(kq.getAvgQuy());

			cell = row.createCell(6);
			cell.setCellValue(kq.getRate());

		}
		String filename = "kpicanhanquy.xlsx";
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/vnd.ms-excel");
		externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
		workbook.write(externalContext.getResponseOutputStream());
		// cancel progress
		facesContext.responseComplete();
	}

	// ca nhan thang
	public void excelPersonalMonth() throws IOException {
		List<PersonalMonth> dataReportPersonalMonth = createDataReportKPIPersonalMonth(this.monthSelectedPersonal,
				this.yearSelectedPersonal1, departmentSelectedPersonalMonth);

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("KPI CÁ NHÂN THÁNG");

		int rownum = 0;
		Cell cell;
		Row row;
		XSSFCellStyle style = createStyleForTitle(workbook);
		style.setAlignment(CellStyle.ALIGN_CENTER);

		row = sheet.createRow(rownum);

		// EmpNo
		cell = row.createCell(0);
		cell.setCellValue("Họ và tên");
		// xep loai// EmpName
		cell = row.createCell(1);
		cell.setCellValue("Đơn vị");
		cell.setCellStyle(style);
		// Salary
		cell = row.createCell(2);
		cell.setCellValue("KPI Phòng 40%");
		cell.setCellStyle(style);
		// Grade
		cell = row.createCell(3);
		cell.setCellValue("KPI cá nhân 60%");
		cell.setCellStyle(style);
		// Bonus
		cell = row.createCell(4);
		cell.setCellValue("Tổng điểm");
		cell.setCellStyle(style);

		cell = row.createCell(5);
		cell.setCellValue("Xếp loại");
		cell.setCellStyle(style);

		for (PersonalMonth kq : dataReportPersonalMonth) {
			rownum++;
			row = sheet.createRow(rownum);
			// ho ten
			cell = row.createCell(0);
			cell.setCellValue(kq.getEmployeeName());
			// Don vi (B)
			cell = row.createCell(1);
			cell.setCellValue(kq.getDepartmentName());

			cell = row.createCell(2);
			cell.setCellValue(kq.getKpiDepartment());

			cell = row.createCell(3);
			cell.setCellValue(kq.getKpiPersonal());

			cell = row.createCell(4);
			cell.setCellValue(kq.getResult());

			cell = row.createCell(5);
			cell.setCellValue(kq.getRate());

		}
		String filename = "kpicanhanthang.xlsx";
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/vnd.ms-excel");
		externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
		workbook.write(externalContext.getResponseOutputStream());
		// cancel progress
		facesContext.responseComplete();
	}
	// END EXCEL

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
