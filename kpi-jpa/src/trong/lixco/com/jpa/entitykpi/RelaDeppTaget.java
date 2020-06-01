package trong.lixco.com.jpa.entitykpi;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import trong.lixco.com.jpa.entitykpi.Taget;

/**
 * @author vantrong
 * @Time 16-04-2018 12:08:21 
 */
@Entity
public class RelaDeppTaget {
	@Id
	@GeneratedValue(strategy = IDENTITY)
	protected Long id;
	private String codeDepart;

	@ManyToOne
	private Taget taget;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	public String getCodeDepart() {
		return codeDepart;
	}

	public void setCodeDepart(String codeDepart) {
		this.codeDepart = codeDepart;
	}

	public Taget getTaget() {
		return taget;
	}

	public void setTaget(Taget taget) {
		this.taget = taget;
	}

}
