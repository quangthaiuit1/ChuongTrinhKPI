package trong.lixco.com.thai.bean;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.jboss.logging.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.primefaces.context.RequestContext;

import trong.lixco.com.account.servicepublics.Department;
import trong.lixco.com.account.servicepublics.DepartmentServicePublic;
import trong.lixco.com.account.servicepublics.DepartmentServicePublicProxy;
import trong.lixco.com.account.servicepublics.Member;
import trong.lixco.com.account.servicepublics.MemberServicePublic;
import trong.lixco.com.account.servicepublics.MemberServicePublicProxy;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.ejb.service.ParamReportDetailService;
import trong.lixco.com.ejb.servicekpi.BehaviourPersonService;
import trong.lixco.com.ejb.servicekpi.EmpPJobService;
import trong.lixco.com.ejb.servicekpi.FormulaKPIService;
import trong.lixco.com.ejb.servicekpi.KPIPersonOfMonthService;
import trong.lixco.com.ejb.servicekpi.KPIPersonService;
import trong.lixco.com.ejb.servicekpi.OrientationPersonService;
import trong.lixco.com.ejb.servicekpi.PositionJobService;
import trong.lixco.com.ejb.thai.kpi.PersonalPerformanceService;
import trong.lixco.com.jpa.entitykpi.BehaviourPerson;
import trong.lixco.com.jpa.entitykpi.EmpPJob;
import trong.lixco.com.jpa.entitykpi.FormulaKPI;
import trong.lixco.com.jpa.entitykpi.KPIDepOfMonth;
import trong.lixco.com.jpa.entitykpi.KPIPerson;
import trong.lixco.com.jpa.entitykpi.KPIPersonOfMonth;
import trong.lixco.com.jpa.entitykpi.OrientationPerson;
import trong.lixco.com.jpa.entitykpi.PositionJob;
import trong.lixco.com.jpa.thai.KPIPersonalOther;
import trong.lixco.com.jpa.thai.KPIPersonalPerformance;
import trong.lixco.com.kpi.general.ApplicationBean;
import trong.lixco.com.thai.bean.entities.InfoPersonalPerformance;
import trong.lixco.com.util.DepartmentUtil;
import trong.lixco.com.util.Notify;

@ManagedBean
@ViewScoped
public class PersonalPerformanceOthersBean extends AbstractBean<KPIPerson> {

	private static final long serialVersionUID = 1L;
	private SimpleDateFormat sf;
	private Notify notify;
	private List<KPIPerson> kPIPersons;
	private List<KPIPerson> kPIPersonFilters;

	private boolean orverideData = false;

	private final int TOTALPARAM = 100;

	private KPIPerson kPIPerson;
	private KPIPerson kPIPersonEdit;
	private List<KPIPersonOfMonth> kpiPersonOfMonths;
	private List<KPIPersonOfMonth> kpiPersonOfMonthAdds;
	private List<KPIPersonOfMonth> kpiPersonOfMonthRemoves;

	// private DepartmentParent departmentParent, departmentParentSearch;
	private PositionJob positionJobSearch;
	// private List<DepartmentParent> departmentParents;
	// private Department departmentSearch;
	private List<Department> departmentSearchs;
	private boolean isEmp = false;// la nhan vien
	private Department departmentSelected;
	private int monthSelected;
	private int[] months = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
	private int quySelected;
	private int[] quys = { 1, 2, 3, 4 };

	// private List<Employee> employees;
	// private Employee employeeSearch;
	private int monthSearch = 0;
	private int yearSearch = 0;
	private boolean select = false;

	private int monthCopy = 0;
	private int yearCopy = 0;

	int totalHV = 100;
	double totalCV = 0;

	private int tabindex;

	private PositionJob positionJobSelect;
	// private List<DepartmentParent> departmentParentSearchs;

	private List<Boolean> list;
	private List<OrientationPerson> orientationPersons;

	private List<KPIDepOfMonth> kpiDepOfMonths;

