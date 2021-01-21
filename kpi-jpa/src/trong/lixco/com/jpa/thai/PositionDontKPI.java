package trong.lixco.com.jpa.thai;

import javax.persistence.Entity;
import javax.persistence.Table;

import trong.lixco.com.jpa.entitykpi.AbstractEntity;

@Entity
@Table(name = "position_dont_kpi")
public class PositionDontKPI extends AbstractEntity {

	private String position_code;

	public PositionDontKPI() {
		super();
	}

	public PositionDontKPI(String position_code) {
		super();
		this.position_code = position_code;
	}

	public String getPosition_code() {
		return position_code;
	}

	public void setPosition_code(String position_code) {
		this.position_code = position_code;
	}
}
