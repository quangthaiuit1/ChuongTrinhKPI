package trong.lixco.com.jpa.entity360;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import trong.lixco.com.jpa.entitykpi.AbstractEntity;

@Entity
public class ChiTietDanhGia extends AbstractEntity {
	@ManyToOne
	private KyDanhGia kyDanhGia;
	private String manhanvien;
	private String tennhanvien;
	private double diem0=0;
	private String nv1;
	private double diem1=0;
	private String nv2;
	private double diem2=0;
	private String nv3;
	private double diem3=0;
	private String nv4;
	private double diem4=0;
	private String nv5;
	private double diem5=0;
	private String nv6;
	private double diem6=0;
	@Transient
	private double sodiem=0;
	@Transient
	private String phongban;
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
	public String getNv1() {
		return nv1;
	}
	public void setNv1(String nv1) {
		this.nv1 = nv1;
	}
	public String getNv2() {
		return nv2;
	}
	public void setNv2(String nv2) {
		this.nv2 = nv2;
	}
	public String getNv3() {
		return nv3;
	}
	public void setNv3(String nv3) {
		this.nv3 = nv3;
	}
	public String getNv4() {
		return nv4;
	}
	public void setNv4(String nv4) {
		this.nv4 = nv4;
	}
	public String getNv5() {
		return nv5;
	}
	public void setNv5(String nv5) {
		this.nv5 = nv5;
	}
	public String getNv6() {
		return nv6;
	}
	public void setNv6(String nv6) {
		this.nv6 = nv6;
	}
	public double getSodiem() {
		return sodiem;
	}
	public void setSodiem(double sodiem) {
		this.sodiem = sodiem;
	}
	public double getDiem1() {
		return diem1;
	}
	public void setDiem1(double diem1) {
		this.diem1 = diem1;
	}
	public double getDiem2() {
		return diem2;
	}
	public void setDiem2(double diem2) {
		this.diem2 = diem2;
	}
	public double getDiem3() {
		return diem3;
	}
	public void setDiem3(double diem3) {
		this.diem3 = diem3;
	}
	public double getDiem4() {
		return diem4;
	}
	public void setDiem4(double diem4) {
		this.diem4 = diem4;
	}
	public double getDiem5() {
		return diem5;
	}
	public void setDiem5(double diem5) {
		this.diem5 = diem5;
	}
	public double getDiem6() {
		return diem6;
	}
	public void setDiem6(double diem6) {
		this.diem6 = diem6;
	}
	public String getPhongban() {
		return phongban;
	}
	public void setPhongban(String phongban) {
		this.phongban = phongban;
	}
	public double getDiem0() {
		return diem0;
	}
	public void setDiem0(double diem0) {
		this.diem0 = diem0;
	}
	
	
}
