package trong.lixco.com.jpa.entitykpi;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
/**
 * @author vantrong
 * @Time 16-04-2018 12:08:21
 * Trach nhiem va nhiem vu
 */
@Entity
public class MissionResponsibility extends AbstractEntity {
	private String no;// so thu tu
	private String content;//noi dung
	private String frequencyRate; // tan xuat (ngay/tuân/thang)
	private double weighted;// trong so
	private String criterion;//tieu chi danh gia
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
	public String getFrequencyRate() {
		return frequencyRate;
	}
	public void setFrequencyRate(String frequencyRate) {
		this.frequencyRate = frequencyRate;
	}
	public double getWeighted() {
		return weighted;
	}
	public void setWeighted(double weighted) {
		this.weighted = weighted;
	}
	public String getCriterion() {
		return criterion;
	}
	public void setCriterion(String criterion) {
		this.criterion = criterion;
	}

	
	public DescribeJob getDescribeJob() {
		return describeJob;
	}
	public void setDescribeJob(DescribeJob describeJob) {
		this.describeJob = describeJob;
	}
	
	
}
