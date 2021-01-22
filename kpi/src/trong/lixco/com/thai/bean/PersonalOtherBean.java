package trong.lixco.com.thai.bean;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
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
import trong.lixco.com.ejb.thai.kpi.BehaviourPersonOtherService;
import trong.lixco.com.ejb.thai.kpi.PersonalOtherDetailService;
import trong.lixco.com.ejb.thai.kpi.PersonalOtherService;
import trong.lixco.com.ejb.thai.kpi.PersonalPerformanceService;
import trong.lixco.com.ejb.thai.kpi.PositionDontKPIService;
import trong.lixco.com.jpa.entitykpi.KPIPerson;
import trong.lixco.com.jpa.entitykpi.PositionJob;
import trong.lixco.com.jpa.thai.BehaviourPersonOther;
import trong.lixco.com.jpa.thai.KPIPersonalOther;
import trong.lixco.com.jpa.thai.KPIPersonalOtherDetail;
import trong.lixco.com.jpa.thai.KPIPersonalPerformance;
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

@Named
@org.omnifaces.cdi.ViewScoped
public class PersonalOtherBean extends AbstractBean<KPIPersonalOther> {

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
	private List<Department> departmentSearchs;
	private Department departmentSelected;
	private List<KPIPerson> kPIPersonFilters;

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

	private List<BehaviourPersonOther> behaviourOthers;
	private List<BehaviourPersonOther> behaviourOthersSelected;
	private List<KPIPersonalOtherDetail> personDetails;
	private List<KPIPersonalOtherDetail> personDetailsDeleted;

	@Inject
	private BehaviourPersonOtherService BEHAVIOUR_PERSON_OTHER_SERVICE;
	@Inject
	private PositionDontKPIService POSITION_DONT_KPI_SERVICE;

