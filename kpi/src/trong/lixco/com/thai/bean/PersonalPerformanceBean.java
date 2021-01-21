package trong.lixco.com.thai.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
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

import trong.lixco.com.account.servicepublics.Department;
import trong.lixco.com.account.servicepublics.DepartmentServicePublic;
import trong.lixco.com.account.servicepublics.DepartmentServicePublicProxy;
import trong.lixco.com.account.servicepublics.Member;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.ejb.servicekpi.FormulaKPIService;
import trong.lixco.com.ejb.servicekpi.OrientationPersonService;
import trong.lixco.com.ejb.servicekpi.PositionJobService;
import trong.lixco.com.ejb.thai.kpi.PersonalPerformanceService;
import trong.lixco.com.ejb.thai.kpi.PositionDontKPIService;
import trong.lixco.com.jpa.entitykpi.FormulaKPI;
import trong.lixco.com.jpa.entitykpi.PositionJob;
import trong.lixco.com.jpa.thai.KPIPersonalPerformance;
import trong.lixco.com.jpa.thai.PositionDontKPI;
import trong.lixco.com.thai.bean.entities.InfoPersonalPerformance;
import trong.lixco.com.thai.bean.staticentity.MessageView;
import trong.lixco.com.util.Notify;

@Named
@ViewScoped
public class PersonalPerformanceBean extends AbstractBean<KPIPersonalPerformance> {
	private static final long serialVersionUID = 1L;
	private Notify notify;

	private int year;

	private String codeDepart;

	private Member member;
	private DepartmentServicePublic departmentServicePublic;// use
	private trong.lixco.com.account.servicepublics.Department[] allDepartmentTemp;
	private List<trong.lixco.com.account.servicepublics.Department> allDepartment;
	private trong.lixco.com.account.servicepublics.Department departmentSelected;
	private List<InfoPersonalPerformance> listInfoPersonalPerformances;
	private KPIPersonalPerformance kpiPersonalPerformance;
	private KPIPersonalPerformance kpiPersonalPerformanceUpdated;
	private PositionJob positionJobEdit;
	private String namePositionJob;
	private boolean enablePerformance;
	// private List<OrientationPerson> allPersonalPerformance;

	// Cong thuc tinh
	private List<FormulaKPI> formulaKPIs;
	private FormulaKPI formulaKPISelect;
	private KPIPersonalPerformance kpy;
	@Inject
	private FormulaKPIService FORMULAKPI_SERVICE;
	// End cong thuc tinh
	// Service
	@Inject
	private OrientationPersonService ORIENTATION_PERSION_SERVICE;

	@Inject
	private PersonalPerformanceService PERSONAL_PERFORMANCE_SERVICE;
	private List<KPIPersonalPerformance> allPersonalPerformance;

	@Inject
	private PositionJobService POSITION_JOB_SERVICE;
	@Inject
	private PositionDontKPIService POSITION_DONT_KPI_SERVICE;

	@Inject
	private Logger logger;

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Override
	public void initItem() {
		enablePerformance = false;
		kpiPersonalPerformance = new KPIPersonalPerformance();
		kpiPersonalPerformanceUpdated = new KPIPersonalPerformance();
		year = new DateTime().getYear();
		departmentSelected = new Department();
		try {
			departmentServicePublic = new DepartmentServicePublicProxy();// use
			allDepartmentTemp = departmentServicePublic.findAll();
			allDepartment = Arrays.asList(allDepartmentTemp);

		} catch (Exception e) {
			e.printStackTrace();
		}
		member = getAccount().getMember();
		// codeDepart = member.getDepartment().getCode();
		// searchItem();
	}

