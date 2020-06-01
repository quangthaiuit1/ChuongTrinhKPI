package trong.lixco.com.beankpi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.bean.ManagedBean;
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

import trong.lixco.com.account.servicepublics.Account;
import trong.lixco.com.account.servicepublics.DepartmentServicePublic;
import trong.lixco.com.account.servicepublics.DepartmentServicePublicProxy;
import trong.lixco.com.account.servicepublics.Member;
import trong.lixco.com.account.servicepublics.MemberServicePublic;
import trong.lixco.com.account.servicepublics.MemberServicePublicProxy;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.classInfor.PrintKPIComp;
import trong.lixco.com.ejb.service.ParamReportDetailService;
import trong.lixco.com.ejb.servicekpi.FormulaKPIService;
import trong.lixco.com.ejb.servicekpi.KPIDepOfYearService;
import trong.lixco.com.ejb.servicekpi.KPIDepService;
import trong.lixco.com.ejb.servicekpi.PathImageAssignService;
import trong.lixco.com.ejb.servicekpi.TagetDepartCateService;
import trong.lixco.com.ejb.servicekpi.TagetDepartService;
import trong.lixco.com.jpa.entitykpi.FormulaKPI;
import trong.lixco.com.jpa.entitykpi.KPIDep;
import trong.lixco.com.jpa.entitykpi.KPIDepOfYear;
import trong.lixco.com.jpa.entitykpi.ParamReportDetail;
import trong.lixco.com.jpa.entitykpi.TagetDepart;
import trong.lixco.com.jpa.entitykpi.TagetDepartCate;
import trong.lixco.com.jpa.entitykpi.TagetDepartCateWeight;
import trong.lixco.com.util.Notify;
import trong.lixco.com.util.PDFMerger;

@ManagedBean
@javax.faces.bean.ViewScoped
// KPI phong ban
public class KPIDepBLDBean extends AbstractBean<KPIDepOfYear> {
	private static final long serialVersionUID = 1L;
	private SimpleDateFormat sf;
	private Notify notify;
	private List<KPIDep> kpiDeps;

	private final int TOTALPARAM = 100;
	private int year;

	private boolean orverideData = false;

	private KPIDep kpiDep;
	private KPIDep kpiDepEdit;
	private List<KPIDepOfYear> kpiDepOfYears;
	private List<KPIDepOfYear> kpiDepOfYearRemoves;
	private List<FormulaKPI> formulaKPIs;
	private FormulaKPI formulaKPISelect;

	private List<Boolean> list;
	private List<TagetDepart> tagetDepart;
	private List<TagetDepart> tagetDeparts;

	private List<TagetDepartCate> tagetDepartCates;
	private TagetDepartCate tagetDepartCate;

	private Member member;
	private DepartmentServicePublic departmentServicePublic;
	private MemberServicePublic memberServicePublic;

	@Inject
	private FormulaKPIService formulaKPIService;
	@Inject
	private ParamReportDetailService paramReportDetailService;
	@Inject
	private TagetDepartService tagetDepartService;
	@Inject
	private TagetDepartCateService tagetDepartCateService;
	@Inject
	private KPIDepService kpiDepService;
	@Inject
	private Logger logger;
	@Inject
	private KPIDepOfYearService kpiDepOfYearService;

	private int tabindex;

	@Override
	protected Logger getLogger() {
		return logger;
	}

	private JasperPrint jasperPrint;

	public void printOnly() {
		notify = new Notify(FacesContext.getCurrentInstance());
		try {
			String pathre = "/resources/reports/kpis/kpidep.jasper";
			String reportPath = null;
			Map<String, Object> importParam = null;
			importParam = installConfigPersonReport();
			List<ParamReportDetail> paramnhaps = paramReportDetailService.findByParamReports_param_name("kpiphongban");
			for (ParamReportDetail pd : paramnhaps) {
				importParam.put(pd.getKey(), pd.getValue());
			}
			reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath(pathre);
			importParam.put("REPORT_LOCALE", new Locale("vi", "VN"));

			List<PrintKPIComp> printKPIComps = new ArrayList<PrintKPIComp>();
			SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
			for (int i = 0; i < kpiDepOfYears.size(); i++) {
				PrintKPIComp pr = new PrintKPIComp(kpiDepOfYears.get(i), sf, departmentServicePublic,
						memberServicePublic);
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

	// Cap nhat dư liêu chung minh
	public void updateDataAssign(FileUploadEvent event) {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (kpm != null && kpm.getId() != null) {
			kpm = kpiDepOfYearService.findById(kpm.getId());
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
						kpm = kpiDepOfYearService.update(kpm);
						kpiDepOfYears.set(kpiDepOfYears.indexOf(kpm), this.kpm);
						RequestContext context = RequestContext.getCurrentInstance();
						context.execute("PF('dialogDataAssign').hide();");
						notice("Tải lên thành công.");
					}
				} catch (Exception e) {
					e.printStackTrace();
					kpm = kpiDepOfYearService.findById(kpm.getId());
					kpiDepOfYears.set(kpiDepOfYears.indexOf(kpm), this.kpm);
					noticeError("Không lưu được. Dữ liệu vượt quá dung lượng");
				}
			} else {
				noticeError("Tài khoản không có quyền thực hiện.");
			}
		} else {
			noticeError("Không chọn nội dung nhập dữ liệu.");
		}

	}

