package trong.lixco.com.jpa.entity360;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import trong.lixco.com.jpa.entitykpi.AbstractEntity;

@Entity
public class PhongTrao extends AbstractEntity {
	@ManyToOne
	private KyDanhGia kyDanhGia;
	private String manhanvien;
	private String tennhanvien;
	private double solanthamgia=0;
	public KyDanhGia getKyDanhGia() {
		return kyDanhGia;
	}
	public void setKyDanhGia(KyDanhGia kyDanhGia) {
		this.kyDanhGia = kyDanhGia;
	}
	public String getManhanvien() {
		return manhanvien;
	}
	public void setManhanvien(String manhanvien) {
		this.manhanvien = manhanvien;
	}
	public String getTennhanvien() {
		return tennhanvien;
	}
	public void setTennhanvien(String tennhanvien) {
		this.tennhanvien = tennhanvien;
	}
	public double getSolanthamgia() {
		return solanthamgia;
	}
	public void setSolanthamgia(double solanthamgia) {
		this.solanthamgia = solanthamgia;
	}
	
	
}
