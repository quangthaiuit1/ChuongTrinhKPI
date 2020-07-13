package trong.lixco.com.jpa.entitykpi;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;
/**
 * @author vantrong
 * @Time 16-04-2018 12:08:21
 * KPI ca nhan theo thang
 */
@Entity
public class KPIPerson extends AbstractEntity {
	private String codeEmp;//Nhan vien
	@Transient
	private String nameEmp;
	@Transient
	private String nameDepart;
	private int kmonth;//thang
	private int kyear;//nam
	
	private Date dateRecei;//ngay nhan/va duy tieu chi danh gia
	private Date dateAssignResult;//ngay duyet ket qua danh gia
	
	private boolean isSignKPI=false;//Duyet cac tieu chi KPI
	private boolean isSignResultKPI=false;//Duyet ket quan KPI
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "kpiPerson")
	private List<KPIPersonOfMonth> kpiPersonOfMonths;
	private double total=0.0;
	private double totalHV=0.0;//Diem hanh vi
	private double totalCV=0.0;// dem cong viec
	
	private String noteSign;
	public KPIPerson(){
		Date d=new Date();
		this.kmonth=d.getMonth()+1;
		this.kyear=d.getYear()+1900;
	}


	public String getCodeEmp() {
		return codeEmp;
	}


	public void setCodeEmp(String codeEmp) {
		this.codeEmp = codeEmp;
	}


	public int getKmonth() {
		return kmonth;
	}

	public void setKmonth(int kmonth) {
		this.kmonth = kmonth;
	}

	public int getKyear() {
		return kyear;
	}

	public void setKyear(int kyear) {
		this.kyear = kyear;
	}

	public boolean isSignKPI() {
		return isSignKPI;
	}

	public void setSignKPI(boolean isSignKPI) {
		this.isSignKPI = isSignKPI;
	}

	public boolean isSignResultKPI() {
		return isSignResultKPI;
	}

	public void setSignResultKPI(boolean isSignResultKPI) {
		this.isSignResultKPI = isSignResultKPI;
	}
	public List<KPIPersonOfMonth> getKpiPersonOfMonths() {
		return kpiPersonOfMonths;
	}

	public void setKpiPersonOfMonths(List<KPIPersonOfMonth> kpiPersonOfMonths) {
		this.kpiPersonOfMonths = kpiPersonOfMonths;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public Date getDateRecei() {
		return dateRecei;
	}

	public void setDateRecei(Date dateRecei) {
		this.dateRecei = dateRecei;
	}

	public Date getDateAssignResult() {
		return dateAssignResult;
	}

	public void setDateAssignResult(Date dateAssignResult) {
		this.dateAssignResult = dateAssignResult;
	}

	public double getTotalHV() {
		return totalHV;
	}

	public void setTotalHV(double totalHV) {
		this.totalHV = totalHV;
	}

	public double getTotalCV() {
		return totalCV;
	}

	public void setTotalCV(double totalCV) {
		this.totalCV = totalCV;
	}

	public String getNameEmp() {
		return nameEmp;
	}


	public void setNameEmp(String nameEmp) {
		this.nameEmp = nameEmp;
	}


	public String getNameDepart() {
		return nameDepart;
	}


	public void setNameDepart(String nameDepart) {
		this.nameDepart = nameDepart;
	}


	public String getNoteSign() {
		return noteSign;
	}


	public void setNoteSign(String noteSign) {
		this.noteSign = noteSign;
	}


	@Override
	public String toString() {
		return "KPIPerson [employee=" + codeEmp + ", kmonth=" + kmonth + ", kyear=" + kyear + ", dateRecei="
				+ dateRecei + ", dateAssignResult=" + dateAssignResult + ", isSignKPI=" + isSignKPI
				+ ", isSignResultKPI=" + isSignResultKPI + ", total=" + total + ", totalHV=" + totalHV + ", totalCV="
				+ totalCV + ", id=" + id + ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate
				+ ", createdUser=" + createdUser + ", note=" + note + "]";
	}

	
}