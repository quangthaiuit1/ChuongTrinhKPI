package trong.lixco.com.jpa.entitykpi;

import javax.persistence.Entity;
/**
 * @author vantrong
 * @Time 16-04-2018 12:08:21
 * Dinh huong KPI ca nhan
 */
@Entity
public class BehaviourPerson extends AbstractEntity {
	private String code;//ma
	private String content;//noi dung
	private int minusPoint;//diem tru
	private boolean isHeader=false;// tieu de
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getMinusPoint() {
		return minusPoint;
	}
	public void setMinusPoint(int minusPoint) {
		this.minusPoint = minusPoint;
	}
	public boolean isHeader() {
		return isHeader;
	}
	public void setHeader(boolean isHeader) {
		this.isHeader = isHeader;
	}
	
	
}