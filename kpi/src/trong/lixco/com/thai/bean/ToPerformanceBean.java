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
import trong.lixco.com.ejb.thai.kpi.DepPerformanceService;
import trong.lixco.com.ejb.thai.kpi.KPIToPerformanceService;
import trong.lixco.com.ejb.thai.kpi.KPIToService;
import trong.lixco.com.jpa.entitykpi.FormulaKPI;
import trong.lixco.com.jpa.thai.KPIDepPerformanceJPA;
import trong.lixco.com.jpa.thai.KPIToPerformance;
import trong.lixco.com.thai.bean.entities.InfoToPerformance;
import trong.lixco.com.thai.bean.staticentity.MessageView;
import trong.lixco.com.util.Notify;

@Named
@ViewScoped
public class ToPerformanceBean extends AbstractBean<KPIToPerformance> {
	private static final long serialVersionUID = 1L;
	private Notify notify;

	private int year;

	private String codeDepart;

	private Member member;
	private DepartmentServicePublic departmentServicePublic;

	private boolean enablePerformance;

	@Inject
	private Logger logger;

	@Override
	protected Logger getLogger() {
		return logger;
	}

	// Thai
	private KPIToPerformance kpiToPerformance;

	@Inject
	private KPIToPerformanceService KPI_TO_PERFORMANCE_SERVICE;

