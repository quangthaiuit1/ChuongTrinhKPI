package trong.lixco.com.thai.bean;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

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
import trong.lixco.com.ejb.servicekpi.KPIPersonService;
import trong.lixco.com.ejb.servicekpi.PositionJobService;
import trong.lixco.com.ejb.thai.kpi.PersonalOtherDetailService;
import trong.lixco.com.ejb.thai.kpi.PersonalOtherService;
import trong.lixco.com.ejb.thai.kpi.PositionDontKPIService;
import trong.lixco.com.jpa.entitykpi.KPIPerson;
import trong.lixco.com.jpa.thai.KPIPersonalOther;
import trong.lixco.com.jpa.thai.KPIPersonalOtherDetail;
import trong.lixco.com.jpa.thai.PositionDontKPI;
import trong.lixco.com.servicepublic.EmployeeDTO;
import trong.lixco.com.servicepublic.EmployeeServicePublic;
import trong.lixco.com.servicepublic.EmployeeServicePublicProxy;
import trong.lixco.com.thai.apitrong.DepartmentData;
import trong.lixco.com.thai.apitrong.DepartmentDataService;
import trong.lixco.com.thai.apitrong.PositionJobData;
import trong.lixco.com.thai.apitrong.PositionJobDataService;
import trong.lixco.com.thai.bean.entities.InfoPersonalPerformance;
import trong.lixco.com.thai.bean.staticentity.MessageView;
import trong.lixco.com.util.DepartmentUtil;
import trong.lixco.com.util.Notify;

@ManagedBean
@ViewScoped
// kpi ca nhan nhom khac duyet
public class PersonalOtherDuyetBean extends AbstractBean<KPIPersonalOther> {
	private static final long serialVersionUID = 1L;
	private int monthSearch = 0;
	private int yearSearch = 0;
	private Department departmentSelected;
	private List<Department> departmentSearchs;
	private Department departmentSearch;
	private List<KPIPersonalOther> kpiPersonalOthers;
	private List<KPIPerson> kPIPersonFilters;
	private List<InfoPersonalPerformance> listInfoPersonalPerformances;
	private List<String> personalOthers;
	private KPIPersonalOther personSelected;
	private List<KPIPersonalOtherDetail> personDetailsDeleted;
	private List<KPIPersonalOtherDetail> personDetails;
	private List<KPIPersonalOtherDetail> details;
	private Member memberPlaying;

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
	@Inject
	private PositionDontKPIService POSITION_DONT_KPI_SERVICE;

