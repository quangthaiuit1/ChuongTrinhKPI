package trong.lixco.com.jpa.entitykpi;

import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * @author vantrong
 * @Time 16-04-2018 12:08:21 Nhom Muc tieu cong ty
 */
@Entity
public class TagetCate extends AbstractEntity {
	private String code;// ma nhom
	private String content; // noi dung
	
	
	@Transient
	private int weight;
	@Transient
	private double ratioWeight;

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

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public double getRatioWeight() {
		return ratioWeight;
	}

	public void setRatioWeight(double ratioWeight) {
		this.ratioWeight = ratioWeight;
	}

	
	
	

}
