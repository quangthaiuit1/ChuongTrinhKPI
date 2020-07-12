package trong.lixco.com.thai.bean;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.joda.time.LocalDate;
import org.primefaces.context.RequestContext;

import trong.lixco.com.account.servicepublics.DepartmentServicePublic;
import trong.lixco.com.account.servicepublics.DepartmentServicePublicProxy;
import trong.lixco.com.account.servicepublics.Member;
import trong.lixco.com.account.servicepublics.MemberServicePublic;
import trong.lixco.com.account.servicepublics.MemberServicePublicProxy;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.ejb.servicekpi.EmpPJobService;
import trong.lixco.com.ejb.servicekpi.PositionJobService;
import trong.lixco.com.ejb.thai.kpi.PersonalOtherDetailService;
import trong.lixco.com.ejb.thai.kpi.PersonalOtherService;
import trong.lixco.com.ejb.thai.kpi.PersonalPerformanceService;
import trong.lixco.com.jpa.entitykpi.EmpPJob;
import trong.lixco.com.jpa.entitykpi.KPIPerson;
import trong.lixco.com.jpa.entitykpi.KPIPersonOfMonth;
import trong.lixco.com.jpa.entitykpi.PositionJob;
import trong.lixco.com.jpa.thai.KPIPersonalOther;
import trong.lixco.com.jpa.thai.KPIPersonalOtherDetail;
import trong.lixco.com.jpa.thai.KPIPersonalPerformance;
import trong.lixco.com.thai.bean.entities.InfoPersonalPerformance;
import trong.lixco.com.util.Notify;

@ManagedBean
@ViewScoped
public class PersonalOtherBean extends AbstractBean<KPIPersonalOther> implements Serializable {

	private static final long serialVersionUID = 1L;
	// list all nhan vien cua phong ban thuoc nhom khac
	private List<KPIPersonalOther> kpiPersonalOthers;
	private List<String> personalOthers;
	private KPIPersonalOther personSelected;
	private PositionJob positionJobSelect;
	private Member memberPlaying;
	// Chuc nang them hanh vi
	private List<InfoPersonalPerformance> listInfoPersonalPerformances;
	@Inject
	private EmpPJobService empPJobService;
	private List<PositionJob> positionJobs;
	private List<KPIPersonalPerformance> listKPIPersonalByEmployee;
	private List<KPIPersonalOtherDetail> listKPIPersonalOtherDetailUpdate;
	@Inject
	private PersonalPerformanceService PERSONAL_PERFORMANCE_SERVICE;
	// end them hanh vi

	private int monthSearch = 0;
	private int yearSearch = 0;
	private SimpleDateFormat sf;
	private Notify notify;
	@Inject
	private PositionJobService POSITION_JOB_SERVICE;
	@Inject
	private PersonalOtherService PERSONAL_OTHER_SERVICE;
	@Inject
	private PersonalOtherDetailService PERSONAL_OTHER_DETAIL_SERVICE;

	MemberServicePublic MEMBER_SERVICE_PUBLIC;
	DepartmentServicePublic DEPARTMENT_SERVICE_PUBLIC;

	@Override
	protected void initItem() {
		sf = new SimpleDateFormat("dd/MM/yyyy");
		LocalDate lc = new LocalDate();
		monthSearch = lc.getMonthOfYear();
		yearSearch = lc.getYear();

		listKPIPersonalOtherDetailUpdate = new ArrayList<>();

		MEMBER_SERVICE_PUBLIC = new MemberServicePublicProxy();
		DEPARTMENT_SERVICE_PUBLIC = new DepartmentServicePublicProxy();
		memberPlaying = getAccount().getMember();
		// get all member is personal other
		personalOthers = new ArrayList<>();
		personalOthers.add("0004068");
		personalOthers.add("0004022");
		personalOthers.add("0004003");
		personalOthers.add("0003224");
		personalOthers.add("0003152");
		kpiPersonalOthers = PERSONAL_OTHER_SERVICE.find(personalOthers, monthSearch, yearSearch);
	}

	public void searchItem() {
		try {
			kpiPersonalOthers = PERSONAL_OTHER_SERVICE.find(personalOthers, monthSearch, yearSearch);
		} catch (Exception e) {
		}
	}

