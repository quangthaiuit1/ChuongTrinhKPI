package trong.lixco.com.bean360;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import trong.lixco.com.account.servicepublics.Department;
import trong.lixco.com.account.servicepublics.DepartmentServicePublic;
import trong.lixco.com.account.servicepublics.DepartmentServicePublicProxy;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.ejb.service360.CaiDatDiemService;
import trong.lixco.com.ejb.service360.CauHoiService;
import trong.lixco.com.ejb.service360.ChiTietDanhGiaService;
import trong.lixco.com.ejb.service360.KyDanhGiaService;
import trong.lixco.com.ejb.service360.PhongTraoPhongService;
import trong.lixco.com.ejb.service360.PhongTraoService;
import trong.lixco.com.ejb.service360.ViPhamService;
import trong.lixco.com.jpa.entity360.CaiDatDiem;
import trong.lixco.com.jpa.entity360.CauHoi;
import trong.lixco.com.jpa.entity360.ChiTietDanhGia;
import trong.lixco.com.jpa.entity360.KyDanhGia;
import trong.lixco.com.jpa.entity360.PhongTrao;
import trong.lixco.com.jpa.entity360.PhongTraoPhong;
import trong.lixco.com.jpa.entity360.ViPham;
import trong.lixco.com.util.Notify;
import trong.lixco.com.util.ToExcel;

