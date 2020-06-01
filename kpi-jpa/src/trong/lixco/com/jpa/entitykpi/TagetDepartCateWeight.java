package trong.lixco.com.jpa.entitykpi;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
/**
 * @author vantrong
 * @Time 16-04-2018 12:08:21
 * Nhom Muc tieu phong nam
 */
@Entity
public class TagetDepartCateWeight{
	@Id
	@GeneratedValue(strategy = IDENTITY)
	protected Long id;
	private int year;
	private int weigth;//trong so nhom
	private String codeDepart;
	@ManyToOne
	private TagetDepartCate tagetDepartCate;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	
	public int getWeigth() {
		return weigth;
	}
	public void setWeigth(int weigth) {
		this.weigth = weigth;
	}
	public TagetDepartCate getTagetDepartCate() {
		return tagetDepartCate;
	}
	public void setTagetDepartCate(TagetDepartCate tagetDepartCate) {
		this.tagetDepartCate = tagetDepartCate;
	}
	public String getCodeDepart() {
		return codeDepart;
	}
	public void setCodeDepart(String codeDepart) {
		this.codeDepart = codeDepart;
	}
	
}
