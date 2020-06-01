package trong.lixco.com.bean360;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
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
import trong.lixco.com.classInfor.ChuaDanhGia360;
import trong.lixco.com.classInfor.KetQuaDanhGia360;
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
import trong.lixco.com.jpa.entity360.PhongTrao;
import trong.lixco.com.jpa.entity360.PhongTraoPhong;
import trong.lixco.com.jpa.entity360.ViPham;
import trong.lixco.com.jpa.entitykpi.KPIDepMonth;
import trong.lixco.com.jpa.entitykpi.KPIPerson;
import trong.lixco.com.util.Notify;
import trong.lixco.com.util.ToExcel;
import trong.lixco.com.util.ToObjectFromClass;
import trong.lixco.util.MOpe;
import trong.lixco.util.NumberUtil;

@Named
@ViewScoped
public class BaoCaoBean extends AbstractBean {
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
	List<KetQuaDanhGia360> ketQuaDanhGia360nhanviens;
	List<KetQuaDanhGia360> ketQuaDanhGia360quanlys;

	@Override
	public void initItem() {
		try {
			ketQuaDanhGia360nhanviens = new ArrayList<KetQuaDanhGia360>();
			ketQuaDanhGia360quanlys = new ArrayList<KetQuaDanhGia360>();
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
						double diem1 = 0;
						double diem2 = 0;
						double diem3 = 0;
						double diem4 = 0;
						double diem5 = 0;
						double diem6 = 0;
						for (int i = 0; i < invs.size(); i++) {
							if (invs.get(i).getDiem0() != 0)
								diem0 = invs.get(i).getDiem0();
							if (invs.get(i).getDiem1() != 0)
								diem1 = invs.get(i).getDiem1();
							if (invs.get(i).getDiem2() != 0)
								diem2 = invs.get(i).getDiem2();
							if (invs.get(i).getDiem3() != 0)
								diem3 = invs.get(i).getDiem3();
							if (invs.get(i).getDiem4() != 0)
								diem4 = invs.get(i).getDiem4();
							if (invs.get(i).getDiem5() != 0)
								diem5 = invs.get(i).getDiem5();
							if (invs.get(i).getDiem6() != 0)
								diem6 = invs.get(i).getDiem6();
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
						KetQuaDanhGia360 kq = new KetQuaDanhGia360();
						kq.setManhanvien(manv);
						kq.setTennhanvien(tennv);
						Member mem = memberServicePublic.findByCode(manv);
						if (mem != null)
							kq.setPhongban(mem.getDepartment().getName());
						if (soluotdanhgia != 0) {
							double diemtrungbinhdanhgia = Math
									.round(((diem0 + diem1 + diem2 + diem3 + diem4 + diem5 + diem6) / soluotdanhgia) * 100.0) / 100.0;

							PhongTrao phongTrao = phongTraoService.findRange(kyDanhGia, manv);
							ViPham viPham = viPhamService.findRange(kyDanhGia, manv);
							PhongTraoPhong phongTraoPhong = phongTraoPhongService.findRange(kyDanhGia, mem
									.getDepartment().getCode());
							boolean status = false;
							for (String code : matruongphongs) {
								if (mem.getCode().equals(code)) {
									status = true;
									break;
								}
							}

							/*
							 * Kpi thang
							 */
							List<KPIDepMonth> kpideps = baoCaoService.findKPIDepMonthYearHCM(year, mem.getDepartment()
									.getCode());
							if (kpideps.size() == 0) {
								kpideps = baoCaoService.findKPIDepMonthYearBD(year, mem.getDepartment().getCode());
							}
							if (kpideps.size() == 0) {
								kpideps = baoCaoService.findKPIDepMonthYearBN(year, mem.getDepartment().getCode());
							}
							double tongkpithangphong = 0;
							int sothangkpiphong = kpideps.size();
							for (int j = 0; j < kpideps.size(); j++) {
								tongkpithangphong = NumberUtil.myMath(tongkpithangphong, kpideps.get(j).getResult(),
										MOpe.ADD, 2);
							}
							tongkpithangphong = NumberUtil.round(tongkpithangphong, 2);
							/*
							 * KPI nam
							 */
							// List<KPIDep> kpidepyears =
							// baoCaoService.findKPIDepYearHCM(year,
							// mem.getDepartment()
							// .getCode());
							// if (kpidepyears.size() == 0) {
							// kpidepyears =
							// baoCaoService.findKPIDepYearBD(year,
							// mem.getDepartment().getCode());
							// }
							// if (kpidepyears.size() == 0) {
							// kpidepyears =
							// baoCaoService.findKPIDepYearBN(year,
							// mem.getDepartment().getCode());
							// }
							// double tongkpinamphong = 0;
							// for (int j = 0; j < kpidepyears.size(); j++) {
							// tongkpinamphong +=
							// kpidepyears.get(j).getResult();
							// }

							List<KPIPerson> kpiEmps = baoCaoService.findKPIPersonHCM(year, manv);
							if (kpiEmps.size() == 0)
								kpiEmps = baoCaoService.findKPIPersonBD(year, manv);
							if (kpiEmps.size() == 0)
								kpiEmps = baoCaoService.findKPIPersonBN(year, manv);

							double tongkpicanhan = 0;
							int sothangKPI = kpiEmps.size();
							for (int j = 0; j < kpiEmps.size(); j++) {
								tongkpicanhan += kpiEmps.get(j).getTotal();
							}
							tongkpicanhan = Math.round(tongkpicanhan * 100.0) / 100.0;
							tongkpicanhan = NumberUtil.myMath(NumberUtil.myMath(tongkpicanhan, 100, MOpe.MULTIPLY, 2),
									100, MOpe.DIVIDE, 2);
							double tilekpiphongthang = 0;
							if (sothangkpiphong != 0) {
								tilekpiphongthang = NumberUtil.myMath(tongkpithangphong, sothangkpiphong, MOpe.DIVIDE,
										2);
								if (phongTraoPhong != null)
									tilekpiphongthang = NumberUtil.myMath(tilekpiphongthang,
											phongTraoPhong.getSodiem(), MOpe.ADD, 2);
							}
							// double tilekpiphongnam = (tongkpinamphong / 12 /
							// 100.0) * 50;
							// Cai dat diem

							if (status) {
								/*
								 * Truong bo phan (50% kpi phong thang 50%kpi
								 * phong nam):80% Danh gia 360:20%
								 */

								// kq.setKpiphong((tilekpiphongthang +
								// tilekpiphongnam) * 0.8);
								kq.setKpiphong(NumberUtil.myMath(tilekpiphongthang, 0.7, MOpe.MULTIPLY, 2));
								kq.setDiemdanhgia(NumberUtil.myMath(diemtrungbinhdanhgia, 3, MOpe.MULTIPLY, 2));
								double[] diem = { kq.getKpiphong(), kq.getDiemdanhgia() };
								double tongdiem = NumberUtil.myMath(diem, MOpe.ADD, 1);

								kq.setTongdiem(tongdiem);
								ketQuaDanhGia360quanlys.add(kq);
							} else {
								// kq.setKpiphong((tilekpiphongthang +
								// tilekpiphongnam) / 100 *
								// caiDatTiLe.getKpiphong());
								if(kq.getManhanvien().equals("0002920"))
									System.out.println("");
								tilekpiphongthang = NumberUtil.myMath(
										NumberUtil.myMath(tilekpiphongthang, 100, MOpe.DIVIDE, 4),
										caiDatTiLe.getKpiphong(), MOpe.MULTIPLY, 1);
								kq.setKpiphong(tilekpiphongthang);
								if (sothangKPI != 0) {
									double diemkpicn[] = { tongkpicanhan, sothangKPI, 100.0 };
									tongkpicanhan = NumberUtil.myMath(diemkpicn, MOpe.DIVIDE, 1);
									kq.setKpicanhan(NumberUtil.myMath(tongkpicanhan, caiDatTiLe.getKpicanhan(),
											MOpe.MULTIPLY, 2));
								}
								kq.setDiemdanhgia((diemtrungbinhdanhgia / 10.0 * caiDatTiLe.getDiemdanhgia()));
								if (phongTrao != null) {
									double diemphongtrao = (phongTrao.getSolanthamgia());
									if (diemphongtrao > caiDatTiLe.getDiemphongtrao())
										diemphongtrao = caiDatTiLe.getDiemphongtrao();
									kq.setDiemphongtrao(diemphongtrao);
								}
								if (viPham != null) {
									double diemvipham = (viPham.getSolanvipham());
									if (diemvipham > caiDatTiLe.getDiemtruvipham())
										diemvipham = caiDatTiLe.getDiemtruvipham();
									kq.setDiemtruvipham(diemvipham);
								}
								double diemth[] = { kq.getKpiphong(), kq.getKpicanhan(), kq.getDiemdanhgia(),
										kq.getDiemphongtrao() };
								double tongdiem = NumberUtil.myMath(NumberUtil.myMath(diemth, MOpe.ADD, 2),
										kq.getDiemtruvipham(), MOpe.SUBTRACT, 1);
								kq.setTongdiem(tongdiem);
								ketQuaDanhGia360nhanviens.add(kq);
							}
						}

					}
				}
			}
			quatrinhdanhgia();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void excelql() {
		try {
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("target='_blank';monitorDownload( showStatus , hideStatus)");
			List<Object[]> listObject = ToObjectFromClass.toObjectKQ360(ketQuaDanhGia360quanlys);
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
			List<Object[]> listObject = ToObjectFromClass.toObjectKQ360(ketQuaDanhGia360nhanviens);
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

	public void guimail(ChuaDanhGia360 chuaDanhGia360) {
		notify = new Notify(FacesContext.getCurrentInstance());
		try {
			Member mem1 = memberServicePublic.findByCode(chuaDanhGia360.getManhanvien());
			if (mem1 != null) {
				if (mem1.getEmail() != null) {
					boolean status = sendMail(mem1);
					if (status) {
						notify.success();
					} else {
						notify.error("Không gửi mail được hoặc không có địa chỉ email.");
					}
				} else {
					notify.error("Không có địa chỉ mail.");
				}
			}
		} catch (Exception e) {
			notify.error();
		}

	}

	// gưi mail
	// Da nhan don hang
	public boolean sendMail(Member member) {
		try {
			String email = member.getEmail();
			Properties props = System.getProperties();
			props.put("mail.smtp.host", "mail.lixco.com");
			props.put("mail.smtp.port", 25);
			Authenticator pa = null;
			final String mailadmin = "trong-nguyenvan@lixco.com";
			final String passmailadmin = "It@2168lix";
			if (mailadmin != null && passmailadmin != null) {
				props.put("mail.smtp.auth", "true");
				pa = new Authenticator() {
					public PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(mailadmin, passmailadmin);
					}
				};
			}// else: no authentication
			Session session = Session.getInstance(props, pa);
			// — Create a new message –
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(mailadmin));

			// msg.setRecipients(Message.RecipientType.TO,
			// InternetAddress.parse(email, false));

			// — Set the subject and body text –
			msg.setSubject(MimeUtility.encodeText("[TỰ ĐỘNG] THÔNG BÁO ĐÁNH GIÁ 360", "utf-8", "B"));

			String text = "";
			String html = "<p>Kính gửi Anh/Chị  <b>"
					+ member.getName()
					+ "</b>,</p>"
					+ "<p>"
					+ "Hệ thống đánh giá 360 độ xin thông báo: Hiện tại Anh/chị chưa hoàn thành việc đánh giá 360 độ theo yêu cầu. Vì vậy, Anh/chị vui lòng truy cập vào phần mềm để hoàn thiện việc đánh giá những vị trí còn thiếu. Rất mong Anh/chị chị sớm hoàn thành để kịp tiến độ của chương trình."
					+ "<p>Mọi thắc mắc xin vui lòng liên hệ: <br/>TRẦN QUỐC TOẢN - PHÒNG NHÂN SỰ</p>";

			Multipart multipart = new MimeMultipart("alternative");
			MimeBodyPart textPart = new MimeBodyPart();
			textPart.setText(text, "utf-8");

			MimeBodyPart htmlPart = new MimeBodyPart();
			htmlPart.setContent(html, "text/html; charset=utf-8");

			multipart.addBodyPart(textPart);
			multipart.addBodyPart(htmlPart);
			msg.setContent(multipart);
			// — Set some other header information –

			msg.setHeader("X-Mailer", "LOTONtechEmail");
			msg.setSentDate(new Date());
			msg.saveChanges();

			// — Send the message –
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));
			Transport.send(msg);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Bao cao chua danh gia
	 * 
	 * @return
	 */
	List<ChuaDanhGia360> chuaDanhGia360s;