	@Override
	protected void initItem() {
		try {

			LocalDate lc = new LocalDate();
			monthSearch = lc.getMonthOfYear();
			yearSearch = lc.getYear();

			EMPLOYEE_SERVICE_PUBLIC = new EmployeeServicePublicProxy();
			MEMBER_SERVICE_PUBLIC = new MemberServicePublicProxy();
			DEPARTMENT_SERVICE_PUBLIC = new DepartmentServicePublicProxy();
			memberPlaying = getAccount().getMember();

			departmentSearchs = new ArrayList<Department>();
			if (getAccount().isAdmin()) {
				Department[] deps = DEPARTMENT_SERVICE_PUBLIC.findAll();
				for (int i = 0; i < deps.length; i++) {
					if (deps[i].getLevelDep() != null)
						if (deps[i].getLevelDep().getLevel() > 1)
							departmentSearchs.add(deps[i]);
				}

			} else {
				// departmentSearchs.add(member.getDepartment());
				Department[] deps = DEPARTMENT_SERVICE_PUBLIC
						.getAllDepartSubByParent(memberPlaying.getDepartment().getDepartment().getCode());
				for (int i = 0; i < deps.length; i++) {
					departmentSearchs.add(deps[i]);
				}
			}
			if (departmentSearchs.size() != 0) {
				departmentSearchs = DepartmentUtil.sort(departmentSearchs);
				departmentSelected = departmentSearchs.get(0);
			}
			// get all member is personal other
			try {
				personalOthers = createAllCodeMemberOther();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			// neu danh sach nhan vien khong lam kpi rong
			if (personalOthers == null || personalOthers.isEmpty()) {
				kpiPersonalOthers = new ArrayList<>();
			} else {
				kpiPersonalOthers = PERSONAL_OTHER_SERVICE.find(personalOthers, monthSearch, yearSearch);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void searchItem() {
		try {
			personalOthers = createAllCodeMemberOther();
			// neu danh sach nhan vien khong lam kpi rong
			if (personalOthers.isEmpty()) {
				kpiPersonalOthers = new ArrayList<>();
			} else {
				kpiPersonalOthers = PERSONAL_OTHER_SERVICE.find(personalOthers, monthSearch, yearSearch);
			}
		} catch (Exception e) {
		}
	}

	public void showDialogOrien(KPIPersonalOther personSelected) {
		this.personSelected = personSelected;
		personDetailsDeleted = new ArrayList<>();
		// listInfoPersonalPerformances = new ArrayList<>();
		if (personSelected.getId() != null) {
			// // tim danh muc hanh vi vi pham
			personDetails = PERSONAL_OTHER_DETAIL_SERVICE.find(personSelected);
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('widgetKPIPersonalOther').show();");
			return;
		} else {
			MessageView.WARN("Chưa lưu thông tin phòng ban/nhân viên!");
		}
	}

	public void showDetailKPI(KPIPersonalOther selected) {
		this.personSelected = selected;
		this.details = PERSONAL_OTHER_DETAIL_SERVICE.find(personSelected);
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('widgetKPIPersonalOtherBehaviour').show();");
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
								// otherDetailsTemp.add(item);
								listDetailAdd.add(item);
								double totalTemp2 = otherQuery.getTotal() - (item.getMinuspoint() * item.getQuantity());
								otherQuery.setTotal(totalTemp2);
							}
						}

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

	public void ajaxHandleQuantityChange(KPIPersonalOtherDetail item) {
		try {
			KPIPersonalOther itemSelected = item.getKpiPersonalOther();
			// cap nhat so luong moi
			KPIPersonalOtherDetail update = PERSONAL_OTHER_DETAIL_SERVICE.update(item);
			// tinh lai tong so diem
			if (update != null) {
				List<KPIPersonalOtherDetail> totalDetail = PERSONAL_OTHER_DETAIL_SERVICE.find(itemSelected);
				double totalPoint = 0;
				for (int i = 0; i < totalDetail.size(); i++) {
					totalPoint = totalPoint + totalDetail.get(i).getMinuspoint();
				}
				itemSelected.setTotal(100 - totalPoint);
				KPIPersonalOther updateOther = PERSONAL_OTHER_SERVICE.update(itemSelected);
				// cap nhat lai danh sach other
				kpiPersonalOthers = PERSONAL_OTHER_SERVICE.find(personalOthers, monthSearch, yearSearch);
				// cap nhat lai danh sach detail
				// details = PERSONAL_OTHER_DETAIL_SERVICE.find(personSelected);
			} else {
				noticeError("Lỗi");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// tra ve danh sach nhom khac
	public List<String> createAllCodeMemberOther() throws RemoteException {
		List<String> personalOthersTemp = new ArrayList<>();
		Department depTemp = DEPARTMENT_SERVICE_PUBLIC.findId(departmentSelected.getId());
		// Department depTempLv2 = null;
		Department depTempLv3 = null;
		if (depTemp.getLevelDep().getLevel() == 4) {
			depTempLv3 = depTemp.getDepartment().getDepartment();
		}
		if (depTemp.getLevelDep().getLevel() == 3) {
			depTempLv3 = depTemp;
		}
		if (depTempLv3 != null) {

			try {
				// toan bo nhan vien nhom khac
				List<EmployeeDTO> allMemberOther = new ArrayList<>();
				EmployeeDTO[] allMemberTemp;
				List<String> depList = new ArrayList<>();
				String[] depArray = null;
				// Phong ban
				depList.add(departmentSelected.getCode());
				depArray = depList.toArray(new String[depList.size()]);
				allMemberTemp = EMPLOYEE_SERVICE_PUBLIC.findByDep(depArray);
				if (allMemberTemp != null) {
					// tat ca nhan vien
					List<EmployeeDTO> allMember = Arrays.asList(allMemberTemp);
					List<PositionDontKPI> allPositionDontKPI = POSITION_DONT_KPI_SERVICE.findAll();
					if (allPositionDontKPI.size() == 0) {
						MessageView.ERROR("Không có vị trí chức vụ");
						return null;
					} else {
						for (int i = 0; i < allMember.size(); i++) {
							// tim vi tri cong viec theo nhan vien
							boolean vitriKhongLamKPI = false;
							PositionJobData[] empPJobs = PositionJobDataService
									.vttheonhanvien(allMember.get(i).getCode());
							// kiem tra vi tri chuc vu khong lam kpi
							for (int k = 0; k < empPJobs.length; k++) {
								for (int j = 0; j < allPositionDontKPI.size(); j++) {
									if (empPJobs[k].getCode().equals(allPositionDontKPI.get(j).getPosition_code())) {
										vitriKhongLamKPI = true;
										break;
									}
								}
								break;
							}
							if (vitriKhongLamKPI) {
								allMemberOther.add(allMember.get(i));
							}
						}
						// Tao list tring toan bo code employee nhom khac
						for (EmployeeDTO m : allMemberOther) {
							personalOthersTemp.add(m.getCode());
						}
						return personalOthersTemp;
					}
				} else {
					return null;
				}

			} catch (Exception e) {
				// TODO: handle exception
				return null;
			}
		}
		// nguoi khong thuoc to khong the tao kpi nhom khac
		else {
			MessageView.ERROR("Không thể tạo mới");
			return null;
		}
	}

	// cap nhat duyet ket qua kpi
	public void updateSignResult() {
		Date date = new Date(monthSearch, yearSearch, 1);
		if (allowUpdate(date)) {
			for (KPIPersonalOther k : kpiPersonalOthers) {
				KPIPersonalOther checkUpdate = PERSONAL_OTHER_SERVICE.update(k);
				if (checkUpdate == null) {
					System.out.println("Loi");
				}
			}
			kpiPersonalOthers = PERSONAL_OTHER_SERVICE.find(personalOthers, monthSearch, yearSearch);
			notice("Thành công");
		} else {
			noticeError("Không có quyền cập nhật");
		}
	}

	// Xoa chi tiet diem tru
	public void removeDetail(KPIPersonalOtherDetail remove) {
		try {
			// KPIPersonalOther temp = remove.getKpiPersonalOther();
			// double total = temp.getTotal() + remove.getMinuspoint();
			// temp.setTotal(total);
			// KPIPersonalOtherDetail kp=
			// PERSONAL_OTHER_DETAIL_SERVICE.findById(remove.getId());
			boolean checkDelete = PERSONAL_OTHER_DETAIL_SERVICE.delete(remove);
			double totalPoint = 0;
			if (checkDelete) {
				List<KPIPersonalOtherDetail> totalKPI = PERSONAL_OTHER_DETAIL_SERVICE
						.find(remove.getKpiPersonalOther());
				if (!totalKPI.isEmpty()) {
					for (int i = 0; i < totalKPI.size(); i++) {
						totalPoint = totalPoint + (totalKPI.get(i).getMinuspoint() * totalKPI.get(i).getQuantity());
					}
				}
				// cap nhat tong diem trong bang personal_other

				KPIPersonalOther update = remove.getKpiPersonalOther();
				update.setTotal(100 - totalPoint);
				KPIPersonalOther checkUpdate = PERSONAL_OTHER_SERVICE.update(update);
				details = PERSONAL_OTHER_DETAIL_SERVICE.find(remove.getKpiPersonalOther());
				if (checkUpdate != null) {
					kpiPersonalOthers = PERSONAL_OTHER_SERVICE.find(personalOthers, monthSearch, yearSearch);
					notice("Thành công");
				} else {
					noticeError("Lỗi");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Logger getLogger() {
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

	public Department getDepartmentSelected() {
		return departmentSelected;
	}

	public void setDepartmentSelected(Department departmentSelected) {
		this.departmentSelected = departmentSelected;
	}

	public List<Department> getDepartmentSearchs() {
		return departmentSearchs;
	}

	public void setDepartmentSearchs(List<Department> departmentSearchs) {
		this.departmentSearchs = departmentSearchs;
	}

	public List<KPIPersonalOther> getKpiPersonalOthers() {
		return kpiPersonalOthers;
	}

	public void setKpiPersonalOthers(List<KPIPersonalOther> kpiPersonalOthers) {
		this.kpiPersonalOthers = kpiPersonalOthers;
	}

	public List<KPIPerson> getkPIPersonFilters() {
		return kPIPersonFilters;
	}

	public void setkPIPersonFilters(List<KPIPerson> kPIPersonFilters) {
		this.kPIPersonFilters = kPIPersonFilters;
	}

	public List<InfoPersonalPerformance> getListInfoPersonalPerformances() {
		return listInfoPersonalPerformances;
	}

	public void setListInfoPersonalPerformances(List<InfoPersonalPerformance> listInfoPersonalPerformances) {
		this.listInfoPersonalPerformances = listInfoPersonalPerformances;
	}

	public List<KPIPersonalOtherDetail> getDetails() {
		return details;
	}

	public void setDetails(List<KPIPersonalOtherDetail> details) {
		this.details = details;
	}

	public Department getDepartmentSearch() {
		return departmentSearch;
	}

	public void setDepartmentSearch(Department departmentSearch) {
		this.departmentSearch = departmentSearch;
	}

}
