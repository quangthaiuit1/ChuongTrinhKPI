package trong.lixco.com.jpa.entity360;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import trong.lixco.com.jpa.entitykpi.AbstractEntity;

@Entity
public class CaiDatTiLe extends AbstractEntity {
	@ManyToOne
	private KyDanhGia kyDanhGia;
	private int kpiphong;
	private int kpicanhan;
	private int diemdanhgia;
	private int diemphongtrao;
	private int diemtruvipham;
	public KyDanhGia getKyDanhGia() {
		return kyDanhGia;
	}
	public void setKyDanhGia(KyDanhGia kyDanhGia) {
		this.kyDanhGia = kyDanhGia;
	}
	public int getKpiphong() {
		return kpiphong;
	}
	public void setKpiphong(int kpiphong) {
		this.kpiphong = kpiphong;
	}
	public int getKpicanhan() {
		return kpicanhan;
	}
	public void setKpicanhan(int kpicanhan) {
		this.kpicanhan = kpicanhan;
	}
	public int getDiemdanhgia() {
		return diemdanhgia;
	}
	public void setDiemdanhgia(int diemdanhgia) {
		this.diemdanhgia = diemdanhgia;
	}
	public int getDiemphongtrao() {
		return diemphongtrao;
	}
	public void setDiemphongtrao(int diemphongtrao) {
		this.diemphongtrao = diemphongtrao;
	}
	public int getDiemtruvipham() {
		return diemtruvipham;
	}
	public void setDiemtruvipham(int diemtruvipham) {
		this.diemtruvipham = diemtruvipham;
	}
	
}
