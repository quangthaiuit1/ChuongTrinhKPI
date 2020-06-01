package trong.lixco.com.jpa.entity360;

import java.util.Date;

import javax.persistence.Entity;

import trong.lixco.com.jpa.entitykpi.AbstractEntity;

@Entity
public class KyDanhGia extends AbstractEntity {
	private String tenkydanhgia;//nam
	private Date ngaybatdau;
	private Date ngayketthuc;
	public String getTenkydanhgia() {
		return tenkydanhgia;
	}
	public void setTenkydanhgia(String tenkydanhgia) {
		this.tenkydanhgia = tenkydanhgia;
	}
	public Date getNgaybatdau() {
		return ngaybatdau;
	}
	public void setNgaybatdau(Date ngaybatdau) {
		this.ngaybatdau = ngaybatdau;
	}
	public Date getNgayketthuc() {
		return ngayketthuc;
	}
	public void setNgayketthuc(Date ngayketthuc) {
		this.ngayketthuc = ngayketthuc;
	} 
	
	
}
