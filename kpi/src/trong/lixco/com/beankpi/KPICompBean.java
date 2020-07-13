package trong.lixco.com.beankpi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.docx4j.org.xhtmlrenderer.pdf.ITextRenderer;
import org.jboss.logging.Logger;
import org.joda.time.DateTime;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.Visibility;

import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.classInfor.PrintKPIComp;
import trong.lixco.com.ejb.service.ParamReportDetailService;
import trong.lixco.com.ejb.servicekpi.FormulaKPIService;
import trong.lixco.com.ejb.servicekpi.KPICompOfYearService;
import trong.lixco.com.ejb.servicekpi.KPICompService;
import trong.lixco.com.ejb.servicekpi.PathImageAssignService;
import trong.lixco.com.ejb.servicekpi.TagetCateService;
import trong.lixco.com.ejb.servicekpi.TagetService;
import trong.lixco.com.jpa.entitykpi.FormulaKPI;
import trong.lixco.com.jpa.entitykpi.KPIComp;
import trong.lixco.com.jpa.entitykpi.KPICompOfYear;
import trong.lixco.com.jpa.entitykpi.ParamReportDetail;
import trong.lixco.com.jpa.entitykpi.Taget;
import trong.lixco.com.jpa.entitykpi.TagetCate;
import trong.lixco.com.jpa.entitykpi.TagetCateWeight;
import trong.lixco.com.util.MediaKPICompBean;
import trong.lixco.com.util.Notify;
import trong.lixco.com.util.PDFMerger;

@ManagedBean
@ViewScoped
// KPI cong ty
public class KPICompBean extends AbstractBean<KPICompOfYear> {
	private static final long serialVersionUID = 1L;
	private SimpleDateFormat sf;
	private Notify notify;
	private List<KPIComp> kpiComps;

	private KPIComp kpiComp;
	private KPIComp kpiCompEdit;
	private List<KPICompOfYear> kpiCompOfYears;
	private List<KPICompOfYear> kpiCompOfYearRemoves;
	private List<FormulaKPI> formulaKPIs;
	private FormulaKPI formulaKPISelect;

	private List<Boolean> list;
	private List<Taget> tagets;

	private int tabindex;
	private boolean orverideData = false;

	@Inject
	private FormulaKPIService formulaKPIService;
	@Inject
	private ParamReportDetailService paramReportDetailService;
	@Inject
	private TagetService tagetService;
	@Inject
	private TagetCateService tagetCateService;
	@Inject
	private KPICompService kpiCompService;
	@Inject
	private Logger logger;

	@Override
	protected Logger getLogger() {
		return logger;
	}

	private JasperPrint jasperPrint;