	@Override
	protected void initItem() {
		try {
			sf = new SimpleDateFormat("dd/MM/yyyy");
			LocalDate lc = new LocalDate();
			monthSearch = lc.getMonthOfYear();
			yearSearch = lc.getYear();
			daySearch = lc.getDayOfMonth();
			personSelected = new KPIPersonalOther();

			listKPIPersonalOtherDetailUpdate = new ArrayList<>();
			personDetailsDeleted = new ArrayList<>();
			DEPARTMENT_SERVICE_PUBLIC = new DepartmentServicePublicProxy();
			departmentSelected = new Department();

			// MEMBER_SERVICE_PUBLIC = new MemberServicePublicProxy();
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
				// departmentSearch = departmentSearchs.get(0);
			}

			// get all member is personal other
			personalOthers = createAllCodeMemberOther();

			// neu danh sach nhan vien khong lam kpi rong
			if (personalOthers == null || personalOthers.isEmpty()) {
				kpiPersonalOthers = new ArrayList<>();
			} else {
				kpiPersonalOthers = PERSONAL_OTHER_SERVICE.find(personalOthers, monthSearch, yearSearch);
			}
			personDetails = new ArrayList<>();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void searchItem() {
		try {
			// get all member is personal other
			personalOthers = createAllCodeMemberOther();
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

	public void removeKPIPersonOther(KPIPersonalOther item) {
		// check kpi da duoc duyet chua
		KPIPersonalOther itemSelected = item;
		if (itemSelected.isSignResult()) {
			noticeError("KPI đã được duyệt không thể xóa");
			return;
		}
		boolean deleteSuccess = PERSONAL_OTHER_SERVICE.delete(item);
		if (deleteSuccess) {
			// try {
			// this.personalOthers = this.createAllCodeMemberOther();
			// } catch (RemoteException e) {
			// e.printStackTrace();
			// }
			kpiPersonalOthers = PERSONAL_OTHER_SERVICE.find(personalOthers, monthSearch, yearSearch);
			notice("Thành công");
		} else {
			noticeError("Lỗi");
		}

	}

	public void saveBehaviourDetail() {
		try {
			// personDetails
			KPIPersonalOther personOtherTemp;
			List<KPIPersonalOther> personOthers = PERSONAL_OTHER_SERVICE.find(null, monthSearch, yearSearch,
					personSelected.getCodeEmp());
			// kpi nhom khac da duoc tao
			if (!personOthers.isEmpty()) {
				personOtherTemp = personOthers.get(0);
				if (personOtherTemp.isSignResult()) {
					MessageView.WARN("KPI đã được duyệt không thể chỉnh sửa");
					return;
				}
				// List<KPIPersonalOtherDetail> details =
				// PERSONAL_OTHER_DETAIL_SERVICE.find(personOther);
				// chi tiet hanh vi da duoc luu chua
				// if (personDetailsDeleted.size() != 0) {
				// xoa danh sach hanh vi
				for (int p = 0; p < personDetailsDeleted.size(); p++) {
					PERSONAL_OTHER_DETAIL_SERVICE.delete(personDetailsDeleted.get(p));
				}

				// for(int i = 0; i < details.size(); i++){
				for (int j = 0; j < personDetails.size(); j++) {
					List<KPIPersonalOtherDetail> listDetailByContent = PERSONAL_OTHER_DETAIL_SERVICE
							.find(personDetails.get(j).getContent(), personOtherTemp.getId());
					// da co hanh vi ben bang chi tiet
					if (listDetailByContent.size() != 0) {
						listDetailByContent.get(0).setQuantity(personDetails.get(j).getQuantity());
						KPIPersonalOtherDetail check = PERSONAL_OTHER_DETAIL_SERVICE.update(listDetailByContent.get(0));
						if (check == null) {
							System.out.println("loi");
						}
					} else {
						personDetails.get(j).setKpiPersonalOther(personOtherTemp);
						KPIPersonalOtherDetail check = PERSONAL_OTHER_DETAIL_SERVICE.create(personDetails.get(j));
						if (check.getId() == null) {
							System.out.println("Loi: khong luu duoc chi tiet kpi ca nhan khac");
						}
					}
				}
				// Tinh tong so diem con lai theo tung nhan vien
				for (int i = 0; i < kpiPersonalOthers.size(); i++) {
					List<KPIPersonalOtherDetail> listDetailByContent = PERSONAL_OTHER_DETAIL_SERVICE
							.find(kpiPersonalOthers.get(i));
					double tongdiem;
					double sodiembitru = 0;
					for (int k = 0; k < listDetailByContent.size(); k++) {
						sodiembitru = sodiembitru + (listDetailByContent.get(k).getMinuspoint()
								* listDetailByContent.get(k).getQuantity());
					}
					tongdiem = 100 - sodiembitru;
					// cap nhat kpi ca nhan khac
					// query personOtherTemp truoc vi khi update lay data cu
					KPIPersonalOther personOtherTemp123 = PERSONAL_OTHER_SERVICE
							.findById(kpiPersonalOthers.get(i).getId());
					personOtherTemp123.setTotal(tongdiem);
					PERSONAL_OTHER_SERVICE.update(personOtherTemp123);
				}
				kpiPersonalOthers = PERSONAL_OTHER_SERVICE.find(personalOthers, monthSearch, yearSearch);

				// notify.success("Thành công");
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, "Thông báo", "Thành công"));
				// }
				// } else {
				// // notify.warning("Không có thông tin thay đổi");
				// FacesContext.getCurrentInstance().addMessage(null,
				// new FacesMessage(FacesMessage.SEVERITY_WARN, "Thông báo",
				// "Không có thông tin thay đổi"));
				// }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showBehaviour() {
		behaviourOthers = BEHAVIOUR_PERSON_OTHER_SERVICE.findAll();
	}

	public void addBehaviourDetail() {
		for (BehaviourPersonOther b : behaviourOthers) {
			if (b.isSelect()) {
				// kiem tra thu co chua moi cho them vao
				boolean isExist = false;
				for (int i = 0; i < personDetails.size(); i++) {
					if (b.getContent().equals(personDetails.get(i).getContent())) {
						isExist = true;
						break;
					}
				}
				if (!isExist) {
					KPIPersonalOtherDetail k = new KPIPersonalOtherDetail();
					k.setContent(b.getContent());
					k.setMinuspoint(b.getMinusPoint());
					k.setQuantity(1);
					personDetails.add(k);
				}
			}
		}
	}

	public void addViTriChucVu() {
		// Them moi
		List<KPIPersonalOtherDetail> listDetailAdd = new ArrayList<>();
		try {
			for (int i = 0; i < listInfoPersonalPerformances.size(); i++) {
				List<KPIPersonalPerformance> vitrichucvu = listInfoPersonalPerformances.get(i)
						.getPersonalPerformances();
				for (int j = 0; j < vitrichucvu.size(); j++) {
					if (vitrichucvu.get(j).isSelect()) {
						// kiem tra da duoc them truoc do chua
						boolean isExist = false;
						for (int k = 0; k < personDetails.size(); k++) {
							if (vitrichucvu.get(j).getContent().equals(personDetails.get(k).getContent())) {
								isExist = true;
								break;
							}
						}
						if (!isExist) {
							KPIPersonalOtherDetail k = new KPIPersonalOtherDetail();
							k.setContent(vitrichucvu.get(j).getContent());
							k.setMinuspoint(vitrichucvu.get(j).getMinuspoint());
							k.setQuantity(1);
							personDetails.add(k);
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void showVitriChucVuCongViec() {
		listInfoPersonalPerformances = new ArrayList<>();
		notify = new Notify(FacesContext.getCurrentInstance());
		if (personSelected.getId() != null) {
			// tat ca vi tri cong viec
			positionJobs = new ArrayList<PositionJob>();
			// List<EmpPJob> empPJobs =
			// empPJobService.findByEmployee(personSelected.getCodeEmp());

			// tim vi tri cong viec theo nhan vien
			PositionJobData[] empPJobs = PositionJobDataService.vttheonhanvien(personSelected.getCodeEmp());
			boolean existPosition = true;
			if (empPJobs == null || empPJobs.length == 0) {
				existPosition = false;
			}
			// co vi tri cong viec
			else {
				for (int i = 0; i < empPJobs.length; i++) {
					PositionJob pj = POSITION_JOB_SERVICE.findByCode(empPJobs[i].getCode());
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
				if (!listCodePJob.isEmpty()) {
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
					// kiem tra bang other_detail row nao co set selecte = true;
					for (int i = 0; i < listInfoPersonalPerformances.size(); i++) {
						this.details = PERSONAL_OTHER_DETAIL_SERVICE.find(personSelected);
						for (int j = 0; j < listInfoPersonalPerformances.get(i).getPersonalPerformances().size(); j++) {
							for (int k = 0; k < details.size(); k++) {
								if (listInfoPersonalPerformances.get(i).getPersonalPerformances().get(j).getContent()
										.equals(details.get(k).getContent())) {
									listInfoPersonalPerformances.get(i).getPersonalPerformances().get(j)
											.setSelect(true);
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
				context.execute("PF('wgvTableViTriChucVu').show();");
			}
			if (!existPosition) {
				noticeError("Không tìm thấy vị trí công việc phù hợp");
				return;
			}
		}
	}

	public void removePersonDetail(KPIPersonalOtherDetail item) {
		List<KPIPersonalOtherDetail> personDetailsNew = new ArrayList<>();
		for (int i = 0; i < personDetails.size(); i++) {
			if (!personDetails.get(i).getContent().equals(item.getContent())) {
				personDetailsNew.add(personDetails.get(i));
			} else {
				personDetailsDeleted.add(personDetails.get(i));
			}
		}
		personDetails = new ArrayList<>();
		personDetails.addAll(personDetailsNew);
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

	public void showDialogOrien(KPIPersonalOther personSelected) {
		this.personSelected = personSelected;
		personDetailsDeleted = new ArrayList<>();
		// listInfoPersonalPerformances = new ArrayList<>();
		notify = new Notify(FacesContext.getCurrentInstance());
		if (personSelected.getId() != null) {
			// // tat ca vi tri cong viec
			// positionJobs = new ArrayList<PositionJob>();
			// // List<EmpPJob> empPJobs =
			// // empPJobService.findByEmployee(personSelected.getCodeEmp());
			//
			// // tim vi tri cong viec theo nhan vien
			// PositionJobData[] empPJobs =
			// PositionJobDataService.vttheonhanvien(personSelected.getCodeEmp());
			// boolean existPosition = true;
			// if (empPJobs == null || empPJobs.length == 0) {
			// existPosition = false;
			// }
			// // co vi tri cong viec
			// else {
			// for (int i = 0; i < empPJobs.length; i++) {
			// PositionJob pj =
			// POSITION_JOB_SERVICE.findByCode(empPJobs[i].getCode());
			// if (pj != null)
			// positionJobs.add(pj);
			// }
			//
			// if (positionJobs.size() != 0)
			// positionJobSelect = positionJobs.get(0);
			// // Thai
			// // Tao danh sach code PJob theo tung nhan vien
			// List<String> listCodePJob = new ArrayList<>();
			// for (int i = 0; i < positionJobs.size(); i++) {
			// listCodePJob.add(positionJobs.get(i).getCode());
			// }
			// // Tao list info
			// if (!listCodePJob.isEmpty()) {
			// listKPIPersonalByEmployee =
			// PERSONAL_PERFORMANCE_SERVICE.find(listCodePJob);
			// Map<String, List<KPIPersonalPerformance>> datagroups11 =
			// listKPIPersonalByEmployee.stream()
			// .collect(Collectors.groupingBy(a -> a.getCodePJob(),
			// Collectors.toList()));
			//
			// listKPIPersonalByEmployee = new ArrayList<>();
			// for (String key : datagroups11.keySet()) {
			// List<KPIPersonalPerformance> invs = datagroups11.get(key);
			// try {
			// InfoPersonalPerformance tgi = new InfoPersonalPerformance();
			// tgi.setPositionJobName(POSITION_JOB_SERVICE.findByCode(key).getName());
			// tgi.setPersonalPerformances(invs);
			// listInfoPersonalPerformances.add(tgi);
			// } catch (Exception e) {
			// // TODO: handle exception
			// }
			// }
			// // kiem tra bang other_detail row nao co set selecte = true;
			// for (int i = 0; i < listInfoPersonalPerformances.size(); i++) {
			// this.details =
			// PERSONAL_OTHER_DETAIL_SERVICE.find(personSelected);
			// for (int j = 0; j <
			// listInfoPersonalPerformances.get(i).getPersonalPerformances().size();
			// j++) {
			// for (int k = 0; k < details.size(); k++) {
			// if
			// (listInfoPersonalPerformances.get(i).getPersonalPerformances().get(j).getContent()
			// .equals(details.get(k).getContent())) {
			// listInfoPersonalPerformances.get(i).getPersonalPerformances().get(j)
			// .setSelect(true);
			// listInfoPersonalPerformances.get(i).getPersonalPerformances().get(j)
			// .setQuantity(details.get(k).getQuantity());
			// break;
			// }
			// }
			// }
			// }
			// }
			// // End Thai
			// // tim danh muc hanh vi vi pham
			personDetails = PERSONAL_OTHER_DETAIL_SERVICE.find(personSelected);
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('widgetKPIPersonalOther').show();");
			return;
			// }
			// if (!existPosition) {
			// noticeError("Không tìm thấy vị trí công việc phù hợp");
			// return;
			// }
		} else {
			notify.warning("Chưa lưu thông tin phòng ban/nhân viên!");
		}
	}

	// tra ve danh sach nhom khac
	public List<String> createAllCodeMemberOther() throws RemoteException {
		// boolean isTruongPhong = false;
		List<String> personalOthersTemp = new ArrayList<>();
		// Department depTemp =
		// DEPARTMENT_SERVICE_PUBLIC.findId(memberPlaying.getDepartment().getId());
		Department depTemp = null;
		if (departmentSelected.getId() == null || departmentSelected.getId() == 0) {
			depTemp = DEPARTMENT_SERVICE_PUBLIC.findId(departmentSearchs.get(0).getId());
		} else {
			depTemp = departmentSelected;
		}

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
				// // Phong ban
				// if (isTruongPhong) {
				// // tim tat ca cac to duoi cap phong
				// DepartmentData[] totalDepByDepartmentArray =
				// DepartmentDataService
				// .timtheophongquanly(depTempLv3.getCode());
				// List<DepartmentData> totalDepByDepartment = new ArrayList<>(
				// Arrays.asList(totalDepByDepartmentArray));
				// for (int t = 0; t < totalDepByDepartment.size(); t++) {
				// depList.add(totalDepByDepartment.get(t).getCode());
				// }
				// // tim theo phong quan ly khong bao gom phong do -> phai
				// // them
				// // phong
				// // do vao
				// depList.add(memberPlaying.getDepartment().getCode());
				// } else {
				// depList.add(memberPlaying.getDepartment().getCode());
				// }
				depList.add(depTempLv3.getCode());
				depArray = depList.toArray(new String[depList.size()]);
				EMPLOYEE_SERVICE_PUBLIC = new EmployeeServicePublicProxy();
				allMemberTemp = EMPLOYEE_SERVICE_PUBLIC.findByDep(depArray);
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
						PositionJobData[] empPJobs = PositionJobDataService.vttheonhanvien(allMember.get(i).getCode());
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
						// neu khong phai truong phong thi them vao
						// if
						// (!allMember.get(i).getCode().equals(depTemp.getCodeMem()))
						// {
						// allMemberOther.add(allMember.get(i));
						// }
					}
					// Tao list tring toan bo code employee nhom khac
					for (EmployeeDTO m : allMemberOther) {
						personalOthersTemp.add(m.getCode());
					}
					return personalOthersTemp;
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

	public void create() {
		try {
			if (EMPLOYEE_SERVICE_PUBLIC == null) {
				EMPLOYEE_SERVICE_PUBLIC = new EmployeeServicePublicProxy();
			}
			this.personalOthers = this.createAllCodeMemberOther();
			if (personalOthers == null || personalOthers.isEmpty()) {
				return;
			}
			if (allowSave(null)) {
				// kiem tra -> ngay cua thang hien tai
				java.util.Date date = new Date();
				Calendar currentDate = Calendar.getInstance();
				currentDate.setTime(date);
				// int monthCurrent = currentDate.get(Calendar.MONTH) + 1;
				// int yearCurrent = currentDate.get(Calendar.YEAR);
				// khong cho tao kpi thang truoc hoac thang sau
				// if (monthSearch != monthCurrent || yearCurrent != yearSearch)
				// {
				// noticeError("Không thể tạo mới");
				// return;
				// }
				for (int i = 0; i < personalOthers.size(); i++) {
					EmployeeDTO memberTemp = EMPLOYEE_SERVICE_PUBLIC.findByCode(personalOthers.get(i));
					KPIPersonalOther temp = new KPIPersonalOther();
					temp.setCodeEmp(personalOthers.get(i));
					temp.setNameEmp(memberTemp.getName());
					temp.setNameDepart(memberTemp.getNameDepart());
					temp.setkMonth(monthSearch);
					temp.setkYear(yearSearch);
					temp.setTotal(100);
					// kiem tra nhan vien do da duoc tao kpi chua
					List<KPIPersonalOther> personIsExist = PERSONAL_OTHER_SERVICE.find(null, monthSearch, yearSearch,
							personalOthers.get(i));
					if (personIsExist.isEmpty()) {
						KPIPersonalOther statusCreate = PERSONAL_OTHER_SERVICE.create(temp);
						if (statusCreate == null) {
							// ghi loi cho nay
							System.out.println("Loi");
						}
					}
				}
				kpiPersonalOthers = PERSONAL_OTHER_SERVICE.find(personalOthers, monthSearch, yearSearch);
				notice("Thành công");
			} else {
				noticeError("Không có quyền");
			}
		} catch (Exception e) {
			e.printStackTrace();
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
			// if (checkDelete) {
			// KPIPersonalOther checkUpdate =
			// PERSONAL_OTHER_SERVICE.update(temp);
			// if (checkUpdate != null) {
			// notice("Thành công");
			// kpiPersonalOthers = PERSONAL_OTHER_SERVICE.find(personalOthers,
			// monthSearch, yearSearch);
			// this.details =
			// PERSONAL_OTHER_DETAIL_SERVICE.find(personSelected);
			//
			// }else {
			// noticeError("Lỗi không cập nhật được");
			// }
			// } else {
			// noticeError("Lỗi");
			// }

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
			notice("Thành công");
		} else {
			noticeError("Không có quyền cập nhật");
		}
	}

	// public void saveDetailRemove() {
	// try {
	// // get kpiperson tu DB len de update list kpipersonOfMonth
	// List<KPIPerson> temps =
	// kPIPersonService.findRange(personSelecting.getCodeEmp(),
	// personSelecting.getKmonth(), personSelecting.getKyear());
	// KPIPerson temp = temps.get(0);
	// temp.setKpiPersonOfMonths(listDetail);
	// temp.setModifiedDate(new Date());
	// KPIPerson resultUpdate = kPIPersonService.updateAssign(temp);
	// if (resultUpdate != null) {
	// notice("Cập nhật thành công");
	// } else {
	// noticeError("Xảy ra lỗi");
	// }
	// } catch (Exception e) {
	// // TODO: handle exception
	// }
	//
	// }
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
				if (checkIsSelect == false) {
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
								// otherDetailsTemp.add(item);
								listDetailAdd.add(item);
								double totalTemp2 = otherQuery.getTotal() - (item.getMinuspoint() * item.getQuantity());
								otherQuery.setTotal(totalTemp2);
							}
						}

						//// item.setKpiPerson(this.personSelected);
						// if (listKPIPersonalOtherDetailUpdate.size() == 0) {
						// listKPIPersonalOtherDetailUpdate.add(item);
						// // Tru diem sau khi them thai do hanh vi tren giao
						//// dien
						// double totalTemp = this.personSelected.getTotal() -
						//// item.getMinuspoint();
						// this.personSelected.setTotal(totalTemp);
						// for (int a = 0; a < kpiPersonalOthers.size(); a++) {
						// if
						//// (kpiPersonalOthers.get(a).getCodeEmp().equals(this.personSelected.getCodeEmp()))
						//// {
						// kpiPersonalOthers.get(a).setTotal(personSelected.getTotal());
						// }
						// }
						//
						// } else {
						// for (int h = 0; h <
						//// listKPIPersonalOtherDetailUpdate.size(); h++) {
						//// if (item.getKpiPerson().getCodeEmp()
						//// .equals(listKPIPersonalOtherDetailUpdate.get(h).getKpiPerson().getCodeEmp()))
						//// {
						//// verify = true;
						//// if (listBehaviour.get(h).getContentAppreciate()
						//// .equals(item.getContentAppreciate())) {
						//// isExist = true;
						//// break;
						//// }
						//// }
						// }
						// if (verify == true && isExist == false) {
						// listKPIPersonalOtherDetailUpdate.add(item);
						// double totalTemp2 = this.personSelected.getTotal() -
						//// item.getMinuspoint();
						// this.personSelected.setTotal(totalTemp2);
						//// for(int a = 0; a < kPIPersons.size(); a++) {
						//// if(kPIPersons.get(a).getCodeEmp().equals(this.personSelected.getCodeEmp()))
						//// {
						//// kPIPersons.get(a).setTotal(personSelected.getTotal());
						//// }
						//// }
						// }
						// if (verify == false && isExist == false) {
						// listKPIPersonalOtherDetailUpdate.add(item);
						// double totalTemp1 = this.personSelected.getTotal() -
						//// item.getMinuspoint();
						//// this.personSelected.setTotal(totalTemp1);
						//// for(int a = 0; a < kPIPersons.size(); a++) {
						//// if(kPIPersons.get(a).getCodeEmp().equals(this.personSelected.getCodeEmp()))
						//// {
						//// kPIPersons.get(a).setTotal(personSelected.getTotal());
						//// }
						//// }
						// }
						// }
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

	public List<BehaviourPersonOther> getBehaviourOthers() {
		return behaviourOthers;
	}

	public void setBehaviourOthers(List<BehaviourPersonOther> behaviourOthers) {
		this.behaviourOthers = behaviourOthers;
	}

	public List<BehaviourPersonOther> getBehaviourOthersSelected() {
		return behaviourOthersSelected;
	}

	public void setBehaviourOthersSelected(List<BehaviourPersonOther> behaviourOthersSelected) {
		this.behaviourOthersSelected = behaviourOthersSelected;
	}

	public List<KPIPersonalOtherDetail> getPersonDetails() {
		return personDetails;
	}

	public void setPersonDetails(List<KPIPersonalOtherDetail> personDetails) {
		this.personDetails = personDetails;
	}

	public List<Department> getDepartmentSearchs() {
		return departmentSearchs;
	}

	public void setDepartmentSearchs(List<Department> departmentSearchs) {
		this.departmentSearchs = departmentSearchs;
	}

	public Department getDepartmentSelected() {
		return departmentSelected;
	}

	public void setDepartmentSelected(Department departmentSelected) {
		this.departmentSelected = departmentSelected;
	}

	public List<KPIPerson> getkPIPersonFilters() {
		return kPIPersonFilters;
	}

	public void setkPIPersonFilters(List<KPIPerson> kPIPersonFilters) {
		this.kPIPersonFilters = kPIPersonFilters;
	}

	public KPIPersonalOther getPersonSelected() {
		return personSelected;
	}

	public void setPersonSelected(KPIPersonalOther personSelected) {
		this.personSelected = personSelected;
	}
}
