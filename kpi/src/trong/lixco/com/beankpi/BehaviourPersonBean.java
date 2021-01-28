package trong.lixco.com.beankpi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
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
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;

import com.ibm.icu.text.SimpleDateFormat;

import trong.lixco.com.account.servicepublics.Account;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.ejb.servicekpi.BehaviourPersonService;
import trong.lixco.com.jpa.entitykpi.BehaviourPerson;
import trong.lixco.com.jpa.thai.BehaviourPersonOther;
import trong.lixco.com.util.Notify;

@Named
@ViewScoped
// Dinh huong KPI ca nhan
public class BehaviourPersonBean extends AbstractBean<BehaviourPerson> {
	private static final long serialVersionUID = 1L;
	private Notify notify;
	private List<BehaviourPerson> behaviourPersons;
	private BehaviourPerson behaviourPerson;
	private BehaviourPerson behaviourPersonEdit;

	@Inject
	private BehaviourPersonService behaviourPersonService;
	@Inject
	private Logger logger;
	private Account member;

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Override
	public void initItem() {
		behaviourPerson = new BehaviourPerson();
		behaviourPersons = new ArrayList<BehaviourPerson>();
		member = getAccount();
		searchItem();
	}

	public void createOrUpdate() {
		notify = new Notify(FacesContext.getCurrentInstance());
		try {
			if (behaviourPerson != null) {
				if (!"".equals(behaviourPerson.getContent())) {
					if (behaviourPerson.getId() == null) {
						if (allowSave(null)) {
							behaviourPerson = installSave(behaviourPerson);
							behaviourPerson = behaviourPersonService.create(behaviourPerson);
							behaviourPersons.add(0, behaviourPerson);
							writeLogInfo("Tạo mới " + behaviourPerson.toString());
							notify.success();
							reset();
						} else {
							behaviourPersonEdit = new BehaviourPerson();
							notify.warningDetail();
						}
					} else {
						if (allowUpdate(null)) {
							behaviourPerson = installUpdate(behaviourPerson);
							behaviourPerson = behaviourPersonService.update(behaviourPerson);
							int index = behaviourPersons.indexOf(behaviourPerson);
							behaviourPersons.set(index, behaviourPerson);
							writeLogInfo("Cập nhật " + behaviourPerson.toString());
							notify.success();
						} else {
							notify.warningDetail();
						}
					}
					searchItem();
				} else {
					notify.warning("Điền đầy đủ thông tin!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			writeLogError(e.getLocalizedMessage());
			notify.warning("Xảy ra lỗi không lưu được!");
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
				BehaviourPerson behaviourPerson = new BehaviourPerson();
				behaviourPerson.setCode(code);
				behaviourPerson.setContent(content);
				if (isHeader == 0) {
					behaviourPerson.setHeader(false);
				}
				if (isHeader == 1) {
					behaviourPerson.setHeader(true);
				}
				behaviourPerson.setMinusPoint((int) diemtru);
				if (member != null) {
					behaviourPerson.setCreatedDate(new Date());
					behaviourPerson.setCreatedUser(member.getMember().getCode());
				}
				try {
					behaviourPersonService.create(behaviourPerson);
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

	public void reset() {
		behaviourPerson = new BehaviourPerson();
	}

	public void showEdit() {
		this.behaviourPerson = behaviourPersonEdit;
	}

	public void delete() {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (behaviourPerson.getId() != null) {
			if (allowDelete(null)) {
				boolean status = behaviourPersonService.delete(behaviourPerson);
				if (status) {
					behaviourPersons.remove(behaviourPerson);
					writeLogInfo("Xoá " + behaviourPerson.toString());
					reset();
					notify.success();
				} else {
					writeLogError("Lỗi khi xoá " + behaviourPerson.toString());
					notify.error();
				}
			} else {
				notify.warningDetail();
			}
		} else {
			notify.warning("Chưa chọn trong danh sách!");
		}
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
					.getRealPath("/resources/maufile/kpiphong_dulieumau.xlsx");
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

	public void searchItem() {
		behaviourPersons = behaviourPersonService.findAll();
	}

	public List<BehaviourPerson> getBehaviourPersons() {
		return behaviourPersons;
	}

	public void setBehaviourPersons(List<BehaviourPerson> behaviourPersons) {
		this.behaviourPersons = behaviourPersons;
	}

	public BehaviourPerson getBehaviourPerson() {
		return behaviourPerson;
	}

	public void setBehaviourPerson(BehaviourPerson behaviourPerson) {
		this.behaviourPerson = behaviourPerson;
	}

	public BehaviourPerson getBehaviourPersonEdit() {
		return behaviourPersonEdit;
	}

	public void setBehaviourPersonEdit(BehaviourPerson behaviourPersonEdit) {
		this.behaviourPersonEdit = behaviourPersonEdit;
	}

}
