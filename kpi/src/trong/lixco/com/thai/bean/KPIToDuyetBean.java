package trong.lixco.com.thai.bean;

import java.io.IOException;
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
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.docx4j.org.xhtmlrenderer.pdf.ITextRenderer;
import org.jboss.logging.Logger;
import org.joda.time.DateTime;
import org.primefaces.context.RequestContext;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.Visibility;

import net.sf.jasperreports.engine.JRDataSource;
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
import trong.lixco.com.classInfor.PrintKPIComp;
import trong.lixco.com.ejb.service.ParamReportDetailService;
import trong.lixco.com.ejb.servicekpi.FormulaKPIService;
import trong.lixco.com.ejb.servicekpi.KPIDepMonthService;
import trong.lixco.com.ejb.servicekpi.KPIDepOfMonthService;
import trong.lixco.com.ejb.servicekpi.KPIDepService;
import trong.lixco.com.ejb.servicekpi.PathImageAssignService;
import trong.lixco.com.ejb.thai.kpi.KPIToService;
import trong.lixco.com.jpa.entitykpi.FormulaKPI;
import trong.lixco.com.jpa.entitykpi.KPIDepMonth;
import trong.lixco.com.jpa.entitykpi.KPIDepOfMonth;
import trong.lixco.com.jpa.entitykpi.KPIDepOfYear;
import trong.lixco.com.jpa.entitykpi.ParamReportDetail;
import trong.lixco.com.jpa.thai.KPITo;
import trong.lixco.com.jpa.thai.KPIToDetail;
import trong.lixco.com.kpi.general.ApplicationBean;
import trong.lixco.com.thai.apitrong.DepartmentData;
import trong.lixco.com.thai.apitrong.DepartmentDataService;
import trong.lixco.com.util.MediaKPIDepMonthBean;
import trong.lixco.com.util.Notify;
import trong.lixco.com.util.PDFMerger;

@ManagedBean
@javax.faces.bean.ViewScoped
public class KPIToDuyetBean extends AbstractBean<KPIDepOfMonth> {
	private static final long serialVersionUID = 1L;
	private SimpleDateFormat sf;
	private Notify notify;
	private List<KPITo> kpiTos;

	private final int TOTALPARAM = 100;

	private KPITo kpiTo;
	private KPITo kpiToEdit;
	private List<KPIToDetail> kpiToDetails;
	private List<KPIToDetail> kpiToDetailRemoves;
	private List<FormulaKPI> formulaKPIs;
	private FormulaKPI formulaKPISelect;

	private Member member;

	private List<Boolean> list;
	// private List<KPIDepOfYear> kpiDepOfYears;

	private int monthCopy = 0;
	private int yearCopy = 0;

	private int monthSearch;
	private int yearSearch;

	private DepartmentServicePublic departmentServicePublic;
	private MemberServicePublic memberServicePublic;

	@Inject
	private FormulaKPIService formulaKPIService;
	@Inject
	private ParamReportDetailService paramReportDetailService;
	// @Inject
	// private KPIDepService kpiDepService;
	@Inject
	private KPIToService KPI_TO_SERVICE;
	@Inject
	private Logger logger;

	private int tabindex;

	@Override
	protected Logger getLogger() {
		return logger;
	}

