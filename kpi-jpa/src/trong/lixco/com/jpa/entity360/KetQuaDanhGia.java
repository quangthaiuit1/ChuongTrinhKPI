package trong.lixco.com.jpa.entity360;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import trong.lixco.com.jpa.entitykpi.AbstractEntity;

@Entity
public class KetQuaDanhGia extends AbstractEntity {
	@ManyToOne
	private KyDanhGia kyDanhGia;
	private String nvduocdanhgia;
	private String nvdanhgia;
	private String maCauHoi;
	private String cauHoi;
	private double diem;
	public KyDanhGia getKyDanhGia() {
		return kyDanhGia;
	}
	public void setKyDanhGia(KyDanhGia kyDanhGia) {
		this.kyDanhGia = kyDanhGia;
	}
	public String getNvduocdanhgia() {
		return nvduocdanhgia;
	}
	public void setNvduocdanhgia(String nvduocdanhgia) {
		this.nvduocdanhgia = nvduocdanhgia;
	}
	public String getNvdanhgia() {
		return nvdanhgia;
	}
	public void setNvdanhgia(String nvdanhgia) {
		this.nvdanhgia = nvdanhgia;
	}

	public String getCauHoi() {
		return cauHoi;
	}
	public void setCauHoi(String cauHoi) {
		this.cauHoi = cauHoi;
	}
	public double getDiem() {
		return diem;
	}
	public void setDiem(double diem) {
		this.diem = diem;
	}
	public String getMaCauHoi() {
		return maCauHoi;
	}
	public void setMaCauHoi(String maCauHoi) {
		this.maCauHoi = maCauHoi;
	}
	
	
}
