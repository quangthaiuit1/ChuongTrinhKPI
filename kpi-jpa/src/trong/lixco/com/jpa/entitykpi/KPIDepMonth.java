package trong.lixco.com.jpa.entitykpi;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

/**
 * @author vantrong
 * @Time 16-04-2018 12:08:21 KPI thang phong
 */
@Entity
public class KPIDepMonth extends AbstractEntity {
	private int month;
	private int year;// nam
	private double result;
	private Date dateRecei;
	private Date dateAssignResult;
	private String codeDepart;
	@Transient
	private String nameDepart;
	private boolean isSignKPI = false;// Duyet cac tieu chi KPI
	private boolean isSignResultKPI = false;// Duyet ket quan KPI
	private boolean isSignKPIBLD = false;// Duyet cac tieu chi KPI
	private boolean isSignResultKPIBLD = false;// Duyet ket quan KPI
	private String noteSign;
	private String noteSignBLD;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "kpiDepMonth")
	private List<KPIDepOfMonth> kpiDepOfMonths;

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public double getResult() {
		return result;
	}

	public void setResult(double result) {
		this.result = result;
	}

	public String getCodeDepart() {
		return codeDepart;
	}

	public void setCodeDepart(String codeDepart) {
		this.codeDepart = codeDepart;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public List<KPIDepOfMonth> getKpiDepOfMonths() {
		return kpiDepOfMonths;
	}

	public void setKpiDepOfMonths(List<KPIDepOfMonth> kpiDepOfMonths) {
		this.kpiDepOfMonths = kpiDepOfMonths;
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

	public String getNameDepart() {
		return nameDepart;
	}

	public void setNameDepart(String nameDepart) {
		this.nameDepart = nameDepart;
	}

	public boolean isSignKPIBLD() {
		return isSignKPIBLD;
	}

	public void setSignKPIBLD(boolean isSignKPIBLD) {
		this.isSignKPIBLD = isSignKPIBLD;
	}

	public boolean isSignResultKPIBLD() {
		return isSignResultKPIBLD;
	}

	public void setSignResultKPIBLD(boolean isSignResultKPIBLD) {
		this.isSignResultKPIBLD = isSignResultKPIBLD;
	}

	public String getNoteSign() {
		return noteSign;
	}

	public void setNoteSign(String noteSign) {
		this.noteSign = noteSign;
	}

	public String getNoteSignBLD() {
		return noteSignBLD;
	}

	public void setNoteSignBLD(String noteSignBLD) {
		this.noteSignBLD = noteSignBLD;
	}

	@Override
	public String toString() {
		return "KPIDepMonth [month=" + month + ", year=" + year + ", result=" + result + ", dateRecei=" + dateRecei
				+ ", dateAssignResult=" + dateAssignResult + ", departmentParent=" + codeDepart + ", isSignKPI="
				+ isSignKPI + ", isSignResultKPI=" + isSignResultKPI + ", id=" + id + ", createdDate=" + createdDate
				+ ", modifiedDate=" + modifiedDate + ", createdUser=" + createdUser + ", note=" + note + "]";
	}

}
