package trong.lixco.com.jpa.entitykpi;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
/**
 * @author vantrong
 * @Time 16-04-2018 12:08:21
 * Tom tac cong viec
 */
@Entity
public class OrverviewJob extends AbstractEntity {
	private String no;// so thu tu
	private String content;//noi dung
	@ManyToOne
	private DescribeJob describeJob;
	public String getNo() {
		return no;
	}
	public void setNo(String no) {
		this.no = no;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public DescribeJob getDescribeJob() {
		return describeJob;
	}
	public void setDescribeJob(DescribeJob describeJob) {
		this.describeJob = describeJob;
	}
	
	
}