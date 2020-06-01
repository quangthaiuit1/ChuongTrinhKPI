package trong.lixco.com.bean360;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.PrimeFaces;

import trong.lixco.com.account.servicepublics.Department;
import trong.lixco.com.account.servicepublics.DepartmentServicePublic;
import trong.lixco.com.account.servicepublics.DepartmentServicePublicProxy;
import trong.lixco.com.account.servicepublics.Member;
import trong.lixco.com.account.servicepublics.MemberServicePublic;
import trong.lixco.com.account.servicepublics.MemberServicePublicProxy;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.ejb.service360.CauHoiService;
import trong.lixco.com.ejb.service360.ChiTietDanhGiaService;
import trong.lixco.com.ejb.service360.KetQuaDanhGiaService;
import trong.lixco.com.ejb.service360.KyDanhGiaService;
import trong.lixco.com.jpa.entity360.CauHoi;
import trong.lixco.com.jpa.entity360.ChiTietDanhGia;
import trong.lixco.com.jpa.entity360.KetQuaDanhGia;
import trong.lixco.com.jpa.entity360.KyDanhGia;
import trong.lixco.com.util.Notify;

@Named
@ViewScoped
public class KetQuaDanhGiaBean extends AbstractBean<KetQuaDanhGia> {
	private static final long serialVersionUID = 1L;
	private Notify notify;
	private List<KetQuaDanhGia> ketQuaDanhGias;
	private List<ChiTietDanhGia> chiTietDanhGias;
	private ChiTietDanhGia chiTietDanhGia;

	DepartmentServicePublic departmentServicePublic;
	@Inject
	private KyDanhGiaService kyDanhGiaService;
	@Inject
	private ChiTietDanhGiaService chiTietDanhGiaService;
	@Inject
	private CauHoiService cauHoiService;
	@Inject
	private Logger logger;
	@Inject
	private KetQuaDanhGiaService ketQuaDanhGiaService;
	trong.lixco.com.account.servicepublics.Member memberdidanhgia;
	MemberServicePublic memberServicePublic;
	trong.lixco.com.account.servicepublics.Member memberduocdanhgia;

	@Override
	protected Logger getLogger() {
		return logger;
	}

	KyDanhGia kyDanhGia;
	Set<String> matruongphongs = new HashSet<String>();