@Named
@ViewScoped
public class QuanTriBean extends AbstractBean<CauHoi> {
	private static final long serialVersionUID = 1L;
	private Notify notify;
	private List<CauHoi> cauHois;
	DepartmentServicePublic departmentServicePublic;
	private List<KyDanhGia> kyDanhGias;
	private KyDanhGia kyDanhGia;
	private KyDanhGia kyDanhGiaNew;
	private CaiDatDiem caiDatDiem;
	private boolean naplai = false;
int year=2019;
	@Inject
	private CaiDatDiemService caiDatDiemService;
	@Inject
	private KyDanhGiaService kyDanhGiaService;
	@Inject
	private ChiTietDanhGiaService chiTietDanhGiaService;
	@Inject
	private PhongTraoService phongTraoService;
	@Inject
	private PhongTraoPhongService phongTraoPhongService;
	@Inject
	private ViPhamService viPhamService;
	@Inject
	private CauHoiService cauHoiService;
	@Inject
	private Logger logger;

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Override
	public void initItem() {
		try {
			kyDanhGiaNew = new KyDanhGia();
			departmentServicePublic = new DepartmentServicePublicProxy();
			trong.lixco.com.account.servicepublics.Member member = getAccount().getMember();
			Set<String> matruongphongs = new HashSet<String>();
			Department[] deps = departmentServicePublic.getAllDepartSubByParent("10001");
			for (int i = 0; i < deps.length; i++) {
				if (deps[i].getLevelDep().getLevel() <= 2) {
					matruongphongs.add(deps[i].getCodeMem());
				}
			}
			boolean status = false;
			for (String code : matruongphongs) {
				if (member.getCode().equals(code)) {
					status = true;
				}
			}
			if (status) {
				// 1: truong phong, 2: nhan vien
				cauHoiService.findRange(1);
			} else {
				// 1: truong phong, 2: nhan vien
				cauHoiService.findRange(2);
			}
			kyDanhGias = kyDanhGiaService.findAll();
			if (kyDanhGias.size() != 0)
				kyDanhGia = kyDanhGias.get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void baocaoketqua() {
		try {
			FacesContext.getCurrentInstance().getExternalContext()
					.redirect("/kpi/pages/360/ketqua.jsf?kydanhgia_id=" + kyDanhGia.getId()+"&year="+year);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	public void baocaoketquachitet() {
		try {
			FacesContext.getCurrentInstance().getExternalContext()
					.redirect("/kpi/pages/360/ketquachitiet.jsf?kydanhgia_id=" + kyDanhGia.getId()+"&year="+year);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void ajaxKyDanhGia() {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (kyDanhGia != null) {
			caiDatDiem = caiDatDiemService.findRange(kyDanhGia);
			if (caiDatDiem == null) {
				caiDatDiem = new CaiDatDiem();
				caiDatDiem.setKyDanhGia(kyDanhGia);
			}
			PrimeFaces.current().executeScript("PF('wgcaidatdiem').show();");
		} else {
			notify.warning("Chưa chọn kỳ đánh giá.");
		}
	}

	public void luucaidatdiem() {
		try {
			notify = new Notify(FacesContext.getCurrentInstance());
			if (caiDatDiem.getId() != null) {
				caiDatDiemService.update(caiDatDiem);
			} else {
				caiDatDiemService.create(caiDatDiem);
			}
			PrimeFaces.current().executeScript("PF('wgcaidatdiem').hide();");
			notify.success();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void luukydanhgia() {
		notify = new Notify(FacesContext.getCurrentInstance());
		try {
			if (allowUpdate(null)) {
				kyDanhGiaService.update(kyDanhGiaNew);
				kyDanhGiaNew = new KyDanhGia();
				kyDanhGias = kyDanhGiaService.findAll();
				notify.success();
			} else {
				notify.warningDetail();
			}
		} catch (Exception e) {
			writeLogError(e.getLocalizedMessage());
			notify.warning("Lỗi khi lưu: " + e.getLocalizedMessage());
		}
	}

	/**
	 * MExcel file cau hoi
	 * 
	 * @param event
	 */
	public void doctuExcelcauhoi(FileUploadEvent event) {

		notify = new Notify(FacesContext.getCurrentInstance());
		UploadedFile uploadedFile = event.getFile();
		uploadedFile.getFileName();
		try {
			uploadedFile.getInputstream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<CauHoi> cauHois = ToExcel.docexcelcauhoi(uploadedFile);
		if (cauHois.size() != 0) {
			if (allowSave(null)) {
				cauHoiService.luucauhoituexcel(cauHois);
				PrimeFaces.current().executeScript("PF('wgnhapcauhoi').hide();");
				notify.success();
			} else {
				notify.warningDetail();
			}
		} else {
			noticeError("Không đọc được dữ liệu");
		}
	}

	public void filemaucauhoi() {
		try {
			PrimeFaces.current().executeScript("target='_blank';monitorDownload( showStatus , hideStatus)");
			String filename = "File excel cau hoi";
			HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance()
					.getExternalContext().getResponse();
			httpServletResponse.addHeader("Content-disposition", "attachment; filename=" + filename + ".xlsx");
			ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
			String file = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/maufile/maufilecauhoi.xlsx");
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
	/**
	 * MExcel file phong trao phong
	 * 
	 * @param event
	 */
	public void doctuExcelphongtraophong(FileUploadEvent event) {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (kyDanhGia == null) {
			noticeError("Chưa chọn kỳ đánh giá");
		} else {
			UploadedFile uploadedFile = event.getFile();
			uploadedFile.getFileName();
			try {
				uploadedFile.getInputstream();
			} catch (IOException e) {
				e.printStackTrace();
			}
			List<PhongTraoPhong> phongTraoPhongs = ToExcel.docexcelthamgiaphongtraophong(uploadedFile);
			if (phongTraoPhongs.size() != 0 && kyDanhGia != null) {
				if (allowSave(null)) {
					phongTraoPhongService.luuPhongTraoPhongtuexcel(kyDanhGia, phongTraoPhongs);
					PrimeFaces.current().executeScript("PF('wgphongtraophong').hide();");
					notify.success();
				} else {
					notify.warningDetail();
				}
			} else {
				noticeError("Không đọc được dữ liệu");
			}
		}
	}

	public void filemauphongtraophong() {
		try {
			PrimeFaces.current().executeScript("target='_blank';monitorDownload( showStatus , hideStatus)");
			String filename = "File excel phong trao phong";
			HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance()
					.getExternalContext().getResponse();
			httpServletResponse.addHeader("Content-disposition", "attachment; filename=" + filename + ".xlsx");
			ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
			String file = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/maufile/maufilephongtraophong.xlsx");
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
	/**
	 * MExcel file phong trao
	 * 
	 * @param event
	 */
	public void doctuExcelphongtrao(FileUploadEvent event) {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (kyDanhGia == null) {
			noticeError("Chưa chọn kỳ đánh giá");
		} else {
			UploadedFile uploadedFile = event.getFile();
			uploadedFile.getFileName();
			try {
				uploadedFile.getInputstream();
			} catch (IOException e) {
				e.printStackTrace();
			}
			List<PhongTrao> phongTraos = ToExcel.docexcelthamgiaphongtrao(uploadedFile);
			if (phongTraos.size() != 0 && kyDanhGia != null) {
				if (allowSave(null)) {
					phongTraoService.luuphongtraotuexcel(kyDanhGia, phongTraos);
					PrimeFaces.current().executeScript("PF('wgphongtrao').hide();");
					notify.success();
				} else {
					notify.warningDetail();
				}
			} else {
				noticeError("Không đọc được dữ liệu");
			}
		}
	}

	public void filemauphongtrao() {
		try {
			PrimeFaces.current().executeScript("target='_blank';monitorDownload( showStatus , hideStatus)");
			String filename = "File excel phongtrao";
			HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance()
					.getExternalContext().getResponse();
			httpServletResponse.addHeader("Content-disposition", "attachment; filename=" + filename + ".xlsx");
			ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
			String file = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/maufile/maufilephongtrao.xlsx");
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

	/**
	 * MExcel file vi pham
	 * 
	 * @param event
	 */
	public void doctuExcelvipham(FileUploadEvent event) {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (kyDanhGia == null) {
			noticeError("Chưa chọn kỳ đánh giá");
		} else {
			UploadedFile uploadedFile = event.getFile();
			uploadedFile.getFileName();
			try {
				uploadedFile.getInputstream();
			} catch (IOException e) {
				e.printStackTrace();
			}
			List<ViPham> viPhams = ToExcel.docexcelvipham(uploadedFile);
			if (viPhams.size() != 0 && kyDanhGia != null) {
				if (allowSave(null)) {
					viPhamService.luuviphamtuexcel(kyDanhGia, viPhams);
					PrimeFaces.current().executeScript("PF('wgvipham').hide();");
					notify.success();
				} else {
					notify.warningDetail();
				}
			} else {
				noticeError("Không đọc được dữ liệu");
			}
		}
	}

	public void filemauvipham() {
		try {
			PrimeFaces.current().executeScript("target='_blank';monitorDownload( showStatus , hideStatus)");
			String filename = "File excel vi phạm";
			HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance()
					.getExternalContext().getResponse();
			httpServletResponse.addHeader("Content-disposition", "attachment; filename=" + filename + ".xlsx");
			ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
			String file = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/maufile/maufilevipham.xlsx");
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

	public void doctuExcelcaidat(FileUploadEvent event) {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (kyDanhGia == null) {
			noticeError("Chưa chọn kỳ đánh giá");
		} else {
			UploadedFile uploadedFile = event.getFile();
			uploadedFile.getFileName();
			try {
				uploadedFile.getInputstream();
			} catch (IOException e) {
				e.printStackTrace();
			}
			List<ChiTietDanhGia> chiTietDanhGias = ToExcel.docexcelcaidat(uploadedFile);
			if (chiTietDanhGias.size() != 0 && kyDanhGia != null) {
				if (allowSave(null)) {
					chiTietDanhGiaService.luucaidattuexcel(naplai, kyDanhGia, chiTietDanhGias);
					PrimeFaces.current().executeScript("PF('wgnhapcaidat').hide();");
					notify.success();
				} else {
					notify.warningDetail();
				}
			} else {
				noticeError("Không đọc được dữ liệu");
			}
		}
	}

	public void filemaucaidat() {
		try {
			PrimeFaces.current().executeScript("target='_blank';monitorDownload( showStatus , hideStatus)");
			String filename = "File excel cai dat";
			HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance()
					.getExternalContext().getResponse();
			httpServletResponse.addHeader("Content-disposition", "attachment; filename=" + filename + ".xlsx");
			ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
			String file = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/maufile/maufilecaidat.xlsx");
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

	public List<CauHoi> getCauHois() {
		return cauHois;
	}

	public void setCauHois(List<CauHoi> cauHois) {
		this.cauHois = cauHois;
	}

	public List<KyDanhGia> getKyDanhGias() {
		return kyDanhGias;
	}

	public void setKyDanhGias(List<KyDanhGia> kyDanhGias) {
		this.kyDanhGias = kyDanhGias;
	}

	public KyDanhGia getKyDanhGia() {
		return kyDanhGia;
	}

	public void setKyDanhGia(KyDanhGia kyDanhGia) {
		this.kyDanhGia = kyDanhGia;
	}

	public KyDanhGia getKyDanhGiaNew() {
		return kyDanhGiaNew;
	}

	public void setKyDanhGiaNew(KyDanhGia kyDanhGiaNew) {
		this.kyDanhGiaNew = kyDanhGiaNew;
	}

	public boolean isNaplai() {
		return naplai;
	}

	public void setNaplai(boolean naplai) {
		this.naplai = naplai;
	}

	public CaiDatDiem getCaiDatDiem() {
		return caiDatDiem;
	}

	public void setCaiDatDiem(CaiDatDiem caiDatDiem) {
		this.caiDatDiem = caiDatDiem;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

}
