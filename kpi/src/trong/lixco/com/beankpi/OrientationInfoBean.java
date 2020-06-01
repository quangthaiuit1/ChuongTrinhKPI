package trong.lixco.com.beankpi;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;
import org.omnifaces.cdi.ViewScoped;

import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.classInfor.OrienInfoEmpl;
import trong.lixco.com.ejb.servicekpi.EmpPJobService;
import trong.lixco.com.ejb.servicekpi.OrientationPersonService;
import trong.lixco.com.ejb.servicekpi.PositionJobService;
import trong.lixco.com.jpa.entitykpi.EmpPJob;
import trong.lixco.com.jpa.entitykpi.OrientationPerson;

@Named
@ViewScoped
// Dinh huong KPI ca nhan
public class OrientationInfoBean extends AbstractBean<OrientationPerson> {
	private static final long serialVersionUID = 1L;
	@Inject
	private OrientationPersonService orientationPersonService;
	@Inject
	private Logger logger;

	private List<OrienInfoEmpl> orienInfoEmpls;

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Inject
	private PositionJobService positionJobService;
	@Inject
	private EmpPJobService empPJobService;

	@Override
	public void initItem() {
		orienInfoEmpls = new ArrayList<OrienInfoEmpl>();
		try {
			String employeeCode = getAccount().getMember().getCode();
			List<EmpPJob> empPJobs = empPJobService.findByEmployee(employeeCode);
			if (getAccount().isAdmin())
				empPJobs = empPJobService.findAll();
			for (int i = 0; i < empPJobs.size(); i++) {
				try {
					OrienInfoEmpl orEmpl = new OrienInfoEmpl();
					orEmpl.setNamePos(positionJobService.findByCode(empPJobs.get(i).getCodePJob()).getName());
					orEmpl.setOrientationPersons(orientationPersonService.findSearch(empPJobs.get(i).getCodePJob()));
					orienInfoEmpls.add(orEmpl);
				} catch (Exception e) {
				}

			}

		} catch (Exception e) {
		}
	}

	public List<OrienInfoEmpl> getOrienInfoEmpls() {
		return orienInfoEmpls;
	}

	public void setOrienInfoEmpls(List<OrienInfoEmpl> orienInfoEmpls) {
		this.orienInfoEmpls = orienInfoEmpls;
	}

}
