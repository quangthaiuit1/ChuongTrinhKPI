package trong.lixco.com.jpa.entitykpi;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author vantrong
 * @Time 16-04-2018 12:08:21 cong thuc tinh KPI ca nhan
 */
@Entity
public class FormulaKPI {
	@Id
	@GeneratedValue(strategy = IDENTITY)
	protected Long id;
	private String note;// ghi chu/ giai thich cong thuc
	private String code;//cong thuc chung
	private String codeVSGood;//So sanh dat
	private String codeGood;//cong thuc dat
	private String codeVSNotGood;//cong thuc khong dat
	private String codeNotGood;//cong thuc khong dat
	private String comment;// ghi chú
	
	private boolean max100=false;
	private boolean min0=false;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getCodeGood() {
		return codeGood;
	}
	public void setCodeGood(String codeGood) {
		this.codeGood = codeGood;
	}
	public String getCodeNotGood() {
		return codeNotGood;
	}
	public void setCodeNotGood(String codeNotGood) {
		this.codeNotGood = codeNotGood;
	}
	public String getCodeVSGood() {
		return codeVSGood;
	}
	public void setCodeVSGood(String codeVSGood) {
		this.codeVSGood = codeVSGood;
	}
	public String getCodeVSNotGood() {
		return codeVSNotGood;
	}
	public void setCodeVSNotGood(String codeVSNotGood) {
		this.codeVSNotGood = codeVSNotGood;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public boolean isMax100() {
		return max100;
	}
	public void setMax100(boolean max100) {
		this.max100 = max100;
	}
	public boolean isMin0() {
		return min0;
	}
	public void setMin0(boolean min0) {
		this.min0 = min0;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FormulaKPI other = (FormulaKPI) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	

}
