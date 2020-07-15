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
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.docx4j.org.xhtmlrenderer.pdf.ITextRenderer;
import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.HttpResponse;
import org.joda.time.LocalDate;
import org.primefaces.context.RequestContext;

import com.ibm.icu.text.SimpleDateFormat;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxExporterConfiguration;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import trong.lixco.com.account.servicepublics.Department;
import trong.lixco.com.account.servicepublics.DepartmentServicePublic;
import trong.lixco.com.account.servicepublics.DepartmentServicePublicProxy;
import trong.lixco.com.account.servicepublics.Member;
import trong.lixco.com.account.servicepublics.MemberServicePublic;
import trong.lixco.com.account.servicepublics.MemberServicePublicProxy;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.classInfor.PrintKPI;
import trong.lixco.com.ejb.servicekpi.KPIDepMonthService;
import trong.lixco.com.ejb.servicekpi.KPIPersonService;
import trong.lixco.com.jpa.entitykpi.KPIDepMonth;
import trong.lixco.com.jpa.entitykpi.KPIPerson;
import trong.lixco.com.jpa.entitykpi.KPIPersonOfMonth;
import trong.lixco.com.jpa.entitykpi.ParamReportDetail;
import trong.lixco.com.jpa.thai.KPIPersonalPerformance;
import trong.lixco.com.thai.bean.entities.DepartmentTotalMonth;
import trong.lixco.com.thai.bean.entities.InfoPersonalPerformance;
import trong.lixco.com.thai.bean.entities.PersonalYear;
import trong.lixco.com.util.Notify;

@ManagedBean
@ViewScoped
public class PDF extends AbstractBean<KPIPerson> {
	private static final long serialVersionUID = 1L;

	private Notify notify;
	private JasperPrint jasperPrint;

	DepartmentServicePublic DEPARTMENT_SERVICE_PUBLIC;
	MemberServicePublic MEMBER_SERVICE_PUBLIC;

	@Inject
	private KPIDepMonthService KPI_DEPARTMENT_MONTH;
	@Inject
	private KPIPersonService KPI_PERSON_SERVICE;
	@Inject
	private KPIPersonService kpiPersionService;

	private int monthSearch = 0;
	private int yearSearch = 0;
	private int quySearch = 1;
	private SimpleDateFormat sf;
	private List<Department> allDepartmentList;
	//
	private List<Department> allDepartment;
	// Tao list code department de test
	private List<String> allCodeDepartmentTest;

	@Override
	protected void initItem() {
		sf = new SimpleDateFormat("dd/MM/yyyy");
		LocalDate lc = new LocalDate();
		monthSearch = lc.getMonthOfYear();
		yearSearch = lc.getYear();
//		daySearch = lc.getDayOfMonth();
		DEPARTMENT_SERVICE_PUBLIC = new DepartmentServicePublicProxy();
		MEMBER_SERVICE_PUBLIC = new MemberServicePublicProxy();
//		//handle get list department from Department table // lam the nao de lay duoc danh sach phong ban tai ho chi minh
//		try {
//			Department[] allDepartmentArray = DEPARTMENT_SERVICE_PUBLIC.findAll();
//			allDepartmentList = Arrays.asList(allDepartmentArray);
//			allDepartment = new ArrayList<>();
//			System.out.println(allDepartment.size());
//			for (Department d : allDepartmentList) {
//				if(d.getLevelDep().getLevel() == 2) {
//					allDepartment.add(d);
//				}
//			}
//			System.out.println(allDepartment.size());
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		//end 
		allCodeDepartmentTest = new ArrayList<>();
		allCodeDepartmentTest.add("30001");
		allCodeDepartmentTest.add("30006");
		allCodeDepartmentTest.add("30007");
		allCodeDepartmentTest.add("30008");
		allCodeDepartmentTest.add("30010");
		allCodeDepartmentTest.add("30012");
		allCodeDepartmentTest.add("30025");
	}

