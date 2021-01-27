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
import java.util.stream.Collectors;

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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.docx4j.org.xhtmlrenderer.pdf.ITextRenderer;
import org.jboss.logging.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.Visibility;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import trong.lixco.com.account.servicepublics.Account;
import trong.lixco.com.account.servicepublics.Department;
import trong.lixco.com.account.servicepublics.DepartmentServicePublic;
import trong.lixco.com.account.servicepublics.DepartmentServicePublicProxy;
import trong.lixco.com.account.servicepublics.Member;
import trong.lixco.com.account.servicepublics.MemberServicePublic;
import trong.lixco.com.account.servicepublics.MemberServicePublicProxy;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.classInfor.OrienInfoEmpl;
import trong.lixco.com.classInfor.PrintKPI;
import trong.lixco.com.ejb.service.ParamReportDetailService;
import trong.lixco.com.ejb.servicekpi.BehaviourPersonService;
import trong.lixco.com.ejb.servicekpi.EmpPJobService;
import trong.lixco.com.ejb.servicekpi.FormulaKPIService;
import trong.lixco.com.ejb.servicekpi.KPIDepOfMonthService;
import trong.lixco.com.ejb.servicekpi.KPIPersonOfMonthService;
import trong.lixco.com.ejb.servicekpi.KPIPersonService;
import trong.lixco.com.ejb.servicekpi.OrientationPersonService;
import trong.lixco.com.ejb.servicekpi.PathImageAssignService;
import trong.lixco.com.ejb.servicekpi.PositionJobService;
import trong.lixco.com.ejb.thai.kpi.PersonalPerformanceService;
import trong.lixco.com.jpa.entitykpi.BehaviourPerson;
import trong.lixco.com.jpa.entitykpi.EmpPJob;
import trong.lixco.com.jpa.entitykpi.FormulaKPI;
import trong.lixco.com.jpa.entitykpi.KPIDepMonth;
import trong.lixco.com.jpa.entitykpi.KPIDepOfMonth;
import trong.lixco.com.jpa.entitykpi.KPIPerson;
import trong.lixco.com.jpa.entitykpi.KPIPersonOfMonth;
import trong.lixco.com.jpa.entitykpi.OrientationPerson;
import trong.lixco.com.jpa.entitykpi.ParamReportDetail;
import trong.lixco.com.jpa.entitykpi.PositionJob;
import trong.lixco.com.jpa.thai.KPIPersonalPerformance;
import trong.lixco.com.kpi.general.ApplicationBean;
import trong.lixco.com.thai.apitrong.PositionJobData;
import trong.lixco.com.thai.apitrong.PositionJobDataService;
import trong.lixco.com.thai.bean.entities.InfoPersonalPerformance;
import trong.lixco.com.util.DepartmentUtil;
import trong.lixco.com.util.Notify;
import trong.lixco.com.util.PDFMerger;

@ManagedBean
@ViewScoped
// KPI ca nhan
public class KPIPersonBean extends AbstractBean<KPIPerson> {
	private static final long serialVersionUID = 1L;
	private SimpleDateFormat sf;
	private Notify notify;
	private List<KPIPerson> kPIPersons;
	private List<KPIPerson> kPIPersonFilters;

	private boolean orverideData = false;

	private final int TOTALPARAM = 100;

	private KPIPerson kPIPerson;
	private KPIPerson kPIPersonEdit;
	private List<KPIPersonOfMonth> kpiPersonOfMonths;
	private List<KPIPersonOfMonth> kpiPersonOfMonthAdds;
	private List<KPIPersonOfMonth> kpiPersonOfMonthRemoves;

	// private DepartmentParent departmentParent, departmentParentSearch;
	private PositionJob positionJobSearch;
	// private List<DepartmentParent> departmentParents;
	private Department departmentSearch;
	private List<Department> departmentSearchs;
	private boolean isEmp = false;// la nhan vien

	// private List<Employee> employees;
	// private Employee employeeSearch;
	private int monthSearch = 0;
	private int yearSearch = 0;
	private boolean select = false;

	private int monthCopy = 0;
	private int yearCopy = 0;

	private int tabindex;

	private PositionJob positionJobSelect;
	// private List<DepartmentParent> departmentParentSearchs;

	private List<Boolean> list;
	private List<OrientationPerson> orientationPersons;

	private List<KPIDepOfMonth> kpiDepOfMonths;

	private List<BehaviourPerson> behaviourPersons;
	private List<FormulaKPI> formulaKPIs;
	private FormulaKPI formulaKPISelect;

	@Inject
	private FormulaKPIService formulaKPIService;
	@Inject
	private BehaviourPersonService behaviourPersonService;
	@Inject
	private ParamReportDetailService paramReportDetailService;
	@Inject
	private OrientationPersonService orientationPersonService;
	// @Inject
	// private DepartmentParentService departmentParentService;
	@Inject
	private KPIPersonService kPIPersonService;
	@Inject
	private PositionJobService positionJobService;
	// @Inject
	// private EmployeeService employeeService;
	@Inject
	private Logger logger;
	@Inject
	private ApplicationBean applicationBean;

	private Department department;
	private List<Department> departments;
	private Member member;
	private List<Member> members;

	DepartmentServicePublic departmentServicePublic;
	MemberServicePublic memberServicePublic;

