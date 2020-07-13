package trong.lixco.com.thai.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.jboss.logging.Logger;
import org.joda.time.DateTime;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.PrimeFaces;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

import com.ibm.icu.text.SimpleDateFormat;

import trong.lixco.com.account.servicepublics.DepartmentServicePublic;
import trong.lixco.com.account.servicepublics.DepartmentServicePublicProxy;
import trong.lixco.com.account.servicepublics.Member;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.ejb.servicekpi.FormulaKPIService;
import trong.lixco.com.ejb.servicekpi.KPIDepMonthService;
import trong.lixco.com.ejb.thai.kpi.DepPerformanceService;
import trong.lixco.com.jpa.entitykpi.FormulaKPI;
import trong.lixco.com.jpa.thai.KPIDepPerformanceJPA;
import trong.lixco.com.jpa.thai.KPIPersonalPerformance;
import trong.lixco.com.thai.bean.entities.InfoDepPerformance;
import trong.lixco.com.thai.bean.entities.InfoPersonalPerformance;
import trong.lixco.com.util.Notify;

@Named
@ViewScoped
public class DepartPerformanceBean extends AbstractBean<KPIDepPerformanceJPA> {
	private static final long serialVersionUID = 1L;
	private Notify notify;

	private int year;

	private String codeDepart;

	private Member member;
	private DepartmentServicePublic departmentServicePublic;

	@Inject
	private Logger logger;

	@Override
	protected Logger getLogger() {
		return logger;
	}

	// Thai
	KPIDepPerformanceJPA kpiDepPerformance;

	@Override
	public void initItem() {
		kpiDepPerformance = new KPIDepPerformanceJPA();
		year = new DateTime().getYear();
		try {
			departmentServicePublic = new DepartmentServicePublicProxy();
		} catch (Exception e) {
			e.printStackTrace();
		}
		member = getAccount().getMember();
		codeDepart = member.getDepartment().getCode();
		searchItem();
	}

	public String nameDepart() {
		try {
			return departmentServicePublic.findByCode("code", codeDepart).getName();
		} catch (Exception e) {
			return "";
		}
	}

	public void createOrUpdate() {
		notify = new Notify(FacesContext.getCurrentInstance());
		try {
			if (kpiDepPerformance != null) {
				if (!"".equals(kpiDepPerformance.getYear() != 0)) {
					if (kpiDepPerformance.getId() == null) {
						if (allowSave(null)) {
							if (codeDepart != null) {
								kpiDepPerformance = installSave(kpiDepPerformance);
								kpiDepPerformance.setCodeDepart(codeDepart);
								kpiDepPerformance = DEPARTMENT_PERFORMANCE_SERVICE.create(kpiDepPerformance);
								listDepartPerformance.add(0, kpiDepPerformance);
								searchItem();// test
								writeLogInfo("Tạo mới " + kpiDepPerformance.toString());
								notify.success();
							} else {
								notify.warning("Tài khoản không thuộc phòng ban nào.");
							}
						} else {
							kpiDepPerformanceUpdate = new KPIDepPerformanceJPA();
							notify.warningDetail();
						}
					} else {
						if (allowUpdate(null)) {
							kpiDepPerformance = installUpdate(kpiDepPerformance);
							kpiDepPerformance = DEPARTMENT_PERFORMANCE_SERVICE.update(kpiDepPerformance);
							int index = listDepartPerformance.indexOf(kpiDepPerformance);
							listDepartPerformance.set(index, kpiDepPerformance);
							searchItem();// test
							writeLogInfo("Cập nhật " + kpiDepPerformance.toString());
							notify.success();
						} else {
							notify.warningDetail();
						}
					}
					reset();
				} else {
					notify.warning("Điền đầy đủ thông tin!");
				}
			}
		} catch (Exception e) {
			writeLogError(e.getLocalizedMessage());
			notify.warning("Mã đã tồn tại!");
		}
	}

	public void reset() {
		int year = kpiDepPerformance.getYear();
		kpiDepPerformance = new KPIDepPerformanceJPA();
		kpiDepPerformance.setYear(year);
	}

	public void showEdit() {
		this.kpiDepPerformance = kpiDepPerformanceUpdate;
		codeDepart = this.kpiDepPerformance.getCodeDepart();
	}