	private List<BehaviourPerson> behaviourPersons;
	private List<FormulaKPI> formulaKPIs;
	private FormulaKPI formulaKPISelect;

	@Inject
	private FormulaKPIService formulaKPIService;
	@Inject
	private BehaviourPersonService behaviourPersonService;
	@Inject
	private ParamReportDetailService paramReportDetailService;
	@Inject
	private OrientationPersonService orientationPersonService;
	// @Inject
	// private DepartmentParentService departmentParentService;
	@Inject
	private KPIPersonService kPIPersonService;
	@Inject
	private PositionJobService positionJobService;
	// @Inject
	// private EmployeeService employeeService;
	@Inject
	private Logger logger;
	@Inject
	private ApplicationBean applicationBean;

	private Department department;
	private List<Department> departments;
	private Member member;
	private List<Member> members;

	DepartmentServicePublic departmentServicePublic;
	MemberServicePublic memberServicePublic;
	@Inject
	private KPIPersonOfMonthService kPIPersonOfMonthService;

	@Override
	public void initItem() {
		departmentSelected = new Department();
		sf = new SimpleDateFormat("dd/MM/yyyy");
		kPIPersons = new ArrayList<KPIPerson>();
		kpiPersonOfMonths = new ArrayList<KPIPersonOfMonth>();
		kpiPersonOfMonthAdds = new ArrayList<KPIPersonOfMonth>();
		kpiPersonOfMonthRemoves = new ArrayList<KPIPersonOfMonth>();
		orientationPersons = new ArrayList<OrientationPerson>();

		try {
			departmentServicePublic = new DepartmentServicePublicProxy();
			memberServicePublic = new MemberServicePublicProxy();
			departments = new ArrayList<Department>();
			members = new ArrayList<Member>();
			member = getAccount().getMember();
			departmentSearchs = new ArrayList<Department>();
			if (getAccount().isAdmin()) {
				Department[] deps = departmentServicePublic.findAll();
				for (int i = 0; i < deps.length; i++) {
					if (deps[i].getLevelDep() != null)
						if (deps[i].getLevelDep().getLevel() > 1)
							departmentSearchs.add(deps[i]);
				}

			} else {
				departmentSearchs.add(member.getDepartment());
			}
			if (departmentSearchs.size() != 0) {
				departmentSearchs = DepartmentUtil.sort(departmentSearchs);
				// departmentSearch = departmentSearchs.get(0);
			}
		} catch (Exception e) {
		}

		LocalDate lc = new LocalDate();
		monthSearch = lc.getMonthOfYear();
		yearSearch = lc.getYear();

		list = Arrays.asList(true, true, true, true, true, false, false, false, false, false, false, true, true, true,
				true, true, true, false, true);
		tabindex = 0;
		searchItem();
		Member[] membersTemp = null;
		try {
			membersTemp = memberServicePublic.findByCodeDepart(member.getDepartment().getCode());
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		allMemberByDepartment = Arrays.asList(membersTemp);
	}

	public void showEdit() {
		kpiPersonOfMonthRemoves.clear();
		kpiPersonOfMonthAdds.clear();
		kpiPersonOfMonths.clear();
		if (kPIPersonEdit != null && kPIPersonEdit.getId() != null) {
			KPIPerson od = kPIPersonService.findByIdAll(kPIPersonEdit.getId());
			if (od != null) {
				kPIPerson = od;
				try {
					member = memberServicePublic.findByCode(kPIPerson.getCodeEmp());
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				for (int i = 0; i < od.getKpiPersonOfMonths().size(); i++) {
					if (od.getKpiPersonOfMonths().get(i).isBehaviour()) {
						kpiPersonOfMonthAdds.add(od.getKpiPersonOfMonths().get(i));
					} else {
						kpiPersonOfMonths.add(od.getKpiPersonOfMonths().get(i));
					}
				}
				for (int i = 0; i < kpiPersonOfMonthAdds.size(); i++) {
					kpiPersonOfMonthAdds.get(i).setNo(i + 1);
				}
				for (int i = 0; i < kpiPersonOfMonths.size(); i++) {
					kpiPersonOfMonths.get(i).setIndex(i + 1);
				}
			}
		}

	}

	public void showBehaviour() {
		behaviourPersons = behaviourPersonService.findAll();
	}

	public void addBehaviour() {
		for (int i = 0; i < behaviourPersons.size(); i++) {
			if (behaviourPersons.get(i).isSelect()) {
				KPIPersonOfMonth item = new KPIPersonOfMonth();
				item.setBehaviour(true);
				item.setRatioComplete(-behaviourPersons.get(i).getMinusPoint());
				item.setRatioCompleteIsWeighted((-behaviourPersons.get(i).getMinusPoint() * 10) / 100);
				item.setContentAppreciate(behaviourPersons.get(i).getContent());
				kpiPersonOfMonthAdds.add(item);
			}

		}
		for (int i = 0; i < kpiPersonOfMonthAdds.size(); i++) {
			kpiPersonOfMonthAdds.get(i).setNo(i + 1);
		}
	}

	public void searchItem() {
		try {
			kPIPersons = kPIPersonService.findRange(member.getCode(), 0, yearSearch);
		} catch (Exception e) {
		}
		try {
			List<String> members = new ArrayList<String>();
			Member mems[] = null;
			if (departmentSelected != null) {
				mems = memberServicePublic.findByCodeDepart(departmentSelected.getCode());
				if (mems != null)
					for (int i = 0; i < mems.length; i++) {
						members.add(mems[i].getCode());
					}
				// Check if month not selected
				kPIPersons = kPIPersonService.findRange(member.getCode(), monthSearch, yearSearch);

				for (int i = 0; i < kPIPersons.size(); i++) {
					try {
						kPIPersons.get(i)
								.setNameEmp(memberServicePublic.findByCode(kPIPersons.get(i).getCodeEmp()).getName());
					} catch (Exception e) {
					}
				}
			}
		} catch (Exception e) {
		}
	}

	public boolean validate() {
		if (kPIPerson != null && member != null)
			return true;
		return false;
	}

	List<Member> allMemberByDepartment = new ArrayList<>();

	public void create() {
		notify = new Notify(FacesContext.getCurrentInstance());
		Date date = new Date(kPIPerson.getKyear(), kPIPerson.getKmonth(), 1);
		if (allowSave(date)) {
			List<KPIPerson> temps = kPIPersonService.findRange(allMemberByDepartment.get(0).getCode(), monthSearch,
					yearSearch);

			if (temps.size() != 0) {
				noticeError("KPI tháng " + monthSearch + " đã được tạo.");
			} else {
				for (int i = 0; i < allMemberByDepartment.size(); i++) {
					// List<KPIPersonOfMonth> listSaves = new
					// ArrayList<KPIPersonOfMonth>();
					// // trong
					// listSaves.addAll(kpiPersonOfMonths);
					// listSaves.addAll(kpiPersonOfMonthAdds);
					KPIPerson kpiPersonNew = new KPIPerson();
					// kpiPersonNew.setKpiPersonOfMonths(listSaves);
					kpiPersonNew.setTotal(100.0);
					kpiPersonNew.setCodeEmp(allMemberByDepartment.get(i).getCode());
					kpiPersonNew.setKmonth(monthSearch);
					KPIPerson wf = kPIPersonService.saveOrUpdate(kpiPersonNew, kpiPersonOfMonthRemoves);
					if (wf != null) {
						this.kPIPersonEdit = wf;
						showEdit();
						searchItem();
						notice("Lưu thành công");
					} else {
						noticeError("Xảy ra lỗi không lưu được");
					}
					// end trong
				}
			}
			System.out.println("ahhi");

		} else {
			notify.warningDetail();
		}
	}

	public void createOrUpdate() {
		notify = new Notify(FacesContext.getCurrentInstance());
		// kPIPerson.setCodeEmp(member.getCode());
		//
		// KPIPerson kpiPersonOld =
		// kPIPersonService.findById(kPIPerson.getId());
		// if (kpiPersonOld.isSignResultKPI()) {
		// this.kPIPersonEdit = kpiPersonOld;
		// showEdit();
		// noticeError("Phiếu đã duyệt kết quả. Không lưu được");
		// } else {
		// Date date2 = new Date(kpiPersonOld.getKyear(),
		// kpiPersonOld.getKmonth(), 1);
		if (allowUpdate(null)) {
			// if (kpiPersonOld.getKmonth() != kPIPerson.getKmonth()
			// || kpiPersonOld.getKyear() != kPIPerson.getKyear()) {
			// List<KPIPerson> temps =
			// kPIPersonService.findRange(kPIPerson.getCodeEmp(),
			// kPIPerson.getKmonth(),
			// kPIPerson.getKyear());
			// if (temps.size() != 0) {
			// boolean status = false;
			// for (int i = 0; i < temps.size(); i++) {
			// if (temps.get(i).isSignKPI()) {
			// status = true;
			// break;
			// }
			// }
			// if (status) {
			// noticeError("Tháng đã duyệt đăng ký không thay thế được.");
			// } else {
			// //
			// //////////////////////////////////////////////////////////////////////////////////////////////////////////
			// RequestContext context = RequestContext.getCurrentInstance();
			// context.execute("PF('dialogConfirm').show();");
			// }
			// } else {
			// List<KPIPersonOfMonth> listSaves = new
			// ArrayList<KPIPersonOfMonth>();
			// double checkp = 0;
			// // thai
			// double checkKPIPerformanceWeighted = 0;
			// boolean checkKPIInvalid = false;
			// // end thai
			// for (int i = 0; i < kpiPersonOfMonths.size(); i++) {
			// checkp += kpiPersonOfMonths.get(i).getWeighted();
			// // thai
			// if (kpiPersonOfMonths.get(i).isKPIPerformance()) {
			// if (kpiPersonOfMonths.get(i).getWeighted() >= 10) {
			// checkKPIPerformanceWeighted = checkKPIPerformanceWeighted
			// + kpiPersonOfMonths.get(i).getWeighted();
			// }
			// }
			// if (kpiPersonOfMonths.get(i).getWeighted() < 10) {
			// checkKPIInvalid = true;
			// }
			// // end thai
			// }
			// if (kpiPersonOfMonths.size() != 0 && checkp != TOTALPARAM) {
			// noticeError("Không lưu được. Tổng trọng số các mục tiêu khác
			// 100%");
			// } else {
			// listSaves.addAll(kpiPersonOfMonths);
			// listSaves.addAll(kpiPersonOfMonthAdds);
			// kPIPerson.setKpiPersonOfMonths(listSaves);
			// KPIPerson wf = kPIPersonService.saveOrUpdate(kPIPerson,
			// kpiPersonOfMonthRemoves);
			// // Thai
			// if (checkKPIInvalid == false && checkKPIPerformanceWeighted >=
			// 80) {
			// // trong
			// if (wf != null) {
			// this.kPIPersonEdit = wf;
			// showEdit();
			// writeLogInfo("Cap nhat" + wf.toString());
			// notice("Lưu thành công.");
			// } else {
			// noticeError("Xảy ra lỗi khi lưu.");
			// }
			// // end trong
			// } else {
			// if (checkKPIInvalid == true) {
			// noticeError("Trọng số KPI phải lớn hơn 10%");
			// } else {
			// noticeError("Tổng trọng số KPI hiệu suất không được bé hơn 80%");
			// }
			// }
			// // end thai
			// }
			// }
			// } else {
			// test
			for (int i = 0; i < allMemberByDepartment.size(); i++) {
				KPIPerson kpiPersonOld = kPIPersonService.findRangeNew(allMemberByDepartment.get(i).getCode(),
						monthSearch, yearSearch);
				List<KPIPersonOfMonth> kpiOfMonths = kpiPersonOld.getKpiPersonOfMonths();
				boolean verify = false;

				for (int j = 0; j < listBehaviour.size(); j++) {
					boolean isExist = false;
					// check xem co dung nguoi can them hay khong
					if (listBehaviour.get(j).getKpiPerson().getCodeEmp().equals(kpiPersonOld.getCodeEmp())) {
						verify = true;
						// check duoi DB da co hanh vi nao chua
						if (kpiOfMonths.size() != 0) {
							for (int k = 0; k < kpiOfMonths.size(); k++) {
								if (kpiOfMonths.get(k).getContentAppreciate()
										.equals(listBehaviour.get(j).getContentAppreciate())) {
									isExist = true;
								}
							}
							if (isExist == false && verify == true) {
								kpiOfMonths.add(listBehaviour.get(j));
							}
						} else {
							if (isExist == false && verify == true) {
								kpiOfMonths.add(listBehaviour.get(j));
							}
						}

					}
				}
				kpiPersonOld.setKpiPersonOfMonths(kpiOfMonths);
				// check trong cai list persons ban dau de lay total cua tung
				// cai
				for (KPIPerson k : kPIPersons) {
					if (k.getCodeEmp().equals(kpiPersonOld.getCodeEmp())) {
						kpiPersonOld.setTotal(k.getTotal());
					}
					break;
				}
				KPIPerson wf = kPIPersonService.updateAssign(kpiPersonOld);
				if (wf != null) {
					this.kPIPersonEdit = wf;
					// showEdit();
					// writeLogInfo("Cap nhat" + wf.toString());
					notice("Lưu thành công.");
				} else {
					noticeError("Xảy ra lỗi khi lưu.");
				}
			}
			listBehaviour = new ArrayList<>();
			// end

			// //use
			// List<KPIPersonOfMonth> listSaves = new
			// ArrayList<KPIPersonOfMonth>();
			// List<KPIPerson> temps =
			// kPIPersonService.findRange(kPIPerson.getCodeEmp(),
			// kPIPerson.getKmonth(),
			// kPIPerson.getKyear());
			// if (temps.size() != 0) {
			// boolean status = false;
			// for (int i = 0; i < temps.size(); i++) {
			// if (temps.get(i).isSignKPI()) {
			// status = true;
			// break;
			// }
			// }
			// listSaves.addAll(kpiPersonOfMonths);
			// listSaves.addAll(kpiPersonOfMonthAdds);
			// kPIPerson.setKpiPersonOfMonths(listSaves);
			// if (status) {
			// // Thai
			// KPIPerson wf = kPIPersonService.updateAssign(kPIPerson);
			// if (wf != null) {
			// this.kPIPersonEdit = wf;
			// showEdit();
			// notice("Lưu thành công");
			// } else {
			// noticeError("Xảy ra lỗi không lưu được");
			// }
			//
			// } else {
			// // Thai
			// KPIPerson wf = kPIPersonService.saveOrUpdate(kPIPerson,
			// kpiPersonOfMonthRemoves);
			// if (wf != null) {
			// this.kPIPersonEdit = wf;
			// showEdit();
			// notice("Lưu thành công");
			// } else {
			// noticeError("Xảy ra lỗi không lưu được");
			// }
			// }
			// }
			//
			//// }
			//
			// } else {
			// notify.warningDetail();
			// }
			// //use
		}
	}

	private List<InfoPersonalPerformance> listInfoPersonalPerformances;
	@Inject
	private EmpPJobService empPJobService;
	private List<PositionJob> positionJobs;
	private List<KPIPersonalPerformance> listKPIPersonalByEmployee;
	@Inject
	private PersonalPerformanceService PERSONAL_PERFORMANCE_SERVICE;
	@Inject
	private PositionJobService POSITION_JOB_SERVICE;

	private KPIPerson personSelected;

	public void showDialogOrien(KPIPerson personSelected) {
		this.personSelected = personSelected;
		listInfoPersonalPerformances = new ArrayList<>();
		notify = new Notify(FacesContext.getCurrentInstance());
		if (personSelected.getId() != null) {
			// tat ca vi tri cong viec
			positionJobs = new ArrayList<PositionJob>();
			List<EmpPJob> empPJobs = empPJobService.findByEmployee(personSelected.getCodeEmp());
			for (int i = 0; i < empPJobs.size(); i++) {
				PositionJob pj = positionJobService.findByCode(empPJobs.get(i).getCodePJob());
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

	private List<KPIPersonOfMonth> listDetail;
	private KPIPerson personSelecting;

	// xem chi tiet diem tru
	public void showDetailBehaviour(KPIPerson personSelected) {
		personSelecting = personSelected;
		KPIPerson temp = kPIPersonService.findRangeNew(personSelected.getCodeEmp(), personSelected.getKmonth(),
				personSelected.getKyear());
		listDetail = temp.getKpiPersonOfMonths();
		System.out.println(listDetail.size());
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('widgetKPIPersonalOtherBehaviour').show();");
	}

	// Xoa chi tiet diem tru
	public void removeDetail(KPIPersonOfMonth remove) {
		if (listDetail.remove(remove) == false) {
			System.out.println("Xoa khong thanh cong");
		}
	}

	public void saveDetailRemove() {
		try {
			// get kpiperson tu DB len de update list kpipersonOfMonth
			List<KPIPerson> temps = kPIPersonService.findRange(personSelecting.getCodeEmp(),
					personSelecting.getKmonth(), personSelecting.getKyear());
			KPIPerson temp = temps.get(0);
			temp.setKpiPersonOfMonths(listDetail);
			temp.setModifiedDate(new Date());
			KPIPerson resultUpdate = kPIPersonService.updateAssign(temp);
			if (resultUpdate != null) {
				notice("Cập nhật thành công");
			} else {
				noticeError("Xảy ra lỗi");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	List<KPIPersonOfMonth> listBehaviour = new ArrayList<>();
	List<KPIPerson> listKPIPersonBehaviourAdded = new ArrayList<>();

	public void getListKPIPerformance() {
		try {
			// Cap nhat list kpi hieu suat de selected khong bi lap
			for (int i = 0; i < listInfoPersonalPerformances.size(); i++) {
				for (int j = 0; j < listInfoPersonalPerformances.get(i).getPersonalPerformances().size(); j++) {
					if (listInfoPersonalPerformances.get(i).getPersonalPerformances().get(j).isSelect()) {
						boolean isExist = false;
						boolean verify = false;
						// check xem item nao duoc chon
						// KPIPerson itemp = this.personSelected;
						KPIPersonOfMonth item = new KPIPersonOfMonth();
						item.setBehaviour(true);
						item.setContentAppreciate(
								listInfoPersonalPerformances.get(i).getPersonalPerformances().get(j).getContent());
						item.setWeighted(
								listInfoPersonalPerformances.get(i).getPersonalPerformances().get(j).getMinuspoint());
						item.setKpiPerson(this.personSelected);
						if (listBehaviour.size() == 0) {
							listBehaviour.add(item);
							// Tru diem sau khi them thai do hanh vi tren giao
							// dien
							double totalTemp = this.personSelected.getTotal() - item.getWeighted();
							this.personSelected.setTotal(totalTemp);
							for (int a = 0; a < kPIPersons.size(); a++) {
								if (kPIPersons.get(a).getCodeEmp().equals(this.personSelected.getCodeEmp())) {
									kPIPersons.get(a).setTotal(personSelected.getTotal());
								}
							}

						} else {
							for (int h = 0; h < listBehaviour.size(); h++) {
								if (item.getKpiPerson().getCodeEmp()
										.equals(listBehaviour.get(h).getKpiPerson().getCodeEmp())) {
									verify = true;
									if (listBehaviour.get(h).getContentAppreciate()
											.equals(item.getContentAppreciate())) {
										isExist = true;
										break;
									}
								}
							}
							if (verify == true && isExist == false) {
								listBehaviour.add(item);
								double totalTemp2 = this.personSelected.getTotal() - item.getWeighted();
								this.personSelected.setTotal(totalTemp2);
								for (int a = 0; a < kPIPersons.size(); a++) {
									if (kPIPersons.get(a).getCodeEmp().equals(this.personSelected.getCodeEmp())) {
										kPIPersons.get(a).setTotal(personSelected.getTotal());
									}
								}
							}
							if (verify == false && isExist == false) {
								listBehaviour.add(item);
								double totalTemp1 = this.personSelected.getTotal() - item.getWeighted();
								this.personSelected.setTotal(totalTemp1);
								for (int a = 0; a < kPIPersons.size(); a++) {
									if (kPIPersons.get(a).getCodeEmp().equals(this.personSelected.getCodeEmp())) {
										kPIPersons.get(a).setTotal(personSelected.getTotal());
									}
								}
							}
						}
						listInfoPersonalPerformances.get(i).getPersonalPerformances().get(j).setSelect(false);
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@Override
	protected Logger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<KPIPersonOfMonth> getListDetail() {
		return listDetail;
	}

	public void setListDetail(List<KPIPersonOfMonth> listDetail) {
		this.listDetail = listDetail;
	}

	public List<InfoPersonalPerformance> getListInfoPersonalPerformances() {
		return listInfoPersonalPerformances;
	}

	public void setListInfoPersonalPerformances(List<InfoPersonalPerformance> listInfoPersonalPerformances) {
		this.listInfoPersonalPerformances = listInfoPersonalPerformances;
	}

	public Notify getNotify() {
		return notify;
	}

	public void setNotify(Notify notify) {
		this.notify = notify;
	}

	public List<KPIPerson> getkPIPersons() {
		return kPIPersons;
	}

	public void setkPIPersons(List<KPIPerson> kPIPersons) {
		this.kPIPersons = kPIPersons;
	}

	public List<KPIPerson> getkPIPersonFilters() {
		return kPIPersonFilters;
	}

	public void setkPIPersonFilters(List<KPIPerson> kPIPersonFilters) {
		this.kPIPersonFilters = kPIPersonFilters;
	}

	public boolean isOrverideData() {
		return orverideData;
	}

	public void setOrverideData(boolean orverideData) {
		this.orverideData = orverideData;
	}

	public KPIPerson getkPIPerson() {
		return kPIPerson;
	}

	public void setkPIPerson(KPIPerson kPIPerson) {
		this.kPIPerson = kPIPerson;
	}

	public KPIPerson getkPIPersonEdit() {
		return kPIPersonEdit;
	}

	public void setkPIPersonEdit(KPIPerson kPIPersonEdit) {
		this.kPIPersonEdit = kPIPersonEdit;
	}

	public List<KPIPersonOfMonth> getKpiPersonOfMonths() {
		return kpiPersonOfMonths;
	}

	public void setKpiPersonOfMonths(List<KPIPersonOfMonth> kpiPersonOfMonths) {
		this.kpiPersonOfMonths = kpiPersonOfMonths;
	}

	public List<KPIPersonOfMonth> getKpiPersonOfMonthAdds() {
		return kpiPersonOfMonthAdds;
	}

	public void setKpiPersonOfMonthAdds(List<KPIPersonOfMonth> kpiPersonOfMonthAdds) {
		this.kpiPersonOfMonthAdds = kpiPersonOfMonthAdds;
	}

	public List<KPIPersonOfMonth> getKpiPersonOfMonthRemoves() {
		return kpiPersonOfMonthRemoves;
	}

	public void setKpiPersonOfMonthRemoves(List<KPIPersonOfMonth> kpiPersonOfMonthRemoves) {
		this.kpiPersonOfMonthRemoves = kpiPersonOfMonthRemoves;
	}

	public PositionJob getPositionJobSearch() {
		return positionJobSearch;
	}

	public void setPositionJobSearch(PositionJob positionJobSearch) {
		this.positionJobSearch = positionJobSearch;
	}

	// public Department getDepartmentSearch() {
	// return departmentSearch;
	// }
	// public void setDepartmentSearch(Department departmentSearch) {
	// this.departmentSearch = departmentSearch;
	// }
	public List<Department> getDepartmentSearchs() {
		return departmentSearchs;
	}

	public void setDepartmentSearchs(List<Department> departmentSearchs) {
		this.departmentSearchs = departmentSearchs;
	}

	public boolean isEmp() {
		return isEmp;
	}

	public void setEmp(boolean isEmp) {
		this.isEmp = isEmp;
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

	public boolean isSelect() {
		return select;
	}

	public void setSelect(boolean select) {
		this.select = select;
	}

	public int getMonthCopy() {
		return monthCopy;
	}

	public void setMonthCopy(int monthCopy) {
		this.monthCopy = monthCopy;
	}

	public int getYearCopy() {
		return yearCopy;
	}

	public void setYearCopy(int yearCopy) {
		this.yearCopy = yearCopy;
	}

	public int getTabindex() {
		return tabindex;
	}

	public void setTabindex(int tabindex) {
		this.tabindex = tabindex;
	}

	public PositionJob getPositionJobSelect() {
		return positionJobSelect;
	}

	public void setPositionJobSelect(PositionJob positionJobSelect) {
		this.positionJobSelect = positionJobSelect;
	}

	public List<Boolean> getList() {
		return list;
	}

	public void setList(List<Boolean> list) {
		this.list = list;
	}

	public List<OrientationPerson> getOrientationPersons() {
		return orientationPersons;
	}

	public void setOrientationPersons(List<OrientationPerson> orientationPersons) {
		this.orientationPersons = orientationPersons;
	}

	public List<KPIDepOfMonth> getKpiDepOfMonths() {
		return kpiDepOfMonths;
	}

	public void setKpiDepOfMonths(List<KPIDepOfMonth> kpiDepOfMonths) {
		this.kpiDepOfMonths = kpiDepOfMonths;
	}

	public List<BehaviourPerson> getBehaviourPersons() {
		return behaviourPersons;
	}

	public void setBehaviourPersons(List<BehaviourPerson> behaviourPersons) {
		this.behaviourPersons = behaviourPersons;
	}

	public List<FormulaKPI> getFormulaKPIs() {
		return formulaKPIs;
	}

	public void setFormulaKPIs(List<FormulaKPI> formulaKPIs) {
		this.formulaKPIs = formulaKPIs;
	}

	public FormulaKPI getFormulaKPISelect() {
		return formulaKPISelect;
	}

	public void setFormulaKPISelect(FormulaKPI formulaKPISelect) {
		this.formulaKPISelect = formulaKPISelect;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public List<Department> getDepartments() {
		return departments;
	}

	public void setDepartments(List<Department> departments) {
		this.departments = departments;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public List<Member> getMembers() {
		return members;
	}

	public void setMembers(List<Member> members) {
		this.members = members;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public Department getDepartmentSelected() {
		return departmentSelected;
	}

	public void setDepartmentSelected(Department departmentSelected) {
		this.departmentSelected = departmentSelected;
	}

	public int getMonthSelected() {
		return monthSelected;
	}

	public void setMonthSelected(int monthSelected) {
		this.monthSelected = monthSelected;
	}

	public int[] getMonths() {
		return months;
	}

	public void setMonths(int[] months) {
		this.months = months;
	}

	public int getQuySelected() {
		return quySelected;
	}

	public void setQuySelected(int quySelected) {
		this.quySelected = quySelected;
	}

	public int[] getQuys() {
		return quys;
	}

	public void setQuys(int[] quys) {
		this.quys = quys;
	}
}