	@Override
	public void initItem() {
		try {
			chiTietDanhGia = new ChiTietDanhGia();
			Date date = new Date();
			memberdidanhgia = getAccount().getMember();
			kyDanhGia = kyDanhGiaService.findRange(date);
			memberServicePublic = new MemberServicePublicProxy();
			departmentServicePublic = new DepartmentServicePublicProxy();

			Department[] deps = departmentServicePublic.getAllDepartSubByParent("10001");
			for (int i = 0; i < deps.length; i++) {
				if (deps[i].getLevelDep().getLevel() <= 2) {
					matruongphongs.add(deps[i].getCodeMem());
				}
			}
			taichitietdanhgia();
			PrimeFaces.current().executeScript("PF('wghuongdan').show();");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void taichitietdanhgia() {
		// danh sach nhan vien duoc danh gia
		chiTietDanhGias = chiTietDanhGiaService.danhsachnhanvienduocdanhgia(kyDanhGia, memberdidanhgia.getCode());
		for (int i = 0; i < chiTietDanhGias.size(); i++) {
			try {
				Member mem = memberServicePublic.findByCode(chiTietDanhGias.get(i).getManhanvien());
				chiTietDanhGias.get(i).setPhongban(mem.getDepartment().getName());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (memberdidanhgia.getCode().equals(chiTietDanhGias.get(i).getManhanvien())) {
				chiTietDanhGias.get(i).setSodiem(chiTietDanhGias.get(i).getDiem0());
			} else if (memberdidanhgia.getCode().equals(chiTietDanhGias.get(i).getNv1())) {
				chiTietDanhGias.get(i).setSodiem(chiTietDanhGias.get(i).getDiem1());
			} else if (memberdidanhgia.getCode().equals(chiTietDanhGias.get(i).getNv2())) {
				chiTietDanhGias.get(i).setSodiem(chiTietDanhGias.get(i).getDiem2());
			} else if (memberdidanhgia.getCode().equals(chiTietDanhGias.get(i).getNv3())) {
				chiTietDanhGias.get(i).setSodiem(chiTietDanhGias.get(i).getDiem3());
			} else if (memberdidanhgia.getCode().equals(chiTietDanhGias.get(i).getNv4())) {
				chiTietDanhGias.get(i).setSodiem(chiTietDanhGias.get(i).getDiem4());
			} else if (memberdidanhgia.getCode().equals(chiTietDanhGias.get(i).getNv5())) {
				chiTietDanhGias.get(i).setSodiem(chiTietDanhGias.get(i).getDiem5());
			} else if (memberdidanhgia.getCode().equals(chiTietDanhGias.get(i).getNv6())) {
				chiTietDanhGias.get(i).setSodiem(chiTietDanhGias.get(i).getDiem6());
			}
		}
	}

	public void taidulieutudanhgia() {
		if (kyDanhGia != null) {
			memberduocdanhgia = memberdidanhgia;
			ketQuaDanhGias = ketQuaDanhGiaService.findRange(memberdidanhgia.getCode(), memberduocdanhgia.getCode(),
					kyDanhGia);
			chiTietDanhGia = chiTietDanhGiaService.findRange(kyDanhGia, memberdidanhgia.getCode());
			if (ketQuaDanhGias.size() != 0) {
				ajaxdanhgia();
				PrimeFaces.current().executeScript("PF('wgketquadanhgia').show();");
			} else {
				if (chiTietDanhGia != null) {
					List<CauHoi> cauHois;
					boolean status = false;
					for (String code : matruongphongs) {
						if (memberduocdanhgia.getCode().equals(code)) {
							status = true;
						}
					}
					if (status) {
						// 1: truong phong, 2: nhan vien
						cauHois = cauHoiService.findRange(1);
					} else {
						// 1: truong phong, 2: nhan vien
						cauHois = cauHoiService.findRange(2);
					}
					for (int i = 0; i < cauHois.size(); i++) {
						KetQuaDanhGia kq = new KetQuaDanhGia();
						kq.setKyDanhGia(kyDanhGia);
						kq.setNvduocdanhgia(memberdidanhgia.getCode());
						kq.setNvdanhgia(memberdidanhgia.getCode());
						kq.setMaCauHoi(cauHois.get(i).getMacauhoi() + "");
						kq.setCauHoi(cauHois.get(i).getNoidung());
						ketQuaDanhGias.add(kq);
					}
					ajaxdanhgia();
					PrimeFaces.current().executeScript("PF('wgketquadanhgia').show();");
				} else {
					noticeError("Nhân viên không có kỳ đánh giá.");
				}
			}

		} else {
			noticeError("Không có kỳ đánh giá trong thời gian này");
		}
	}

	public void ajaxdanhgia() {
		try {
			double tongdiem = 0;
			for (int i = 0; i < ketQuaDanhGias.size(); i++) {
				tongdiem += ketQuaDanhGias.get(i).getDiem();
			}
			double kq = Math.round((tongdiem / ketQuaDanhGias.size()) * 10) / 10.0;
			chiTietDanhGia.setSodiem(kq);
		} catch (Exception e) {
		}

	}

	public void chonnvdanhgia(ChiTietDanhGia ct) {
		try {
			chiTietDanhGia = ct;
			if (kyDanhGia != null) {
				memberduocdanhgia = memberServicePublic.findByCode(ct.getManhanvien());
				// if (memberduocdanhgia.equals(memberdidanhgia)) {
				// taidulieutudanhgia();
				// } else {
				ketQuaDanhGias = ketQuaDanhGiaService.findRange(memberdidanhgia.getCode(), memberduocdanhgia.getCode(),
						kyDanhGia);
				if (ketQuaDanhGias.size() != 0) {
				} else {
					List<CauHoi> cauHois;
					boolean status = false;
					for (String code : matruongphongs) {
						if (memberduocdanhgia.getCode().equals(code)) {
							status = true;
						}
					}
					if (status) {
						// 1: truong phong, 2: nhan vien
						cauHois = cauHoiService.findRange(1);
					} else {
						// 1: truong phong, 2: nhan vien
						cauHois = cauHoiService.findRange(2);
					}
					for (int i = 0; i < cauHois.size(); i++) {
						KetQuaDanhGia kq = new KetQuaDanhGia();
						kq.setKyDanhGia(kyDanhGia);
						kq.setNvduocdanhgia(memberduocdanhgia.getCode());
						kq.setNvdanhgia(memberdidanhgia.getCode());
						kq.setCauHoi(cauHois.get(i).getNoidung());
						ketQuaDanhGias.add(kq);
					}
				}
				ajaxdanhgia();
				PrimeFaces.current().executeScript("PF('wgketquadanhgia').show();");
				// }
			} else {
				noticeError("Không có kỳ đánh giá trong thời gian này");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void luuketquadanhgia() {
		boolean status = false;
		for (int i = 0; i < ketQuaDanhGias.size(); i++) {
			if (ketQuaDanhGias.get(i).getDiem() == 0) {
				status = true;
				break;
			}
		}
		if (status) {
			noticeError("Vui lòng đánh giá tất cả tiêu chí.");
		} else {
			for (int i = 0; i < ketQuaDanhGias.size(); i++) {
				ketQuaDanhGiaService.luuketquadanhgia(ketQuaDanhGias);
			}
			if (memberdidanhgia.getCode().equals(chiTietDanhGia.getManhanvien())) {
				chiTietDanhGia.setDiem0(chiTietDanhGia.getSodiem());
			} else if (memberdidanhgia.getCode().equals(chiTietDanhGia.getNv1())) {
				chiTietDanhGia.setDiem1(chiTietDanhGia.getSodiem());
			} else if (memberdidanhgia.getCode().equals(chiTietDanhGia.getNv2())) {
				chiTietDanhGia.setDiem2(chiTietDanhGia.getSodiem());
			} else if (memberdidanhgia.getCode().equals(chiTietDanhGia.getNv3())) {
				chiTietDanhGia.setDiem3(chiTietDanhGia.getSodiem());
			} else if (memberdidanhgia.getCode().equals(chiTietDanhGia.getNv4())) {
				chiTietDanhGia.setDiem4(chiTietDanhGia.getSodiem());
			} else if (memberdidanhgia.getCode().equals(chiTietDanhGia.getNv5())) {
				chiTietDanhGia.setDiem5(chiTietDanhGia.getSodiem());
			} else if (memberdidanhgia.getCode().equals(chiTietDanhGia.getNv6())) {
				chiTietDanhGia.setDiem6(chiTietDanhGia.getSodiem());
			}
			chiTietDanhGiaService.update(chiTietDanhGia);

			taichitietdanhgia();

			notice("Đã lưu thành công.");

		}
	}

	public List<KetQuaDanhGia> getKetQuaDanhGias() {
		return ketQuaDanhGias;
	}

	public void setKetQuaDanhGias(List<KetQuaDanhGia> ketQuaDanhGias) {
		this.ketQuaDanhGias = ketQuaDanhGias;
	}

	public trong.lixco.com.account.servicepublics.Member getMemberduocdanhgia() {
		return memberduocdanhgia;
	}

	public void setMemberduocdanhgia(trong.lixco.com.account.servicepublics.Member memberduocdanhgia) {
		this.memberduocdanhgia = memberduocdanhgia;
	}

	public List<ChiTietDanhGia> getChiTietDanhGias() {
		return chiTietDanhGias;
	}

	public void setChiTietDanhGias(List<ChiTietDanhGia> chiTietDanhGias) {
		this.chiTietDanhGias = chiTietDanhGias;
	}

	public ChiTietDanhGia getChiTietDanhGia() {
		return chiTietDanhGia;
	}

	public void setChiTietDanhGia(ChiTietDanhGia chiTietDanhGia) {
		this.chiTietDanhGia = chiTietDanhGia;
	}

}
