package trong.lixco.com.thai.bean.entities;

public class TongHopABC {
	private long id;
	private String tenphongban;
	private long tongso;
	private long loaiA;
	private long loaiB;
	private long loaiC;
	private long loaiD;
	private long khongxet;
	private long thaisan;
	private long nghiom;
	private String ghichu = "";

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTenphongban() {
		return tenphongban;
	}

	public void setTenphongban(String tenphongban) {
		this.tenphongban = tenphongban;
	}

	public long getTongso() {
		return tongso;
	}

	public void setTongso(long tongso) {
		this.tongso = tongso;
	}

	public long getLoaiA() {
		return loaiA;
	}

	public void setLoaiA(long loaiA) {
		this.loaiA = loaiA;
	}

	public long getLoaiB() {
		return loaiB;
	}

	public void setLoaiB(long loaiB) {
		this.loaiB = loaiB;
	}

	public long getLoaiC() {
		return loaiC;
	}

	public void setLoaiC(long loaiC) {
		this.loaiC = loaiC;
	}

	public long getLoaiD() {
		return loaiD;
	}

	public void setLoaiD(long loaiD) {
		this.loaiD = loaiD;
	}

	public long getKhongxet() {
		return khongxet;
	}

	public void setKhongxet(long khongxet) {
		this.khongxet = khongxet;
	}

	public long getThaisan() {
		return thaisan;
	}

	public void setThaisan(long thaisan) {
		this.thaisan = thaisan;
	}

	public long getNghiom() {
		return nghiom;
	}

	public void setNghiom(long nghiom) {
		this.nghiom = nghiom;
	}

	public String getGhichu() {
		return ghichu;
	}

	public void setGhichu(String ghichu) {
		this.ghichu = ghichu;
	}
}
