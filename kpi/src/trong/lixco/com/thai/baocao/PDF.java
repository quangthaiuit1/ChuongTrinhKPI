package trong.lixco.com.thai.baocao;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
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

import org.docx4j.org.xhtmlrenderer.pdf.ITextRenderer;
import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.HttpResponse;
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
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.classInfor.PrintKPI;
import trong.lixco.com.ejb.servicekpi.KPIDepMonthService;
import trong.lixco.com.ejb.servicekpi.KPIPersonService;
import trong.lixco.com.jpa.entitykpi.KPIDepMonth;
import trong.lixco.com.jpa.entitykpi.KPIPerson;
import trong.lixco.com.jpa.entitykpi.KPIPersonOfMonth;
import trong.lixco.com.jpa.entitykpi.ParamReportDetail;
import trong.lixco.com.util.Notify;

@ManagedBean
@ViewScoped
public class PDF extends AbstractBean<KPIPerson> {
	private static final long serialVersionUID = 1L;

	@Inject
	KPIPersonService kpiPersionService;
	@Inject
	private KPIPersonService kPIPersonService;
	private Notify notify;
	private JasperPrint jasperPrint;

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
	
	@Inject
	private KPIDepMonthService KPI_DEPARTMENT_MONTH;
	
	@Override
	protected void initItem() {
		// TODO Auto-generated method stub

	}
	
	// Create and Download report
	public void printPDF() throws IOException, JRException {
		List<KPIDepMonth> dataSource = new ArrayList<>();
		dataSource = KPI_DEPARTMENT_MONTH.findKPIDepYear(2020, null);
//		this.PDF(dataSource);
	}

	public void showPDF() throws JRException, IOException {
		/* Convert List to JRBeanCollectionDataSource */
		
		List<KPIDepMonth> dataSource = new ArrayList<>();
		dataSource = KPI_DEPARTMENT_MONTH.findKPIDepMonth(4,2020, null);
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