	public void copy() {
		notify = new Notify(FacesContext.getCurrentInstance());
		try {
			if (kpiTo.getId() != null) {
				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('dialogCopy').show();");
			} else {
				notify.warning("Chưa chọn dữ liệu để copy!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private KPIDepOfMonth kpm;

	public void selectData(KPIDepOfMonth kpm) {
		this.kpm = kpm;
	}

	private StreamedContent streamedContent;
	@Inject
	KPIDepOfMonthService kpiDepOfMonthService;

	public void processDataAssign(KPIDepOfMonth kpm) {
		if (kpm != null && kpm.getId() != null) {
			this.kpm = kpiDepOfMonthService.findById(kpm.getId());
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

	@Inject
	PathImageAssignService pathImageAssignService;

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
		}
	}

	private JasperPrint jasperPrint;

	public void printOnly() {
		notify = new Notify(FacesContext.getCurrentInstance());
		try {
			String pathre = "/resources/reports/kpis/kpidepmonth.jasper";
			String reportPath = null;
			Map<String, Object> importParam = null;
			importParam = installConfigPersonReport();
			List<ParamReportDetail> paramnhaps = paramReportDetailService
					.findByParamReports_param_name("kpiphongthang");
			for (ParamReportDetail pd : paramnhaps) {
				importParam.put(pd.getKey(), pd.getValue());
			}
			reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath(pathre);
			importParam.put("REPORT_LOCALE", new Locale("vi", "VN"));

			List<PrintKPIComp> printKPIComps = new ArrayList<PrintKPIComp>();
			SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
			// cho nay chua handle duoc//
			// for (int i = 0; i < kpiTos.size(); i++) {
			// PrintKPIComp pr = new PrintKPIComp(KPIDepOfMonths.get(i), sf,
			// departmentServicePublic,
			// memberServicePublic);
			// printKPIComps.add(pr);
			// }

			printKPIComps.sort(Comparator.comparing(PrintKPIComp::getNo));
			JRDataSource beanDataSource = new JRBeanCollectionDataSource(printKPIComps);
			jasperPrint = JasperFillManager.fillReport(reportPath, importParam, beanDataSource);
			data = JasperExportManager.exportReportToPdf(jasperPrint);
			mediaKPIDepMonthBean.setData(data);
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('showpdfreport').show();");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Inject
	MediaKPIDepMonthBean mediaKPIDepMonthBean;
	byte[] data;
	@Inject
	private ApplicationBean applicationBean;

	public StreamedContent getMedia() throws IOException {
		StreamedContent a = mediaKPIDepMonthBean.getMedia();
		return a;
	}

	public void process(String idp) {
		if (kpiToDetails.size() != 0) {
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("document.getElementById('" + idp + "').click();");
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
		try {
			departmentServicePublic = new DepartmentServicePublicProxy();
			memberServicePublic = new MemberServicePublicProxy();
		} catch (Exception e) {
			e.printStackTrace();
		}
		member = getAccount().getMember();
		reset();
		tabindex = 0;
		list = Arrays.asList(true, true, true, true, false, true, false, false, false, false, true, false, true, true,
				true, true, false, true);

	}

	private KPIToDetail kpy;

	public void showListFormula(KPIToDetail param) {
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
			for (int i = 0; i < kpiToDetails.size(); i++) {
				KPIToDetail kp = kpiToDetails.get(i);
				if (kpiToDetails.get(i).getIndex() == kpy.getIndex()) {
					kpy.setFormulaKPI(formulaKPISelect);
					kpy.setComputation(formulaKPISelect.getCode());
					kpiToDetails.set(i, kpy);
					notify.success();
					break;
				}
			}
		}
	}

	public void removeDetail(KPIToDetail item) {
		for (int i = 0; i < kpiToDetails.size(); i++) {
			if (kpiToDetails.get(i).getIndex() == item.getIndex()) {
				kpiToDetails.remove(i);
				kpiToDetailRemoves.add(item);
				break;
			}

		}
	}

	int totalHV = 20, totalCV = 0;

	public void caculatorResult() {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (allowUpdate(null) && allowSave(null)) {
			totalCV = 0;
			totalHV = 0;
			for (int i = 0; i < kpiToDetails.size(); i++) {
				KPIToDetail item = kpiToDetails.get(i);
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
						// kpiDepOfYears.get(i).setRatioComplete(0);
						// kpiDepOfYears.get(i).setRatioCompleteIsWeighted(0);
						totalCV += 0;
						// e.printStackTrace();
					}

				} else {
					try {
						if (!"".equals(TH.trim()) && !"".equals(KH.trim())) {
							engine.eval("var TH = " + TH);
							engine.eval("var KH = " + KH);
							iscacul = true;
						} else {
							iscacul = false;
						}
					} catch (Exception e) {
						iscacul = false;
						kpiToDetails.get(i).setRatioComplete(0);
						kpiToDetails.get(i).setRatioCompleteIsWeighted(0);
						totalCV += 0;
						// e.printStackTrace();
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
									engine.eval(
											"var KQ = Math.abs((TH.getTime() - KH.getTime()) / (24 * 60 * 60 * 1000))");
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
						kpiToDetails.get(i).setRatioComplete(Math.round(result));
						kpiToDetails.get(i).setRatioCompleteIsWeighted(
								Math.round((result * kpiToDetails.get(i).getWeighted()) / 100));
						totalCV += kpiToDetails.get(i).getRatioCompleteIsWeighted();

					} catch (Exception e) {
						// e.printStackTrace();
						kpiToDetails.get(i).setRatioComplete(0);
						kpiToDetails.get(i).setRatioCompleteIsWeighted(0);
						totalCV += 0;
					}

				}
			}
			totalCV = totalCV * 100 / 100;
			for (int i = 0; i < kpiToDetails.size(); i++) {
				if (kpiToDetails.get(i).getContentAppreciate() != null
						&& !"".equals(kpiToDetails.get(i).getContentAppreciate().trim()))
					totalHV += kpiToDetails.get(i).getRatioCompleteIsWeighted();
			}
			if (totalHV < 0)
				totalHV = 0;
			kpiTo.setResult(totalHV);
		} else {
			notify.warningDetail();
		}
	}

	public void updatesignKPIRegister() {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (kpiTo.getId() != null) {
			Date date = new Date(kpiTo.getYear(), kpiTo.getMonth(), 1);
			if (allowUpdate(date)) {
				try {
					KPITo wf = KPI_TO_SERVICE.findById(kpiTo.getId());
					if (wf.isSignKPIBLD()) {
						kpiTo.setSignKPI(wf.isSignKPI());
						notify.warning("BLĐ đã duyệt đăng ký, không điều chỉnh được.");
					} else {
						wf.setSignKPI(kpiTo.isSignKPI());
						wf = KPI_TO_SERVICE.updateSign(wf);
						if (kpiTos.contains(wf)) {
							kpiTos.set(kpiTos.indexOf(wf), wf);
						}
						kpiTos.set(kpiTos.indexOf(wf), wf);
						if (wf.isSignKPI())
							notice("Đã duyệt đăng ký");
					}
				} catch (Exception e) {
					e.printStackTrace();
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
		if (kpiTo.getId() != null) {
			Date date = new Date(kpiTo.getYear(), kpiTo.getMonth(), 1);
			if (allowUpdate(date)) {
				try {
					KPITo wf = KPI_TO_SERVICE.findById(kpiTo.getId());
					if (wf.isSignResultKPIBLD()) {
						kpiTo.setSignResultKPI(wf.isSignResultKPI());
						notify.warning("BLĐ đã duyệt kết quả, không điều chỉnh được.");
					} else {
						wf.setSignResultKPI(kpiTo.isSignResultKPI());
						wf = KPI_TO_SERVICE.updateSignResult(wf);
						if (kpiTos.contains(wf)) {
							kpiTos.set(kpiTos.indexOf(wf), wf);
						}
						kpiTos.set(kpiTos.indexOf(wf), wf);
						if (wf.isSignResultKPI())
							notice("Đã duyệt kết quả");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				notify.warningDetail();
			}
		} else {
			notify.warning("Chưa chọn phiếu.");
		}
	}

	public void updatesignKPIBLD() {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (kpiTo.getId() != null) {
			// // VT0005: Tong giam doc
			// boolean status = false;
			// try {
			// Member mem = getAccount().getMember();
			// if (mem.getCode().equals("VT0005")) {
			// status = true;
			// } else {
			// String emp =
			// kpiDepMonth.getDepartmentParent().getManagerEmployee().getEmployeeCode();
			// if (mem.getCode().equals(emp))
			// status = true;
			// }
			// } catch (Exception e) {
			// // TODO: handle exception
			// }
			// if (status) {
			Date date = new Date(kpiTo.getYear(), kpiTo.getMonth(), 1);
			if (allowUpdate(date)) {
				try {
					KPITo wf = KPI_TO_SERVICE.findById(kpiTo.getId());
					wf.setSignKPIBLD(kpiTo.isSignKPIBLD());
					wf.setSignResultKPIBLD(kpiTo.isSignResultKPIBLD());
					wf = KPI_TO_SERVICE.update(wf);
					if (kpiTos.contains(wf)) {
						kpiTos.set(kpiTos.indexOf(wf), wf);
					}
					kpiTos.set(kpiTos.indexOf(wf), wf);
					writeLogInfo("Duyet KPI thang (phong) " + wf.toString());
					notify.success();
				} catch (Exception e) {
					e.printStackTrace();
				}
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
		Date date = new Date(kpiTo.getYear(), kpiTo.getYear(), 1);
		if (allowSave(date)) {
			List<KPITo> kps = KPI_TO_SERVICE.findKPIDepMonth(kpiTo.getMonth(), kpiTo.getYear(), kpiTo.getCodeDepart());
			for (int i = 0; i < kps.size(); i++) {
				KPI_TO_SERVICE.delete(kps.get(i));
			}
			List<KPIToDetail> listSaves = new ArrayList<>();
			listSaves.addAll(kpiToDetails);
			kpiTo.setKpi_to_chitiet(listSaves);
			Long id = kpiTo.getId();
			kpiTo.setId(null);
			KPITo wf = KPI_TO_SERVICE.saveOrUpdate(kpiTo, kpiToDetailRemoves);
			if (wf != null) {
				if (id != null)
					KPI_TO_SERVICE.delete(KPI_TO_SERVICE.findById(id));
				writeLogInfo("Tao moi " + wf.toString());
				searchItem();
				notify.success();
			} else {
				notify.error();
			}
		} else {
			notify.warningDetail();
		}
	}

	public void createAndDeleteCopy() {
		notify = new Notify(FacesContext.getCurrentInstance());
		kpiTo.setId(null);
		kpiTo.setMonth(monthCopy);
		kpiTo.setYear(yearCopy);
		kpiTo.setSignKPI(false);
		kpiTo.setSignResultKPI(false);
		kpiTo.setResult(0);
		kpiTo.setDateRecei(new DateTime(yearCopy, monthCopy, 1, 0, 0).withDayOfMonth(1).minusDays(1).toDate());
		kpiTo.setDateAssignResult(
				new DateTime(yearCopy, monthCopy, 1, 0, 0).plusMonths(1).withDayOfMonth(1).minusDays(1).toDate());

		List<KPITo> temps = KPI_TO_SERVICE.findKPIDepMonth(monthCopy, yearCopy, kpiTo.getCodeDepart());
		for (int i = 0; i < temps.size(); i++) {
			KPI_TO_SERVICE.delete(temps.get(i));
		}

		List<KPIToDetail> listSaves = new ArrayList<>();
		listSaves.addAll(kpiToDetails);

		for (int i = 0; i < listSaves.size(); i++) {
			listSaves.get(i).setId(null);
			listSaves.get(i).setPerformKPI(null);
			listSaves.get(i).setRatioComplete(0);
			listSaves.get(i).setRatioCompleteIsWeighted(0);
		}

		kpiTo.setKpi_to_chitiet(listSaves);
		KPITo wf = KPI_TO_SERVICE.saveOrUpdate(kpiTo, null);
		if (wf != null) {
			kpiTo = KPI_TO_SERVICE.findByIdAll(wf.getId());
			kpiToEdit = kpiTo;
			kpiTos.add(wf);
			writeLogInfo("Tao moi " + wf.toString());
			notify.success();
			searchItem();
		} else {
			notify.error();
		}
	}

	public void createCopy() {
		notify = new Notify(FacesContext.getCurrentInstance());
		Date date = new Date(yearCopy, monthCopy, 1);
		if (allowSave(date)) {
			List<KPITo> temps = KPI_TO_SERVICE.findKPIDepMonth(monthCopy, yearCopy, kpiTo.getCodeDepart());
			if (temps.size() != 0) {
				boolean status = false;
				for (int i = 0; i < temps.size(); i++) {
					if (temps.get(i).isSignKPI()) {
						status = true;
						break;
					}
				}
				if (status) {
					notify.warning("Tháng đã duyệt không thay thế được.");
				} else {
					RequestContext context = RequestContext.getCurrentInstance();
					context.execute("PF('dialogConfirmCopy').show();");
				}
			} else {
				kpiTo.setId(null);
				kpiTo.setMonth(monthCopy);
				kpiTo.setYear(yearCopy);
				kpiTo.setSignKPI(false);
				kpiTo.setSignResultKPI(false);
				kpiTo.setResult(0);
				kpiTo.setDateRecei(new DateTime(yearCopy, monthCopy, 1, 0, 0).withDayOfMonth(1).minusDays(1).toDate());
				kpiTo.setDateAssignResult(new DateTime(yearCopy, monthCopy, 1, 0, 0).plusMonths(1).withDayOfMonth(1)
						.minusDays(1).toDate());

				List<KPIToDetail> listSaves = new ArrayList<>();
				listSaves.addAll(kpiToDetails);

				for (int i = 0; i < listSaves.size(); i++) {
					listSaves.get(i).setId(null);
					listSaves.get(i).setPerformKPI(null);
					listSaves.get(i).setRatioComplete(0);
					listSaves.get(i).setRatioCompleteIsWeighted(0);
				}

				kpiTo.setKpi_to_chitiet(listSaves);
				KPITo wf = KPI_TO_SERVICE.saveOrUpdate(kpiTo, null);
				if (wf != null) {
					kpiTo = KPI_TO_SERVICE.findByIdAll(wf.getId());
					kpiToEdit = kpiTo;
					kpiTos.add(wf);
					writeLogInfo("Tao moi " + wf.toString());
					notify.success();
					searchItem();
				} else {
					notify.error();
				}
			}
		} else {
			notify.warningDetail();
		}

	}

	public void createOrUpdate() {
		notify = new Notify(FacesContext.getCurrentInstance());
		Date date = new Date(kpiTo.getYear(), kpiTo.getYear(), 1);

		kpiTo.setCodeDepart(codeDepart);
		if (kpiTo.getId() == null) {

			List<KPITo> kps = KPI_TO_SERVICE.findKPIDepMonth(kpiTo.getMonth(), kpiTo.getYear(), kpiTo.getCodeDepart());
			if (kps.size() == 0) {
				if (allowSave(date)) {
					List<KPIToDetail> listSaves = new ArrayList<>();
					double checkp = 0;
					for (int i = 0; i < kpiToDetails.size(); i++) {
						checkp += kpiToDetails.get(i).getWeighted();
					}
					if (kpiToDetails.size() != 0 && checkp != TOTALPARAM) {
						noticeError("Không lưu được. Tổng trọng số các mục tiêu khác 100%");
					} else {
						listSaves.addAll(kpiToDetails);
						kpiTo.setKpi_to_chitiet(listSaves);
						KPITo wf = KPI_TO_SERVICE.saveOrUpdate(kpiTo, kpiToDetailRemoves);
						if (wf != null) {
							searchItem();
							writeLogInfo("Tao moi " + wf.toString());
							notice("Lưu thành công");
						} else {
							noticeError("Xảy ra lỗi không lưu được");
						}
					}
				} else {
					notify.warningDetail();
				}
			} else {
				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('dialogConfirm').show();");
			}
		} else {
			KPITo kpiCompOld = KPI_TO_SERVICE.findById(kpiTo.getId());
			if (kpiCompOld.isSignResultKPI()) {
				this.kpiToEdit = kpiCompOld;
				showEdit();
				noticeError("Phiếu đã duyệt kết quả. Không lưu được");
			} else {
				Date date2 = new Date(kpiCompOld.getYear(), kpiCompOld.getYear(), 1);
				if (allowUpdate(date) && allowUpdate(date2)) {
					if (kpiCompOld.getMonth() != kpiTo.getMonth() || kpiCompOld.getYear() != kpiTo.getYear()) {
						List<KPITo> temps = KPI_TO_SERVICE.findKPIDepMonth(kpiTo.getMonth(), kpiTo.getYear(),
								kpiTo.getCodeDepart());
						if (temps.size() != 0) {
							boolean status = false;
							for (int i = 0; i < temps.size(); i++) {
								if (temps.get(i).isSignKPI()) {
									status = true;
									break;
								}
							}
							if (status) {
								noticeError("Tháng đã duyệt đăng ký không thay thế được.");
							} else {
								RequestContext context = RequestContext.getCurrentInstance();
								context.execute("PF('dialogConfirm').show();");
							}

						} else {
							List<KPIToDetail> listSaves = new ArrayList<>();
							double checkp = 0;
							for (int i = 0; i < kpiToDetails.size(); i++) {
								checkp += kpiToDetails.get(i).getWeighted();
							}
							if (kpiToDetails.size() != 0 && checkp != TOTALPARAM) {
								noticeError("Không lưu được. Tổng trọng số các mục tiêu khác 100%");
							} else {
								listSaves.addAll(kpiToDetails);
								kpiTo.setKpi_to_chitiet(listSaves);
								KPITo wf = KPI_TO_SERVICE.saveOrUpdate(kpiTo, kpiToDetailRemoves);
								if (wf != null) {
									kpiTo = KPI_TO_SERVICE.findByIdAll(wf.getId());
									kpiToDetails = kpiTo.getKpi_to_chitiet();
									for (int i = 0; i < kpiToDetails.size(); i++) {
										kpiToDetails.get(i).setIndex(i);
									}
									searchItem();
									notice("Lưu thành công");
								} else {
									noticeError("Xảy ra lỗi không lưu được");
								}
							}
						}
					} else {
						List<KPIToDetail> listSaves = new ArrayList<>();
						double checkp = 0;
						for (int i = 0; i < kpiToDetails.size(); i++) {
							checkp += kpiToDetails.get(i).getWeighted();
						}
						if (kpiToDetails.size() != 0 && checkp != TOTALPARAM) {
							noticeError("Không lưu được. Tổng trọng số các mục tiêu khác 100%");
						} else {
							List<KPITo> temps = KPI_TO_SERVICE.findKPIDepMonth(kpiTo.getMonth(), kpiTo.getYear(),
									kpiTo.getCodeDepart());
							if (temps.size() != 0) {
								boolean status = false;
								for (int i = 0; i < temps.size(); i++) {
									if (temps.get(i).isSignKPI()) {
										status = true;
										break;
									}
								}
								listSaves.addAll(kpiToDetails);
								kpiTo.setKpi_to_chitiet(listSaves);
								if (status) {
									KPITo wf = KPI_TO_SERVICE.updateAssign(kpiTo);
									if (wf != null) {
										kpiTo = KPI_TO_SERVICE.findByIdAll(wf.getId());
										kpiToDetails = kpiTo.getKpi_to_chitiet();
										for (int i = 0; i < kpiToDetails.size(); i++) {
											kpiToDetails.get(i).setIndex(i);
										}
										searchItem();
										notice("Lưu thành công");
									} else {
										noticeError("Xảy ra lỗi không lưu được");
									}
								} else {
									KPITo wf = KPI_TO_SERVICE.saveOrUpdate(kpiTo, kpiToDetailRemoves);
									if (wf != null) {
										kpiTo = KPI_TO_SERVICE.findByIdAll(wf.getId());
										kpiToDetails = kpiTo.getKpi_to_chitiet();
										for (int i = 0; i < kpiToDetails.size(); i++) {
											kpiToDetails.get(i).setIndex(i);
										}
										searchItem();
										notice("Lưu thành công");
									} else {
										noticeError("Xảy ra lỗi không lưu được");
									}
								}
							}
						}
					}
				} else {
					notify.warningDetail();
				}
			}
		}
	}

	public void loadTaget() {
		// kpiDepOfYears = kpiDepService.findKPIDepOfYear(kpiDepMonth.getYear(),
		// kpiDepMonth.getCodeDepart());
	}

	// public void getListTaget() {
	// for (int i = 0; i < kpiDepOfYears.size(); i++) {
	// if (kpiDepOfYears.get(i).isSelect()) {
	// KPIDepOfMonth item = new KPIDepOfMonth();
	// item.setContentAppreciate(kpiDepOfYears.get(i).getContentAppreciate());
	// KPIDepOfMonths.add(item);
	// }
	// }
	// for (int i = 0; i < KPIDepOfMonths.size(); i++) {
	// KPIDepOfMonths.get(i).setNoid(i + 1);
	// KPIDepOfMonths.get(i).setIndex(i);
	// }
	// }

	public void addNone() {
		KPIToDetail item = new KPIToDetail();
		item.setContentAppreciate("");
		kpiToDetails.add(item);
		for (int i = 0; i < kpiToDetails.size(); i++) {
			kpiToDetails.get(i).setIndex(i);
		}
	}

	private String codeDepart;

	public void reset() {

		kpiTo = new KPITo();
		DateTime dt = new DateTime();
		monthCopy = dt.getMonthOfYear();
		yearCopy = dt.getYear();
		monthSearch = dt.getMonthOfYear();
		yearSearch = dt.getYear();
		codeDepart = member.getDepartment().getCode();

		kpiTo.setMonth(dt.getMonthOfYear());
		kpiTo.setYear(dt.getYear());
		kpiTo.setDateRecei(dt.withDayOfMonth(1).minusDays(1).toDate());
		kpiTo.setDateAssignResult(dt.plusMonths(1).withDayOfMonth(1).minusDays(1).toDate());

		try {
			// String employeeCode =
			// applicationBean.getAccount().getMember().getCode();
			// employee = employeeService.findByCode(employeeCode);
			// List<DepartmentParent> deps =
			// departmentParentService.findByEmpDep(employee);
			// if (applicationBean.getAccount().isAdmin()) {
			// departmentParents = departmentParentService.findAll();
			// admin = true;
			// } else {
			// if (deps.size() != 0) {
			// departmentParents = deps;
			// } else {
			// if
			// (employee.equals(employee.getDepartment().getDepartmentParent().getEmployee()))
			// {
			// departmentParents = new ArrayList<DepartmentParent>();
			// departmentParents.add(employee.getDepartment().getDepartmentParent());
			// } else {
			// departmentParents = new ArrayList<DepartmentParent>();
			// departmentParents.add(employee.getDepartment().getDepartmentParent());
			// }
			// }
			// }
			// kpiDepMonth.setDepartmentParent(employee.getDepartment().getDepartmentParent());
		} catch (Exception e) {

		}
		kpiToDetails = new ArrayList<>();
		kpiToDetailRemoves = new ArrayList<>();
		// kpiDepOfYears = new ArrayList<KPIDepOfYear>();
		searchItem();
	}

	private String nameDepart;

	public void showEdit() {
		kpiToDetailRemoves.clear();
		kpiToDetails.clear();
		if (kpiToEdit != null && kpiToEdit.getId() != null) {
			KPITo od = KPI_TO_SERVICE.findByIdAll(kpiToEdit.getId());
			if (od != null) {
				kpiTo = od;
				codeDepart = this.kpiTo.getCodeDepart();
				try {
					nameDepart = departmentServicePublic.findByCode("code", codeDepart).getName();
				} catch (Exception e) {
				}
				kpiToDetails = od.getKpi_to_chitiet();
				for (int i = 0; i < kpiToDetails.size(); i++) {
					kpiToDetails.get(i).setIndex(i);
				}
			}
		}
	}

	public void delete() {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (kpiTo != null && kpiTo.getId() != null) {
			kpiTo = KPI_TO_SERVICE.findByIdAll(kpiTo.getId());
			if (kpiTo.isSignKPI()) {
				notify.warning("Phiếu này đã duyệt không xóa được.");
			} else {
				Date date = new Date(kpiTo.getYear(), kpiTo.getYear(), 1);
				if (allowDelete(date)) {
					boolean result = KPI_TO_SERVICE.delete(kpiTo);
					if (result) {
						kpiTos.remove(kpiTo);
						writeLogInfo("Xoa " + kpiTo.toString());
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
			if (getAccount().isAdmin()) {
				kpiTos = KPI_TO_SERVICE.findKPIDepMonth(monthSearch, yearSearch, null);
			} else {
				if (member.getDepartment() != null) {
					// lay phong cap 2

					Department depLv2 = null;
					if (member.getDepartment().getLevelDep().getLevel() == 3) {
						depLv2 = member.getDepartment().getDepartment();
					}
					if (member.getDepartment().getLevelDep().getLevel() == 2) {
						depLv2 = member.getDepartment();
					}
					// tim tat ca cac to duoi cap phong
					DepartmentData[] totalDepByDepartmentArray = DepartmentDataService
							.timtheophongquanly(depLv2.getCode());
					List<DepartmentData> totalDepByDepartment = new ArrayList<>(
							Arrays.asList(totalDepByDepartmentArray));
					List<String> departmentCodes = new ArrayList<>();
					for (DepartmentData d : totalDepByDepartment) {
						departmentCodes.add(d.getCode());
					}
					kpiTos = KPI_TO_SERVICE.findKPIToByListDep(monthSearch, yearSearch, departmentCodes);
				}
			}
			for (int i = 0; i < kpiTos.size(); i++) {
				try {
					kpiTos.get(i).setNameDepart(
							departmentServicePublic.findByCode("code", kpiTos.get(i).getCodeDepart()).getName());
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		} catch (Exception e) {
		}

	}

	public List<KPITo> getKpiTos() {
		return kpiTos;
	}

	public void setKpiTos(List<KPITo> kpiTos) {
		this.kpiTos = kpiTos;
	}

	public KPITo getKpiTo() {
		return kpiTo;
	}

	public void setKpiTo(KPITo kpiTo) {
		this.kpiTo = kpiTo;
	}

	public KPITo getKpiToEdit() {
		return kpiToEdit;
	}

	public void setKpiToEdit(KPITo kpiToEdit) {
		this.kpiToEdit = kpiToEdit;
	}

	public List<KPIToDetail> getKpiToDetails() {
		return kpiToDetails;
	}

	public void setKpiToDetails(List<KPIToDetail> kpiToDetails) {
		this.kpiToDetails = kpiToDetails;
	}

	public List<KPIToDetail> getKpiToDetailRemoves() {
		return kpiToDetailRemoves;
	}

	public void setKpiToDetailRemoves(List<KPIToDetail> kpiToDetailRemoves) {
		this.kpiToDetailRemoves = kpiToDetailRemoves;
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

	public int getTabindex() {
		return tabindex;
	}

	public void setTabindex(int tabindex) {
		this.tabindex = tabindex;
	}

	public int getMonthCopy() {
		return monthCopy;
	}

	public void setMonthCopy(int monthCopy) {
		this.monthCopy = monthCopy;
	}

	public int getYearCopy() {
		return yearCopy;
	}

	public void setYearCopy(int yearCopy) {
		this.yearCopy = yearCopy;
	}

	public int getMonthSearch() {
		return monthSearch;
	}

	public void setMonthSearch(int monthSearch) {
		this.monthSearch = monthSearch;
	}

	public int getYearSearch() {
		return yearSearch;
	}

	public void setYearSearch(int yearSearch) {
		this.yearSearch = yearSearch;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public String getCodeDepart() {
		return codeDepart;
	}

	public void setCodeDepart(String codeDepart) {
		this.codeDepart = codeDepart;
	}

	public String getNameDepart() {
		return nameDepart;
	}

	public void setNameDepart(String nameDepart) {
		this.nameDepart = nameDepart;
	}
}