	public void showReportNew() throws JRException, IOException {
		String reportPath = FacesContext.getCurrentInstance().getExternalContext()
				.getRealPath("/resources/thaireports/kpi/personalMonth.jasper");
		List<DepartmentTotalMonth> dataReportDepartmentYear = createDataReportKPIDepartmentYear();

		JRDataSource beanDataSource = new JRBeanCollectionDataSource(dataReportDepartmentYear);
		Map<String, Object> importParam = new HashMap<String, Object>();

		String image = FacesContext.getCurrentInstance().getExternalContext()
				.getRealPath("/resources/gfx/lixco_logo.png");
		importParam.put("logo", image);
		importParam.put("listKPIDepartmentYear", beanDataSource);
		JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, importParam, beanDataSource);
		FacesContext facesContext = FacesContext.getCurrentInstance();
		OutputStream outputStream;
		outputStream = facesContext.getExternalContext().getResponseOutputStream();
		JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
		//print excel
		JRXlsxExporter exporter = new JRXlsxExporter();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput("D:\\demo\\sample_report.xlsx"));
		//Set configuration as you like it!!
		SimpleXlsxExporterConfiguration configuration = null;
        configuration = new SimpleXlsxExporterConfiguration();
        configuration.setKeepWorkbookTemplateSheets(true);
        exporter.setConfiguration(configuration);
        exporter.exportReport();
		//end excel
		facesContext.responseComplete();
	}

	// Entity bao cao KPI ca nhan nam
	public void createDataReportPersonalYear() throws RemoteException {
		List<PersonalYear> personalYears = new ArrayList<>();
		// search by code Emp and year -> all KPIPerson 2019

		// ****cho nay lam sao lay duoc list nhan vien (- nhan vien thang do chua vo lam
		// thang sau vo)
//		List<KPIPerson> temp = KPI_PERSON_SERVICE.findRange(12, 2019);
		// Tam thoi test bang list codeEmp cho san
		List<String> temp = new ArrayList<>();
		temp.add("0001977");
		temp.add("0001954");
		temp.add("0003165");
		temp.add("0001954");
		temp.add("0002828");
		temp.add("0001140");
		temp.add("0000004");
		temp.add("0001794");
		temp.add("0000328");

		for (String k : temp) {
			// tao 1 item personalYear
			PersonalYear personalYearTemp = new PersonalYear();
			// Tim nhan vien theo codeEmp KPIPerson -> nameEmp, nameDepart
			Member memberTemp = MEMBER_SERVICE_PUBLIC.findByCode(k);
			personalYearTemp.setNameEmp(memberTemp.getName());
			personalYearTemp.setNameDepartment(memberTemp.getDepartment().getName());
			// list KPIPerson by Employee and year
			List<KPIPerson> kpiPersonByCodeEmp = KPI_PERSON_SERVICE.findRange(memberTemp.getCode(), 2019);

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
		System.out.println(personalYears.size());
	}

	// Entity bao cao KPI ca nhan quy
	public void createDataReportKPIPersonalQuy() {
//		switch (quySearch) {
//		case :
//			
//			break;
//
//		default:
//			break;
//		}
	}

	// Entity bao cao KPI ca nhan thang
	public void createDataReportKPIPersonalYear() {

	}

	// Entity bao cao phong nam
	// *** lam sao lay duoc danh sach phong ban dang hoat dong tai ho chi minh
	public List<DepartmentTotalMonth> createDataReportKPIDepartmentYear() {
		Department departmentTemp = new Department();
		// Danh sach diem phong thang
		List<DepartmentTotalMonth> departmentTotalMonth = new ArrayList<>();
//		for (Department d : allDepartment) {
		for (String d : allCodeDepartmentTest) {
			List<KPIDepMonth> temp = KPI_DEPARTMENT_MONTH.find(d, 2019);
			// Select department -> nameDepart
			try {
				departmentTemp = DEPARTMENT_SERVICE_PUBLIC.findByCode("code", d);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DepartmentTotalMonth departmentTotalTemp = new DepartmentTotalMonth();
			departmentTotalTemp.setNameDepart(departmentTemp.getName());
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
			departmentTotalMonth.add(departmentTotalTemp);
		}
		System.out.println(departmentTotalMonth.size());
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

	// Create and Download report
	public void printPDF() throws IOException, JRException {
		List<KPIDepMonth> dataSource = new ArrayList<>();
		dataSource = KPI_DEPARTMENT_MONTH.findKPIDepYear(2020, null);
//		this.PDF(dataSource);
	}

//	public void getList() {
//		listKPIPersonalByEmployee = PERSONAL_PERFORMANCE_SERVICE.find(listCodePJob);
//		Map<String, List<KPIPersonalPerformance>> datagroups11 = listKPIPersonalByEmployee.stream()
//				.collect(Collectors.groupingBy(a -> a.getCodePJob(), Collectors.toList()));
//
//		listKPIPersonalByEmployee = new ArrayList<>();
//		for (String key : datagroups11.keySet()) {
//			List<KPIPersonalPerformance> invs = datagroups11.get(key);
//			try {
//				InfoPersonalPerformance tgi = new InfoPersonalPerformance();
//				tgi.setPositionJobName(POSITION_JOB_SERVICE.findByCode(key).getName());
//				tgi.setPersonalPerformances(invs);
//				listInfoPersonalPerformances.add(tgi);
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
//		}
//	}
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
}