	@Override
	public void initItem() {
		enablePerformance = false;
		kpiToPerformance = new KPIToPerformance();
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
			if (kpiToPerformance != null) {
				if (!"".equals(kpiToPerformance.getYear() != 0)) {
					if (kpiToPerformance.getId() == null) {
						if (allowSave(null)) {
							if (codeDepart != null) {
								kpiToPerformance = installSave(kpiToPerformance);
								kpiToPerformance.setCodeDepart(codeDepart);
								kpiToPerformance = KPI_TO_PERFORMANCE_SERVICE.create(kpiToPerformance);
								listToPerformance.add(0, kpiToPerformance);
								searchItem();// test
								writeLogInfo("Tạo mới " + kpiToPerformance.toString());
								notify.success();
							} else {
								notify.warning("Tài khoản không thuộc phòng ban nào.");
							}
						} else {
							kpiToPerformanceUpdate = new KPIToPerformance();
							notify.warningDetail();
						}
					} else {
						if (allowUpdate(null)) {
							kpiToPerformance = installUpdate(kpiToPerformance);
							kpiToPerformance = KPI_TO_PERFORMANCE_SERVICE.update(kpiToPerformance);
							int index = listToPerformance.indexOf(kpiToPerformance);
							listToPerformance.set(index, kpiToPerformance);
							searchItem();// test
							writeLogInfo("Cập nhật " + kpiToPerformance.toString());
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
		int year = kpiToPerformance.getYear();
		kpiToPerformance = new KPIToPerformance();
		kpiToPerformance.setYear(year);
	}

	public void showEdit() {
		this.kpiToPerformance = kpiToPerformanceUpdate;
		codeDepart = this.kpiToPerformance.getCodeDepart();
		if (kpiToPerformanceUpdate.isDisable()) {
			enablePerformance = false;
		} else {
			enablePerformance = true;
		}
	}

	public void handleEnable() {
		try {
			if (kpiToPerformanceUpdate.getId() == null) {
				return;
			}
			kpiToPerformanceUpdate.setDisable(false);
			KPIToPerformance k = KPI_TO_PERFORMANCE_SERVICE.update(kpiToPerformanceUpdate);
			if (k != null) {
				kpiToPerformanceUpdate = new KPIToPerformance();
				MessageView.INFO("Thành công");
			} else {
				MessageView.ERROR("Lỗi");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void handleDisable() {
		try {
			if (kpiToPerformanceUpdate.getId() == null) {
				return;
			}
			kpiToPerformanceUpdate.setDisable(true);
			KPIToPerformance k = KPI_TO_PERFORMANCE_SERVICE.update(kpiToPerformanceUpdate);
			if (k != null) {
				kpiToPerformanceUpdate = new KPIToPerformance();
				MessageView.INFO("Thành công");
			} else {
				MessageView.ERROR("Lỗi");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void delete() {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (kpiToPerformance.getId() != null) {
			if (allowDelete(null)) {
				boolean status = KPI_TO_PERFORMANCE_SERVICE.delete(kpiToPerformance);
				if (status) {
					listToPerformance.remove(kpiToPerformance);
					updateListInfo();
					writeLogInfo("Xoá " + kpiToPerformance.toString());
					reset();

					notify.success();
				} else {
					writeLogError("Lỗi khi xoá " + kpiToPerformance.toString());
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
		Map<String, List<KPIToPerformance>> datagroups1 = listToPerformance.stream()
				.collect(Collectors.groupingBy(p -> p.getCodeDepart(), Collectors.toList()));

		listInfoToPerformance = new ArrayList<InfoToPerformance>();
		for (String key : datagroups1.keySet()) {
			List<KPIToPerformance> invs = datagroups1.get(key);
			try {
				InfoToPerformance tgi = new InfoToPerformance();
				tgi.setNameDepart(departmentServicePublic.findByCode("code", key).getName());
				tgi.setListToPerformance(invs);
				listInfoToPerformance.add(tgi);
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
				int codeDepart = (int) Double.parseDouble(row.getCell(0).toString());
				String content = row.getCell(1).toString();
				int formulaKPIIdInt = (int) Double.parseDouble(row.getCell(2).toString());
				long formulaKPIIdLong = (long) formulaKPIIdInt;
				int year = (int) Double.parseDouble(row.getCell(3).toString());
				// Luu doi tuong xuong DB
				KPIToPerformance kpiToPerformanceCreate = new KPIToPerformance();
				kpiToPerformanceCreate.setCodeDepart(Integer.toString(codeDepart));
				kpiToPerformanceCreate.setContent(content);
				FormulaKPI formulaKPITemp = FORMULA_KPI_SERVICE.findById(formulaKPIIdLong);
				kpiToPerformanceCreate.setComputation(formulaKPITemp.getCode());
				kpiToPerformanceCreate.setFormulaKPI(formulaKPITemp);
				kpiToPerformanceCreate.setYear(year);
				kpiToPerformanceCreate.setCreatedDate(new Date());
				kpiToPerformanceCreate.setCreatedUser(member.getName());
				kpiToPerformanceCreate.setDisable(false);
				kpiToPerformanceCreate.setOldData(false);
				try {
					KPI_TO_PERFORMANCE_SERVICE.create(kpiToPerformanceCreate);
				} catch (Exception e) {
					e.printStackTrace();
				}

			} catch (Exception e) {
				e.printStackTrace();
				MessageView.ERROR("Lỗi");
			}
		}
		notice("Thành công");

	}

	// end import file excel
	public void fileDuLieuKPICaNhanMau() {
		try {
			PrimeFaces.current().executeScript("target='_blank';monitorDownload( showStatus , hideStatus)");
			String filename = "KPITo_dulieumau";
			HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance()
					.getExternalContext().getResponse();
			httpServletResponse.addHeader("Content-disposition", "attachment; filename=" + filename + ".xlsx");
			ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
			String file = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/maufile/KPITo_dulieumau.xlsx");
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
	List<InfoToPerformance> listInfoToPerformance;
	List<KPIToPerformance> listToPerformance;
	private KPIToPerformance kpiToPerformanceUpdate;

	public void searchItem() {
		if (getAccount().isAdmin())
			listToPerformance = KPI_TO_PERFORMANCE_SERVICE.find(year, null);
		else
			listToPerformance = KPI_TO_PERFORMANCE_SERVICE.find(year, member.getDepartment().getCode());

		Map<String, List<KPIToPerformance>> datagroups1 = listToPerformance.stream()
				.collect(Collectors.groupingBy(p -> p.getCodeDepart(), Collectors.toList()));

		listInfoToPerformance = new ArrayList<InfoToPerformance>();
		for (String key : datagroups1.keySet()) {
			List<KPIToPerformance> invs = datagroups1.get(key);
			try {
				InfoToPerformance tgi = new InfoToPerformance();
				tgi.setNameDepart(departmentServicePublic.findByCode("code", key).getName());
				tgi.setListToPerformance(invs);
				listInfoToPerformance.add(tgi);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

	}

	private List<FormulaKPI> formulaKPIs;
	private FormulaKPI formulaKPISelect;
	private KPIToPerformance kpy;
	@Inject
	private FormulaKPIService formulaKPIService;

	public void showListFormula(KPIToPerformance param) {
		notify = new Notify(FacesContext.getCurrentInstance());
		formulaKPIs = formulaKPIService.findAll();
		formulaKPISelect = param.getFormulaKPI();
		kpy = param;
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('dialogFormula1').show();");
	}

	@Inject
	private KPIToService KPI_TO_SERVICE;

	public void updateFormula() {
		notify = new Notify(FacesContext.getCurrentInstance());
		try {
			if (formulaKPISelect == null) {
				notify.warning("Chưa chọn công thức!");
			} else {
				for (int i = 0; i < listToPerformance.size(); i++) {
					KPIToPerformance kp = listToPerformance.get(i);
					if (listToPerformance.get(i).getIndex() == kpy.getIndex()) {
						kpy.setFormulaKPI(formulaKPISelect);
						kpy.setComputation(formulaKPISelect.getCode());
						listToPerformance.set(i, kpy);
						notify.success();
						break;
					}
				}
			}

		} catch (Exception e) {
		}
	}

	// End Thai

	public List<InfoToPerformance> getListInfoToPerformance() {
		return listInfoToPerformance;
	}

	public void setListInfoToPerformance(List<InfoToPerformance> listInfoToPerformance) {
		this.listInfoToPerformance = listInfoToPerformance;
	}

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

	public KPIToPerformance getKpiToPerformanceUpdate() {
		return kpiToPerformanceUpdate;
	}

	public void setKpiToPerformanceUpdate(KPIToPerformance kpiToPerformanceUpdate) {
		this.kpiToPerformanceUpdate = kpiToPerformanceUpdate;
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

	public KPIToPerformance getKpy() {
		return kpy;
	}

	public void setKpy(KPIToPerformance kpy) {
		this.kpy = kpy;
	}

	public FormulaKPIService getFormulaKPIService() {
		return formulaKPIService;
	}

	public void setFormulaKPIService(FormulaKPIService formulaKPIService) {
		this.formulaKPIService = formulaKPIService;
	}

	public boolean isEnablePerformance() {
		return enablePerformance;
	}

	public void setEnablePerformance(boolean enablePerformance) {
		this.enablePerformance = enablePerformance;
	}

	public KPIToPerformance getKpiToPerformance() {
		return kpiToPerformance;
	}

	public void setKpiToPerformance(KPIToPerformance kpiToPerformance) {
		this.kpiToPerformance = kpiToPerformance;
	}
}
