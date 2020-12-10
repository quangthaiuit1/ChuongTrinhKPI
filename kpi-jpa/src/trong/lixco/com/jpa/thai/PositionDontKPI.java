package trong.lixco.com.jpa.thai;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import trong.lixco.com.jpa.entitykpi.AbstractEntity;
import trong.lixco.com.jpa.entitykpi.PositionJob;

@Entity
@Table(name = "position_dont_kpi")
public class PositionDontKPI extends AbstractEntity {
	
	@OneToOne
	private PositionJob positionjob;


	public PositionJob getPositionjob() {
		return positionjob;
	}

	public void setPositionjob(PositionJob positionjob) {
		this.positionjob = positionjob;
	}
}