	public void searchItem() {
		if (getAccount().isAdmin()) {
			List<PositionJob> positionJobByDepartment = POSITION_JOB_SERVICE
					.findByDepartmentP(this.departmentSelected.getCode());
			// build list code position job
			List<String> codeJobsString = new ArrayList<>();
			for (int i = 0; i < positionJobByDepartment.size(); i++) {
				codeJobsString.add(positionJobByDepartment.get(i).getCode());
			}
			// get all list position job in list postionJob code
			allPersonalPerformance = PERSONAL_PERFORMANCE_SERVICE.find(codeJobsString);
		} else {
			allPersonalPerformance = PERSONAL_PERFORMANCE_SERVICE.findSearch("PC");
		}

		Map<String, List<KPIPersonalPerformance>> datagroups11 = allPersonalPerformance.stream()
				.collect(Collectors.groupingBy(a -> a.getCodePJob(), Collectors.toList()));

		listInfoPersonalPerformances = new ArrayList<>();
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
		this.departmentSelected = new Department();
	}

	// public String nameDepart() {
	// try {
	// return departmentServicePublic.findByCode("code", codeDepart).getName();
	// } catch (Exception e) {
	// return "";
	// }
	// }
	//
	public void createOrUpdate() {
		notify = new Notify(FacesContext.getCurrentInstance());
		try {
			// Check kpi da duoc khoi tao hay chua
			if (kpiPersonalPerformance != null) {
				// check kpi da co duoi DB chua
				if (kpiPersonalPerformance.getId() == null) {
					if (allowSave(null)) {
						kpiPersonalPerformance = installSave(kpiPersonalPerformance);
						kpiPersonalPerformance.setCodePJob(positionJobEdit.getCode());
						kpiPersonalPerformance = PERSONAL_PERFORMANCE_SERVICE.create(kpiPersonalPerformance);
						allPersonalPerformance.add(0, kpiPersonalPerformance);
						// cap nhat list info
						updateListInfo();
						writeLogInfo("Tạo mới " + kpiPersonalPerformance.toString());
						notify.success();
					} else {
						kpiPersonalPerformanceUpdated = new KPIPersonalPerformance();
						notify.warningDetail();
					}
				} else {
					if (allowUpdate(null)) {
						kpiPersonalPerformance = installUpdate(kpiPersonalPerformance);
						kpiPersonalPerformance = PERSONAL_PERFORMANCE_SERVICE.update(kpiPersonalPerformance);
						int index = allPersonalPerformance.indexOf(kpiPersonalPerformance);
						allPersonalPerformance.set(index, kpiPersonalPerformance);
						writeLogInfo("Cập nhật " + kpiPersonalPerformance.toString());
						notify.success();
					} else {
						notify.warningDetail();
					}
				}
				reset();
			}
		} catch (Exception e) {
			writeLogError(e.getLocalizedMessage());
			notify.warning("Mã đã tồn tại!");
		}
	}

	public void reset() {
		kpiPersonalPerformance = new KPIPersonalPerformance();
	}

	public void showEdit() {
		this.kpiPersonalPerformance = kpiPersonalPerformanceUpdated;
		positionJobEdit = POSITION_JOB_SERVICE.findByCode(kpiPersonalPerformance.getCodePJob());
		namePositionJob = positionJobEdit.getName();
		// codeDepart = this.kpi.getCodeDepart();
		if (kpiPersonalPerformanceUpdated.isDisable()) {
			enablePerformance = false;
		} else {
			enablePerformance = true;
		}
	}