	@Override
	public void initItem() {
		sf = new SimpleDateFormat("dd/MM/yyyy");
		kPIPersons = new ArrayList<KPIPerson>();
		kpiPersonOfMonths = new ArrayList<KPIPersonOfMonth>();
		kpiPersonOfMonthAdds = new ArrayList<KPIPersonOfMonth>();
		kpiPersonOfMonthRemoves = new ArrayList<KPIPersonOfMonth>();
		orientationPersons = new ArrayList<OrientationPerson>();

		try {
			departmentServicePublic = new DepartmentServicePublicProxy();
			memberServicePublic = new MemberServicePublicProxy();
			departments = new ArrayList<Department>();
			members = new ArrayList<Member>();
			member = getAccount().getMember();
			departmentSearchs = new ArrayList<Department>();
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
		} catch (Exception e) {
		}

		LocalDate lc = new LocalDate();
		monthSearch = lc.getMonthOfYear();
		yearSearch = lc.getYear();

		list = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true,
				true);
		tabindex = 0;
		reset();
		searchItem();
	}

	private JasperPrint jasperPrint;

	public void printList() {
		notify = new Notify(FacesContext.getCurrentInstance());
		try {
			String pathre = "/resources/reports/kpis/kpiperson.jasper";
			String reportPath = null;
			Map<String, Object> importParam = null;
			importParam = installConfigPersonReport();
			List<ParamReportDetail> paramnhaps = paramReportDetailService
					.findByParamReports_param_name("kpicanhanthang");
			for (ParamReportDetail pd : paramnhaps) {
				importParam.put(pd.getKey(), pd.getValue());
			}
			reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath(pathre);
			importParam.put("REPORT_LOCALE", new Locale("vi", "VN"));

			List<PrintKPI> printKPIPRs = new ArrayList<PrintKPI>();
			SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");

			for (int j = 0; j < kPIPersons.size(); j++) {
				if (kPIPersons.get(j).isSelect()) {
					List<PrintKPI> printKPIs = new ArrayList<PrintKPI>();
					List<KPIPersonOfMonth> expds = kPIPersonService.findKPIPerson(kPIPersons.get(j));
					for (int i = 0; i < expds.size(); i++) {
						PrintKPI pr = new PrintKPI(expds.get(i), sf, empPJobService, memberServicePublic,
								positionJobService);
						printKPIs.add(pr);
					}
					boolean status = true;
					for (int i = 0; i < printKPIs.size(); i++) {
						if ("I".equals(printKPIs.get(i).getHeaderGroupCode())) {
							status = false;
							break;
						}
					}
					if (status && expds.size() != 0) {
						PrintKPI pk = new PrintKPI(expds.get(0), true, empPJobService, memberServicePublic,
								positionJobService);
						pk.setHeaderGroupCode("I");
						pk.setHeaderGroupName("Phẩm chất - thái độ - hành vi");
						pk.setHeaderGroupWeighted(10.0);
						printKPIs.add(pk);
					}
					printKPIPRs.addAll(printKPIs);
				}
			}

			printKPIPRs.sort(Comparator.comparing(PrintKPI::getDepartmentName).thenComparing(PrintKPI::getHearder)
					.thenComparing(PrintKPI::getEmployeeName).thenComparing(PrintKPI::getHeaderGroupCode)
					.thenComparing(PrintKPI::getNo));

			JRDataSource beanDataSource = new JRBeanCollectionDataSource(printKPIPRs);
			jasperPrint = JasperFillManager.fillReport(reportPath, importParam, beanDataSource);
			byte[] data = JasperExportManager.exportReportToPdf(jasperPrint);
			showreport(data);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void printOnly() {
		notify = new Notify(FacesContext.getCurrentInstance());
		try {
			String pathre = "/resources/reports/kpis/kpiperson.jasper";
			List<KPIPersonOfMonth> expds = kPIPersonService.findKPIPerson(kPIPerson);
			String reportPath = null;
			Map<String, Object> importParam = null;
			importParam = installConfigPersonReport();
			List<ParamReportDetail> paramnhaps = paramReportDetailService
					.findByParamReports_param_name("kpicanhanthang");
			for (ParamReportDetail pd : paramnhaps) {
				importParam.put(pd.getKey(), pd.getValue());
			}
			reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath(pathre);
			importParam.put("REPORT_LOCALE", new Locale("vi", "VN"));

			List<PrintKPI> printKPIs = new ArrayList<PrintKPI>();
			SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
			for (int i = 0; i < expds.size(); i++) {
				PrintKPI pr = new PrintKPI(expds.get(i), sf, empPJobService, memberServicePublic, positionJobService);
				printKPIs.add(pr);
			}
			boolean status = true;
			for (int i = 0; i < printKPIs.size(); i++) {
				if ("I".equals(printKPIs.get(i).getHeaderGroupCode())) {
					status = false;
					break;
				}
			}
			if (status && expds.size() != 0) {
				PrintKPI pk = new PrintKPI(expds.get(0), true, empPJobService, memberServicePublic, positionJobService);
				pk.setHeaderGroupCode("I");
				pk.setHeaderGroupName("Phẩm chất - thái độ - hành vi");
				pk.setHeaderGroupWeighted(10.0);
				printKPIs.add(pk);
			}
			printKPIs.sort(Comparator.comparing(PrintKPI::getHeaderGroupCode).thenComparing(PrintKPI::getNo));

			JRDataSource beanDataSource = new JRBeanCollectionDataSource(printKPIs);
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

	public void process() {
		if (kpiPersonOfMonths.size() != 0) {
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("document.getElementById('formdataassign:report').click();");
		} else {
			// Khong co du lieu
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('thongbao').show();");
		}
	}

	public void processList() {
		List<KPIPerson> exps = new ArrayList<KPIPerson>();
		notify = new Notify(FacesContext.getCurrentInstance());
		for (int j = 0; j < kPIPersons.size(); j++) {
			if (kPIPersons.get(j).isSelect())
				exps.add(kPIPersons.get(j));
		}

		if (exps.size() != 0) {
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("document.getElementById('formdataassign:report').click();");
		} else {
			// Khong co du lieu
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('thongbao').show();");
		}
	}

	public void onToggle(ToggleEvent e) {
		System.out.println(list.size());
		System.out.println(e.getData()+"-"+(e.getVisibility() == Visibility.VISIBLE));
		list.set((Integer) e.getData(), e.getVisibility() == Visibility.VISIBLE);
		
	}

	// thai
	public void removeDetailThai(KPIPersonOfMonth item) {
		for (int i = 0; i < kpiPersonOfMonths.size(); i++) {
			if (kpiPersonOfMonths.get(i).getIndex() == item.getIndex()) {
				try {
					KPIPerson kpiCompOld = kPIPersonService.findById(kpiPersonOfMonths.get(i).getKpiPerson().getId());
					if (kpiCompOld.isSignKPI()) {
						noticeError("KPI đã duyệt không xóa được.");
					} else {
						kpiPersonOfMonths.remove(i);
						kpiPersonOfMonthRemoves.add(item);
						break;
					}
				} catch (Exception e) {
					kpiPersonOfMonths.remove(i);
					kpiPersonOfMonthRemoves.add(item);
				}

			}

		}
	}
	// end thai

	public void removeDetail(int no) {
		try {
			KPIPerson kpiPersonOld = kPIPersonService.findById(kPIPerson.getId());
			if (kpiPersonOld.isSignKPI()) {
				noticeError("KPI đã duyệt không xóa được.");
			} else {
				notify = new Notify(FacesContext.getCurrentInstance());
				if (kpiPersonOfMonths.get(no - 1).getId() != null)
					kpiPersonOfMonthRemoves.add(kpiPersonOfMonths.get(no - 1));
				kpiPersonOfMonths.remove(no - 1);
				double total = 0;
				for (int i = 0; i < kpiPersonOfMonths.size(); i++) {
					// kpiPersonOfMonths.get(i).setNo(i + 1);
					total += kpiPersonOfMonths.get(i).getRatioCompleteIsWeighted();
				}
				for (int i = 0; i < kpiPersonOfMonths.size(); i++) {
					kpiPersonOfMonths.get(i).setIndex(i + 1);
				}
				kPIPerson.setTotal(total);
			}
		} catch (Exception e) {
		}
	}

	public void addDetail() {
		try {
			KPIPerson kpiPersonOld = kPIPersonService.findById(kPIPerson.getId());
			if (kpiPersonOld.isSignKPI()) {
				noticeError("KPI đã duyệt không thêm được.");
			} else {
				KPIPersonOfMonth item = new KPIPersonOfMonth();
				item.setContentAppreciate("");
				kpiPersonOfMonths.add(item);
				// for (int i = 0; i < kpiPersonOfMonths.size(); i++) {
				// kpiPersonOfMonths.get(i).setNo(i + 1);
				// }
				for (int i = 0; i < kpiPersonOfMonths.size(); i++) {
					kpiPersonOfMonths.get(i).setIndex(i + 1);
				}
			}
		} catch (Exception e) {
		}
	}

	private KPIPersonOfMonth kpy;

	public void showListFormula(KPIPersonOfMonth param) {
		try {
			KPIPerson kpiPersonOld = kPIPersonService.findById(kPIPerson.getId());
			if (kpiPersonOld.isSignKPI()) {
				noticeError("KPI đã duyệt không điều chỉnh được.");
			} else {
				notify = new Notify(FacesContext.getCurrentInstance());
				formulaKPIs = formulaKPIService.findAll();
				formulaKPISelect = param.getFormulaKPI();
				kpy = param;
				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('dialogFormula').show();");
			}
		} catch (Exception e) {
		}
	}

	public void updateFormula() {
		notify = new Notify(FacesContext.getCurrentInstance());
		try {
			KPIPerson kpiPersonOld = kPIPersonService.findById(kPIPerson.getId());
			if (kpiPersonOld.isSignKPI()) {
				noticeError("KPI đã duyệt không cập nhật được.");
			} else {
				if (formulaKPISelect == null) {
					notify.warning("Chưa chọn công thức!");
				} else {
					for (int i = 0; i < kpiPersonOfMonths.size(); i++) {
						// if (kpiPersonOfMonths.get(i).getNo() == kpy.getNo())
						// {
						// kpy.setFormulaKPI(formulaKPISelect);
						// kpy.setCodeFormula(formulaKPISelect.getCode());
						// kpiPersonOfMonths.set(i, kpy);
						// notify.success();
						// break;
						// }
						if (kpiPersonOfMonths.get(i).getIndex() == kpy.getIndex()) {
							kpy.setFormulaKPI(formulaKPISelect);
							kpy.setCodeFormula(formulaKPISelect.getCode());
							kpiPersonOfMonths.set(i, kpy);
							notify.success();
							break;
						}
					}
				}
			}
		} catch (Exception e) {
		}
	}

	public void showBehaviour() {
		behaviourPersons = behaviourPersonService.findAll();
		System.out.println(behaviourPersons.size());
	}

	public void addBehaviour() {
		for (int i = 0; i < behaviourPersons.size(); i++) {
			if (behaviourPersons.get(i).isSelect()) {
				KPIPersonOfMonth item = new KPIPersonOfMonth();
				item.setBehaviour(true);
				item.setRatioComplete(-behaviourPersons.get(i).getMinusPoint());
				item.setRatioCompleteIsWeighted((-behaviourPersons.get(i).getMinusPoint() * 10) / 100);
				item.setContentAppreciate(behaviourPersons.get(i).getContent());
				kpiPersonOfMonthAdds.add(item);
			}

		}
		for (int i = 0; i < kpiPersonOfMonthAdds.size(); i++) {
			kpiPersonOfMonthAdds.get(i).setNo(i + 1);
		}
	}

	// // Thai
	// private List<KPIPersonalPerformance> kpiPersonalPerformances;
	//
	// public void loadPersonalPerformance() {
	// kpiPersonalPerformances = PERSONAL_PERFORMANCE_SERVICE.findAll();
	// showDialogOrien();
	// }

	public void getListKPIPerformance() {
		try {
			KPIPerson kpiCompOld = kPIPersonService.findById(kPIPerson.getId());
			if (kpiCompOld.isSignKPI()) {
				noticeError("KPI đã duyệt không thêm được.");
			} else {
				// List<KPIPersonalPerformance>
				// listKPIPersonalPerformanceSelected =
				// getListKPIPersonalPerformanceSelected();
				// for (int i = 0; i <
				// listKPIPersonalPerformanceSelected.size(); i++) {
				// // check xem item nao duoc chon
				// KPIPersonOfMonth item = new KPIPersonOfMonth();
				// item.setKPIPerformance(true);
				// item.setContentAppreciate(listKPIPersonalPerformanceSelected.get(i).getContent());
				// item.setCodeFormula(listKPIPersonalPerformanceSelected.get(i).getComputation());
				// item.setFormulaKPI(listKPIPersonalPerformanceSelected.get(i).getFormulaKPI());
				// kpiPersonOfMonths.add(item);
				// }
				// Cap nhat list kpi hieu suat de selected khong bi lap
				for (int i = 0; i < listInfoPersonalPerformances.size(); i++) {
					for (int j = 0; j < listInfoPersonalPerformances.get(i).getPersonalPerformances().size(); j++) {
						if (listInfoPersonalPerformances.get(i).getPersonalPerformances().get(j).isSelect()) {
							// check xem item nao duoc chon
							KPIPersonOfMonth item = new KPIPersonOfMonth();
							item.setKPIPerformance(true);
							item.setContentAppreciate(
									listInfoPersonalPerformances.get(i).getPersonalPerformances().get(j).getContent());
							item.setCodeFormula(listInfoPersonalPerformances.get(i).getPersonalPerformances().get(j)
									.getComputation());
							item.setFormulaKPI(listInfoPersonalPerformances.get(i).getPersonalPerformances().get(j)
									.getFormulaKPI());
							item.setShowFormula(false);
							kpiPersonOfMonths.add(item);
							listInfoPersonalPerformances.get(i).getPersonalPerformances().get(j).setSelect(false);
						}
					}
				}
				// End
				for (int i = 0; i < kpiPersonOfMonths.size(); i++) {
					// kpiPersonOfMonths.get(i).setNoid(i + 1);
					kpiPersonOfMonths.get(i).setIndex(i);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	// End Thai
	public void removeDetailAdd(int no) {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (kpiPersonOfMonthAdds.get(no - 1).getId() != null)
			kpiPersonOfMonthRemoves.add(kpiPersonOfMonthAdds.get(no - 1));
		kpiPersonOfMonthAdds.remove(no - 1);
		double total = 0;
		for (int i = 0; i < kpiPersonOfMonthAdds.size(); i++) {
			kpiPersonOfMonthAdds.get(i).setNo(i + 1);
			total += kpiPersonOfMonthAdds.get(i).getRatioCompleteIsWeighted();
		}
		for (int i = 0; i < kpiPersonOfMonthAdds.size(); i++) {
			kpiPersonOfMonthAdds.get(i).setIndex(i + 1);
		}
		kPIPerson.setTotal(total);
	}

	public void ajaxSelectDep() {
		// if (departmentParent != null)
		// employees = employeeService.findByDepp(departmentParent);
	}

	public boolean validate() {
		if (kPIPerson != null && member != null)
			return true;
		return false;
	}

	public void createAndDelete() {
		List<KPIPerson> temps = kPIPersonService.findRange(kPIPerson.getCodeEmp(), kPIPerson.getKmonth(),
				kPIPerson.getKyear());
		for (int i = 0; i < temps.size(); i++) {
			kPIPersonService.delete(temps.get(i));
		}
		List<KPIPersonOfMonth> listSaves = new ArrayList<KPIPersonOfMonth>();
		listSaves.addAll(kpiPersonOfMonths);
		listSaves.addAll(kpiPersonOfMonthAdds);
		kPIPerson.setKpiPersonOfMonths(listSaves);
		Long id = kPIPerson.getId();
		kPIPerson.setId(null);
		KPIPerson wf = kPIPersonService.saveOrUpdate(kPIPerson, kpiPersonOfMonthRemoves);
		if (wf != null) {
			if (id != null)
				kPIPersonService.delete(kPIPersonService.findById(id));
			kPIPerson = kPIPersonService.findByIdAll(wf.getId());
			writeLogInfo("Tao moi " + wf.toString());
			notify.success();
			searchItem();
		} else {
			notify.error();
		}
	}

	public void createAndDeleteCopy() {
		notify = new Notify(FacesContext.getCurrentInstance());
		kPIPerson.setId(null);
		kPIPerson.setKmonth(monthCopy);
		kPIPerson.setKyear(yearCopy);
		kPIPerson.setSignKPI(false);
		kPIPerson.setSignResultKPI(false);
		kPIPerson.setTotal(0);
		kPIPerson.setTotalHV(0);
		kPIPerson.setTotalCV(0);
		kPIPerson.setDateRecei(new DateTime(yearCopy, monthCopy, 1, 0, 0).withDayOfMonth(1).minusDays(1).toDate());
		kPIPerson.setDateAssignResult(
				new DateTime(yearCopy, monthCopy, 1, 0, 0).plusMonths(1).withDayOfMonth(1).minusDays(1).toDate());
		List<KPIPerson> temps = kPIPersonService.findRange(kPIPerson.getCodeEmp(), kPIPerson.getKmonth(),
				kPIPerson.getKyear());
		for (int i = 0; i < temps.size(); i++) {
			kPIPersonService.delete(temps.get(i));
		}
		List<KPIPersonOfMonth> listSaves = new ArrayList<KPIPersonOfMonth>();
		listSaves.addAll(kpiPersonOfMonths);
		listSaves.addAll(kpiPersonOfMonthAdds);
		for (int i = 0; i < listSaves.size(); i++) {
			listSaves.get(i).setId(null);
			listSaves.get(i).setPerformKPI(null);
			listSaves.get(i).setRatioComplete(0);
			listSaves.get(i).setRatioCompleteIsWeighted(0);
			listSaves.get(i).setNameAssign(null);
		}
		kPIPerson.setKpiPersonOfMonths(listSaves);
		KPIPerson wf = kPIPersonService.saveOrUpdate(kPIPerson, kpiPersonOfMonthRemoves);
		if (wf != null) {
			kPIPerson = kPIPersonService.findByIdAll(wf.getId());
			kPIPersonEdit = kPIPerson;
			writeLogInfo("Tao moi " + wf.toString());
			notify.success();
			searchItem();
		} else {
			notify.error();
		}

	}

	public void updatesignKPI() {
		notify = new Notify(FacesContext.getCurrentInstance());
		Date date = new Date(kPIPerson.getKyear(), kPIPerson.getKmonth(), 1);
		if (allowUpdate(date)) {
			KPIPerson wf = kPIPersonService.update(kPIPerson);
			if (kPIPersons.contains(wf)) {
				kPIPersons.set(kPIPersons.indexOf(wf), wf);
			}
			kPIPersons.set(kPIPersons.indexOf(wf), wf);
			writeLogInfo("Duyet KPI" + wf.toString());
			notify.success();
		} else {
			notify.warningDetail();
		}
	}

	public void createCopy() {
		notify = new Notify(FacesContext.getCurrentInstance());
		Date date = new Date(yearCopy, monthCopy, 1);
		if (allowSave(date)) {
			List<KPIPerson> temps = kPIPersonService.findRange(kPIPerson.getCodeEmp(), monthCopy, yearCopy);
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
				kPIPerson.setId(null);
				kPIPerson.setKmonth(monthCopy);
				kPIPerson.setKyear(yearCopy);
				kPIPerson.setSignKPI(false);
				kPIPerson.setSignResultKPI(false);
				kPIPerson.setTotal(0);
				kPIPerson.setTotalHV(0);
				kPIPerson.setTotalCV(0);
				kPIPerson.setDateRecei(
						new DateTime(yearCopy, monthCopy, 1, 0, 0).withDayOfMonth(1).minusDays(1).toDate());
				kPIPerson.setDateAssignResult(new DateTime(yearCopy, monthCopy, 1, 0, 0).plusMonths(1).withDayOfMonth(1)
						.minusDays(1).toDate());
				List<KPIPersonOfMonth> listSaves = new ArrayList<KPIPersonOfMonth>();
				listSaves.addAll(kpiPersonOfMonths);
				listSaves.addAll(kpiPersonOfMonthAdds);

				for (int i = 0; i < listSaves.size(); i++) {
					listSaves.get(i).setId(null);
					listSaves.get(i).setPerformKPI(null);
					listSaves.get(i).setRatioComplete(0);
					listSaves.get(i).setRatioCompleteIsWeighted(0);
					listSaves.get(i).setNameAssign(null);
				}

				kPIPerson.setKpiPersonOfMonths(listSaves);
				KPIPerson wf = kPIPersonService.saveOrUpdate(kPIPerson, kpiPersonOfMonthRemoves);
				if (wf != null) {
					kPIPerson = kPIPersonService.findByIdAll(wf.getId());
					kPIPersonEdit = kPIPerson;
					kPIPersons.add(wf);
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
		if (validate()) {
			kPIPerson.setCodeEmp(member.getCode());
			Date date = new Date(kPIPerson.getKyear(), kPIPerson.getKmonth(), 1);
			if (kPIPerson.getId() == null) {
				if (allowSave(date)) {
					List<KPIPerson> temps = kPIPersonService.findRange(kPIPerson.getCodeEmp(), kPIPerson.getKmonth(),
							kPIPerson.getKyear());
					// check kpi da duoc tao hay chua
					if (temps.size() != 0) {
						boolean status = false;
						for (int i = 0; i < temps.size(); i++) {
							if (temps.get(i).isSignKPI()) {
								status = true;
								break;
							}
						}
						if (status) {
							noticeError("Tháng đã duyệt không thay thế được.");
						} else {
							RequestContext context = RequestContext.getCurrentInstance();
							context.execute("PF('dialogConfirm').show();");
						}
					} else {
						List<KPIPersonOfMonth> listSaves = new ArrayList<KPIPersonOfMonth>();
						double checkp = 0;
						for (int i = 0; i < kpiPersonOfMonths.size(); i++) {
							checkp += kpiPersonOfMonths.get(i).getWeighted();
						}
						if (kpiPersonOfMonths.size() != 0 && checkp != TOTALPARAM) {
							noticeError("Không lưu được. Tổng trọng số các mục tiêu khác 100%");
						} else {
							// trong
							listSaves.addAll(kpiPersonOfMonths);
							listSaves.addAll(kpiPersonOfMonthAdds);
							kPIPerson.setKpiPersonOfMonths(listSaves);
							KPIPerson wf = kPIPersonService.saveOrUpdate(kPIPerson, kpiPersonOfMonthRemoves);
							if (wf != null) {
								this.kPIPersonEdit = wf;
								showEdit();
								searchItem();
								notice("Lưu thành công");
							} else {
								noticeError("Xảy ra lỗi không lưu được");
							}
							// end trong
						}
					}
				} else {
					notify.warningDetail();
				}
			} else {
				KPIPerson kpiPersonOld = kPIPersonService.findById(kPIPerson.getId());
				if (kpiPersonOld.isSignResultKPI()) {
					this.kPIPersonEdit = kpiPersonOld;
					showEdit();
					noticeError("Phiếu đã duyệt kết quả. Không lưu được");
				} else {
					Date date2 = new Date(kpiPersonOld.getKyear(), kpiPersonOld.getKmonth(), 1);
					if (allowUpdate(date) && allowUpdate(date2)) {
						if (kpiPersonOld.getKmonth() != kPIPerson.getKmonth()
								|| kpiPersonOld.getKyear() != kPIPerson.getKyear()) {
							List<KPIPerson> temps = kPIPersonService.findRange(kPIPerson.getCodeEmp(),
									kPIPerson.getKmonth(), kPIPerson.getKyear());
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
									// //////////////////////////////////////////////////////////////////////////////////////////////////////////
									RequestContext context = RequestContext.getCurrentInstance();
									context.execute("PF('dialogConfirm').show();");
								}
							} else {
								List<KPIPersonOfMonth> listSaves = new ArrayList<KPIPersonOfMonth>();
								double checkp = 0;
								// thai
								double checkKPIPerformanceWeighted = 0;
								boolean checkKPIInvalid = false;
								// end thai
								for (int i = 0; i < kpiPersonOfMonths.size(); i++) {
									checkp += kpiPersonOfMonths.get(i).getWeighted();
									// thai
									if (kpiPersonOfMonths.get(i).isKPIPerformance()) {
										if (kpiPersonOfMonths.get(i).getWeighted() >= 10) {
											checkKPIPerformanceWeighted = checkKPIPerformanceWeighted
													+ kpiPersonOfMonths.get(i).getWeighted();
										}
									}
									if (kpiPersonOfMonths.get(i).getWeighted() < 10) {
										checkKPIInvalid = true;
									}
									// end thai
								}
								if (kpiPersonOfMonths.size() != 0 && checkp != TOTALPARAM) {
									noticeError("Không lưu được. Tổng trọng số các mục tiêu khác 100%");
								} else {
									listSaves.addAll(kpiPersonOfMonths);
									listSaves.addAll(kpiPersonOfMonthAdds);
									kPIPerson.setKpiPersonOfMonths(listSaves);
									KPIPerson wf = kPIPersonService.saveOrUpdate(kPIPerson, kpiPersonOfMonthRemoves);
									// Thai
									if (checkKPIInvalid == false && checkKPIPerformanceWeighted >= 80) {
										// trong
										if (wf != null) {
											this.kPIPersonEdit = wf;
											showEdit();
											writeLogInfo("Cap nhat" + wf.toString());
											notice("Lưu thành công.");
										} else {
											noticeError("Xảy ra lỗi khi lưu.");
										}
										// end trong
									} else {
										if (checkKPIInvalid == true) {
											noticeError("Trọng số KPI phải lớn hơn 10%");
										} else {
											noticeError("Tổng trọng số KPI hiệu suất không được bé hơn 80%");
										}
									}
									// end thai
								}
							}
						} else {
							List<KPIPersonOfMonth> listSaves = new ArrayList<KPIPersonOfMonth>();
							double checkp = 0;
							// thai
							double checkKPIPerformanceWeighted = 0;
							boolean checkKPIInvalid = false;
							// end thai
							for (int i = 0; i < kpiPersonOfMonths.size(); i++) {
								checkp += kpiPersonOfMonths.get(i).getWeighted();
								// thai
								if (kpiPersonOfMonths.get(i).isKPIPerformance()) {
									if (kpiPersonOfMonths.get(i).getWeighted() >= 10) {
										checkKPIPerformanceWeighted = checkKPIPerformanceWeighted
												+ kpiPersonOfMonths.get(i).getWeighted();
									}
								}
								if (kpiPersonOfMonths.get(i).getWeighted() < 10) {
									checkKPIInvalid = true;
								}
								// end thai
							}
							if (kpiPersonOfMonths.size() != 0 && checkp != TOTALPARAM) {
								noticeError("Không lưu được. Tổng trọng số các mục tiêu khác 100%");
							} else {
								List<KPIPerson> temps = kPIPersonService.findRange(kPIPerson.getCodeEmp(),
										kPIPerson.getKmonth(), kPIPerson.getKyear());
								if (temps.size() != 0) {
									boolean status = false;
									for (int i = 0; i < temps.size(); i++) {
										if (temps.get(i).isSignKPI()) {
											status = true;
											break;
										}
									}
									listSaves.addAll(kpiPersonOfMonths);
									listSaves.addAll(kpiPersonOfMonthAdds);
									kPIPerson.setKpiPersonOfMonths(listSaves);
									if (status) {
										// Thai
										if (checkKPIInvalid == false && checkKPIPerformanceWeighted >= 80) {
											KPIPerson wf = kPIPersonService.updateAssign(kPIPerson);
											if (wf != null) {
												this.kPIPersonEdit = wf;
												showEdit();
												notice("Lưu thành công");
											} else {
												noticeError("Xảy ra lỗi không lưu được");
											}
										} else {
											if (checkKPIInvalid == true) {
												noticeError("Trọng số KPI phải lớn hơn 10%");
											} else {
												noticeError("Tổng trọng số KPI hiệu suất không được bé hơn 80%");
											}
										}

									} else {
										// Thai
										if (checkKPIInvalid == false && checkKPIPerformanceWeighted >= 80) {
											KPIPerson wf = kPIPersonService.saveOrUpdate(kPIPerson,
													kpiPersonOfMonthRemoves);
											if (wf != null) {
												this.kPIPersonEdit = wf;
												showEdit();
												notice("Lưu thành công");
											} else {
												noticeError("Xảy ra lỗi không lưu được");
											}
										} else {
											if (checkKPIInvalid == true) {
												noticeError("Trọng số KPI phải lớn hơn 10%");
											} else {
												noticeError("Tổng trọng số KPI hiệu suất không được bé hơn 80%");
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
			}
		} else {
			notify.warning("Điền đầy đủ thông tin!");
		}
	}

	public void updateAssign() {
		notify = new Notify(FacesContext.getCurrentInstance());
		KPIPerson kpi = kPIPersonService.findById(kPIPerson.getId());
		if (kpi.isSignResultKPI()) {
			notice("Tháng đã duyệt không điều chỉnh được.");
		} else {
			if (kpi.getKmonth() == kPIPerson.getKmonth() || kpi.getKyear() == kPIPerson.getKyear()) {
				List<KPIPersonOfMonth> listSaves = new ArrayList<KPIPersonOfMonth>();
				double checkp = 0;
				for (int i = 0; i < kpiPersonOfMonths.size(); i++) {
					checkp += kpiPersonOfMonths.get(i).getWeighted();
				}
				if (kpiPersonOfMonths.size() != 0 && checkp != TOTALPARAM) {
					noticeError("Không lưu được. Tổng trọng số các mục tiêu khác 100%");
				} else {
					listSaves.addAll(kpiPersonOfMonths);
					listSaves.addAll(kpiPersonOfMonthAdds);
					kPIPerson.setKpiPersonOfMonths(listSaves);
					KPIPerson wf = kPIPersonService.updateAssign(kPIPerson);
					if (wf != null) {
						this.kPIPersonEdit = wf;
						showEdit();
						writeLogInfo("Cap nhat" + wf.toString());
						notify.success();
					} else {
						notify.error();
					}
				}
			} else {
				notify.warning("Tháng đã duyệt đăng ký. Tải lại dữ liệu để nhập kết quả.");
			}

		}
	}

	private KPIPersonOfMonth kpm;

	public void selectData(KPIPersonOfMonth kpm) {
		this.kpm = kpm;
	}

	@Inject
	KPIPersonOfMonthService kPIPersonOfMonthService;
	@Inject
	PathImageAssignService pathImageAssignService;

	// Cap nhat dư liêu chung minh
	public void updateDataAssign(FileUploadEvent event) {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (kpm != null && kpm.getId() != null) {
			kpm = kPIPersonOfMonthService.findById(kpm.getId());
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
						kpm = kPIPersonOfMonthService.update(kpm);
						kpiPersonOfMonths.set(kpiPersonOfMonths.indexOf(kpm), this.kpm);
						RequestContext context = RequestContext.getCurrentInstance();
						context.execute("PF('dialogDataAssign').hide();");
						notice("Tải lên thành công.");
					}
				} catch (Exception e) {
					e.printStackTrace();
					kpm = kPIPersonOfMonthService.findById(kpm.getId());
					kpiPersonOfMonths.set(kpiPersonOfMonths.indexOf(kpm), this.kpm);
					noticeError("Không lưu được. Dữ liệu vượt quá dung lượng");
				}
			} else {
				noticeError("Tài khoản không có quyền thực hiện.");
			}
		} else {
			noticeError("Không chọn nội dung nhập dữ liệu.");
		}

	}

	private StreamedContent streamedContent;

	public void processDataAssign(KPIPersonOfMonth kpm) {
		if (kpm != null && kpm.getId() != null) {
			this.kpm = kPIPersonOfMonthService.findById(kpm.getId());
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

	@Inject
	private EmpPJobService empPJobService;
	private List<PositionJob> positionJobs;
	@Inject
	private PersonalPerformanceService PERSONAL_PERFORMANCE_SERVICE;
	@Inject
	private PositionJobService POSITION_JOB_SERVICE;

	private List<KPIPersonalPerformance> listKPIPersonalByEmployee;
	private List<InfoPersonalPerformance> listInfoPersonalPerformances;

	public void showDialogOrien() {
		listInfoPersonalPerformances = new ArrayList<>();
		notify = new Notify(FacesContext.getCurrentInstance());
		if (kPIPerson.getId() != null) {
			// tat ca vi tri cong viec
			positionJobs = new ArrayList<PositionJob>();
			// List<EmpPJob> empPJobs =
			// empPJobService.findByEmployee(kPIPerson.getCodeEmp());
			// tim vi tri cong viec theo nhan vien
			PositionJobData[] empPJobs = PositionJobDataService.vttheonhanvien(kPIPerson.getCodeEmp());
			if (empPJobs == null || empPJobs.length == 0) {
				notify.warning("Không tìm thấy vị trí công việc phù hợp!");
				return;
			}
			for (int i = 0; i < empPJobs.length; i++) {
				PositionJob pj = positionJobService.findByCode(empPJobs[i].getCode());
				if (pj != null)
					positionJobs.add(pj);
			}

			if (positionJobs.size() != 0)
				positionJobSelect = positionJobs.get(0);
			// Thai
			// Tao danh sach code PJob theo tung nhan vien
			List<String> listCodePJob = new ArrayList<>();
			for (int i = 0; i < positionJobs.size(); i++) {
				listCodePJob.add(positionJobs.get(i).getCode());
			}
			if (listCodePJob.isEmpty()) {
				notify.warning("Không tìm thấy công việc phù hợp!");
				return;
			}
			// Tao list info
			listKPIPersonalByEmployee = PERSONAL_PERFORMANCE_SERVICE.find(listCodePJob);
			Map<String, List<KPIPersonalPerformance>> datagroups11 = listKPIPersonalByEmployee.stream()
					.collect(Collectors.groupingBy(a -> a.getCodePJob(), Collectors.toList()));

			listKPIPersonalByEmployee = new ArrayList<>();
			for (String key : datagroups11.keySet()) {
				List<KPIPersonalPerformance> invs = datagroups11.get(key);
				try {
					InfoPersonalPerformance tgi = new InfoPersonalPerformance();
					tgi.setPositionJobName(POSITION_JOB_SERVICE.findByCode(key).getName());
					tgi.setPersonalPerformances(invs);
					listInfoPersonalPerformances.add(tgi);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			// End Thai
			// showOrientationPerson();
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('widgetKPIPersonalPerformance').show();");
		} else {
			notify.warning("Chưa lưu thông tin phòng ban/nhân viên!");
		}
	}

	@Inject
	private KPIDepOfMonthService kpiDepOfMonthService;

	public void showDialogKPIDepart() {
		notify = new Notify(FacesContext.getCurrentInstance());
		try {
			KPIPerson kpiPersonOld = kPIPersonService.findById(kPIPerson.getId());
			if (kpiPersonOld.isSignKPI()) {
				noticeError("KPI đã duyệt không thêm được.");
			} else {
				if (kPIPerson.getId() != null) {
					Member member = memberServicePublic.findByCode(kPIPerson.getCodeEmp());
					Department dep = member.getDepartment();
					if (dep.getLevelDep().getLevel() < 2) {
						notify.warning("Nhân viên không thuộc cấp quản lý phòng.");
					} else {
						Department departmentlv2 = findDeplevel2(dep);
						KPIDepMonth kpim = kpiDepOfMonthService.findKPIDepMonth(kPIPerson.getKmonth(),
								kPIPerson.getKyear(), departmentlv2.getCode());
						if (kpim != null) {
							kpiDepOfMonths = kpim.getKpiDepOfMonths();
							RequestContext context = RequestContext.getCurrentInstance();
							context.execute("PF('dialogKPIDepart').show();");
						} else {
							notify.warning("Không có KPI phòng trong tháng.");
						}
					}
				} else {
					notify.warning("Chưa lưu thông tin phòng ban/nhân viên!");
				}
			}
		} catch (Exception e) {
		}

	}

	public Department findDeplevel2(Department department) {
		if (department.getLevelDep().getLevel() == 2) {
			return department;
		} else {
			return findDeplevel2(department.getDepartment());
		}
	}

	public void copy() {
		notify = new Notify(FacesContext.getCurrentInstance());
		try {
			if (kPIPerson.getId() != null) {
				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('dialogCopy').show();");
			} else {
				notify.warning("Chưa chọn dữ liệu để copy!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private List<OrienInfoEmpl> orienInfoEmpls;

	public void showOrientationPerson() {
		orienInfoEmpls = new ArrayList<OrienInfoEmpl>();
		try {
			String employeeCode = getAccount().getMember().getCode();
			List<EmpPJob> empPJobs = empPJobService.findByEmployee(employeeCode);
			for (int i = 0; i < empPJobs.size(); i++) {
				try {
					OrienInfoEmpl orEmpl = new OrienInfoEmpl();
					orEmpl.setNamePos(positionJobService.findByCode(empPJobs.get(i).getCodePJob()).getName());
					orEmpl.setOrientationPersons(orientationPersonService.findSearch(empPJobs.get(i).getCodePJob()));
					orienInfoEmpls.add(orEmpl);
				} catch (Exception e) {
				}

			}

		} catch (Exception e) {
		}
	}

	public void getKPIDepart() {
		for (int i = 0; i < kpiDepOfMonths.size(); i++) {
			if (kpiDepOfMonths.get(i).isSelect()) {
				KPIPersonOfMonth item = new KPIPersonOfMonth();
				item.setContentAppreciate(kpiDepOfMonths.get(i).getContentAppreciate());
				item.setKPIPerformance(true);
				if (kpiDepOfMonths.get(i).getFormulaKPI() != null) {
					item.setFormulaKPI(kpiDepOfMonths.get(i).getFormulaKPI());
					item.setCodeFormula(kpiDepOfMonths.get(i).getFormulaKPI().getCode());
				}
				item.setShowFormula(false);
				kpiPersonOfMonths.add(item);
			}
		}
		// for (int i = 0; i < kpiPersonOfMonths.size(); i++) {
		// kpiPersonOfMonths.get(i).setNo(i + 1);
		// }
		for (int i = 0; i < kpiPersonOfMonths.size(); i++) {
			kpiPersonOfMonths.get(i).setIndex(i + 1);
		}
	}

	public void getOrientationPerson(OrientationPerson orientationPerson) {
		try {
			KPIPerson kpiPersonOld = kPIPersonService.findById(kPIPerson.getId());
			if (kpiPersonOld.isSignKPI()) {
				noticeError("KPI đã duyệt không thêm được.");
			} else {
				KPIPersonOfMonth item = new KPIPersonOfMonth();
				try {
					item.setCodeFormula(orientationPerson.getFormulaKPI().getCode());
				} catch (Exception e) {
				}
				item.setOrientationPerson(orientationPerson);
				item.setContentAppreciate(orientationPerson.getContent());
				kpiPersonOfMonths.add(item);
				// for (int i = 0; i < kpiPersonOfMonths.size(); i++) {
				// kpiPersonOfMonths.get(i).setNo(i + 1);
				// }
				for (int i = 0; i < kpiPersonOfMonths.size(); i++) {
					kpiPersonOfMonths.get(i).setIndex(i + 1);
				}
			}
		} catch (Exception e) {
		}
	}

	int totalHV = 10;
	double totalCV = 0;

	public void caculatorResult() {
		totalCV = 0;
		totalHV = 10;
		for (int i = 0; i < kpiPersonOfMonths.size(); i++) {
			KPIPersonOfMonth item = kpiPersonOfMonths.get(i);
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
					kpiPersonOfMonths.get(i).setRatioComplete(0);
					kpiPersonOfMonths.get(i).setRatioCompleteIsWeighted(0);
					totalCV += 0;
					e.printStackTrace();
				}

			} else {
				try {
					if(TH!=null&&!"".equals(TH.trim())&&KH!=null&&!"".equals(KH.trim())){
					engine.eval("var TH = " + TH);
					engine.eval("var KH = " + KH);
					}else{
						iscacul = false;
						kpiPersonOfMonths.get(i).setRatioComplete(0);
						kpiPersonOfMonths.get(i).setRatioCompleteIsWeighted(0);
						totalCV += 0;
					}
				} catch (Exception e) {
					iscacul = false;
					kpiPersonOfMonths.get(i).setRatioComplete(0);
					kpiPersonOfMonths.get(i).setRatioCompleteIsWeighted(0);
					totalCV += 0;
				}
			}
			if (iscacul) {
				try {
					String KQ = "";
					if (item.getFormulaKPI() != null) {
						KQ = item.getFormulaKPI().getCodeVSGood();
					} else {
						KQ = item.getOrientationPerson().getFormulaKPI().getCodeVSGood();
					}
					engine.eval("var KQ = " + KQ);
					if (!"".equals(item.getParamA()))
						engine.eval("var A = " + item.getParamA());
					if (!"".equals(item.getParamB()))
						engine.eval("var B = " + item.getParamB());
					if (!"".equals(item.getParamC()))
						engine.eval("var C = " + item.getParamC());
					if (!"".equals(item.getParamD()))
						engine.eval("var D = " + item.getParamD());
					if (!"".equals(item.getParamE()))
						engine.eval("var E = " + item.getParamE());
					if (!"".equals(item.getParamF()))
						engine.eval("var F = " + item.getParamF());
					if ((boolean) engine.get("KQ")) {
						// ket qua thu hien theo tinh dung
						String RESULT = "";
						if (item.getFormulaKPI() != null) {
							RESULT = item.getFormulaKPI().getCodeGood();
						} else {
							RESULT = item.getOrientationPerson().getFormulaKPI().getCodeGood();
						}
						engine.eval("var RESULT = " + RESULT);
					} else {
						// ket qua false
						String code = "";
						if (item.getFormulaKPI() != null) {
							code = item.getFormulaKPI().getCodeNotGood();
						} else {
							code = item.getOrientationPerson().getFormulaKPI().getCodeNotGood();
						}
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

					kpiPersonOfMonths.get(i).setRatioComplete(Math.round(result));
					kpiPersonOfMonths.get(i).setRatioCompleteIsWeighted(
							Math.round((result * kpiPersonOfMonths.get(i).getWeighted()) / 100));
					totalCV += kpiPersonOfMonths.get(i).getRatioCompleteIsWeighted();

				} catch (Exception e) {
					kpiPersonOfMonths.get(i).setRatioComplete(0);
					kpiPersonOfMonths.get(i).setRatioCompleteIsWeighted(0);
					totalCV += 0;
				}

			}
		}
		totalCV = totalCV * 90 / 100;
		for (int i = 0; i < kpiPersonOfMonthAdds.size(); i++) {
			if (kpiPersonOfMonthAdds.get(i).getContentAppreciate() != null
					&& !"".equals(kpiPersonOfMonthAdds.get(i).getContentAppreciate().trim()))
				totalHV += kpiPersonOfMonthAdds.get(i).getRatioCompleteIsWeighted();
		}
		// if (totalHV < 0)
		// totalHV = 0;
		kPIPerson.setTotalHV(Math.round(totalHV));
		double a = (double) Math.round(totalCV * 10) / 10;
		kPIPerson.setTotalCV(Math.round(a));
		kPIPerson.setTotal(Math.round(kPIPerson.getTotalCV() + totalHV));
		
		System.out.println(list.size());
		System.out.println(list);
		
	}

	boolean manager = false;
	private Account account;

	public void reset() {
		DateTime dt = new DateTime();
		kPIPerson = new KPIPerson();
		monthCopy = dt.getMonthOfYear();
		yearCopy = dt.getYear();
		kPIPerson.setDateRecei(new DateTime().withDayOfMonth(1).minusDays(1).toDate());
		kPIPerson.setDateAssignResult(new DateTime().plusMonths(1).withDayOfMonth(1).minusDays(1).toDate());
		kpiPersonOfMonths.clear();
		kpiPersonOfMonthAdds.clear();
		orientationPersons.clear();
		kpiPersonOfMonthRemoves.clear();
		// departmentParent = null;
		// employees = null;

		try {
			// String employeeCode =
			// applicationBean.getAccount().getMember().getCode();
			// employee = employeeService.findByCode(employeeCode);
			// account = applicationBean.getAccount();
			// List<DepartmentParent> deps =
			// departmentParentService.findByEmpDep(employee);
			// try {
			// departmentParent =
			// employee.getDepartment().getDepartmentParent();
			// } catch (Exception e) {
			// }
			//
			// if (account.isAdmin()) {
			// departmentParentSearchs = departmentParentService.findAll();
			// if (departmentParentSearchs.size() != 0)
			// departmentParentSearch = departmentParentSearchs.get(0);
			// departmentParents = departmentParentSearchs;
			// ajaxSelectDep();
			// } else {
			// if (deps.size() != 0) {
			// departmentParentSearchs = deps;
			// if (departmentParentSearchs.size() != 0)
			// departmentParentSearch = departmentParentSearchs.get(0);
			// departmentParents = departmentParentSearchs;
			// ajaxSelectDep();
			// } else {
			// if (employee.equals(departmentParent.getEmployee())) {
			// manager = true;
			// departmentParentSearchs = new ArrayList<DepartmentParent>();
			// departmentParentSearch =
			// employee.getDepartment().getDepartmentParent();
			// departmentParentSearchs.add(departmentParentSearch);
			// ajaxSelectDep();
			//
			// departmentParents = new ArrayList<DepartmentParent>();
			// departmentParents.add(departmentParent);
			// } else {
			// departmentParentSearchs = new ArrayList<DepartmentParent>();
			// departmentParentSearch =
			// employee.getDepartment().getDepartmentParent();
			// departmentParentSearchs.add(departmentParentSearch);
			//
			// departmentParents = new ArrayList<DepartmentParent>();
			// departmentParents.add(departmentParent);
			//
			// employeeSearch = employee;
			// employees = new ArrayList<Employee>();
			// employees.add(employee);
			// isEmp = true;
			// }
			//
			// }
			// }
			// kPIPerson.setEmployee(employee);
			// ajaxdepartmentSearch();
		} catch (Exception e) {
		}
	}

	public void ajaxdepartmentSearch() {
		// if (departmentParentSearch != null) {
		// departmentSearchs =
		// departmentService.findDepartment(departmentParentSearch);
		// departmentSearch = null;
		//
		// }
	}

	public void showEdit() {
		kpiPersonOfMonthRemoves.clear();
		kpiPersonOfMonthAdds.clear();
		kpiPersonOfMonths.clear();
		if (kPIPersonEdit != null && kPIPersonEdit.getId() != null) {
			KPIPerson od = kPIPersonService.findByIdAll(kPIPersonEdit.getId());
			if (od != null) {
				kPIPerson = od;
				try {
					member = memberServicePublic.findByCode(kPIPerson.getCodeEmp());
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				for (int i = 0; i < od.getKpiPersonOfMonths().size(); i++) {
					if (od.getKpiPersonOfMonths().get(i).isBehaviour()) {
						kpiPersonOfMonthAdds.add(od.getKpiPersonOfMonths().get(i));
					} else {
						kpiPersonOfMonths.add(od.getKpiPersonOfMonths().get(i));
					}
				}
				for (int i = 0; i < kpiPersonOfMonthAdds.size(); i++) {
					kpiPersonOfMonthAdds.get(i).setNo(i + 1);
				}
				for (int i = 0; i < kpiPersonOfMonths.size(); i++) {
					kpiPersonOfMonths.get(i).setIndex(i + 1);
				}
			}
		}

	}

	public void delete() {
		notify = new Notify(FacesContext.getCurrentInstance());
		try {
			KPIPerson kpiPersonOld = kPIPersonService.findById(kPIPerson.getId());
			if (kpiPersonOld.isSignKPI()) {
				noticeError("KPI đã duyệt không xóa được.");
			} else {
				if (kPIPerson != null && kPIPerson.getId() != null) {
					kPIPerson = kPIPersonService.findByIdAll(kPIPerson.getId());
					if (kPIPerson.isSignKPI()) {
						notify.warning("Phiếu này đã duyệt không xóa được.");
					} else {
						Date date = new Date(kPIPerson.getKyear(), kPIPerson.getKmonth(), 1);
						if (allowDelete(date)) {
							boolean result = kPIPersonService.delete(kPIPerson);
							if (result) {
								kPIPersons.remove(kPIPerson);
								writeLogInfo("Xoa " + kPIPerson.toString());
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
		} catch (Exception e) {
		}
	}

	public void searchItem() {
		try {
			kPIPersons = kPIPersonService.findRange(member.getCode(), 0, yearSearch);
		} catch (Exception e) {

		}

		try {
			List<String> members = new ArrayList<String>();
			Member mems[] = null;
			if (departmentSearch != null) {
				if (getAccount().isAdmin()) {
					mems = memberServicePublic.findByCodeDepart(departmentSearch.getCode());
					if (mems != null)
						for (int i = 0; i < mems.length; i++) {
							members.add(mems[i].getCode());
						}
					kPIPersons = kPIPersonService.findRange(null, monthSearch, yearSearch, members);
				} else {
					kPIPersons = kPIPersonService.findRange(member.getCode(), monthSearch, yearSearch);
				}

				for (int i = 0; i < kPIPersons.size(); i++) {
					try {
						kPIPersons.get(i)
								.setNameEmp(memberServicePublic.findByCode(kPIPersons.get(i).getCodeEmp()).getName());
					} catch (Exception e) {
					}
				}
			}
		} catch (Exception e) {

		}

	}

	public void eventSelect() {
		if (kPIPersons != null)
			for (int i = 0; i < kPIPersons.size(); i++) {
				kPIPersons.get(i).setSelect(select);
			}
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

	public List<KPIPerson> getKpiPersons() {
		return kPIPersons;
	}

	public void setKpiPersons(List<KPIPerson> kpiPersons) {
		this.kPIPersons = kpiPersons;
	}

	public KPIPerson getkPIPerson() {
		return kPIPerson;
	}

	public void setkPIPerson(KPIPerson kPIPerson) {
		this.kPIPerson = kPIPerson;
	}

	public KPIPerson getkPIPersonEdit() {
		return kPIPersonEdit;
	}

	public void setkPIPersonEdit(KPIPerson kPIPersonEdit) {
		this.kPIPersonEdit = kPIPersonEdit;
	}

	public PositionJob getPositionJobSearch() {
		return positionJobSearch;
	}

	public void setPositionJobSearch(PositionJob positionJobSearch) {
		this.positionJobSearch = positionJobSearch;
	}

	public List<Boolean> getList() {
		return list;
	}

	public void setList(List<Boolean> list) {
		this.list = list;
	}

	public List<KPIPerson> getkPIPersons() {
		return kPIPersons;
	}

	public void setkPIPersons(List<KPIPerson> kPIPersons) {
		this.kPIPersons = kPIPersons;
	}

	public List<OrientationPerson> getOrientationPersons() {
		return orientationPersons;
	}

	public void setOrientationPersons(List<OrientationPerson> orientationPersons) {
		this.orientationPersons = orientationPersons;
	}

	public List<KPIPersonOfMonth> getKpiPersonOfMonths() {
		return kpiPersonOfMonths;
	}

	public void setKpiPersonOfMonths(List<KPIPersonOfMonth> kpiPersonOfMonths) {
		this.kpiPersonOfMonths = kpiPersonOfMonths;
	}

	public List<KPIPersonOfMonth> getKpiPersonOfMonthAdds() {
		return kpiPersonOfMonthAdds;
	}

	public void setKpiPersonOfMonthAdds(List<KPIPersonOfMonth> kpiPersonOfMonthAdds) {
		this.kpiPersonOfMonthAdds = kpiPersonOfMonthAdds;
	}

	public int getYearSearch() {
		return yearSearch;
	}

	public void setYearSearch(int yearSearch) {
		this.yearSearch = yearSearch;
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

	public boolean isSelect() {
		return select;
	}

	public void setSelect(boolean select) {
		this.select = select;
	}

	public int getTabindex() {
		return tabindex;
	}

	public void setTabindex(int tabindex) {
		this.tabindex = tabindex;
	}

	public List<BehaviourPerson> getBehaviourPersons() {
		return behaviourPersons;
	}

	public void setBehaviourPersons(List<BehaviourPerson> behaviourPersons) {
		this.behaviourPersons = behaviourPersons;
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

	public PositionJob getPositionJobSelect() {
		return positionJobSelect;
	}

	public void setPositionJobSelect(PositionJob positionJobSelect) {
		this.positionJobSelect = positionJobSelect;
	}

	public List<PositionJob> getPositionJobs() {
		return positionJobs;
	}

	public void setPositionJobs(List<PositionJob> positionJobs) {
		this.positionJobs = positionJobs;
	}

	public List<KPIDepOfMonth> getKpiDepOfMonths() {
		return kpiDepOfMonths;
	}

	public void setKpiDepOfMonths(List<KPIDepOfMonth> kpiDepOfMonths) {
		this.kpiDepOfMonths = kpiDepOfMonths;
	}

	public boolean isEmp() {
		return isEmp;
	}

	public void setEmp(boolean isEmp) {
		this.isEmp = isEmp;
	}

	public List<KPIPerson> getkPIPersonFilters() {
		return kPIPersonFilters;
	}

	public void setkPIPersonFilters(List<KPIPerson> kPIPersonFilters) {
		this.kPIPersonFilters = kPIPersonFilters;
	}

	public KPIPersonOfMonth getKpm() {
		return kpm;
	}

	public void setKpm(KPIPersonOfMonth kpm) {
		this.kpm = kpm;
	}

	public StreamedContent getStreamedContent() {
		return streamedContent;
	}

	public void setStreamedContent(StreamedContent streamedContent) {
		this.streamedContent = streamedContent;
	}

	public boolean isOrverideData() {
		return orverideData;
	}

	public void setOrverideData(boolean orverideData) {
		this.orverideData = orverideData;
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

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public List<Member> getMembers() {
		return members;
	}

	public void setMembers(List<Member> members) {
		this.members = members;
	}

	public int getMonthSearch() {
		return monthSearch;
	}

	public void setMonthSearch(int monthSearch) {
		this.monthSearch = monthSearch;
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

	public List<OrienInfoEmpl> getOrienInfoEmpls() {
		return orienInfoEmpls;
	}

	public void setOrienInfoEmpls(List<OrienInfoEmpl> orienInfoEmpls) {
		this.orienInfoEmpls = orienInfoEmpls;
	}

	// Thai
	public List<KPIPersonalPerformance> getListKPIPersonalByEmployee() {
		return listKPIPersonalByEmployee;
	}

	public void setListKPIPersonalByEmployee(List<KPIPersonalPerformance> listKPIPersonalByEmployee) {
		this.listKPIPersonalByEmployee = listKPIPersonalByEmployee;
	}

	public List<InfoPersonalPerformance> getListInfoPersonalPerformances() {
		return listInfoPersonalPerformances;
	}

	public void setListInfoPersonalPerformances(List<InfoPersonalPerformance> listInfoPersonalPerformances) {
		this.listInfoPersonalPerformances = listInfoPersonalPerformances;
	}
}
