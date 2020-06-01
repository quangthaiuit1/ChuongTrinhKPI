package trong.lixco.com.jpa.entitykpi;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
/**
 * @author vantrong
 * @Time 16-04-2018 12:08:21
 * Muc tieu phong trong nam
 */
@Entity
public class TagetDepart extends AbstractEntity {
	private int year;// nam
	private String code; // ma
	private String content; //noi dung
	private int weighted;//trong so
	private int weightParent;//trong so nhom chinh
	private String codeDepart;
	
	@ManyToOne
	private TagetDepartCate kTagetDepartCate;
	
	public TagetDepart(){
		year=new Date().getYear()+1900;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
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
	
	public int getWeighted() {
		return weighted;
	}
	public void setWeighted(int weighted) {
		this.weighted = weighted;
	}

	public TagetDepartCate getkTagetDepartCate() {
		return kTagetDepartCate;
	}
	public void setkTagetDepartCate(TagetDepartCate kTagetDepartCate) {
		this.kTagetDepartCate = kTagetDepartCate;
	}
	public int getWeightParent() {
		return weightParent;
	}
	public void setWeightParent(int weightParent) {
		this.weightParent = weightParent;
	}
	public String getCodeDepart() {
		return codeDepart;
	}
	public void setCodeDepart(String codeDepart) {
		this.codeDepart = codeDepart;
	}
}
