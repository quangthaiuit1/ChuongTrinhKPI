package trong.lixco.com.jpa.thai;

import javax.persistence.Entity;

import trong.lixco.com.jpa.entitykpi.AbstractEntity;
@Entity
public class BehaviourPersonOther extends AbstractEntity {
	private String code;// ma
	private String content;// noi dung
	private int minusPoint;// diem tru
	private boolean isHeader = false;// tieu de

	public String getCode() {
		return code;
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

	public void setCode(String code) {
		this.code = code;
	}
}
