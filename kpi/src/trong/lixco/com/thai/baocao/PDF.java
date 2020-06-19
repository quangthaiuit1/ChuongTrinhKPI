package trong.lixco.com.thai.baocao;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.ejb.servicekpi.KPIPersonService;
import trong.lixco.com.jpa.entitykpi.KPIPerson;

@ManagedBean
@ViewScoped
public class PDF extends AbstractBean<KPIPerson> {
	private static final long serialVersionUID = 1L;

	@Inject
	KPIPersonService kpiPersionService;

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
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperPath, parameters,
				new JREmptyDataSource());
		// Tao file PDF
		OutputStream outputStream;
		outputStream = facesContext.getExternalContext().getResponseOutputStream();
		// Viet noi dung vao file PDF

		try {
			JasperExportManager.exportReportToPdfFile(jasperPrint,"D:\\demo\\reportPersonalMonth.pdf");
//			JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
			facesContext.responseComplete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Create and Download report
	public void printPDF() throws IOException, JRException {
		List<KPIPerson> dataSource = new ArrayList<>();
		dataSource = kpiPersionService.findRange(1, 2020);
		this.createPDF(dataSource);
	}

	@Override
	protected void initItem() {
		// TODO Auto-generated method stub

	}

	@Override
	protected Logger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}
}
