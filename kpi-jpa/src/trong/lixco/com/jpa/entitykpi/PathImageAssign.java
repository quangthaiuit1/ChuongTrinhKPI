package trong.lixco.com.jpa.entitykpi;

/**
 * @author vantrong
 *
 * 01-08-2017
 */
import javax.persistence.Entity;

@Entity
// Duong dan luu tru du lieu chung minh
public class PathImageAssign extends AbstractEntity {
	private String path;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
