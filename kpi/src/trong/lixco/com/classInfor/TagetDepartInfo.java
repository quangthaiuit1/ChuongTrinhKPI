package trong.lixco.com.classInfor;

import java.util.List;

import trong.lixco.com.jpa.entitykpi.TagetDepart;

public class TagetDepartInfo {
	private String nameDepart;
	private List<TagetDepart> tagetDeparts;
	public String getNameDepart() {
		return nameDepart;
	}
	public void setNameDepart(String nameDepart) {
		this.nameDepart = nameDepart;
	}
	public List<TagetDepart> getTagetDeparts() {
		return tagetDeparts;
	}
	public void setTagetDeparts(List<TagetDepart> tagetDeparts) {
		this.tagetDeparts = tagetDeparts;
	}
	

}
