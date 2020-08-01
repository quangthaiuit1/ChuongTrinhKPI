package trong.lixco.com.thai.bean.entities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;
import org.omnifaces.cdi.ViewScoped;

import trong.lixco.com.account.servicepublics.AccountServicePublic;
import trong.lixco.com.account.servicepublics.AccountServicePublicProxy;
import trong.lixco.com.account.servicepublics.Department;
import trong.lixco.com.account.servicepublics.DepartmentServicePublic;
import trong.lixco.com.account.servicepublics.DepartmentServicePublicProxy;
import trong.lixco.com.account.servicepublics.LockTableServicePublic;
import trong.lixco.com.account.servicepublics.LockTableServicePublicProxy;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.ejb.servicekpi.KPIDepMonthService;
import trong.lixco.com.jpa.entitykpi.KPIDepMonth;

@Named
@ViewScoped
public class Reminder extends AbstractBean<KPIDepMonth>{
	private static final long serialVersionUID = 1L;
	private List<KPIDepMonth> allKpiDepByMonth;
	Date currentDate;

	private Thread _THREAD;
	@Inject
	KPIDepMonthService KPI_DEPARTMENT_SERVICE;
	private List<Department> allDepartment;
	private List<String> allCodeDepartment;
	private List<Department> allDepartmentList;
	private DepartmentServicePublic DEPARTMENT_SERVICE_PUBLIC;

	@Override
	protected void initItem() {
		currentDate = new Date();
		System.out.println("Thais dep trai");
		allKpiDepByMonth = KPI_DEPARTMENT_SERVICE.findAll();
	}

	public void start(int dateOfMonth, int hour, int minute, boolean runAtStart) {
		_THREAD = new Thread() {
			@Override
			public void run() {
				process(dateOfMonth, hour, minute, runAtStart);
			}
		};
		_THREAD.start();
	}

	public void stop() {
		_THREAD.stop();
	}

	private void process(int dateOfMonth, int hour, int minute, boolean runAtStart) {
		try {
			if (runAtStart) {
				excute();
			}
			Date date = new Date();
			int monthCurrent = date.getMonth();
			while (true) {
				long sleepTime = timeToSleep(monthCurrent, dateOfMonth, hour, minute, 0);
				Thread.sleep(sleepTime);
				excute();
				monthCurrent = monthCurrent + 1;

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private long timeToSleep(int month, int dateOfMonth, int hour, int minute, int second) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, dateOfMonth);
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, second);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTimeInMillis() - System.currentTimeMillis();
	}

	AccountServicePublic accountServicePublic;

	private LockTableServicePublic lockTableServicePublic;
	public void excute() {
		try {
//			Department[] allDepartmentArray = CommonService.findAll();
//			initItem();
				lockTableServicePublic = new LockTableServicePublicProxy();
				accountServicePublic = new AccountServicePublicProxy();

			DEPARTMENT_SERVICE_PUBLIC = new DepartmentServicePublicProxy();
			Department[] allDepartmentArr = DEPARTMENT_SERVICE_PUBLIC.findAll();
			List<Department> departmentHCMList = new ArrayList<>();
			
			// tao list department HCM
			for (int i = 0; i < allDepartmentArr.length; i++) {
				if (allDepartmentArr[i].getLevelDep().getLevel() == 2
						&& allDepartmentArr[i].getDepartment().getId() == 191) {
					departmentHCMList.add(allDepartmentArr[i]);
				}
			}
			Department[] departmentHCMArray = departmentHCMList.toArray(new Department[departmentHCMList.size()]);
			
			//tu danh sach phong se get danh sach mail truong phong
			//List kpi department month
			allKpiDepByMonth = KPI_DEPARTMENT_SERVICE.find(currentDate.getMonth(), currentDate.getYear()); 
			
			for (int i = 0; i < departmentHCMArray.length; i++) {
				boolean check = false;
				for (int j = 0; j < allKpiDepByMonth.size(); j++) {
					if (departmentHCMArray[i].getDepartment().getCode()
							.equals(allKpiDepByMonth.get(j).getCodeDepart())) {
						check = true;
					}
				}
				if (check) {
					System.out.println("size array: " + departmentHCMArray);
					departmentHCMArray = removeTheElement(departmentHCMArray, i);
				}
				System.out.println("size array: " + departmentHCMArray);
			}
//			
			System.out.println("thais");
//			List<String> listMail = new ArrayList<>();
//			listMail.add("quangthaiuit1@gmail.com");
//			listMail.add("thai-dinhquang@lixco.com");
//			listMail.add("trong-nguyenvan@lixco.com");
//			
//			Mail.processSendMailDepartmentSignKPI("yeucausuachua@lixco.com", "It@2019", listMail);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static Department[] removeTheElement(Department[] arr, int index) {

// If the array is empty 
// or the index is not in array range 
// return the original array 
		if (arr == null || index < 0 || index >= arr.length) {

			return arr;
		}

// Create another array of size one less 
		Department[] anotherArray = new Department[arr.length - 1];

// Copy the elements except the index 
// from original array to the other array 
		for (int i = 0, k = 0; i < arr.length; i++) {

// if the index is 
// the removal element index 
			if (i == index) {
				continue;
			}

// if the index is not 
// the removal element index 
			anotherArray[k++] = arr[i];
		}

// return the resultant array 
		return anotherArray;
	}
	
	public Date getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}

