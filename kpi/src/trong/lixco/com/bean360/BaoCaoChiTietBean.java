package trong.lixco.com.bean360;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.context.RequestContext;

import trong.lixco.com.account.servicepublics.Department;
import trong.lixco.com.account.servicepublics.DepartmentServicePublic;
import trong.lixco.com.account.servicepublics.DepartmentServicePublicProxy;
import trong.lixco.com.account.servicepublics.Member;
import trong.lixco.com.account.servicepublics.MemberServicePublic;
import trong.lixco.com.account.servicepublics.MemberServicePublicProxy;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.classInfor.ChiTietKetQuaDanhGia360;
import trong.lixco.com.ejb.service360.BaoCaoService;
import trong.lixco.com.ejb.service360.CaiDatDiemService;
import trong.lixco.com.ejb.service360.CaiDatTiLeService;
import trong.lixco.com.ejb.service360.KyDanhGiaService;
import trong.lixco.com.ejb.service360.PhongTraoPhongService;
import trong.lixco.com.ejb.service360.PhongTraoService;
import trong.lixco.com.ejb.service360.ViPhamService;
import trong.lixco.com.ejb.servicekpi.KPIDepMonthService;
import trong.lixco.com.ejb.servicekpi.KPIPersonService;
import trong.lixco.com.jpa.entity360.CaiDatDiem;
import trong.lixco.com.jpa.entity360.CaiDatTiLe;
import trong.lixco.com.jpa.entity360.ChiTietDanhGia;
import trong.lixco.com.jpa.entity360.KyDanhGia;
import trong.lixco.com.util.Notify;
import trong.lixco.com.util.ToExcel;
import trong.lixco.com.util.ToObjectFromClass;

@Named
@ViewScoped
public class BaoCaoChiTietBean extends AbstractBean {
	private static final long serialVersionUID = 1L;
	private Notify notify;
	@Inject
	private Logger logger;
	@Inject
	private KyDanhGiaService kyDanhGiaService;
	@Inject
	private BaoCaoService baoCaoService;
	@Inject
	CaiDatDiemService caiDatDiemService;
	@Inject
	PhongTraoService phongTraoService;
	@Inject
	PhongTraoPhongService phongTraoPhongService;
	@Inject
	ViPhamService viPhamService;
	@Inject
	CaiDatTiLeService caiDatTiLeService;
	MemberServicePublic memberServicePublic;
	DepartmentServicePublic departmentServicePublic;
	@Inject
	KPIDepMonthService kpiDepMonthService;
	@Inject
	KPIPersonService kpiPersonService;

	@Override
	protected Logger getLogger() {
		return logger;
	}

	KyDanhGia kyDanhGia;
	CaiDatTiLe caiDatTiLe;
	CaiDatDiem cddiem;
	List<ChiTietKetQuaDanhGia360> chiTietKetQuaDanhGia360nhanviens;
	List<ChiTietKetQuaDanhGia360> chiTietKetQuaDanhGia360quanlys;