	public void handleEnable() {
		try {
			if (kpiPersonalPerformanceUpdated.getId() == null) {
				return;
			}
			kpiPersonalPerformanceUpdated.setDisable(false);
			KPIPersonalPerformance k = PERSONAL_PERFORMANCE_SERVICE.update(kpiPersonalPerformanceUpdated);
			if (k != null) {
				kpiPersonalPerformanceUpdated = new KPIPersonalPerformance();
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
			if (kpiPersonalPerformanceUpdated.getId() == null) {
				return;
			}
			kpiPersonalPerformanceUpdated.setDisable(true);
			KPIPersonalPerformance k = PERSONAL_PERFORMANCE_SERVICE.update(kpiPersonalPerformanceUpdated);
			if (k != null) {
				kpiPersonalPerformanceUpdated = new KPIPersonalPerformance();
				MessageView.INFO("Thành công");
			} else {
				MessageView.ERROR("Lỗi");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showListFormula(KPIPersonalPerformance param) {
		notify = new Notify(FacesContext.getCurrentInstance());
		formulaKPIs = FORMULAKPI_SERVICE.findAll();
		formulaKPISelect = param.getFormulaKPI();
		kpy = param;
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('dialogFormula11').show();");
	}

	public void updateFormula() {
		notify = new Notify(FacesContext.getCurrentInstance());
		try {
			if (formulaKPISelect == null) {
				notify.warning("Chưa chọn công thức!");
			} else {
				for (int i = 0; i < allPersonalPerformance.size(); i++) {
					KPIPersonalPerformance kp = allPersonalPerformance.get(i);
					if (allPersonalPerformance.get(i).getIndex() == kpy.getIndex()) {
						kpy.setFormulaKPI(formulaKPISelect);
						kpy.setComputation(formulaKPISelect.getCode());
						allPersonalPerformance.set(i, kpy);
						notify.success();
						break;
					}
				}
			}

		} catch (Exception e) {
		}
	}

	// update list info personal performance
	public void updateListInfo() {
		Map<String, List<KPIPersonalPerformance>> datagroups11 = allPersonalPerformance.stream()
				.collect(Collectors.groupingBy(a -> a.getCodePJob(), Collectors.toList()));

		listInfoPersonalPerformances = new ArrayList<>();
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
	}

	public void delete() {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (kpiPersonalPerformance.getId() != null) {
			if (allowDelete(null)) {
				boolean status = PERSONAL_PERFORMANCE_SERVICE.delete(kpiPersonalPerformance);
				if (status) {
					allPersonalPerformance.remove(kpiPersonalPerformance);
					// Cap nhat list info
					updateListInfo();
					writeLogInfo("Xoá " + kpiPersonalPerformance.toString());
					reset();
					notify.success();
				} else {
					writeLogError("Lỗi khi xoá " + kpiPersonalPerformance.toString());
					notify.error();
				}
			} else {
				notify.warningDetail();
			}
		} else {
			notify.warning("Chưa chọn trong danh sách!");
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
		boolean isError = false;

		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			row = sheet.getRow(i);
			try {
				int codePJob = (int) Double.parseDouble(row.getCell(0).toString());
				String content = row.getCell(1).toString();
				int formulaKPIIdInt = 0;
				if (row.getCell(2).toString() != null && !row.getCell(2).toString().isEmpty()) {
					formulaKPIIdInt = (int) Double.parseDouble(row.getCell(2).toString());
				}
				// get diem tru
				double diemtru = 0;
				if (row.getCell(3) != null && row.getCell(3).toString() != null
						&& !row.getCell(3).toString().isEmpty()) {
					diemtru = Double.parseDouble(row.getCell(3).toString());
				}
				long formulaKPIIdLong = (long) formulaKPIIdInt;
				// //Luu doi tuong xuong DB
				KPIPersonalPerformance kpiPersonalPerformanceCreate = new KPIPersonalPerformance();
				kpiPersonalPerformanceCreate.setCodePJob(Integer.toString(codePJob));
				kpiPersonalPerformanceCreate.setContent(content);
				FormulaKPI formulaKPITemp = FORMULA_KPI_SERVICE.findById(formulaKPIIdLong);
				if (formulaKPITemp != null) {
					kpiPersonalPerformanceCreate.setComputation(formulaKPITemp.getCode());
					kpiPersonalPerformanceCreate.setFormulaKPI(formulaKPITemp);
				}
				kpiPersonalPerformanceCreate.setCreatedDate(new Date());
				kpiPersonalPerformanceCreate.setCreatedUser(member.getName());
				kpiPersonalPerformanceCreate.setDisable(false);
				kpiPersonalPerformanceCreate.setOldData(false);
				kpiPersonalPerformanceCreate.setHeader(false);
				kpiPersonalPerformanceCreate.setMinuspoint(diemtru);
				try {
					PERSONAL_PERFORMANCE_SERVICE.create(kpiPersonalPerformanceCreate);
				} catch (Exception e) {
					e.printStackTrace();
				}

			} catch (Exception e) {
				isError = true;
				e.printStackTrace();
			}
		}
		if (isError) {
			notify.error("Lỗi!");
		} else {
			notice("Thành công");
		}
	}

	// end import file excel
	public void fileDuLieuKPICaNhanMau() {
		try {
			PrimeFaces.current().executeScript("target='_blank';monitorDownload( showStatus , hideStatus)");
			String filename = "KPICaNhan_dulieumau";
			HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance()
					.getExternalContext().getResponse();
			httpServletResponse.addHeader("Content-disposition", "attachment; filename=" + filename + ".xlsx");
			ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
			String file = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/maufile/kpinhanvien.xlsx");
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

	//
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

	public List<trong.lixco.com.account.servicepublics.Department> getAllDepartment() {
		return allDepartment;
	}

	public void setAllDepartment(List<trong.lixco.com.account.servicepublics.Department> allDepartment) {
		this.allDepartment = allDepartment;
	}

	public trong.lixco.com.account.servicepublics.Department getDepartmentSelected() {
		return departmentSelected;
	}

	public void setDepartmentSelected(trong.lixco.com.account.servicepublics.Department departmentSelected) {
		this.departmentSelected = departmentSelected;
	}

	public List<InfoPersonalPerformance> getListInfoPersonalPerformances() {
		return listInfoPersonalPerformances;
	}

	public void setListInfoPersonalPerformances(List<InfoPersonalPerformance> listInfoPersonalPerformances) {
		this.listInfoPersonalPerformances = listInfoPersonalPerformances;
	}

	public KPIPersonalPerformance getKpiPersonalPerformance() {
		return kpiPersonalPerformance;
	}

	public void setKpiPersonalPerformance(KPIPersonalPerformance kpiPersonalPerformance) {
		this.kpiPersonalPerformance = kpiPersonalPerformance;
	}

	public KPIPersonalPerformance getKpiPersonalPerformanceUpdated() {
		return kpiPersonalPerformanceUpdated;
	}

	public void setKpiPersonalPerformanceUpdated(KPIPersonalPerformance kpiPersonalPerformanceUpdated) {
		this.kpiPersonalPerformanceUpdated = kpiPersonalPerformanceUpdated;
	}

	public String getNamePositionJob() {
		return namePositionJob;
	}

	public void setNamePositionJob(String namePositionJob) {
		this.namePositionJob = namePositionJob;
	}

	public FormulaKPI getFormulaKPISelect() {
		return formulaKPISelect;
	}

	public void setFormulaKPISelect(FormulaKPI formulaKPISelect) {
		this.formulaKPISelect = formulaKPISelect;
	}

	public List<FormulaKPI> getFormulaKPIs() {
		return formulaKPIs;
	}

	public void setFormulaKPIs(List<FormulaKPI> formulaKPIs) {
		this.formulaKPIs = formulaKPIs;
	}

	public boolean isEnablePerformance() {
		return enablePerformance;
	}

	public void setEnablePerformance(boolean enablePerformance) {
		this.enablePerformance = enablePerformance;
	}

	// su dung de nap du lieu mau
	public void handleFileUploadTest(FileUploadEvent event) throws IOException {

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
				echoAsCSVFileTest(wb.getSheetAt(i));
			}
		} catch (Exception ex) {
		} finally {
			try {
				inp.close();
			} catch (IOException ex) {
			}
		}
	}

	public void echoAsCSVFileTest(Sheet sheet) {
		Row row = null;
		boolean isError = false;

		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			row = sheet.getRow(i);
			try {
				int codePJob = (int) Double.parseDouble(row.getCell(0).toString());
				// //Luu doi tuong xuong DB
				PositionDontKPI cpCreate = new PositionDontKPI();
				cpCreate.setPosition_code(Integer.toString(codePJob));
				cpCreate.setCreatedDate(new Date());
				try {
					POSITION_DONT_KPI_SERVICE.create(cpCreate);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				isError = true;
				e.printStackTrace();
			}
		}
		if (isError) {
			MessageView.ERROR("Lỗi!");
		} else {
			MessageView.INFO("Thành công");
		}
	}
}
