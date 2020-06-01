package trong.lixco.com.jpa.entitykpi;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * @author vantrong
 * @Time 16-04-2018 12:08:21 Chuc vu (trong KPI theo mo ta cong viec)
 */
@Entity
public class EmpPJob extends AbstractEntity {
	private String codeEmp;
	private String codePJob;

	public String getCodeEmp() {
		return codeEmp;
	}

	public void setCodeEmp(String codeEmp) {
		this.codeEmp = codeEmp;
	}

	public String getCodePJob() {
		return codePJob;
	}

	public void setCodePJob(String codePJob) {
		this.codePJob = codePJob;
	}


}