	public void showDialogOrien(KPIPersonalOther personSelected) {
		this.personSelected = personSelected;
		listInfoPersonalPerformances = new ArrayList<>();
		notify = new Notify(FacesContext.getCurrentInstance());
		if (personSelected.getId() != null) {
			// tat ca vi tri cong viec
			positionJobs = new ArrayList<PositionJob>();
			List<EmpPJob> empPJobs = empPJobService.findByEmployee(personSelected.getCodeEmp());
			for (int i = 0; i < empPJobs.size(); i++) {
				PositionJob pj = POSITION_JOB_SERVICE.findByCode(empPJobs.get(i).getCodePJob());
				if (pj != null)
					positionJobs.add(pj);
			}

			if (positionJobs.size() != 0)
				positionJobSelect = positionJobs.get(0);
			// Thai
			// Tao danh sach code PJob theo tung nhan vien
			List<String> listCodePJob = new ArrayList<>();
			for (int i = 0; i < positionJobs.size(); i++) {
				listCodePJob.add(positionJobs.get(i).getCode());
			}
			// Tao list info
			listKPIPersonalByEmployee = PERSONAL_PERFORMANCE_SERVICE.find(listCodePJob);
			Map<String, List<KPIPersonalPerformance>> datagroups11 = listKPIPersonalByEmployee.stream()
					.collect(Collectors.groupingBy(a -> a.getCodePJob(), Collectors.toList()));

			listKPIPersonalByEmployee = new ArrayList<>();
			for (String key : datagroups11.keySet()) {
				List<KPIPersonalPerformance> invs = datagroups11.get(key);
				try {
					InfoPersonalPerformance tgi = new InfoPersonalPerformance();
					tgi.setPositionJobName(POSITION_JOB_SERVICE.findByCode(key).getName());
					tgi.setPersonalPerformances(invs);
					listInfoPersonalPerformances.add(tgi);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			// End Thai
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('widgetKPIPersonalOther').show();");
		} else {
			notify.warning("Chưa lưu thông tin phòng ban/nhân viên!");
		}
	}

	public void create() throws RemoteException {
		if (allowSave(null)) {
			// kiem tra xem da duoc tao moi chua
//			boolean isCreated = false;
			List<KPIPersonalOther> personalOtherIsCreated = PERSONAL_OTHER_SERVICE
					.find(memberPlaying.getDepartment().getName(), monthSearch, yearSearch);
			if (!personalOtherIsCreated.isEmpty()) {
				noticeError("KPI tháng " + monthSearch + " năm " + yearSearch + " đã được tạo");
			} else {
				// lay toan bo danh sach tu ben account luu xuong db
				for (int i = 0; i < personalOthers.size(); i++) {
					Member memberTemp = MEMBER_SERVICE_PUBLIC.findByCode(personalOthers.get(i));
					KPIPersonalOther temp = new KPIPersonalOther();
					temp.setCodeEmp(personalOthers.get(i));
					temp.setNameEmp(memberTemp.getName());
					temp.setNameDepart(memberTemp.getDepartment().getName());
					temp.setkMonth(monthSearch);
					temp.setkYear(yearSearch);
					temp.setTotal(100);
					KPIPersonalOther statusCreate = PERSONAL_OTHER_SERVICE.create(temp);
					if (statusCreate == null) {
						// ghi loi cho nay
						System.out.println("Loi");
					}
				}
				kpiPersonalOthers = PERSONAL_OTHER_SERVICE.find(personalOthers, monthSearch, yearSearch);
				notice("Tạo mới thành công");
			}
		} else {
			noticeError("Không có quyền");
		}
	}

	public void updateListKPIPersonalOther() {
		try {
			List<KPIPersonalOtherDetail> otherDetailsTemp = this.personSelected.getKpiPersonalOtherDetails();
			// Cap nhat list kpi hieu suat de selected khong bi lap
			for (int i = 0; i < listInfoPersonalPerformances.size(); i++) {
				for (int j = 0; j < listInfoPersonalPerformances.get(i).getPersonalPerformances().size(); j++) {
					if (listInfoPersonalPerformances.get(i).getPersonalPerformances().get(j).isSelect()) {
						boolean isExist = false;
						boolean verify = false;
						KPIPersonalOtherDetail item = new KPIPersonalOtherDetail();
						item.setContent(
								listInfoPersonalPerformances.get(i).getPersonalPerformances().get(j).getContent());
						item.setMinuspoint(
								listInfoPersonalPerformances.get(i).getPersonalPerformances().get(j).getMinuspoint());
						// check xem item nao duoc chon

						// check duoi DB co list detail chua
						if (otherDetailsTemp.size() == 0) {
							item.setKpiPersonalOther(personSelected);
							otherDetailsTemp.add(item);
							double totalTemp2 = this.personSelected.getTotal() - item.getMinuspoint();
							this.personSelected.setTotal(totalTemp2);
						} else {
							for (int k = 0; k < otherDetailsTemp.size(); k++) {
								if (otherDetailsTemp.get(k).getContent().equals(item.getContent())) {
									isExist = true;
								}
							}
							if (isExist == false) {
								item.setKpiPersonalOther(personSelected);
								otherDetailsTemp.add(item);
								double totalTemp2 = this.personSelected.getTotal() - item.getMinuspoint();
								this.personSelected.setTotal(totalTemp2);
							}
						}

////						item.setKpiPerson(this.personSelected);
//						if (listKPIPersonalOtherDetailUpdate.size() == 0) {
//							listKPIPersonalOtherDetailUpdate.add(item);
//							// Tru diem sau khi them thai do hanh vi tren giao dien
//							double totalTemp = this.personSelected.getTotal() - item.getMinuspoint();
//							this.personSelected.setTotal(totalTemp);
//							for (int a = 0; a < kpiPersonalOthers.size(); a++) {
//								if (kpiPersonalOthers.get(a).getCodeEmp().equals(this.personSelected.getCodeEmp())) {
//									kpiPersonalOthers.get(a).setTotal(personSelected.getTotal());
//								}
//							}
//
//						} else {
//							for (int h = 0; h < listKPIPersonalOtherDetailUpdate.size(); h++) {
////								if (item.getKpiPerson().getCodeEmp()
////										.equals(listKPIPersonalOtherDetailUpdate.get(h).getKpiPerson().getCodeEmp())) {
////									verify = true;
////									if (listBehaviour.get(h).getContentAppreciate()
////											.equals(item.getContentAppreciate())) {
////										isExist = true;
////										break;
////									}
////								}
//							}
//							if (verify == true && isExist == false) {
//								listKPIPersonalOtherDetailUpdate.add(item);
//								double totalTemp2 = this.personSelected.getTotal() - item.getMinuspoint();
//								this.personSelected.setTotal(totalTemp2);
////								for(int a = 0; a < kPIPersons.size(); a++) {
////									if(kPIPersons.get(a).getCodeEmp().equals(this.personSelected.getCodeEmp())) {
////										kPIPersons.get(a).setTotal(personSelected.getTotal());
////									}
////								}
//							}
//							if (verify == false && isExist == false) {
//								listKPIPersonalOtherDetailUpdate.add(item);
//								double totalTemp1 = this.personSelected.getTotal() - item.getMinuspoint();
////								this.personSelected.setTotal(totalTemp1);
////								for(int a = 0; a < kPIPersons.size(); a++) {
////									if(kPIPersons.get(a).getCodeEmp().equals(this.personSelected.getCodeEmp())) {
////										kPIPersons.get(a).setTotal(personSelected.getTotal());
////									}
////								}
//							}
//						}
						listInfoPersonalPerformances.get(i).getPersonalPerformances().get(j).setSelect(false);
					}
				}
			}
			KPIPersonalOther statusUpdate = PERSONAL_OTHER_SERVICE.update(this.personSelected);
			if (statusUpdate != null) {
				notice("Thành công");
			} else {
				noticeError("Lỗi");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	@Override
	protected Logger getLogger() {
		// TODO Auto-generated method stub
		return null;
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

	public List<KPIPersonalOther> getKpiPersonalOthers() {
		return kpiPersonalOthers;
	}

	public void setKpiPersonalOthers(List<KPIPersonalOther> kpiPersonalOthers) {
		this.kpiPersonalOthers = kpiPersonalOthers;
	}

	public List<InfoPersonalPerformance> getListInfoPersonalPerformances() {
		return listInfoPersonalPerformances;
	}

	public void setListInfoPersonalPerformances(List<InfoPersonalPerformance> listInfoPersonalPerformances) {
		this.listInfoPersonalPerformances = listInfoPersonalPerformances;
	}

}
