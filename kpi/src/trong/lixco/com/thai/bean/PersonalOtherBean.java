package trong.lixco.com.thai.bean;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;
import org.joda.time.LocalDate;
import org.primefaces.context.RequestContext;

import trong.lixco.com.account.servicepublics.Department;
import trong.lixco.com.account.servicepublics.DepartmentServicePublic;
import trong.lixco.com.account.servicepublics.DepartmentServicePublicProxy;
import trong.lixco.com.account.servicepublics.Member;
import trong.lixco.com.account.servicepublics.MemberServicePublic;
import trong.lixco.com.account.servicepublics.MemberServicePublicProxy;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.ejb.servicekpi.EmpPJobService;
import trong.lixco.com.ejb.servicekpi.KPIPersonService;
import trong.lixco.com.ejb.servicekpi.PositionJobService;
import trong.lixco.com.ejb.thai.kpi.PersonalOtherDetailService;
import trong.lixco.com.ejb.thai.kpi.PersonalOtherService;
import trong.lixco.com.ejb.thai.kpi.PersonalPerformanceService;
import trong.lixco.com.jpa.entitykpi.EmpPJob;
import trong.lixco.com.jpa.entitykpi.KPIPerson;
import trong.lixco.com.jpa.entitykpi.PositionJob;
import trong.lixco.com.jpa.thai.KPIPersonalOther;
import trong.lixco.com.jpa.thai.KPIPersonalOtherDetail;
import trong.lixco.com.jpa.thai.KPIPersonalPerformance;
import trong.lixco.com.servicepublic.EmployeeDTO;
import trong.lixco.com.servicepublic.EmployeeServicePublic;
import trong.lixco.com.servicepublic.EmployeeServicePublicProxy;
import trong.lixco.com.thai.bean.entities.InfoPersonalPerformance;
import trong.lixco.com.util.Notify;

@Named
@org.omnifaces.cdi.ViewScoped
public class PersonalOtherBean extends AbstractBean<KPIPersonalOther>{

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
	private List<KPIPersonalOtherDetail> details;
	@Inject
	private PersonalPerformanceService PERSONAL_PERFORMANCE_SERVICE;
	// end them hanh vi

	private int monthSearch = 0;
	private int yearSearch = 0;
	private int daySearch = 0;
	private SimpleDateFormat sf;
	private Notify notify;
	@Inject
	private PositionJobService POSITION_JOB_SERVICE;
	@Inject
	private PersonalOtherService PERSONAL_OTHER_SERVICE;
	@Inject
	private PersonalOtherDetailService PERSONAL_OTHER_DETAIL_SERVICE;
	@Inject
	private KPIPersonService KPI_PERSON_SERVICE;
	private EmployeeServicePublic EMPLOYEE_SERVICE_PUBLIC;
	private MemberServicePublic MEMBER_SERVICE_PUBLIC;
	DepartmentServicePublic DEPARTMENT_SERVICE_PUBLIC;
	