	@Override
	protected Logger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}
}
//static public void start(int hour, int dateOfMonth) throws SchedulerException, InterruptedException {
//
//// PHAN GUI MAIL KPI CA NHAN (27 HANG THANG)
////JobDetail jobMailPersonalSignKPI = JobBuilder.newJob(JobMailPersonalSignKPI.class)
////		.withIdentity("jobMailPersonalSignKPI", "group1").build();
////// Tao trigger nhac dki kpi cuoi thang
////StringBuilder cronPersonalSignKPI = new StringBuilder();
////cronPersonalSignKPI.append("0 11 ");
////cronPersonalSignKPI.append(hour + " " + dateOfMonth + " * ?");
////System.out.println(cronPersonalSignKPI.toString());
////
////Trigger triggerPersonalSignKPI = TriggerBuilder.newTrigger()
////		.withIdentity("cronTriggerPersonalSignKPI", "group1")
////		.withSchedule(CronScheduleBuilder.cronSchedule(cronPersonalSignKPI.toString())).build();
////
////Scheduler schedulerPersonalSignKPI = StdSchedulerFactory.getDefaultScheduler();
////if (schedulerPersonalSignKPI.checkExists(jobMailPersonalSignKPI.getKey())) {
////	schedulerPersonalSignKPI.deleteJob(jobMailPersonalSignKPI.getKey());
////}
////schedulerPersonalSignKPI.scheduleJob(jobMailPersonalSignKPI, triggerPersonalSignKPI);
////schedulerPersonalSignKPI.start();
//
//// PHAN GUI MAIL DANH GIA KET QUA KPI PHONG
//JobDetail jobMailResultEvaluationDepartment = JobBuilder.newJob(JobMailResultEvaluationDepartmentKPI.class)
//		.withIdentity("jobMailResultEvaluationDepartment", "group3").build();
//StringBuilder cronResultEvaluationDepartment = new StringBuilder();
//cronResultEvaluationDepartment.append("0 4 ");
//cronResultEvaluationDepartment.append(hour + " " + dateOfMonth + " * ?");
//
//Trigger triggerResultEvaluationDepartment = TriggerBuilder.newTrigger()
//		.withIdentity("cronResultEvaluation", "group3")
//		.withSchedule(CronScheduleBuilder.cronSchedule(cronResultEvaluationDepartment.toString())).build();
//Scheduler schedulerResultEvaluationDepartment = StdSchedulerFactory.getDefaultScheduler();
//
//if (schedulerResultEvaluationDepartment.checkExists(jobMailResultEvaluationDepartment.getKey())) {
//	schedulerResultEvaluationDepartment.deleteJob(jobMailResultEvaluationDepartment.getKey());
//}
//schedulerResultEvaluationDepartment.scheduleJob(jobMailResultEvaluationDepartment,
//		triggerResultEvaluationDepartment);
//schedulerResultEvaluationDepartment.start();
//
//// PHAN GUI MAIL THONG BAO DANG KI KPI PHONG
//JobDetail jobMailDepartmentSignKPI = JobBuilder.newJob(JobMailDepartmentSignKPI.class)
//		.withIdentity("jobMailDepartmentSignKPI", "group4").build();
//StringBuilder cronDepartmentSignKPI = new StringBuilder();
//cronDepartmentSignKPI.append("0 12 ");
//cronDepartmentSignKPI.append(hour + " " + dateOfMonth + " * ?");
//
//Trigger DepartmentSignKPI = TriggerBuilder.newTrigger().withIdentity("cronResultEvaluation", "group4")
//		.withSchedule(CronScheduleBuilder.cronSchedule(cronDepartmentSignKPI.toString())).build();
//Scheduler schedulerDepartmentSignKPI = StdSchedulerFactory.getDefaultScheduler();
//
//if (schedulerDepartmentSignKPI.checkExists(jobMailDepartmentSignKPI.getKey())) {
//	schedulerDepartmentSignKPI.deleteJob(jobMailDepartmentSignKPI.getKey());
//}
//schedulerDepartmentSignKPI.scheduleJob(jobMailDepartmentSignKPI, DepartmentSignKPI);
//schedulerDepartmentSignKPI.start();
//}
