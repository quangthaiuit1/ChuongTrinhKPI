package trong.lixco.com.thai.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.jboss.logging.Logger;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;

import com.ibm.icu.text.SimpleDateFormat;

import trong.lixco.com.account.servicepublics.Account;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.ejb.thai.kpi.BehaviourPersonOtherService;
import trong.lixco.com.jpa.entitykpi.FormulaKPI;
import trong.lixco.com.jpa.thai.BehaviourPersonOther;
import trong.lixco.com.jpa.thai.KPIPersonalPerformance;
import trong.lixco.com.util.Notify;

@ManagedBean
@ViewScoped
public class BehaviourPersonOtherBean extends AbstractBean<BehaviourPersonOther> {

	private static final long serialVersionUID = 1L;

	private List<BehaviourPersonOther> allBehaviourOther;
	private BehaviourPersonOther behaviourSelected;
	private BehaviourPersonOther behaviourNew;
	private Account member;
	private Notify notify;

	@Inject
	private BehaviourPersonOtherService BEHAVIOUR_PERSON_OTHER_SERVICE;

	@Override
	protected void initItem() {
		notify = new Notify(FacesContext.getCurrentInstance());
		allBehaviourOther = BEHAVIOUR_PERSON_OTHER_SERVICE.findAll();
		allBehaviourOther.sort(Comparator.comparing(BehaviourPersonOther::getCode));
		if (allBehaviourOther == null || allBehaviourOther.size() == 0) {
			allBehaviourOther = new ArrayList<>();
		}
		behaviourSelected = new BehaviourPersonOther();
		behaviourNew = new BehaviourPersonOther();
		member = getAccount();
	}

	public void deleteItem() {
		try {
			if (behaviourSelected.getId() != null) {
				boolean success = BEHAVIOUR_PERSON_OTHER_SERVICE.delete(behaviourSelected);
				if (success) {
					allBehaviourOther = BEHAVIOUR_PERSON_OTHER_SERVICE.findAll();
					allBehaviourOther.sort(Comparator.comparing(BehaviourPersonOther::getCode));
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, "Thông báo", "Thành công"));
				} else {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, "Thông báo", "Lỗi"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveOrUpdate() {
		try {
			if (behaviourNew.getId() != null) {
				BehaviourPersonOther b = BEHAVIOUR_PERSON_OTHER_SERVICE.update(behaviourNew);
				if (b != null) {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, "Thông báo", "Thành công"));
				} else {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, "Thông báo", "Lỗi"));
				}
			} else {
				if (StringUtils.isNotEmpty(behaviourNew.getCode()) && StringUtils.isNotEmpty(behaviourNew.getContent())
						&& behaviourNew.getMinusPoint() != 0) {
					BehaviourPersonOther c = BEHAVIOUR_PERSON_OTHER_SERVICE.create(behaviourNew);
					if (c != null) {
						allBehaviourOther = BEHAVIOUR_PERSON_OTHER_SERVICE.findAll();
						allBehaviourOther.sort(Comparator.comparing(BehaviourPersonOther::getCode));
						FacesContext.getCurrentInstance().addMessage(null,
								new FacesMessage(FacesMessage.SEVERITY_INFO, "Thông báo", "Thành công"));
					} else {
						FacesContext.getCurrentInstance().addMessage(null,
								new FacesMessage(FacesMessage.SEVERITY_ERROR, "Thông báo", "Lỗi"));
					}
				} else {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
							"Thông báo", "Vui lòng điền đầy đủ thông tin"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void behaviourSelected() {
		behaviourNew = behaviourSelected;
	}

	public void reset() {
		behaviourNew = new BehaviourPersonOther();
	}

	public void fileDuLieuThaiDoHanhViMau() {
		try {
			PrimeFaces.current().executeScript("target='_blank';monitorDownload( showStatus , hideStatus)");
			String filename = "ThaiDo_HV_dulieumau";
			HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance()
					.getExternalContext().getResponse();
			httpServletResponse.addHeader("Content-disposition", "attachment; filename=" + filename + ".xlsx");
			ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
			String file = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/maufile/thaidohanhvinhomkhac.xlsx");
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

	public void echoAsCSVFile(Sheet sheet) {
		Row row = null;
		boolean isError = false;

		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			row = sheet.getRow(i);
			try {
				String code = row.getCell(0).toString();
				String content = row.getCell(1).toString();
				int isHeader = 0;
				if (row.getCell(2).toString() != null && !row.getCell(2).toString().isEmpty()) {
					isHeader = (int) Double.parseDouble(row.getCell(2).toString());
				}
				// get diem tru
				double diemtru = 0;
				if (row.getCell(3).toString() != null && !row.getCell(3).toString().isEmpty()) {
					diemtru = Double.parseDouble(row.getCell(3).toString());
				}
				// Luu doi tuong xuong DB
				BehaviourPersonOther behaviourPersonalOther = new BehaviourPersonOther();
				behaviourPersonalOther.setCode(code);
				behaviourPersonalOther.setContent(content);
				if (isHeader == 0) {
					behaviourPersonalOther.setHeader(false);
				}
				if (isHeader == 1) {
					behaviourPersonalOther.setHeader(true);
				}
				behaviourPersonalOther.setMinusPoint((int) diemtru);
				if (member != null) {
					behaviourPersonalOther.setCreatedDate(new Date());
					behaviourPersonalOther.setCreatedUser(member.getMember().getCode());
				}
				try {
					BEHAVIOUR_PERSON_OTHER_SERVICE.create(behaviourPersonalOther);
				} catch (Exception e) {
					isError = true;
					e.printStackTrace();
				}

			} catch (Exception e) {
				isError = true;
				e.printStackTrace();
			}
		}
		if (isError) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Thông báo", "Lỗi"));
		} else {
			// notice("Thành công");
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Thông báo", "Thành công"));
		}
	}

	@Override
	protected Logger getLogger() {
		return null;
	}

	public List<BehaviourPersonOther> getAllBehaviourOther() {
		return allBehaviourOther;
	}

	public void setAllBehaviourOther(List<BehaviourPersonOther> allBehaviourOther) {
		this.allBehaviourOther = allBehaviourOther;
	}

	public BehaviourPersonOther getBehaviourSelected() {
		return behaviourSelected;
	}

	public void setBehaviourSelected(BehaviourPersonOther behaviourSelected) {
		this.behaviourSelected = behaviourSelected;
	}

	public BehaviourPersonOther getBehaviourNew() {
		return behaviourNew;
	}

	public void setBehaviourNew(BehaviourPersonOther behaviourNew) {
		this.behaviourNew = behaviourNew;
	}
}
