package trong.lixco.com.classInfor;

import java.util.List;

import trong.lixco.com.jpa.entitykpi.OrientationPerson;

public class OrienInfoEmpl {
	private String namePos;
	private List<OrientationPerson> orientationPersons;
	public String getNamePos() {
		return namePos;
	}
	public void setNamePos(String namePos) {
		this.namePos = namePos;
	}
	public List<OrientationPerson> getOrientationPersons() {
		return orientationPersons;
	}
	public void setOrientationPersons(List<OrientationPerson> orientationPersons) {
		this.orientationPersons = orientationPersons;
	}

}
