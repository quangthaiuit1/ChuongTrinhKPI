package trong.lixco.com.thai.mail;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import trong.lixco.com.account.servicepublics.Department;
import trong.lixco.com.account.servicepublics.DepartmentServicePublic;
import trong.lixco.com.account.servicepublics.DepartmentServicePublicProxy;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.ejb.servicekpi.KPIDepMonthService;
import trong.lixco.com.jpa.entitykpi.KPIDepMonth;
import trong.lixco.com.thai.bean.Mail;
import trong.lixco.com.jpa.entitykpi.KPIPerson;

public class JobMailResultEvaluationDepartmentKPI extends AbstractBean<KPIPerson> implements Job {
	@Inject
	private KPIDepMonthService KPI_DEPARTMENT_MONTH_SERVICE; 
	private List<Department> allDepartment;
	private List<String> allCodeDepartment;
	private static DepartmentServicePublic DEPARTMENT_SERVICE_PUBLIC = new DepartmentServicePublicProxy();
	private List<Department> allDepartmentList;
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		//lay danh sach phong ban chua dang ki kpi
		ZonedDateTime nowDateTime = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
		int currentMonth = nowDateTime.getMonthValue();
//		List<KPIDepMonth> kpiDepMonths = KPI_DEPARTMENT_MONTH_SERVICE.find(2,nowDateTime.getYear());
		allCodeDepartment = new ArrayList<>();
		
		
		try {
			Department[] allDepartmentArray = DEPARTMENT_SERVICE_PUBLIC.findAll();
			allDepartmentList = Arrays.asList(allDepartmentArray);
			allDepartment = new ArrayList<>();
			for (int i = 0; i < allDepartmentArray.length; i++) {
				if (allDepartmentArray[i].getDepartment() != null) {
					if (allDepartmentArray[i].getDepartment().getId() == 191
							&& allDepartmentArray[i].getLevelDep().getLevel() == 2) {
						allDepartment.add(allDepartmentArray[i]);
						// lap de tao list code cua toan bo nhan vien
						allCodeDepartment.add(allDepartmentArray[i].getCode());
					}
				}
			}
			
			String[] allCodeDepartmentArray = new String[allCodeDepartment.size()];
			allCodeDepartmentArray = allCodeDepartment.toArray(allCodeDepartmentArray);
			System.out.println("thais");
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
		System.out.println("Reminder Notification:- " + new Date());
//		Mail.processSendMailPersonalSignKPI("yeucausuachua@lixco.com", "It@2019", "thai-dinhquang@lixco.com");
	}
	@Override
	protected void initItem() {
		// TODO Auto-generated method stub
		DEPARTMENT_SERVICE_PUBLIC = new DepartmentServicePublicProxy();
	}
	@Override
	protected Logger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}
}