	@Override
	public void initItem() {
		try {
			chiTietKetQuaDanhGia360nhanviens = new ArrayList<ChiTietKetQuaDanhGia360>();
			chiTietKetQuaDanhGia360quanlys = new ArrayList<ChiTietKetQuaDanhGia360>();
			memberServicePublic = new MemberServicePublicProxy();
			Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext()
					.getRequestParameterMap();
			String parameterOne = params.get("kydanhgia_id");
			String parameterYear = params.get("year");
			if (parameterOne != null && parameterYear != null) {
				int year = Integer.parseInt(parameterYear);
				kyDanhGia = kyDanhGiaService.findById(Long.parseLong(parameterOne));
				caiDatTiLe = caiDatTiLeService.findById(1l);
				if (kyDanhGia != null) {
					cddiem = caiDatDiemService.findRange(kyDanhGia);
					List<ChiTietDanhGia> cts = new ArrayList<ChiTietDanhGia>();
					cts.addAll(baoCaoService.baocaoHCM(kyDanhGia));
					cts.addAll(baoCaoService.baocaoBD(kyDanhGia));
					cts.addAll(baoCaoService.baocaoBN(kyDanhGia));
					departmentServicePublic = new DepartmentServicePublicProxy();
					Department[] deps = departmentServicePublic.getAllDepartSubByParent("10001");
					Set<String> matruongphongs = new HashSet<String>();
					for (int i = 0; i < deps.length; i++) {
						if (deps[i].getLevelDep().getLevel() <= 2) {
							matruongphongs.add(deps[i].getCodeMem());
						}
					}

					Map<String, List<ChiTietDanhGia>> datagroups1 = cts.stream().collect(
							Collectors.groupingBy(p -> p.getManhanvien(), Collectors.toList()));
					for (String manv : datagroups1.keySet()) {
						List<ChiTietDanhGia> invs = datagroups1.get(manv);
						String tennv = "";
						double diem0 = 0;
						String tennv1 = "";
						double diem1 = 0;
						String tennv2 = "";
						double diem2 = 0;
						String tennv3 = "";
						double diem3 = 0;
						String tennv4 = "";
						double diem4 = 0;
						String tennv5 = "";
						double diem5 = 0;
						String tennv6 = "";
						double diem6 = 0;
						for (int i = 0; i < invs.size(); i++) {
							if (invs.get(i).getDiem0() != 0) {
								diem0 = invs.get(i).getDiem0();
							}
							if (invs.get(i).getDiem1() != 0) {
								diem1 = invs.get(i).getDiem1();

							}
							tennv1 = invs.get(i).getNv1();
							if (invs.get(i).getDiem2() != 0) {
								diem2 = invs.get(i).getDiem2();

							}
							tennv2 = invs.get(i).getNv2();
							if (invs.get(i).getDiem3() != 0) {
								diem3 = invs.get(i).getDiem3();

							}
							tennv3 = invs.get(i).getNv3();
							if (invs.get(i).getDiem4() != 0) {
								diem4 = invs.get(i).getDiem4();

							}
							tennv4 = invs.get(i).getNv4();
							if (invs.get(i).getDiem5() != 0) {
								diem5 = invs.get(i).getDiem5();

							}
							tennv5 = invs.get(i).getNv5();
							if (invs.get(i).getDiem6() != 0) {
								diem6 = invs.get(i).getDiem6();

							}
							tennv6 = invs.get(i).getNv6();
							tennv = invs.get(i).getTennhanvien();
						}
						double soluotdanhgia = 0;
						if (diem0 != 0) {
							soluotdanhgia += 1;
						}
						if (diem1 != 0) {
							soluotdanhgia += 1;
						}
						if (diem2 != 0) {
							soluotdanhgia += 1;
						}
						if (diem3 != 0) {
							soluotdanhgia += 1;
						}
						if (diem4 != 0) {
							soluotdanhgia += 1;
						}
						if (diem5 != 0) {
							soluotdanhgia += 1;
						}
						if (diem6 != 0) {
							soluotdanhgia += 1;
						}
						double diemtrungbinhdanhgia = 0.0;
						if (soluotdanhgia != 0)
							diemtrungbinhdanhgia = Math
									.round(((diem0 + diem1 + diem2 + diem3 + diem4 + diem5 + diem6) / soluotdanhgia) * 100.0) / 100.0;
						boolean status = false;
						ChiTietKetQuaDanhGia360 kq = new ChiTietKetQuaDanhGia360();
						kq.setManhanvien(manv);
						kq.setTennhanvien(tennv);
						kq.setDiemtudanhgia(diem0);
						kq.setDiem1(diem1);
						kq.setDiem2(diem2);
						kq.setDiem3(diem3);
						kq.setDiem4(diem4);
						kq.setDiem5(diem5);
						kq.setDiemtrungbinh(diemtrungbinhdanhgia);
						Member mem = memberServicePublic.findByCode(manv);
						if (mem != null)
							kq.setPhongban(mem.getDepartment().getName());
						if (!"".equals(tennv1)) {
							Member mem1 = memberServicePublic.findByCode(tennv1);
							if (mem1 != null)
								kq.setNv1(mem1.getName());
						}
						if (!"".equals(tennv2)) {
							Member mem2 = memberServicePublic.findByCode(tennv2);
							if (mem2 != null)
								kq.setNv2(mem2.getName());
						}
						if (!"".equals(tennv3)) {
							Member mem3 = memberServicePublic.findByCode(tennv3);
							if (mem3 != null)
								kq.setNv3(mem3.getName());
						}
						if (!"".equals(tennv4)) {
							Member mem4 = memberServicePublic.findByCode(tennv4);
							if (mem4 != null)
								kq.setNv4(mem4.getName());
						}
						if (!"".equals(tennv5)) {
							Member mem5 = memberServicePublic.findByCode(tennv5);
							if (mem5 != null)
								kq.setNv5(mem5.getName());
						}
						if (!"".equals(tennv6)) {
							Member mem6 = memberServicePublic.findByCode(tennv6);
							if (mem6 != null)
								kq.setNv1(mem6.getName());
						}
						for (String code : matruongphongs) {
							if (mem.getCode().equals(code)) {
								status = true;
								break;
							}
						}
						if (status) {
							/*
							 * Truong bo phan
							 */
							chiTietKetQuaDanhGia360quanlys.add(kq);
						} else {
							chiTietKetQuaDanhGia360nhanviens.add(kq);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void excelql() {
		try {
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("target='_blank';monitorDownload( showStatus , hideStatus)");
			List<Object[]> listObject = ToObjectFromClass.toObjectKQ360Chitiet(chiTietKetQuaDanhGia360quanlys);
			String filename = "kq360";
			HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance()
					.getExternalContext().getResponse();
			httpServletResponse.addHeader("Content-disposition", "attachment; filename=" + filename + ".xls");
			ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
			ToExcel.writeXLS(listObject, servletOutputStream);
			FacesContext.getCurrentInstance().responseComplete();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void excelnv() {
		try {
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("target='_blank';monitorDownload( showStatus , hideStatus)");
			List<Object[]> listObject = ToObjectFromClass.toObjectKQ360Chitiet(chiTietKetQuaDanhGia360nhanviens);
			String filename = "kq360";
			HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance()
					.getExternalContext().getResponse();
			httpServletResponse.addHeader("Content-disposition", "attachment; filename=" + filename + ".xls");
			ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
			ToExcel.writeXLS(listObject, servletOutputStream);
			FacesContext.getCurrentInstance().responseComplete();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public KyDanhGia getKyDanhGia() {
		return kyDanhGia;
	}

	public void setKyDanhGia(KyDanhGia kyDanhGia) {
		this.kyDanhGia = kyDanhGia;
	}

	public CaiDatTiLe getCaiDatTiLe() {
		return caiDatTiLe;
	}

	public void setCaiDatTiLe(CaiDatTiLe caiDatTiLe) {
		this.caiDatTiLe = caiDatTiLe;
	}

	public CaiDatDiem getCddiem() {
		return cddiem;
	}

	public void setCddiem(CaiDatDiem cddiem) {
		this.cddiem = cddiem;
	}

	public List<ChiTietKetQuaDanhGia360> getChiTietKetQuaDanhGia360nhanviens() {
		return chiTietKetQuaDanhGia360nhanviens;
	}

	public void setChiTietKetQuaDanhGia360nhanviens(List<ChiTietKetQuaDanhGia360> ChiTietKetQuaDanhGia360nhanviens) {
		this.chiTietKetQuaDanhGia360nhanviens = ChiTietKetQuaDanhGia360nhanviens;
	}

	public List<ChiTietKetQuaDanhGia360> getChiTietKetQuaDanhGia360quanlys() {
		return chiTietKetQuaDanhGia360quanlys;
	}

	public void setChiTietKetQuaDanhGia360quanlys(List<ChiTietKetQuaDanhGia360> ChiTietKetQuaDanhGia360quanlys) {
		this.chiTietKetQuaDanhGia360quanlys = ChiTietKetQuaDanhGia360quanlys;
	}

}