	private KPIDepOfYear kpm;

	public void selectData(KPIDepOfYear kpm) {
		this.kpm = kpm;
	}

	private StreamedContent streamedContent;
	@Inject
	PathImageAssignService pathImageAssignService;

	public void processDataAssign(KPIDepOfYear kpm) {
		if (kpm != null && kpm.getId() != null) {
			this.kpm = kpiDepOfYearService.findById(kpm.getId());
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
				try {
					byte[] file = PDFMerger.getFile(pathImageAssignService.findById(1l).getPath(), kpm.getNameAssign());
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

	public void process() {
		if (kpiDepOfYears.size() != 0) {
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
		try {
			departmentServicePublic = new DepartmentServicePublicProxy();
			memberServicePublic = new MemberServicePublicProxy();
		} catch (Exception e) {
			e.printStackTrace();
		}
		member = getAccount().getMember();
		year = new Date().getYear() + 1900;
		tagetDepartCates = tagetDepartCateService.findAll();
		sf = new SimpleDateFormat("dd/MM/yyyy");
		reset();
		tabindex = 0;
		list = Arrays.asList(true, true, true, true, false, true, false, false, false, false, true, false, true, true,
				true, true, false, true);
	}

	private KPIDepOfYear kpy;

	public void showListFormula(KPIDepOfYear param) {
		notify = new Notify(FacesContext.getCurrentInstance());
		formulaKPIs = formulaKPIService.findAll();
		formulaKPISelect = param.getFormulaKPI();
		kpy = param;
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('dialogFormula').show();");
	}

	public void updateFormula() {
		notify = new Notify(FacesContext.getCurrentInstance());
		try {
			KPIDep kpiCompOld = kpiDepService.findById(kpiDep.getId());
			if (kpiCompOld.isSignKPI()) {
				noticeError("KPI đã duyệt không cập nhật được.");
			} else {
				if (formulaKPISelect == null) {
					notify.warning("Chưa chọn công thức!");
				} else {
					for (int i = 0; i < kpiDepOfYears.size(); i++) {
						if (kpiDepOfYears.get(i).getId() == kpy.getId()) {
							kpy.setFormulaKPI(formulaKPISelect);
							kpy.setComputation(formulaKPISelect.getCode());
							kpiDepOfYears.set(i, kpy);
							notify.success();
							break;
						}
					}
				}
			}
		} catch (Exception e) {
		}
	}

	public void removeDetail(KPIDepOfYear item) {
		for (int i = 0; i < kpiDepOfYears.size(); i++) {

			if (kpiDepOfYears.get(i).getId() == item.getId()) {
				try {
					KPIDep kpiCompOld = kpiDepService.findById(kpiDepOfYears.get(i).getKpiDep().getId());
					if (kpiCompOld.isSignKPI()) {
						noticeError("KPI đã duyệt không xóa được.");
					} else {
						kpiDepOfYearService.delete(item);
						kpiDepOfYears.remove(i);
						kpiDepOfYearRemoves.add(item);
						break;
					}
				} catch (Exception e) {
					kpiDepOfYearService.delete(item);
					kpiDepOfYears.remove(i);
					kpiDepOfYearRemoves.add(item);
				}

			}

		}
	}

	int totalHV = 20, totalCV = 0;

	public void caculatorResult() {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (allowUpdate(null) && allowSave(null)) {
			totalCV = 0;
			totalHV = 20;
			for (int i = 0; i < kpiDepOfYears.size(); i++) {
				KPIDepOfYear item = kpiDepOfYears.get(i);
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
						kpiDepOfYears.get(i).setRatioComplete(0);
						kpiDepOfYears.get(i).setRatioCompleteIsWeighted(0);
						totalCV += 0;
					}

				} else {
					try {
						engine.eval("var TH = " + TH);
						engine.eval("var KH = " + KH);
					} catch (Exception e) {
						iscacul = false;
						kpiDepOfYears.get(i).setRatioComplete(0);
						kpiDepOfYears.get(i).setRatioCompleteIsWeighted(0);
						totalCV += 0;
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
						try {
							if (item.getFormulaKPI().isMax100())
								if (result > 100)
									result = 100.0;
						} catch (Exception e) {
						}
						kpiDepOfYears.get(i).setRatioComplete(Math.round(result));
						kpiDepOfYears.get(i).setRatioCompleteIsWeighted(
								Math.round((result * kpiDepOfYears.get(i).getWeighted()) / 100));
						totalCV += kpiDepOfYears.get(i).getRatioCompleteIsWeighted();

					} catch (Exception e) {
						kpiDepOfYears.get(i).setRatioComplete(0);
						kpiDepOfYears.get(i).setRatioCompleteIsWeighted(0);
						totalCV += 0;
						e.printStackTrace();
					}

				}
			}
			totalCV = Math.round(totalCV * 100 / 100);
			for (int i = 0; i < kpiDepOfYears.size(); i++) {
				if (kpiDepOfYears.get(i).getContentAppreciate() != null
						&& !"".equals(kpiDepOfYears.get(i).getContentAppreciate().trim()))
					totalHV += kpiDepOfYears.get(i).getRatioCompleteIsWeighted();
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
		List<TagetDepartCate> tagetDepartCates = new ArrayList<TagetDepartCate>();
		for (int i = 0; i < kpiDepOfYears.size(); i++) {
			boolean status = true;
			for (int j = 0; j < tagetDepartCates.size(); j++) {
				if (kpiDepOfYears.get(i).getTagetDepart().getkTagetDepartCate().equals(tagetDepartCates.get(j))) {
					status = false;
					break;
				}
			}
			if (status) {
				kpiDepOfYears.get(i).getTagetDepart().getkTagetDepartCate().setRatioWeight(0);
				tagetDepartCates.add(kpiDepOfYears.get(i).getTagetDepart().getkTagetDepartCate());
			}
		}

		for (int i = 0; i < kpiDepOfYears.size(); i++) {
			for (int j = 0; j < tagetDepartCates.size(); j++) {
				if (kpiDepOfYears.get(i).getTagetDepart().getkTagetDepartCate().equals(tagetDepartCates.get(j))) {
					tagetDepartCates.get(j).setRatioWeight(
							tagetDepartCates.get(j).getRatioWeight()
									+ kpiDepOfYears.get(i).getRatioCompleteIsWeighted());
				}
			}
		}

		double total = 0;
		for (int j = 0; j < tagetDepartCates.size(); j++) {
			boolean status = true;
			for (int i = 0; i < kpiDepOfYears.size(); i++) {
				if (kpiDepOfYears.get(i).getTagetDepart().getkTagetDepartCate().equals(tagetDepartCates.get(j))) {
					double value = 0;
					try {
						value = (tagetDepartCates.get(j).getRatioWeight() * kpiDepOfYears.get(i).getWeightedParent()) / 100;
					} catch (Exception e) {
					}
					kpiDepOfYears.get(i).setWeightedParentRation(value);
					if (status) {
						total += kpiDepOfYears.get(i).getWeightedParentRation();
						status = false;
					}
				}
			}

		}
		kpiDep.setResult(total);

	}

	public void updatesignKPIRegister() {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (kpiDep.getId() != null) {
			if (allowUpdate(null)) {
				KPIDep wf = kpiDepService.findById(kpiDep.getId());
				if (wf.isSignKPIBLD()) {
					kpiDep.setSignKPI(wf.isSignKPI());
					notify.warning("BLĐ đã duyệt đăng ký, không điều chỉnh được.");
				} else {
					wf.setSignKPI(kpiDep.isSignKPI());
					wf = kpiDepService.update(wf);
					if (kpiDeps.contains(wf)) {
						kpiDeps.set(kpiDeps.indexOf(wf), wf);
					}
					kpiDeps.set(kpiDeps.indexOf(wf), wf);
					writeLogWarning("Duyet KPI nam:" + wf.toString());
					notify.success();
				}

			} else {
				notify.warningDetail();
			}
		} else {
			notify.warning("Chưa chọn phiếu.");
		}
	}

	public void updatesignKPIResult() {
		notify = new Notify(FacesContext.getCurrentInstance());
		KPIDep wf = kpiDepService.findById(kpiDep.getId());
		if (wf.isSignResultKPIBLD()) {
			kpiDep.setSignResultKPI(wf.isSignResultKPI());
			notify.warning("BLĐ đã duyệt kết quả, không điều chỉnh được.");
		} else {
			wf.setSignResultKPI(kpiDep.isSignResultKPI());
			wf = kpiDepService.update(wf);
			if (kpiDeps.contains(wf)) {
				kpiDeps.set(kpiDeps.indexOf(wf), wf);
			}
			kpiDeps.set(kpiDeps.indexOf(wf), wf);
			writeLogWarning("Duyet KPI nam:" + wf.toString());
			notify.success();
		}
	}

	public void updatesignKPIBLD() {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (kpiDep.getId() != null) {
			// // VT0005: Tong giam doc
			// boolean status = false;
			// try {
			// Member mem = getAccount().getMember();
			// if (mem.getCode().equals("VT0005")) {
			// status = true;
			// } else {
			// String emp =
			// kpiDep.getDepartmentParent().getManagerEmployee().getEmployeeCode();
			// if (mem.getCode().equals(emp))
			// status = true;
			// }
			// } catch (Exception e) {
			// // TODO: handle exception
			// }
			// if (status) {
			if (allowUpdate(null)) {
				KPIDep wf = kpiDepService.findById(kpiDep.getId());
				wf.setSignKPIBLD(kpiDep.isSignKPIBLD());
				wf.setSignResultKPIBLD(kpiDep.isSignResultKPIBLD());
				wf = kpiDepService.update(wf);
				if (kpiDeps.contains(wf)) {
					kpiDeps.set(kpiDeps.indexOf(wf), wf);
				}
				kpiDeps.set(kpiDeps.indexOf(wf), wf);
				writeLogWarning("BLD Duyet KPI nam:" + wf.toString());
				notify.success();
			} else {
				notify.warningDetail();
			}
			// } else {
			// showEdit();
			// notify.warning("Phòng không thuộc quản lý. Không duyệt được.");
			// }
		} else {
			notify.warning("Chưa chọn phiếu.");
		}
	}

	public void createAndDelete() {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (allowSave(null)) {
			KPIDep kp = kpiDepService.findKPIDep(kpiDep.getYear(), kpiDep.getCodeDepart());
			kpiDepService.delete(kp);
			writeLogWarning("Cap nhat.\nDu lieu cu:" + kp.toString());

			List<KPIDepOfYear> listSaves = new ArrayList<KPIDepOfYear>();
			listSaves.addAll(kpiDepOfYears);
			kpiDep.setKpiDepOfYears(listSaves);
			Long id = kpiDep.getId();
			kpiDep.setId(null);
			KPIDep wf = kpiDepService.saveOrUpdate(kpiDep, kpiDepOfYearRemoves);
			if (wf != null) {
				if (id != null)
					kpiDepService.delete(kpiDepService.findById(id));
				kpiDep = kpiDepService.findByIdAll(wf.getId());
				writeLogWarning("Du lieu moi:" + kpiDep.toString());
				searchItem();
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
		// // VT0005: Tong giam doc
		// boolean statusas = false;
		// try {
		// Member mem = getAccount().getMember();
		// if (mem.getCode().equals("VT0005")) {
		// statusas = true;
		// } else {
		// String emp =
		// kpiDep.getDepartmentParent().getManagerEmployee().getEmployeeCode();
		// if (mem.getCode().equals(emp))
		// statusas = true;
		// }
		// } catch (Exception e) {
		// // TODO: handle exception
		// }
		//
		// if (statusas) {
		kpiDep.setCodeDepart(codeDepart);
		if (kpiDep.getId() == null) {

			KPIDep kp = kpiDepService.findKPIDep(kpiDep.getYear(), kpiDep.getCodeDepart());
			if (kp == null) {
				if (allowSave(null)) {
					double checkp = 0;
					for (int i = 0; i < kpiDepOfYears.size(); i++) {
						checkp += kpiDepOfYears.get(i).getWeightedParent();
					}
					if (kpiDepOfYears.size() != 0 && checkp != TOTALPARAM) {
						noticeError("Không lưu được. Tổng trọng số các nhóm khác 100%.");
					} else {
						List<KPIDepOfYear> listSaves = new ArrayList<KPIDepOfYear>();
						listSaves.addAll(kpiDepOfYears);
						kpiDep.setKpiDepOfYears(listSaves);
						KPIDep wf = kpiDepService.saveOrUpdate(kpiDep, kpiDepOfYearRemoves);
						if (wf != null) {
							kpiDep = kpiDepService.findByIdAll(wf.getId());
							kpiDeps.add(wf);
							writeLogWarning("Tao moi:" + wf.toString());
							notify.success();
						} else {
							notify.error();
						}
					}
				} else {
					notify.warningDetail();
				}
			} else {
				if (kp.isSignKPI()) {
					notify.warning("Năm đã duyệt không thay thế được.");
				} else {
					RequestContext context = RequestContext.getCurrentInstance();
					context.execute("PF('dialogConfirm').show();");
				}
			}
		} else {
			if (allowUpdate(null)) {
				KPIDep kpiDepOld = kpiDepService.findById(kpiDep.getId());
				if (kpiDepOld.isSignKPI()) {
					this.kpiDepEdit = kpiDepOld;
					showEdit();
					notify.warning("Phiếu đã duyệt.");
				} else {
					if (kpiDepOld.getYear() != kpiDep.getYear()) {
						KPIDep temp = kpiDepService.findKPIDep(kpiDep.getYear(), kpiDep.getCodeDepart());
						if (temp != null) {
							if (temp.isSignKPI() || temp.isSignKPIBLD()) {
								notify.warning("Năm đã duyệt không thay thế được.");
							} else {
								RequestContext context = RequestContext.getCurrentInstance();
								context.execute("PF('dialogConfirm').show();");
							}
						} else {
							List<KPIDepOfYear> listSaves = new ArrayList<KPIDepOfYear>();
							double checkp = 0;
							for (int i = 0; i < kpiDepOfYears.size(); i++) {
								checkp += kpiDepOfYears.get(i).getWeightedParent();
							}
							if (kpiDepOfYears.size() != 0 && checkp != TOTALPARAM) {
								noticeError("Không lưu được. Tổng trọng số các nhóm khác 100%.");
							} else {
								listSaves.addAll(kpiDepOfYears);
								KPIDep wf = kpiDepService.saveOrUpdate(kpiDep, kpiDepOfYearRemoves);
								if (wf != null) {
									kpiDep = kpiDepService.findByIdAll(wf.getId());
									kpiDepOfYears = kpiDep.getKpiDepOfYears();
									searchItem();
									writeLogWarning("Cap nhat:" + wf.toString());
									notify.success();
								} else {
									notify.error();
								}
							}
						}
					} else {
						List<KPIDepOfYear> listSaves = new ArrayList<KPIDepOfYear>();
						double checkp = 0;
						for (int i = 0; i < kpiDepOfYears.size(); i++) {
							KPIDepOfYear a = kpiDepOfYears.get(i);
							checkp += kpiDepOfYears.get(i).getWeightedParent();
						}
						if (kpiDepOfYears.size() != 0 && checkp != TOTALPARAM) {

							noticeError("Không lưu được. Tổng trọng số các nhóm khác 100%.");
						} else {
							KPIDep temp = kpiDepService.findKPIDep(kpiDep.getYear(), kpiDep.getCodeDepart());
							if (temp != null) {
								if (temp.isSignKPI() && !kpiDep.isSignKPI()) {
									notify.warning("KPI đã duyệt đăng ký. Tải lại dữ liệu để nhập kết quả.");
								} else {
									listSaves.addAll(kpiDepOfYears);
									kpiDep.setKpiDepOfYears(listSaves);
									KPIDep wf = kpiDepService.saveOrUpdate(kpiDep, kpiDepOfYearRemoves);
									if (wf != null) {
										kpiDep = kpiDepService.findByIdAll(wf.getId());
										kpiDepOfYears = kpiDep.getKpiDepOfYears();
										searchItem();
										writeLogWarning("Cap nhat:" + wf.toString());
										notify.success();
									} else {
										notify.error();
									}
								}
							}
						}
					}
				}
			} else {
				notify.warningDetail();
			}
		}
		// } else {
		// notify.warning("Phòng không thuộc quản lý. Không điều chỉnh được.");
		// }

	}

	public void processa(KPIDepOfYear item) {
		System.out.println(item.toString());
	}

	public void loadTaget() {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (kpiDep.getCodeDepart() == null) {
			notify.warning("Chưa lưu KPI phòng");
		} else {
			if (allowUpdate(null) && allowSave(null)) {
				tagetDeparts = tagetDepartService.findSearch(kpiDep.getYear(), kpiDep.getCodeDepart());
				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('dialogTaget').show();");
			} else {
				notify.warningDetail();
			}
		}
	}

	public void getListTaget() {
		notify = new Notify(FacesContext.getCurrentInstance());
		try {
			KPIDep kpiCompOld = kpiDepService.findById(kpiDep.getId());
			if (kpiCompOld.isSignKPI()) {
				noticeError("KPI đã duyệt không thêm được.");
			} else {
				for (int i = 0; i < tagetDeparts.size(); i++) {
					if (tagetDeparts.get(i).isSelect()) {
						KPIDepOfYear item = new KPIDepOfYear();
						int no = 0;
						try {
							no = Integer.parseInt(tagetDeparts.get(i).getCode());
						} catch (Exception e) {
							// TODO: handle exception
						}
						item.setNo(no);
						item.setContentAppreciate(tagetDeparts.get(i).getContent());

						TagetDepartCateWeight tw = tagetDepartCateService.findSearch(kpiDep.getYear(), tagetDeparts
								.get(i).getkTagetDepartCate(), codeDepart);
						item.setWeightedParent(tw.getWeigth());
						tagetDeparts.get(i).getkTagetDepartCate().setWeight(tw.getWeigth());

						item.setTagetDepart(tagetDeparts.get(i));
						item.setKpiDep(kpiDep);
						item = kpiDepOfYearService.create(item);
						if (item != null)
							kpiDepOfYears.add(item);

					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void addNone() {
		// notify = new Notify(FacesContext.getCurrentInstance());
		// try {
		// KPIDep kpiCompOld = kpiDepService.findById(kpiDep.getId());
		// if (kpiCompOld.isSignKPI()) {
		// noticeError("KPI đã duyệt không thêm được.");
		// } else {
		// KPIDepOfYear item = new KPIDepOfYear();
		// item.setContentAppreciate("");
		// TagetDepart tg =
		// tagetDepartService.findTagetByCater(tagetDepartCate);
		// if (tg == null) {
		// notify.warning("Không có mục tiêu phòng trong nhóm này");
		// } else {
		// item.setTagetDepart(tg);
		// item.setKpiDep(kpiDep);
		// item = kpiDepOfYearService.create(item);
		// if (item != null) {
		// kpiDepOfYears.add(item);
		// for (int i = 0; i < kpiDepOfYears.size(); i++) {
		// kpiDepOfYears.get(i).setIndex(i);
		// }
		// kpiDepOfYears.stream().sorted(
		// (KPIDepOfYear o1, KPIDepOfYear o2) -> {
		// try {
		// int i = o1.getTagetDepart().getkTagetDepartCate().getCode()
		// .compareTo(o2.getTagetDepart().getkTagetDepartCate().getCode());
		// if (i == 0)
		// return o1.getIndex() > o2.getIndex() ? 1 : 0;
		// return i;
		// } catch (Exception e) {
		// return -1;
		// }
		// });
		// }
		// }
		// }
		// } catch (Exception e) {
		// // TODO: handle exception
		// }
	}

	private Account account;
	private String codeDepart;

	public void reset() {
		kpiDep = new KPIDep();
		DateTime dt = new DateTime();
		kpiDep.setYear(dt.getYear());
		codeDepart = member.getDepartment().getCode();
		try {
			// String employeeCode = getAccount().getMember().getCode();
			// employee = employeeService.findByCode(employeeCode);
			// account = getAccount();
			// List<DepartmentParent> deps =
			// departmentParentService.findByEmpDep(employee);
			// if (account.isAdmin()) {
			// departmentParents = departmentParentService.findAll();
			// } else {
			// if (deps.size() != 0) {
			// departmentParents = deps;
			// } else {
			// departmentParents = new ArrayList<DepartmentParent>();
			// codeDepart = employee.getDepartment().getDepartmentParent();
			// departmentParents.add(codeDepart);
			// }
			// }
			// kpiDep.setDepartmentParent(codeDepart);
		} catch (Exception e) {
		}

		kpiDepOfYears = new ArrayList<KPIDepOfYear>();
		kpiDepOfYearRemoves = new ArrayList<KPIDepOfYear>();
		tagetDeparts = new ArrayList<TagetDepart>();
		searchItem();
	}
public String nameDepart;
	public void showEdit() {
		kpiDepOfYearRemoves.clear();
		kpiDepOfYears.clear();
		if (kpiDepEdit != null && kpiDepEdit.getId() != null) {
			KPIDep od = kpiDepService.findByIdAll(kpiDepEdit.getId());
			if (od != null) {
				kpiDep = od;
				codeDepart = this.kpiDep.getCodeDepart();
				try {
					nameDepart = departmentServicePublic.findByCode("code", codeDepart).getName();
				} catch (Exception e) {
				}
				List<KPIDepOfYear> temps = od.getKpiDepOfYears();
				for (int i = 0; i < temps.size(); i++) {
					kpiDepOfYears.add(temps.get(i));
				}
			}
		}
	}

	public void delete() {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (kpiDep != null && kpiDep.getId() != null) {
			kpiDep = kpiDepService.findByIdAll(kpiDep.getId());
			if (kpiDep.isSignKPI()) {
				notify.warning("Phiếu này đã duyệt không xóa được.");
			} else {
				Date date = new Date(kpiDep.getYear(), kpiDep.getYear(), 1);
				if (allowDelete(date)) {
					boolean result = kpiDepService.delete(kpiDep);
					if (result) {
						kpiDeps.remove(kpiDep);
						writeLogWarning("Xoa:" + kpiDep.toString());
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
		try {
			kpiDeps = kpiDepService.findKPIDep(null, year);
			for (int i = 0; i < kpiDeps.size(); i++) {
				try {
					kpiDeps.get(i).setNameDepart(
							departmentServicePublic.findByCode("code", kpiDeps.get(i).getCodeDepart()).getName());
				} catch (Exception e) {
				}

			}
		} catch (Exception e) {
		}

	}

	public List<KPIDep> getKpiDeps() {
		return kpiDeps;
	}

	public void setKpiDeps(List<KPIDep> kpiDeps) {
		this.kpiDeps = kpiDeps;
	}

	public KPIDep getKpiDep() {
		return kpiDep;
	}

	public void setKpiDep(KPIDep kpiDep) {
		this.kpiDep = kpiDep;
	}

	public KPIDep getKpiDepEdit() {
		return kpiDepEdit;
	}

	public void setKpiDepEdit(KPIDep kpiDepEdit) {
		this.kpiDepEdit = kpiDepEdit;
	}

	public List<KPIDepOfYear> getKpiDepOfYears() {
		return kpiDepOfYears;
	}

	public void setKpiDepOfYears(List<KPIDepOfYear> kpiDepOfYears) {
		this.kpiDepOfYears = kpiDepOfYears;
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

	public List<Boolean> getList() {
		return list;
	}

	public void setList(List<Boolean> list) {
		this.list = list;
	}

	public List<TagetDepart> getTagetDeparts() {
		return tagetDeparts;
	}

	public void setTagetDeparts(List<TagetDepart> tagetDeparts) {
		this.tagetDeparts = tagetDeparts;
	}

	public int getTabindex() {
		return tabindex;
	}

	public void setTabindex(int tabindex) {
		this.tabindex = tabindex;
	}

	public List<TagetDepartCate> getTagetDepartCates() {
		return tagetDepartCates;
	}

	public void setTagetDepartCates(List<TagetDepartCate> tagetDepartCates) {
		this.tagetDepartCates = tagetDepartCates;
	}

	public TagetDepartCate getTagetDepartCate() {
		return tagetDepartCate;
	}

	public void setTagetDepartCate(TagetDepartCate tagetDepartCate) {
		this.tagetDepartCate = tagetDepartCate;
	}

	public String getCodeDepart() {
		return codeDepart;
	}

	public void setCodeDepart(String codeDepart) {
		this.codeDepart = codeDepart;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public boolean isOrverideData() {
		return orverideData;
	}

	public void setOrverideData(boolean orverideData) {
		this.orverideData = orverideData;
	}

	public String getNameDepart() {
		return nameDepart;
	}

	public void setNameDepart(String nameDepart) {
		this.nameDepart = nameDepart;
	}

}
