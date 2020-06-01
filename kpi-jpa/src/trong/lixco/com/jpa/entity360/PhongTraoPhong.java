package trong.lixco.com.jpa.entity360;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import trong.lixco.com.jpa.entitykpi.AbstractEntity;

@Entity
public class PhongTraoPhong extends AbstractEntity {
	@ManyToOne
	private KyDanhGia kyDanhGia;
	private String maphong;
	private String tenphong;
	private double sodiem=0;
	public KyDanhGia getKyDanhGia() {
		return kyDanhGia;
	}
	public void setKyDanhGia(KyDanhGia kyDanhGia) {
		this.kyDanhGia = kyDanhGia;
	}
	public String getMaphong() {
		return maphong;
	}
	public void setMaphong(String maphong) {
		this.maphong = maphong;
	}
	public String getTenphong() {
		return tenphong;
	}
	public void setTenphong(String tenphong) {
		this.tenphong = tenphong;
	}
	public double getSodiem() {
		return sodiem;
	}
	public void setSodiem(double sodiem) {
		this.sodiem = sodiem;
	}


	
}