	public void quatrinhdanhgia() {
		this.chuaDanhGia360s = new ArrayList<ChuaDanhGia360>();
		List<ChiTietDanhGia> cts = baoCaoService.baocaoHCM(kyDanhGia);
		List<ChuaDanhGia360> chuaDanhGia360s = new ArrayList<ChuaDanhGia360>();
		for (int i = 0; i < cts.size(); i++) {
			ChiTietDanhGia ct = cts.get(i);
			if (ct.getDiem0() == 0) {
				List<ChiTietDanhGia> temps = baoCaoService.danhsachchuadanhgiaBD(kyDanhGia, ct.getManhanvien(),
						ct.getManhanvien());
				if (temps.size() != 0) {
					ChuaDanhGia360 cd = new ChuaDanhGia360(ct.getManhanvien(), ct.getManhanvien());
					chuaDanhGia360s.add(cd);
				}
			}
			if (ct.getDiem1() == 0) {
				if (!"".equals(ct.getNv1())) {
					List<ChiTietDanhGia> temps = baoCaoService.danhsachchuadanhgiaBD(kyDanhGia, ct.getNv1(),
							ct.getManhanvien());
					if (temps.size() != 0) {
						ChuaDanhGia360 cd = new ChuaDanhGia360(ct.getNv1(), ct.getManhanvien());
						chuaDanhGia360s.add(cd);
					}
				}
			}
			if (ct.getDiem2() == 0) {
				if (!"".equals(ct.getNv2())) {
					List<ChiTietDanhGia> temps = baoCaoService.danhsachchuadanhgiaBD(kyDanhGia, ct.getNv2(),
							ct.getManhanvien());
					if (temps.size() != 0) {
						ChuaDanhGia360 cd = new ChuaDanhGia360(ct.getNv2(), ct.getManhanvien());
						chuaDanhGia360s.add(cd);
					}
				}
			}
			if (ct.getDiem3() == 0) {
				if (!"".equals(ct.getNv3())) {
					List<ChiTietDanhGia> temps = baoCaoService.danhsachchuadanhgiaBD(kyDanhGia, ct.getNv3(),
							ct.getManhanvien());
					if (temps.size() != 0) {
						ChuaDanhGia360 cd = new ChuaDanhGia360(ct.getNv3(), ct.getManhanvien());
						chuaDanhGia360s.add(cd);
					}
				}
			}
			if (ct.getDiem4() == 0) {
				if (!"".equals(ct.getNv4())) {
					List<ChiTietDanhGia> temps = baoCaoService.danhsachchuadanhgiaBD(kyDanhGia, ct.getNv4(),
							ct.getManhanvien());
					if (temps.size() != 0) {
						ChuaDanhGia360 cd = new ChuaDanhGia360(ct.getNv4(), ct.getManhanvien());
						chuaDanhGia360s.add(cd);
					}
				}
			}
			if (ct.getDiem5() == 0) {
				if (!"".equals(ct.getNv5())) {
					List<ChiTietDanhGia> temps = baoCaoService.danhsachchuadanhgiaBD(kyDanhGia, ct.getNv5(),
							ct.getManhanvien());
					if (temps.size() != 0) {
						ChuaDanhGia360 cd = new ChuaDanhGia360(ct.getNv5(), ct.getManhanvien());
						chuaDanhGia360s.add(cd);
					}
				}
			}
			if (ct.getDiem6() == 0) {
				if (!"".equals(ct.getNv6())) {
					List<ChiTietDanhGia> temps = baoCaoService.danhsachchuadanhgiaBD(kyDanhGia, ct.getNv6(),
							ct.getManhanvien());
					if (temps.size() != 0) {
						ChuaDanhGia360 cd = new ChuaDanhGia360(ct.getNv6(), ct.getManhanvien());
						chuaDanhGia360s.add(cd);
					}
				}
			}
		}
		Map<String, List<ChuaDanhGia360>> datagroups1 = chuaDanhGia360s.stream().collect(
				Collectors.groupingBy(p -> p.getManhanvien(), Collectors.toList()));
		for (String manv : datagroups1.keySet()) {
			List<ChuaDanhGia360> invs = datagroups1.get(manv);
			try {
				String nhanvien = "";
				for (int j = 0; j < invs.size(); j++) {
					Member mem1 = memberServicePublic.findByCode(invs.get(j).getNhanvienchuadanhgia());
					if (mem1 != null) {
						nhanvien += mem1.getName() + ";";
					}
				}
				Member mem = memberServicePublic.findByCode(manv);
				if (mem != null) {
					ChuaDanhGia360 cd = new ChuaDanhGia360(mem.getDepartment().getName(), mem.getCode(), mem.getName(),
							nhanvien);
					this.chuaDanhGia360s.add(cd);
				}
			} catch (Exception e) {
			}

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

	public List<KetQuaDanhGia360> getKetQuaDanhGia360nhanviens() {
		return ketQuaDanhGia360nhanviens;
	}

	public void setKetQuaDanhGia360nhanviens(List<KetQuaDanhGia360> ketQuaDanhGia360nhanviens) {
		this.ketQuaDanhGia360nhanviens = ketQuaDanhGia360nhanviens;
	}

	public List<KetQuaDanhGia360> getKetQuaDanhGia360quanlys() {
		return ketQuaDanhGia360quanlys;
	}

	public void setKetQuaDanhGia360quanlys(List<KetQuaDanhGia360> ketQuaDanhGia360quanlys) {
		this.ketQuaDanhGia360quanlys = ketQuaDanhGia360quanlys;
	}

	public List<ChuaDanhGia360> getChuaDanhGia360s() {
		return chuaDanhGia360s;
	}

	public void setChuaDanhGia360s(List<ChuaDanhGia360> chuaDanhGia360s) {
		this.chuaDanhGia360s = chuaDanhGia360s;
	}

}
