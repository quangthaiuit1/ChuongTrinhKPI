package trong.lixco.com.jpa.entitykpi;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
/**
 * @author vantrong
 * @Time 16-04-2018 12:08:21
 * Quan he ben ngoai
 */
@Entity
public class RelaExternal extends AbstractEntity {
	private String object;//doi tuong
	private String relationship;//moi quan he
	
	@ManyToOne
	private DescribeJob describeJob;
	
	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public String getRelationship() {
		return relationship;
	}

	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}

	public DescribeJob getDescribeJob() {
		return describeJob;
	}

	public void setDescribeJob(DescribeJob describeJob) {
		this.describeJob = describeJob;
	}


	
}
