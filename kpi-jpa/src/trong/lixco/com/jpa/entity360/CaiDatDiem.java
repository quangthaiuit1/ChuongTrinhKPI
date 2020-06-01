package trong.lixco.com.jpa.entity360;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import trong.lixco.com.jpa.entitykpi.AbstractEntity;

@Entity
public class CaiDatDiem extends AbstractEntity {
	@ManyToOne
	private KyDanhGia kyDanhGia;
	private int vipham;
	private int phongtrao;
	public KyDanhGia getKyDanhGia() {
		return kyDanhGia;
	}
	public void setKyDanhGia(KyDanhGia kyDanhGia) {
		this.kyDanhGia = kyDanhGia;
	}
	public int getVipham() {
		return vipham;
	}
	public void setVipham(int vipham) {
		this.vipham = vipham;
	}
	public int getPhongtrao() {
		return phongtrao;
	}
	public void setPhongtrao(int phongtrao) {
		this.phongtrao = phongtrao;
	}
	
}