	@Override
	protected void initItem() {
		sf = new SimpleDateFormat("dd/MM/yyyy");
		LocalDate lc = new LocalDate();
		monthSearch = lc.getMonthOfYear();
		yearSearch = lc.getYear();
		daySearch = lc.getDayOfMonth();

		listKPIPersonalOtherDetailUpdate = new ArrayList<>();

		EMPLOYEE_SERVICE_PUBLIC = new EmployeeServicePublicProxy();
		MEMBER_SERVICE_PUBLIC = new MemberServicePublicProxy();
		DEPARTMENT_SERVICE_PUBLIC = new DepartmentServicePublicProxy();
		memberPlaying = getAccount().getMember();
		// get all member is personal other
		try {
			personalOthers = createAllCodeMemberOther();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
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
			if(!listCodePJob.isEmpty()) {
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
				//kiem tra bang other_detail row nao co set selecte = true;
				for (int i = 0; i < listInfoPersonalPerformances.size(); i++) {
					this.details = PERSONAL_OTHER_DETAIL_SERVICE.find(personSelected);
					for (int j = 0; j < listInfoPersonalPerformances.get(i).getPersonalPerformances().size(); j++) {
						for (int k = 0; k < details.size(); k++) {
							if (listInfoPersonalPerformances.get(i).getPersonalPerformances().get(j).getContent()
									.equals(details.get(k).getContent())) {
								listInfoPersonalPerformances.get(i).getPersonalPerformances().get(j).setSelect(true);
								listInfoPersonalPerformances.get(i).getPersonalPerformances().get(j)
										.setQuantity(details.get(k).getQuantity());
								break;
							}
						}
					}
				}
			}

			// End Thai
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('widgetKPIPersonalOther').show();");
		} else {
			notify.warning("Chưa lưu thông tin phòng ban/nhân viên!");
		}
	}
	//tra ve danh sach nhom khac
	public List<String> createAllCodeMemberOther() throws RemoteException {
		List<String> personalOthersTemp = new ArrayList<>();
		Department depTemp = DEPARTMENT_SERVICE_PUBLIC.findId(memberPlaying.getDepartment().getId());
		try {
			// toan bo nhan vien nhom khac
			List<EmployeeDTO> allMemberOther = new ArrayList<>();
			//tat ca nhan vien binh thuong
			List<EmployeeDTO> allMemberNormal = new ArrayList<>();
			
			//Phong ban
			List<String> depList = new ArrayList<>();
			depList.add(memberPlaying.getDepartment().getCode());
			String[] depArray = depList.toArray(new String[depList.size()]);
			EmployeeDTO[] allMemberTemp = EMPLOYEE_SERVICE_PUBLIC.findByDep(depArray);
			//tat ca nhan vien
			List<EmployeeDTO> allMember = Arrays.asList(allMemberTemp);
			for(int i = 0; i < allMember.size(); i++) {
				KPIPerson temp = KPI_PERSON_SERVICE.findRangeNew(allMember.get(i).getCode(), monthSearch, yearSearch);
				//kiem tra co hay khong
				if(temp != null) {
					allMemberNormal.add(allMember.get(i));
				}else {
					if(!allMember.get(i).getCode().equals(depTemp.getCodeMem())) {
						allMemberOther.add(allMember.get(i));
					}
				}
			}
			//Tao list tring toan bo code employee nhom khac
			for(EmployeeDTO m : allMemberOther) {
				personalOthersTemp.add(m.getCode());
			}
			return personalOthersTemp;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}
	public void create() throws RemoteException {
		this.personalOthers = this.createAllCodeMemberOther();
		if (allowSave(null)) {
			//check cho phep tao tu ngay 10
			if(daySearch <= 10) {
				noticeError("Chỉ được tạo sau ngày 10");
			}else {
				for (int i = 0; i < personalOthers.size(); i++) {
					EmployeeDTO memberTemp = EMPLOYEE_SERVICE_PUBLIC.findByCode(personalOthers.get(i));
					KPIPersonalOther temp = new KPIPersonalOther();
					temp.setCodeEmp(personalOthers.get(i));
					temp.setNameEmp(memberTemp.getName());
					temp.setNameDepart(memberTemp.getNameDepart());
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

	public void showDetailKPI(KPIPersonalOther selected) {
		this.personSelected = selected;
		this.details = PERSONAL_OTHER_DETAIL_SERVICE.find(personSelected);
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('widgetKPIPersonalOtherBehaviour').show();");
	}

	// Xoa chi tiet diem tru
	public void removeDetail(KPIPersonalOtherDetail remove) {
		try {
//			KPIPersonalOther temp = remove.getKpiPersonalOther();
//			double total = temp.getTotal() + remove.getMinuspoint();
//			temp.setTotal(total);
//			KPIPersonalOtherDetail kp=	PERSONAL_OTHER_DETAIL_SERVICE.findById(remove.getId());
			boolean checkDelete = PERSONAL_OTHER_DETAIL_SERVICE.delete(remove);
//			if (checkDelete) {
//				KPIPersonalOther checkUpdate = PERSONAL_OTHER_SERVICE.update(temp);
//				if (checkUpdate != null) {
//					notice("Thành công");
//					kpiPersonalOthers = PERSONAL_OTHER_SERVICE.find(personalOthers, monthSearch, yearSearch);
//					this.details = PERSONAL_OTHER_DETAIL_SERVICE.find(personSelected);
//
//				}else {
//					noticeError("Lỗi không cập nhật được");
//				}
//			} else {
//				noticeError("Lỗi");
//			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	// cap nhat duyet ket qua kpi
	public void updateSignResult() {
		notify = new Notify(FacesContext.getCurrentInstance());
		Date date = new Date(monthSearch, yearSearch, 1);
		if (allowUpdate(date)) {
			for (KPIPersonalOther k : kpiPersonalOthers) {
				KPIPersonalOther checkUpdate = PERSONAL_OTHER_SERVICE.update(k);
				if (checkUpdate == null) {
					System.out.println("Loi");
				}
			}
			kpiPersonalOthers = PERSONAL_OTHER_SERVICE.find(personalOthers, monthSearch, yearSearch);
			notice("Thanh cong");
		} else {
			noticeError("Không có quyền cập nhật");
		}
	}

//	public void saveDetailRemove() {
//		try {
//			// get kpiperson tu DB len de update list kpipersonOfMonth
//			List<KPIPerson> temps = kPIPersonService.findRange(personSelecting.getCodeEmp(),
//					personSelecting.getKmonth(), personSelecting.getKyear());
//			KPIPerson temp = temps.get(0);
//			temp.setKpiPersonOfMonths(listDetail);
//			temp.setModifiedDate(new Date());
//			KPIPerson resultUpdate = kPIPersonService.updateAssign(temp);
//			if (resultUpdate != null) {
//				notice("Cập nhật thành công");
//			} else {
//				noticeError("Xảy ra lỗi");
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//
//	}
	public void updateListKPIPersonalOtherNew() {
		// Kiem tra co loi trong qua trinh xoa hay khong
		boolean status = false;
		// Xoa
		List<KPIPersonalOtherDetail> listDelete = PERSONAL_OTHER_DETAIL_SERVICE.find(this.personSelected);
		for (KPIPersonalOtherDetail k : listDelete) {
			boolean checkDelete = PERSONAL_OTHER_DETAIL_SERVICE.delete(k);
			if (checkDelete == false) {
				status = true;
				noticeError("Loi xay ra");
			}
		}
		
		if (status == false) {
			// Them moi
			List<KPIPersonalOtherDetail> listDetailAdd = new ArrayList<>();
			try {
				KPIPersonalOther otherQuery = (PERSONAL_OTHER_SERVICE.find(null, monthSearch, yearSearch,
						this.personSelected.getCodeEmp())).get(0);
				int dem = 0;
				boolean checkIsSelect = false;
				for (int i = 0; i < listInfoPersonalPerformances.size(); i++) {
					for (int j = 0; j < listInfoPersonalPerformances.get(i).getPersonalPerformances().size(); j++) {
						if (listInfoPersonalPerformances.get(i).getPersonalPerformances().get(j).isSelect()) {
							checkIsSelect = true;
							KPIPersonalOtherDetail item = new KPIPersonalOtherDetail();
							item.setContent(
									listInfoPersonalPerformances.get(i).getPersonalPerformances().get(j).getContent());
							item.setMinuspoint(listInfoPersonalPerformances.get(i).getPersonalPerformances().get(j)
									.getMinuspoint());
							item.setQuantity(
									listInfoPersonalPerformances.get(i).getPersonalPerformances().get(j).getQuantity());
							item.setKpiPersonalOther(otherQuery);
							listDetailAdd.add(item);
							double totalTemp2;
							if (dem == 0) {
								totalTemp2 = 100 - (item.getMinuspoint() * item.getQuantity());
							} else {
								totalTemp2 = otherQuery.getTotal() - (item.getMinuspoint() * item.getQuantity());
							}
							otherQuery.setTotal(totalTemp2);
							dem++;
						}
					}
				}
				if(checkIsSelect == false) {
					otherQuery.setTotal(100);
				}
				otherQuery.setKpiPersonalOtherDetails(listDetailAdd);
				// check kpi da duyet ket qua chua
				if (otherQuery.isSignResult()) {
					noticeError("KPI đã được duyệt không thể chỉnh sửa");
				} else {
					KPIPersonalOther statusUpdate = PERSONAL_OTHER_SERVICE.update(otherQuery);
					kpiPersonalOthers = PERSONAL_OTHER_SERVICE.find(personalOthers, monthSearch, yearSearch);
					if (statusUpdate != null) {
						notice("Thành công");
						
					} else {
						noticeError("Lỗi");
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	public void updateListKPIPersonalOther() {
		List<KPIPersonalOtherDetail> listDetailAdd = new ArrayList<>();
		try {
			KPIPersonalOther otherQuery = (PERSONAL_OTHER_SERVICE.find(null, monthSearch, yearSearch,
					this.personSelected.getCodeEmp())).get(0);
			List<KPIPersonalOtherDetail> otherDetailsTemp = otherQuery.getKpiPersonalOtherDetails();
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
						item.setQuantity(
								listInfoPersonalPerformances.get(i).getPersonalPerformances().get(j).getQuantity());
						// check xem item nao duoc chon

						// check duoi DB co list detail chua
						if (otherDetailsTemp.size() == 0) {
							item.setKpiPersonalOther(otherQuery);
							listDetailAdd.add(item);
							double totalTemp2 = otherQuery.getTotal() - (item.getMinuspoint() * item.getQuantity());
							otherQuery.setTotal(totalTemp2);
						} else {
							for (int k = 0; k < otherDetailsTemp.size(); k++) {
								if (otherDetailsTemp.get(k).getContent().equals(item.getContent())) {
									isExist = true;
								}
							}
							if (isExist == false) {
								item.setKpiPersonalOther(otherQuery);
//								otherDetailsTemp.add(item);
								listDetailAdd.add(item);
								double totalTemp2 = otherQuery.getTotal() - (item.getMinuspoint() * item.getQuantity());
								otherQuery.setTotal(totalTemp2);
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
			otherQuery.setKpiPersonalOtherDetails(listDetailAdd);
			// check kpi da duyet ket qua chua
			if (otherQuery.isSignResult()) {
				noticeError("KPI đã được duyệt không thể chỉnh sửa");
			} else {
				KPIPersonalOther statusUpdate = PERSONAL_OTHER_SERVICE.update(otherQuery);
				kpiPersonalOthers = PERSONAL_OTHER_SERVICE.find(personalOthers, monthSearch, yearSearch);
				if (statusUpdate != null) {
					notice("Thành công");
					kpiPersonalOthers = PERSONAL_OTHER_SERVICE.find(personalOthers, monthSearch, yearSearch);
				} else {
					noticeError("Lỗi");
				}
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

	public List<KPIPersonalOtherDetail> getDetails() {
		return details;
	}

	public void setDetails(List<KPIPersonalOtherDetail> details) {
		this.details = details;
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
