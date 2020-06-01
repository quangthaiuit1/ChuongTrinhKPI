package trong.lixco.com.beankpi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;
import org.joda.time.LocalDate;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.context.RequestContext;

import trong.lixco.com.account.servicepublics.DepartmentServicePublic;
import trong.lixco.com.account.servicepublics.DepartmentServicePublicProxy;
import trong.lixco.com.account.servicepublics.Member;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.ejb.servicekpi.DepartmentMeetingService;
import trong.lixco.com.jpa.entitykpi.DepartmentMeeting;
import trong.lixco.com.kpi.general.ApplicationBean;
import trong.lixco.com.util.Notify;

@Named
@ViewScoped
public class DepartmentMeetingBean extends AbstractBean<DepartmentMeeting> {
	private static final long serialVersionUID = 1L;
	private SimpleDateFormat sf;
	private Notify notify;
	private List<DepartmentMeeting> departmentMeetings;
	private List<DepartmentMeeting> departmentMeetingFilters;

	private DepartmentMeeting departmentMeeting;
	private DepartmentMeeting departmentMeetingEdit;
	private List<String> codeDepart;

	private Member member;
	private DepartmentServicePublic departmentServicePublic;

	private int yearS;
	private String codeDepartS;

	@Inject
	private DepartmentMeetingService departmentMeetingService;
	@Inject
	private Logger logger;

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Override
	public void initItem() {
		sf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			departmentServicePublic = new DepartmentServicePublicProxy();
		} catch (Exception e) {
		}
		member = getAccount().getMember();
		reset();
	}

	public void createOrUpdate() {
		notify = new Notify(FacesContext.getCurrentInstance());
		LocalDate lc = new LocalDate();
		Date date = lc.withWeekOfWeekyear(departmentMeeting.getWeek()).withYear(departmentMeeting.getYear())
				.dayOfWeek().withMinimumValue().toDate();
		if (departmentMeeting.getId() == null) {
			try {
				departmentMeeting.setCodeDepart(getAccount().getMember().getDepartment().getCode());
				// departmentMeeting.setIsfinal(isfinal);
				List<DepartmentMeeting> kps = departmentMeetingService.findDepartmentMeeting(departmentMeeting.getWeek(),
						departmentMeeting.getYear(), departmentMeeting.getCodeDepart());
				if (kps.size() == 0) {
					if (allowSave(date)) {
						DepartmentMeeting wf = departmentMeetingService.create(departmentMeeting);
						if (wf != null) {
							searchItem();
							writeLogInfo("Tao moi " + wf.toString());
							notify.success();
						} else {
							notify.error();
						}
					} else {
						notify.warningDetail();
					}
				} else {
					FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Thông báo!",
							"Đã có biên bản họp tuần " + departmentMeeting.getWeek() + " năm "
									+ departmentMeeting.getYear());
					RequestContext.getCurrentInstance().showMessageInDialog(message);
				}
			} catch (Exception e) {
				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Thông báo!",
						"Xảy ra lỗi khi lưu ");
				RequestContext.getCurrentInstance().showMessageInDialog(message);
			}
			
		} else {
			DepartmentMeeting kpiCompOld = departmentMeetingService.findById(departmentMeeting.getId());
			Date date2 = lc.withWeekOfWeekyear(kpiCompOld.getWeek()).withYear(kpiCompOld.getYear()).dayOfWeek()
					.withMinimumValue().toDate();
			if (allowUpdate(date) && allowUpdate(date2)) {
				if (kpiCompOld.getWeek() != departmentMeeting.getWeek()
						|| kpiCompOld.getYear() != departmentMeeting.getYear()) {
					List<DepartmentMeeting> temps = departmentMeetingService
							.findDepartmentMeeting(departmentMeeting.getWeek(), departmentMeeting.getYear(),
									departmentMeeting.getCodeDepart());
					if (temps.size() != 0) {
						FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Thông báo!",
								"Đã có biên bản họp tuần " + departmentMeeting.getWeek() + " năm "
										+ departmentMeeting.getYear());
						RequestContext.getCurrentInstance().showMessageInDialog(message);
					} else {
						DepartmentMeeting wf = departmentMeetingService.update(departmentMeeting);
						if (wf != null) {
							departmentMeeting = departmentMeetingService.findById(wf.getId());
							writeLogInfo("Cap nhat" + wf.toString());
							searchItem();
							notify.success();
						} else {
							notify.error();
						}
					}
				} else {
					DepartmentMeeting wf = departmentMeetingService.update(departmentMeeting);
					if (wf != null) {
						departmentMeeting = departmentMeetingService.findById(wf.getId());
						searchItem();
						writeLogInfo("Cap nhat" + wf.toString());
						notify.success();
					} else {
						notify.error();
					}
				}
			} else {
				notify.warningDetail();
			}
		}

	}

	public void createOrUpdateEmpl() {
		notify = new Notify(FacesContext.getCurrentInstance());
		LocalDate lc = new LocalDate();
		Date date = lc.withWeekOfWeekyear(departmentMeeting.getWeek()).withYear(departmentMeeting.getYear())
				.dayOfWeek().withMinimumValue().toDate();
		if (departmentMeeting.getId() == null) {
			departmentMeeting.setCodeDepart(getAccount().getMember().getDepartment().getCode());
			// departmentMeeting.setIsfinal(isfinal);
			List<DepartmentMeeting> kps = departmentMeetingService.findDepartmentMeeting(departmentMeeting.getWeek(),
					departmentMeeting.getYear(), departmentMeeting.getCodeDepart());
			if (kps.size() == 0) {
				if (allowSave(date)) {
					DepartmentMeeting wf = departmentMeetingService.create(departmentMeeting);
					if (wf != null) {
						searchItem();
						writeLogInfo("Tao moi " + wf.toString());
						notify.success();
					} else {
						notify.error();
					}
				} else {
					notify.warningDetail();
				}
			} else {
				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Thông báo!",
						"Đã có biên bản họp tuần " + departmentMeeting.getWeek() + " năm "
								+ departmentMeeting.getYear());
				RequestContext.getCurrentInstance().showMessageInDialog(message);
			}
		} else {
			DepartmentMeeting kpiCompOld = departmentMeetingService.findById(departmentMeeting.getId());
			Date date2 = lc.withWeekOfWeekyear(kpiCompOld.getWeek()).withYear(kpiCompOld.getYear()).dayOfWeek()
					.withMinimumValue().toDate();
			if (kpiCompOld.isIsfinal()) {
				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Thông báo!",
						"Biên bản đã được duyệt");
				RequestContext.getCurrentInstance().showMessageInDialog(message);
			} else {
				if (allowUpdate(date) && allowUpdate(date2)) {
					if (kpiCompOld.getWeek() != departmentMeeting.getWeek()
							|| kpiCompOld.getYear() != departmentMeeting.getYear()) {
						List<DepartmentMeeting> temps = departmentMeetingService.findDepartmentMeeting(
								departmentMeeting.getWeek(), departmentMeeting.getYear(),
								departmentMeeting.getCodeDepart());
						if (temps.size() != 0) {
							FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Thông báo!",
									"Đã có biên bản họp tuần " + departmentMeeting.getWeek() + " năm "
											+ departmentMeeting.getYear());
							RequestContext.getCurrentInstance().showMessageInDialog(message);
						} else {
							DepartmentMeeting wf = departmentMeetingService.update(departmentMeeting);
							if (wf != null) {
								departmentMeeting = departmentMeetingService.findById(wf.getId());
								writeLogInfo("Cap nhat" + wf.toString());
								searchItem();
								notify.success();
							} else {
								notify.error();
							}
						}
					} else {
						DepartmentMeeting wf = departmentMeetingService.update(departmentMeeting);
						if (wf != null) {
							departmentMeeting = departmentMeetingService.findById(wf.getId());
							searchItem();
							writeLogInfo("Cap nhat" + wf.toString());
							notify.success();
						} else {
							notify.error();
						}
					}
				} else {
					notify.warningDetail();
				}
			}
		}

	}

	@Inject
	ApplicationBean applicationBean;
	boolean admin = false;
	private String codeEmp;

	public void reset() {
		LocalDate today = new LocalDate();
		departmentMeeting = new DepartmentMeeting();
		departmentMeeting.setWeek(today.getWeekOfWeekyear());
		departmentMeeting.setYear(today.getYear());

		yearS = today.getYear();

		try {
			codeEmp = applicationBean.getAccount().getMember().getCode();
			// if (applicationBean.getAccount().isAdmin()) {
			// departmentParents = departmentParentService.findAll();
			// admin = true;
			// } else if
			// (employee.equals(employee.getDepartment().getDepartmentParent().getEmployee()))
			// {
			// departmentParents = new ArrayList<DepartmentParent>();
			// departmentParents.add(employee.getDepartment().getDepartmentParent());
			// } else {
			// departmentParents = new ArrayList<DepartmentParent>();
			// departmentParents.add(employee.getDepartment().getDepartmentParent());
			// }
			// departmentMeeting.setDepartmentParent(employee.getDepartment().getDepartmentParent());
		} catch (Exception e) {

		}
		searchItem();
	}

	public void showEdit() {
		if (departmentMeetingEdit != null && departmentMeetingEdit.getId() != null) {
			DepartmentMeeting od = departmentMeetingService.findById(departmentMeetingEdit.getId());
			if (od != null) {
				departmentMeeting = od;
			}
		}
	}

	public void delete() {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (departmentMeeting != null && departmentMeeting.getId() != null) {
			departmentMeeting = departmentMeetingService.findById(departmentMeeting.getId());
			if (departmentMeeting.isIsfinal()) {
				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Thông báo!",
						"Biên bản họp này đã duyệt, không xóa được");
				RequestContext.getCurrentInstance().showMessageInDialog(message);
			} else {
				LocalDate lc = new LocalDate();
				Date date = lc.withWeekOfWeekyear(departmentMeeting.getWeek()).withYear(departmentMeeting.getYear())
						.dayOfWeek().withMinimumValue().toDate();
				if (allowDelete(date)) {
					boolean result = departmentMeetingService.delete(departmentMeeting);
					if (result) {
						departmentMeetings.remove(departmentMeeting);
						writeLogInfo("Xoa " + departmentMeeting.toString());
						reset();
						notify.success();
					} else {
						notify.warning("Không xoá được!");
					}
				} else {
					notify.warningDetail();
				}
			}
		} else {
			notify.warning("Chưa chọn phiếu!");
		}
	}

	public void searchItem() {
		try {
			if (codeDepartS != null) {
				departmentMeetings = departmentMeetingService.findDepartmentMeeting(yearS, null, false);
			} else {
				if (getAccount().isAdmin()) {
					departmentMeetings = departmentMeetingService.findDepartmentMeeting(yearS, null, false);
				} else {
					departmentMeetings = departmentMeetingService.findDepartmentMeeting(yearS, member.getDepartment()
							.getCode(), false);
				}
			}
			for (int i = 0; i < departmentMeetings.size(); i++) {
				try {
					departmentMeetings.get(i).setNameDepart(
							departmentServicePublic.findByCode("code", departmentMeetings.get(i).getCodeDepart())
									.getName());
				} catch (Exception e) {
				}
			}

		} catch (Exception e) {
		}
	}

	public List<DepartmentMeeting> getDepartmentMeetings() {
		return departmentMeetings;
	}

	public void setDepartmentMeetings(List<DepartmentMeeting> departmentMeetings) {
		this.departmentMeetings = departmentMeetings;
	}

	public DepartmentMeeting getDepartmentMeeting() {
		return departmentMeeting;
	}

	public void setDepartmentMeeting(DepartmentMeeting departmentMeeting) {
		this.departmentMeeting = departmentMeeting;
	}

	public DepartmentMeeting getDepartmentMeetingEdit() {
		return departmentMeetingEdit;
	}

	public void setDepartmentMeetingEdit(DepartmentMeeting departmentMeetingEdit) {
		this.departmentMeetingEdit = departmentMeetingEdit;
	}

	public List<String> getCodeDepart() {
		return codeDepart;
	}

	public void setCodeDepart(List<String> codeDepart) {
		this.codeDepart = codeDepart;
	}

	public String getCodeDepartS() {
		return codeDepartS;
	}

	public void setCodeDepartS(String codeDepartS) {
		this.codeDepartS = codeDepartS;
	}

	public int getYearS() {
		return yearS;
	}

	public void setYearS(int yearS) {
		this.yearS = yearS;
	}

	public List<DepartmentMeeting> getDepartmentMeetingFilters() {
		return departmentMeetingFilters;
	}

	public void setDepartmentMeetingFilters(List<DepartmentMeeting> departmentMeetingFilters) {
		this.departmentMeetingFilters = departmentMeetingFilters;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

}
