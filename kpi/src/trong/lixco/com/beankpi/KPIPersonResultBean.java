package trong.lixco.com.beankpi;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.joda.time.DateTime;
import org.omnifaces.cdi.ViewScoped;

import trong.lixco.com.account.servicepublics.DepartmentServicePublic;
import trong.lixco.com.account.servicepublics.DepartmentServicePublicProxy;
import trong.lixco.com.account.servicepublics.Member;
import trong.lixco.com.account.servicepublics.MemberServicePublic;
import trong.lixco.com.account.servicepublics.MemberServicePublicProxy;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.ejb.servicekpi.KPIPersonService;
import trong.lixco.com.jpa.entitykpi.KPIPerson;

@ManagedBean
@ViewScoped
// KPI ca nhan
public class KPIPersonResultBean extends AbstractBean<KPIPerson> {
	private static final long serialVersionUID = 1L;
	private List<KPIPerson> kPIPersons;
	private int monthSearch = 0;
	private int yearSearch = 0;

	DepartmentServicePublic departmentServicePublic;
	MemberServicePublic memberServicePublic;

	@Inject
	private KPIPersonService kPIPersonService;
	@Inject
	private Logger logger;

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Override
	public void initItem() {
		kPIPersons = new ArrayList<KPIPerson>();
		DateTime dt = new DateTime();
		monthSearch = dt.getMonthOfYear();
		yearSearch = dt.getYear();

		departmentServicePublic = new DepartmentServicePublicProxy();
		memberServicePublic = new MemberServicePublicProxy();

		searchItem();
	}

	public void searchItem() {
		try {
			List<String> members = new ArrayList<String>();
			Member mems[] = null;
			String codeDepart = getAccount().getMember().getDepartment().getCode();
			mems = memberServicePublic.findByCodeDepart(codeDepart);
			if (mems != null)
				for (int i = 0; i < mems.length; i++) {
					members.add(mems[i].getCode());
				}
			kPIPersons = kPIPersonService.findRange(null, monthSearch, yearSearch, members);

			for (int i = 0; i < kPIPersons.size(); i++) {
				try {
					kPIPersons.get(i).setNameEmp(
							memberServicePublic.findByCode(kPIPersons.get(i).getCodeEmp()).getName());
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {

		}
	}

	public List<KPIPerson> getkPIPersons() {
		return kPIPersons;
	}

	public void setkPIPersons(List<KPIPerson> kPIPersons) {
		this.kPIPersons = kPIPersons;
	}

	public int getMonthSearch() {
		return monthSearch;
	}

	public void setMonthSearch(int monthSearch) {
		this.monthSearch = monthSearch;
	}

	public int getYearSearch() {
		return yearSearch;
	}

	public void setYearSearch(int yearSearch) {
		this.yearSearch = yearSearch;
	}

}