	public void delete() {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (kpiDepPerformance.getId() != null) {
			if (allowDelete(null)) {
				boolean status = DEPARTMENT_PERFORMANCE_SERVICE.delete(kpiDepPerformance);
				if (status) {
					listDepartPerformance.remove(kpiDepPerformance);
					updateListInfo();
					writeLogInfo("Xoá " + kpiDepPerformance.toString());
					reset();

					notify.success();
				} else {
					writeLogError("Lỗi khi xoá " + kpiDepPerformance.toString());
					notify.error();
				}
			} else {
				notify.warningDetail();
			}
		} else {
			notify.warning("Chưa chọn trong danh sách!");
		}
	}

	// update list info personal performance
	public void updateListInfo() {
		Map<String, List<KPIDepPerformanceJPA>> datagroups1 = listDepartPerformance.stream()
				.collect(Collectors.groupingBy(p -> p.getCodeDepart(), Collectors.toList()));

		listInfoDepartPerformance = new ArrayList<InfoDepPerformance>();
		for (String key : datagroups1.keySet()) {
			List<KPIDepPerformanceJPA> invs = datagroups1.get(key);
			try {
				InfoDepPerformance tgi = new InfoDepPerformance();
				tgi.setNameDepart(departmentServicePublic.findByCode("code", key).getName());
				tgi.setListDepPerformance(invs);
				listInfoDepartPerformance.add(tgi);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	// import file excel
	public void handleFileUpload(FileUploadEvent event) throws IOException {

		// context.execute("PF('dlg1').show();");
		long size = event.getFile().getSize();

		String filename = FilenameUtils.getBaseName(event.getFile().getFileName());

		String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/temp/");
		File checkFile = new File(path);
		if (!checkFile.exists()) {
			checkFile.mkdirs();
		}

		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
		String name = filename + fmt.format(new Date())
				+ event.getFile().getFileName().substring(event.getFile().getFileName().lastIndexOf('.'));
		File file = new File(path + "/" + name);

		InputStream is = event.getFile().getInputstream();
		OutputStream out = new FileOutputStream(file);
		byte buf[] = new byte[(int) size];
		int len;
		while ((len = is.read(buf)) > 0)
			out.write(buf, 0, len);
		is.close();
		out.close();

		InputStream inp = null;
		try {
			inp = new FileInputStream(file.getAbsolutePath());
			Workbook wb = WorkbookFactory.create(inp);

			for (int i = 0; i < wb.getNumberOfSheets(); i++) {
				System.out.println(wb.getSheetAt(i).getSheetName());
				echoAsCSVFile(wb.getSheetAt(i));
			}
		} catch (Exception ex) {
		} finally {
			try {
				inp.close();
			} catch (IOException ex) {
			}
		}
	}

	@Inject
	private FormulaKPIService FORMULA_KPI_SERVICE;
	public void echoAsCSVFile(Sheet sheet) {
		Row row = null;
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			row = sheet.getRow(i);
			try {
				int codeDepart = (int)Double.parseDouble(row.getCell(0).toString());
				String content = row.getCell(1).toString();
				int formulaKPIIdInt = (int)Double.parseDouble(row.getCell(2).toString());
				long formulaKPIIdLong = (long)formulaKPIIdInt;
				int year = (int)Double.parseDouble(row.getCell(3).toString());
				//Luu doi tuong xuong DB
				KPIDepPerformanceJPA kpiDepPerformanceCreate = new KPIDepPerformanceJPA();
				kpiDepPerformanceCreate.setCodeDepart(Integer.toString(codeDepart));
				kpiDepPerformanceCreate.setContent(content);
				FormulaKPI formulaKPITemp = FORMULA_KPI_SERVICE.findById(formulaKPIIdLong);
				kpiDepPerformanceCreate.setComputation(formulaKPITemp.getCode());
				kpiDepPerformanceCreate.setFormulaKPI(formulaKPITemp);
				kpiDepPerformanceCreate.setYear(year);
				kpiDepPerformanceCreate.setCreatedDate(new Date());
				kpiDepPerformanceCreate.setCreatedUser(member.getName());
				kpiDepPerformanceCreate.setDisable(false);
				kpiDepPerformanceCreate.setOldData(false);
				try {
					DEPARTMENT_PERFORMANCE_SERVICE.create(kpiDepPerformanceCreate);
				} catch (Exception e) {
					e.printStackTrace();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		notice("Thành công");

	}
	// end import file excel
	public void fileDuLieuKPICaNhanMau() {
		try {
			PrimeFaces.current().executeScript("target='_blank';monitorDownload( showStatus , hideStatus)");
			String filename = "KPIPhong_dulieumau";
			HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance()
					.getExternalContext().getResponse();
			httpServletResponse.addHeader("Content-disposition", "attachment; filename=" + filename + ".xlsx");
			ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
			String file = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/maufile/kpiphong.xlsx");
			InputStream inputStream = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			int len;
			while ((len = inputStream.read(buffer)) != -1) {
				servletOutputStream.write(buffer, 0, len);
			}
			FacesContext.getCurrentInstance().responseComplete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// Thai
	@Inject
	DepPerformanceService DEPARTMENT_PERFORMANCE_SERVICE;
	List<InfoDepPerformance> listInfoDepartPerformance;
	List<KPIDepPerformanceJPA> listDepartPerformance;
	private KPIDepPerformanceJPA kpiDepPerformanceUpdate;

	public void searchItem() {
		if (getAccount().isAdmin())
			listDepartPerformance = DEPARTMENT_PERFORMANCE_SERVICE.find(year, null);
		else
			listDepartPerformance = DEPARTMENT_PERFORMANCE_SERVICE.find(year, member.getDepartment().getCode());

		Map<String, List<KPIDepPerformanceJPA>> datagroups1 = listDepartPerformance.stream()
				.collect(Collectors.groupingBy(p -> p.getCodeDepart(), Collectors.toList()));

		listInfoDepartPerformance = new ArrayList<InfoDepPerformance>();
		for (String key : datagroups1.keySet()) {
			List<KPIDepPerformanceJPA> invs = datagroups1.get(key);
			try {
				InfoDepPerformance tgi = new InfoDepPerformance();
				tgi.setNameDepart(departmentServicePublic.findByCode("code", key).getName());
				tgi.setListDepPerformance(invs);
				listInfoDepartPerformance.add(tgi);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

	}

	private List<FormulaKPI> formulaKPIs;
	private FormulaKPI formulaKPISelect;
	private KPIDepPerformanceJPA kpy;
	@Inject
	private FormulaKPIService formulaKPIService;

	public void showListFormula(KPIDepPerformanceJPA param) {
		notify = new Notify(FacesContext.getCurrentInstance());
		formulaKPIs = formulaKPIService.findAll();
		formulaKPISelect = param.getFormulaKPI();
		kpy = param;
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('dialogFormula1').show();");
	}

	@Inject
	private KPIDepMonthService kpiDepMonthService;

	public void updateFormula() {
		notify = new Notify(FacesContext.getCurrentInstance());
		try {
			if (formulaKPISelect == null) {
				notify.warning("Chưa chọn công thức!");
			} else {
				for (int i = 0; i < listDepartPerformance.size(); i++) {
					KPIDepPerformanceJPA kp = listDepartPerformance.get(i);
					if (listDepartPerformance.get(i).getIndex() == kpy.getIndex()) {
						kpy.setFormulaKPI(formulaKPISelect);
						kpy.setComputation(formulaKPISelect.getCode());
						listDepartPerformance.set(i, kpy);
						notify.success();
						break;
					}
				}
			}

		} catch (Exception e) {
		}
	}

	public List<InfoDepPerformance> getListInfoDepartPerformance() {
		return listInfoDepartPerformance;
	}

	public void setListInfoDepartPerformance(List<InfoDepPerformance> listInfoDepartPerformance) {
		this.listInfoDepartPerformance = listInfoDepartPerformance;
	}

	public List<KPIDepPerformanceJPA> getListDepartPerformance() {
		return listDepartPerformance;
	}

	public void setListDepartPerformance(List<KPIDepPerformanceJPA> listDepartPerformance) {
		this.listDepartPerformance = listDepartPerformance;
	}

	// End Thai

	// End Thai

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
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

	public KPIDepPerformanceJPA getKpiDepPerformanceUpdate() {
		return kpiDepPerformanceUpdate;
	}

	public void setKpiDepPerformanceUpdate(KPIDepPerformanceJPA kpiDepPerformanceUpdate) {
		this.kpiDepPerformanceUpdate = kpiDepPerformanceUpdate;
	}

	public KPIDepPerformanceJPA getKpiDepPerformance() {
		return kpiDepPerformance;
	}

	public void setKpiDepPerformance(KPIDepPerformanceJPA kpiDepPerformance) {
		this.kpiDepPerformance = kpiDepPerformance;
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

	public KPIDepPerformanceJPA getKpy() {
		return kpy;
	}

	public void setKpy(KPIDepPerformanceJPA kpy) {
		this.kpy = kpy;
	}

	public FormulaKPIService getFormulaKPIService() {
		return formulaKPIService;
	}

	public void setFormulaKPIService(FormulaKPIService formulaKPIService) {
		this.formulaKPIService = formulaKPIService;
	}

}