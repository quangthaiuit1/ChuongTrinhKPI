package trong.lixco.com.jpa.entity360;

import javax.persistence.Entity;

import trong.lixco.com.jpa.entitykpi.AbstractEntity;

@Entity
public class CauHoi extends AbstractEntity {

	private int nhomcauhoi;
	private int macauhoi;
	private String noidung;

	public int getNhomcauhoi() {
		return nhomcauhoi;
	}

	public void setNhomcauhoi(int nhomcauhoi) {
		this.nhomcauhoi = nhomcauhoi;
	}

	public int getMacauhoi() {
		return macauhoi;
	}

	public void setMacauhoi(int macauhoi) {
		this.macauhoi = macauhoi;
	}

	public String getNoidung() {
		return noidung;
	}

	public void setNoidung(String noidung) {
		this.noidung = noidung;
	}
}
