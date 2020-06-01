package trong.lixco.com.classInfor;


public class ChuaDanhGia360 {
	private String phongban;
	private String manhanvien;
	private String tennhanvien;
	private String nhanvienchuadanhgia;

	
	public ChuaDanhGia360(String manhanvien, String nhanvienchuadanhgia) {
		this.manhanvien = manhanvien;
		this.nhanvienchuadanhgia = nhanvienchuadanhgia;
	}
	public ChuaDanhGia360(String phongban,String manhanvien,String tennhanvien, String nhanvienchuadanhgia) {
		this.phongban=phongban;
		this.manhanvien = manhanvien;
		this.tennhanvien=tennhanvien;
		this.nhanvienchuadanhgia = nhanvienchuadanhgia;
	}

	public String getPhongban() {
		return phongban;
	}

	public void setPhongban(String phongban) {
		this.phongban = phongban;
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

	public String getNhanvienchuadanhgia() {
		return nhanvienchuadanhgia;
	}

	public void setNhanvienchuadanhgia(String nhanvienchuadanhgia) {
		this.nhanvienchuadanhgia = nhanvienchuadanhgia;
	}

	@Override
	public String toString() {
		return "ChuaDanhGia360 [phongban=" + phongban + ", manhanvien=" + manhanvien + ", tennhanvien=" + tennhanvien
				+ ", nhanvienchuadanhgia=" + nhanvienchuadanhgia + "]";
	}

}