	public void printOnly() {
		notify = new Notify(FacesContext.getCurrentInstance());
		try {
			String pathre = "/resources/reports/kpis/kpicomp.jasper";
			String reportPath = null;
			Map<String, Object> importParam = null;
			importParam = installConfigPersonReport();
			List<ParamReportDetail> paramnhaps = paramReportDetailService.findByParamReports_param_name("kpicongty");
			for (ParamReportDetail pd : paramnhaps) {
				importParam.put(pd.getKey(), pd.getValue());
			}
			reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath(pathre);
			importParam.put("REPORT_LOCALE", new Locale("vi", "VN"));

			List<PrintKPIComp> printKPIComps = new ArrayList<PrintKPIComp>();
			SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
			for (int i = 0; i < kpiCompOfYears.size(); i++) {
				PrintKPIComp pr = new PrintKPIComp(kpiCompOfYears.get(i), sf);
				printKPIComps.add(pr);
			}

			printKPIComps.sort(Comparator.comparing(PrintKPIComp::getTagetCateNo).thenComparing(PrintKPIComp::getNo));
			JRDataSource beanDataSource = new JRBeanCollectionDataSource(printKPIComps);
			jasperPrint = JasperFillManager.fillReport(reportPath, importParam, beanDataSource);
			byte[] data = JasperExportManager.exportReportToPdf(jasperPrint);
			showreport(data);
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

	public void updatesignKPI() {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (kpiComp.getId() != null) {
			if (allowUpdate(null)) {
				KPIComp wf = kpiCompService.update(kpiComp);
				if (kpiComps.contains(wf)) {
					kpiComps.set(kpiComps.indexOf(wf), wf);
				}
				kpiComps.set(kpiComps.indexOf(wf), wf);
				notify.success();
			} else {
				notify.warningDetail();
			}
		} else {
			notify.warning("Chưa chọn phiếu.");
		}
	}

	@Inject
	private KPICompOfYearService kpiCompOfYearService;

	// Cap nhat dư liêu chung minh
	public void updateDataAssign(FileUploadEvent event) {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (kpm != null && kpm.getId() != null) {
			kpm = kpiCompOfYearService.findById(kpm.getId());
			if (allowUpdate(null)) {
				try (InputStream input = event.getFile().getInputstream()) {
					byte[] file = IOUtils.toByteArray(input);
					byte[] fileold = PDFMerger.getFile(pathImageAssignService.findById(1l).getPath(),
							kpm.getNameAssign());
					if (!orverideData) {
						if (fileold != null) {
							file = PDFMerger.mergePdfs(fileold, file);
						}

					}
					if (file == null) {
						notice("File vượt quá dung lượng.");
					} else {
						PDFMerger.getDelete(pathImageAssignService.findById(1l).getPath(), kpm.getNameAssign());
						String filename = System.currentTimeMillis() + "-" + kpm.getId() + ".pdf";
						File directory = new File(pathImageAssignService.findById(1l).getPath(), filename);
						FileUtils.writeByteArrayToFile(directory, file);
						kpm.setNameAssign(filename);
						kpm = kpiCompOfYearService.update(kpm);
						kpiCompOfYears.set(kpiCompOfYears.indexOf(kpm), this.kpm);
						RequestContext context = RequestContext.getCurrentInstance();
						context.execute("PF('dialogDataAssign').hide();");
						notice("Tải lên thành công.");
					}
				} catch (Exception e) {
					e.printStackTrace();
					kpm = kpiCompOfYearService.findById(kpm.getId());
					kpiCompOfYears.set(kpiCompOfYears.indexOf(kpm), this.kpm);
					noticeError("Không lưu được. Dữ liệu vượt quá dung lượng");
				}
			} else {
				noticeError("Tài khoản không có quyền thực hiện.");
			}
		} else {
			noticeError("Không chọn nội dung nhập dữ liệu.");
		}

	}

	private KPICompOfYear kpm;

	public void selectData(KPICompOfYear kpm) {
		this.kpm = kpm;
	}

	private StreamedContent streamedContent;
	@Inject
	PathImageAssignService pathImageAssignService;

	public void processDataAssign(KPICompOfYear kpm) {
		if (kpm != null && kpm.getId() != null) {
			this.kpm = kpiCompOfYearService.findById(kpm.getId());
			byte[] file = PDFMerger.getFile(pathImageAssignService.findById(1l).getPath(), this.kpm.getNameAssign());
			if (file != null) {
				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("document.getElementById('formdataassign:process').click();");
			} else {
				notice("Không có dữ liệu chứng minh.");
			}
		} else {
			notice("Lưu dữ liệu trước khi nạp dữ liệu chứng minh.");
		}
	}

	public void showPDFData() throws IOException {
		if (kpm != null && kpm.getId() != null) {
			notify = new Notify(FacesContext.getCurrentInstance());
			try {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				ExternalContext externalContext = facesContext.getExternalContext();
				byte[] file = PDFMerger.getFile(pathImageAssignService.findById(1l).getPath(), kpm.getNameAssign());
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

					response.setContentLength(file.length);
					response.getOutputStream().write(file, 0, file.length);
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

	@Inject
	MediaKPICompBean mediaKPICompBean;
	byte[] data;

	public StreamedContent getMedia() throws IOException {
		StreamedContent a = mediaKPICompBean.getMedia();
		return a;
	}

	public void process() {
		if (kpiCompOfYears.size() != 0) {
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("document.getElementById('formdataassign:report').click();");
		} else {
			// Khong co du lieu
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('thongbao').show();");
		}
	}

	public void onToggle(ToggleEvent e) {
		list.set((Integer) e.getData(), e.getVisibility() == Visibility.VISIBLE);
	}

	@Override
	public void initItem() {
		sf = new SimpleDateFormat("dd/MM/yyyy");
		kpiComp = new KPIComp();
		DateTime dt = new DateTime();
		kpiComp.setYear(dt.getYear());
		kpiComps = kpiCompService.findAll();
		kpiCompOfYears = new ArrayList<KPICompOfYear>();
		kpiCompOfYearRemoves = new ArrayList<KPICompOfYear>();
		tagets = new ArrayList<Taget>();
		list = Arrays.asList(true, true, true, true, true, true, false, false, false, false, false, true, true, true,
				true, true, false, true);

		tabindex = 0;

	}

	private KPICompOfYear kpy;

	public void showListFormula(KPICompOfYear param) {
		notify = new Notify(FacesContext.getCurrentInstance());
		formulaKPIs = formulaKPIService.findAll();
		formulaKPISelect = param.getFormulaKPI();
		kpy = param;
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('dialogFormula').show();");
	}

	public void updateFormula() {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (formulaKPISelect == null) {
			notify.warning("Chưa chọn công thức!");
		} else {
			for (int i = 0; i < kpiCompOfYears.size(); i++) {
				if (kpiCompOfYears.get(i).getNo() == kpy.getNo()
						&& kpiCompOfYears.get(i).getTaget().equals(kpy.getTaget())) {
					kpy.setFormulaKPI(formulaKPISelect);
					kpy.setComputation(formulaKPISelect.getCode());
					kpiCompOfYears.set(i, kpy);
					notify.success();
					break;
				}
			}
		}
	}

	public void removeDetail(KPICompOfYear item) {
		for (int i = 0; i < kpiCompOfYears.size(); i++) {
			if (kpiCompOfYears.get(i).getNo() == item.getNo()
					&& kpiCompOfYears.get(i).getTaget().equals(item.getTaget())) {
				kpiCompOfYears.remove(i);
				kpiCompOfYearRemoves.add(item);
				break;
			}

		}
	}

	int totalHV = 20, totalCV = 0;

	public void caculatorResult() {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (allowUpdate(null) && allowSave(null)) {
			totalCV = 0;
			totalHV = 20;
			for (int i = 0; i < kpiCompOfYears.size(); i++) {
				KPICompOfYear item = kpiCompOfYears.get(i);
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
						e.printStackTrace();
					}

				} else {
					try {
						engine.eval("var TH = " + TH);
						engine.eval("var KH = " + KH);
					} catch (Exception e) {
						iscacul = false;
						e.printStackTrace();
					}
				}
				if (iscacul) {
					try {
						engine.eval("var KQ = " + item.getFormulaKPI().getCodeVSGood());

						if (!"".equals(item.getParamA()))
							engine.eval("var A = " + item.getParamA());
						if (!"".equals(item.getParamB()))
							engine.eval("var B = " + item.getParamB());
						if (!"".equals(item.getParamC()))
							engine.eval("var C = " + item.getParamC());

						if ((boolean) engine.get("KQ")) {
							// ket qua thu hien theo tinh dung
							engine.eval("var RESULT = " + item.getFormulaKPI().getCodeGood());
						} else {
							// ket qua false
							String code = item.getFormulaKPI().getCodeNotGood();
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
						kpiCompOfYears.get(i).setRatioComplete((double) Math.round(result));
						kpiCompOfYears.get(i).setRatioCompleteIsWeighted(
								(double) Math.round((result * kpiCompOfYears.get(i).getWeighted()) / 100));
						totalCV += kpiCompOfYears.get(i).getRatioCompleteIsWeighted();

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
			totalCV = totalCV * 100 / 100;
			for (int i = 0; i < kpiCompOfYears.size(); i++) {
				if (kpiCompOfYears.get(i).getContentAppreciate() != null
						&& !"".equals(kpiCompOfYears.get(i).getContentAppreciate().trim()))
					totalHV += kpiCompOfYears.get(i).getRatioCompleteIsWeighted();
			}
			if (totalHV < 0)
				totalHV = 0;
			processResult();
		} else {
			notify.warningDetail();
		}
	}

	public void processResult() {
		// Lay danh sach nhom
		List<TagetCate> tagetCates = new ArrayList<TagetCate>();
		for (int i = 0; i < kpiCompOfYears.size(); i++) {
			boolean status = true;
			for (int j = 0; j < tagetCates.size(); j++) {
				if (kpiCompOfYears.get(i).getTaget().getkTagetCate().equals(tagetCates.get(j))) {
					status = false;
					break;
				}
			}
			if (status) {
				kpiCompOfYears.get(i).getTaget().getkTagetCate().setRatioWeight(0);
				tagetCates.add(kpiCompOfYears.get(i).getTaget().getkTagetCate());
			}
		}

		for (int i = 0; i < kpiCompOfYears.size(); i++) {
			for (int j = 0; j < tagetCates.size(); j++) {
				if (kpiCompOfYears.get(i).getTaget().getkTagetCate().equals(tagetCates.get(j))) {
					tagetCates.get(j).setRatioWeight(
							tagetCates.get(j).getRatioWeight() + kpiCompOfYears.get(i).getRatioCompleteIsWeighted());
				}
			}
		}

		double total = 0;
		for (int j = 0; j < tagetCates.size(); j++) {
			boolean status = true;
			for (int i = 0; i < kpiCompOfYears.size(); i++) {
				if (kpiCompOfYears.get(i).getTaget().getkTagetCate().equals(tagetCates.get(j))) {
					double value = 0;
					try {
						value = (tagetCates.get(j).getRatioWeight() * kpiCompOfYears.get(i).getWeightedParent()) / 100;
					} catch (Exception e) {
					}
					kpiCompOfYears.get(i).setWeightedParentRation(value);
					if (status) {
						total += kpiCompOfYears.get(i).getWeightedParentRation();
						status = false;
					}
				}
			}

		}
		kpiComp.setResult(total);

	}

	public void createAndDelete() {
		notify = new Notify(FacesContext.getCurrentInstance());
		Date date = new Date(kpiComp.getYear(), kpiComp.getYear(), 1);
		if (allowSave(date)) {
			List<KPIComp> kps = kpiCompService.findKPIComp(kpiComp.getYear());
			for (int i = 0; i < kps.size(); i++) {
				kpiCompService.delete(kps.get(i));
			}
			List<KPICompOfYear> listSaves = new ArrayList<KPICompOfYear>();
			listSaves.addAll(kpiCompOfYears);
			kpiComp.setKpiCompOfYears(listSaves);
			KPIComp wf = kpiCompService.saveOrUpdate(kpiComp, kpiCompOfYearRemoves);
			if (wf != null) {
				kpiComp = kpiCompService.findByIdAll(wf.getId());
				kpiComps.add(wf);
				writeLogInfo("Tao moi " + wf.toString());
				notify.success();
			} else {
				notify.error();
			}
		} else {
			notify.warningDetail();
		}
	}

	public void createOrUpdate() {
		notify = new Notify(FacesContext.getCurrentInstance());
		Date date = new Date(kpiComp.getYear(), kpiComp.getYear(), 1);
		if (kpiComp.getId() == null) {
			List<KPIComp> kps = kpiCompService.findKPIComp(kpiComp.getYear());
			if (kps.size() == 0) {
				if (allowSave(date)) {
					List<KPICompOfYear> listSaves = new ArrayList<KPICompOfYear>();
					listSaves.addAll(kpiCompOfYears);
					kpiComp.setKpiCompOfYears(listSaves);
					KPIComp wf = kpiCompService.saveOrUpdate(kpiComp, kpiCompOfYearRemoves);
					if (wf != null) {
						kpiComp = kpiCompService.findByIdAll(wf.getId());
						kpiComps.add(wf);
						writeLogInfo("Tao moi " + wf.toString());
						notify.success();
					} else {
						notify.error();
					}
				} else {
					notify.warningDetail();
				}
			} else {
				boolean status = false;
				for (int i = 0; i < kps.size(); i++) {
					if (kps.get(i).isSignKPI()) {
						status = true;
						break;
					}
				}
				if (status) {
					notify.warning("Năm đã duyệt không thay thế được.");
				} else {
					RequestContext context = RequestContext.getCurrentInstance();
					context.execute("PF('dialogConfirm').show();");
				}

			}
		} else {
			KPIComp kpiCompOld = kpiCompService.findById(kpiComp.getId());
			if (kpiCompOld.isSignKPI()) {
				this.kpiCompEdit = kpiCompOld;
				showEdit();
				notify.warning("Phiếu đã duyệt.");
			} else {
				Date date2 = new Date(kpiCompOld.getYear(), kpiCompOld.getYear(), 1);
				if (allowUpdate(date) && allowUpdate(date2)) {
					List<KPICompOfYear> listSaves = new ArrayList<KPICompOfYear>();
					listSaves.addAll(kpiCompOfYears);
					kpiComp.setKpiCompOfYears(listSaves);
					KPIComp wf = kpiCompService.saveOrUpdate(kpiComp, kpiCompOfYearRemoves);
					if (wf != null) {
						kpiComp = kpiCompService.findByIdAll(wf.getId());
						kpiCompOfYears = kpiComp.getKpiCompOfYears();
						if (kpiComps.contains(wf)) {
							kpiComps.set(kpiComps.indexOf(wf), wf);
						} else {
							kpiComps.add(wf);
						}
						kpiComps.set(kpiComps.indexOf(wf), wf);
						writeLogInfo("Cap nhat" + wf.toString());
						notify.success();
					} else {
						notify.error();
					}
				} else {
					notify.warningDetail();
				}
			}
		}

	}

	public void showTaget() {
		tagets = tagetService.findSearch(kpiComp.getYear(), null);
	}

	public void loadTaget() {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (allowUpdate(null) && allowSave(null)) {
			kpiCompOfYears.clear();
			tagets = tagetService.findSearch(kpiComp.getYear(), null);
			for (int i = 0; i < tagets.size(); i++) {
				KPICompOfYear item = new KPICompOfYear();
				int no = 0;
				try {
					no = Integer.parseInt(tagets.get(i).getCode());
				} catch (Exception e) {
					// TODO: handle exception
				}
				item.setNo(no);
				item.setWeighted(tagets.get(i).getWeighted());
				item.setWeightedParent(tagets.get(i).getkTagetCate().getWeight());
				item.setContentAppreciate(tagets.get(i).getContent());
				item.setTaget(tagets.get(i));
				kpiCompOfYears.add(item);
			}
		} else {
			notify.warningDetail();
		}
	}

	public void getListTaget() {
		for (int i = 0; i < tagets.size(); i++) {
			if (tagets.get(i).isSelect()) {
				KPICompOfYear item = new KPICompOfYear();
				int no = 0;
				try {
					no = Integer.parseInt(tagets.get(i).getCode());
				} catch (Exception e) {
					// TODO: handle exception
				}
				item.setNo(no);
				item.setContentAppreciate(tagets.get(i).getContent());

				TagetCateWeight tw = tagetCateService.findSearch(kpiComp.getYear(), tagets.get(i).getkTagetCate());
				tagets.get(i).getkTagetCate().setWeight(tw.getWeigth());

				item.setTaget(tagets.get(i));
				kpiCompOfYears.add(item);
			}
		}
	}

	public void reset() {
		kpiComp = new KPIComp();
		DateTime dt = new DateTime();
		kpiComp.setYear(dt.getYear());
		kpiCompOfYears.clear();

	}

	public void showEdit() {
		kpiCompOfYearRemoves.clear();
		kpiCompOfYears.clear();
		if (kpiCompEdit != null && kpiCompEdit.getId() != null) {
			KPIComp od = kpiCompService.findByIdAll(kpiCompEdit.getId());
			if (od != null) {
				kpiComp = od;
				kpiCompOfYears = od.getKpiCompOfYears();
			}
		}
	}

	public void delete() {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (kpiComp != null) {
			KPIComp kpic = kpiCompService.findById(kpiComp.getId());
			if (kpic.isSignKPI()) {
				notify.warning("Phiếu này đã duyệt không xóa được.");
			} else {
				Date date = new Date(kpiComp.getYear(), kpiComp.getYear(), 1);
				if (allowDelete(date)) {
					boolean result = kpiCompService.delete(kpiComp);
					if (result) {
						kpiComps.remove(kpiComp);
						writeLogInfo("Xoa " + kpiComp.toString());
						reset();
						notify.success();
					} else {
						notify.warning("Không xoá được!");
					}
				} else {
					notify.warningDetail();
				}
			}
		} else {
			notify.warning("Chưa chọn phiếu!");
		}
	}

	public void searchItem() {
		kpiComps = kpiCompService.findAll();

	}

	public List<KPIComp> getKpiComps() {
		return kpiComps;
	}

	public void setKpiComps(List<KPIComp> kpiComps) {
		this.kpiComps = kpiComps;
	}

	public KPIComp getKpiComp() {
		return kpiComp;
	}

	public void setKpiComp(KPIComp kpiComp) {
		this.kpiComp = kpiComp;
	}

	public KPIComp getKpiCompEdit() {
		return kpiCompEdit;
	}

	public void setKpiCompEdit(KPIComp kpiCompEdit) {
		this.kpiCompEdit = kpiCompEdit;
	}

	public List<KPICompOfYear> getKpiCompOfYears() {
		return kpiCompOfYears;
	}

	public void setKpiCompOfYears(List<KPICompOfYear> kpiCompOfYears) {
		this.kpiCompOfYears = kpiCompOfYears;
	}

	public List<Boolean> getList() {
		return list;
	}

	public void setList(List<Boolean> list) {
		this.list = list;
	}

	public List<Taget> getTagets() {
		return tagets;
	}

	public void setTagets(List<Taget> tagets) {
		this.tagets = tagets;
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

	public int getTabindex() {
		return tabindex;
	}

	public void setTabindex(int tabindex) {
		this.tabindex = tabindex;
	}

	public boolean isOrverideData() {
		return orverideData;
	}

	public void setOrverideData(boolean orverideData) {
		this.orverideData = orverideData;
	}

}