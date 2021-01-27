package trong.lixco.com.beankpi;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;
import org.joda.time.DateTime;
import org.omnifaces.cdi.ViewScoped;

import trong.lixco.com.account.servicepublics.DepartmentServicePublic;
import trong.lixco.com.account.servicepublics.DepartmentServicePublicProxy;
import trong.lixco.com.account.servicepublics.Member;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.classInfor.TagetDepartInfo;
import trong.lixco.com.ejb.servicekpi.TagetDepartCateService;
import trong.lixco.com.ejb.servicekpi.TagetDepartService;
import trong.lixco.com.ejb.servicekpi.TagetService;
import trong.lixco.com.jpa.entitykpi.Taget;
import trong.lixco.com.jpa.entitykpi.TagetDepart;
import trong.lixco.com.jpa.entitykpi.TagetDepartCate;
import trong.lixco.com.jpa.entitykpi.TagetDepartCateWeight;
import trong.lixco.com.kpi.general.ApplicationBean;
import trong.lixco.com.util.Notify;

@Named
@ViewScoped
public class TagetDepartBean extends AbstractBean<TagetDepart> {
	private static final long serialVersionUID = 1L;
	private Notify notify;
	private List<TagetDepart> tagetDeparts;
	private List<Taget> tagets;
	private TagetDepart tagetDepart;
	private TagetDepart tagetDepartEdit;
	private TagetDepartCate tagetDepartCate;
	private int weight = 0;

	private int year;
	private int yearTaget;
	private List<TagetDepartCate> tagetDepartCates;

	private String codeDepart;

	private Member member;
	private DepartmentServicePublic departmentServicePublic;

	@Inject
	private TagetDepartService tagetDepartService;
	@Inject
	private TagetDepartCateService tagetDepartCateService;
	@Inject
	private Logger logger;
	@Inject
	private TagetService tagetService;

	@Inject
	private ApplicationBean applicationBean;

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Override
	public void initItem() {
		tagetDepart = new TagetDepart();
		year = new DateTime().getYear();
		yearTaget = year;
		try {
			departmentServicePublic = new DepartmentServicePublicProxy();
		} catch (Exception e) {
			e.printStackTrace();
		}
		member = getAccount().getMember();
		if (member.getDepartment().getLevelDep().getLevel() == 3) {
			codeDepart = member.getDepartment().getDepartment().getCode();
		}
		if (member.getDepartment().getLevelDep().getLevel() == 4) {
			codeDepart = member.getDepartment().getDepartment().getDepartment().getCode();
		}
		if (member.getDepartment().getLevelDep().getLevel() == 2) {
			codeDepart = member.getDepartment().getCode();
		}

		ajaxTagetDepart();
		searchItem();
	}

	public void ajaxTagetDepart() {
		tagetDepartCates = tagetDepartCateService.findAll();
		for (int i = 0; i < tagetDepartCates.size(); i++) {
			TagetDepartCateWeight TagetDepartCateWeight = tagetDepartCateService.findSearch(tagetDepart.getYear(),
					tagetDepartCates.get(i), codeDepart);
			tagetDepartCates.get(i).setWeight(TagetDepartCateWeight.getWeigth());
		}
		try {
			tagetDepartCate = tagetDepartCates.get(0);
		} catch (Exception e) {
		}
	}

	public void loadTaget() {
		notify = new Notify(FacesContext.getCurrentInstance());
		tagets = tagetService.findSearch(yearTaget, null);
		for (int i = 0; i < tagets.size(); i++) {
			boolean a = tagetService.checkDeppAndTaget(codeDepart, tagets.get(i));
			tagets.get(i).setColor(a);
		}

	}

	public void getListTaget() {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (codeDepart != null) {
			for (int i = 0; i < tagets.size(); i++) {
				if (tagets.get(i).isSelect()) {
					TagetDepartCate tdc = tagetDepartCateService.findById(tagets.get(i).getkTagetCate().getId());
					TagetDepart td = new TagetDepart();
					td.setkTagetDepartCate(tdc);
					td.setWeighted(tagets.get(i).getWeighted());
					td.setCode(tagets.get(i).getCode());
					td.setYear(yearTaget);
					td.setContent(tagets.get(i).getContent());
					td.setCodeDepart(codeDepart);
					if (allowSave(null)) {
						td = installSave(td);
						td = tagetDepartService.create(td);
						tagetDeparts.add(0, td);
						writeLogInfo("Tạo mới " + td.toString());
					} else {
						notify.warningDetail();
					}
				}
			}
			searchItem();
			notify.success();
		} else {
			notify.warning("Tài khoản không thuộc phòng ban nào.");
		}
		tagetDeparts = tagetDeparts.stream().sorted((TagetDepart o1, TagetDepart o2) -> {
			try {
				int i = o1.getkTagetDepartCate().getCode().compareTo(o2.getkTagetDepartCate().getCode());
				if (i == 0)
					return o1.getIndex() > o2.getIndex() ? 1 : -1;
				return i;
			} catch (Exception e) {
				return -1;
			}
		}).collect(Collectors.toList());
		;

	}

	private TagetDepart tagetDepartSelect;

	public void installListDepP(TagetDepart tagetDepart) {
		// this.tagetDepartSelect = tagetDepart;
		// employees=employeeService.findByDepp(tagetDepart.getDepartmentParent());
		// List<Employee> tempdbs =
		// tagetDepartService.findEmpByTagetDepart(tagetDepart);
		// for (int i = 0; i < employees.size(); i++) {
		// for (int j = 0; j < tempdbs.size(); j++) {
		// if (tempdbs.get(j).equals(employees.get(i))) {
		// employees.get(i).setSelect(true);
		// break;
		// }
		// }
		// }
	}

	//
	public void saveDepP() {
		// notify = new Notify(FacesContext.getCurrentInstance());
		// if (allowUpdate(null) && allowSave(null)) {
		// List<Employee> temps = new ArrayList<Employee>();
		// for (int i = 0; i < employees.size(); i++) {
		// if (employees.get(i).isSelect()) {
		// temps.add(employees.get(i));
		// }
		// }
		// tagetDepartService.createRelaEmpTaget(tagetDepartSelect, temps);
		// notify.success();
		// } else {
		// notify.warningDetail();
		// }
	}

	public void saveWeight() {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (allowUpdate(null) && allowSave(null)) {
			for (int i = 0; i < tagetDepartCates.size(); i++) {
				TagetDepartCateWeight tagetDepartCateWeight = tagetDepartCateService.findSearch(tagetDepart.getYear(),
						tagetDepartCates.get(i), codeDepart);
				tagetDepartCateWeight.setCodeDepart(codeDepart);
				tagetDepartCateWeight.setTagetDepartCate(tagetDepartCates.get(i));
				tagetDepartCateWeight.setYear(tagetDepart.getYear());
				tagetDepartCateWeight.setWeigth(tagetDepartCates.get(i).getWeight());
				if (tagetDepartCateWeight.getId() != null) {
					tagetDepartCateService.update(tagetDepartCateWeight);
				} else {
					tagetDepartCateService.create(tagetDepartCateWeight);
				}
			}
			notify.success();
		} else {
			notify.warningDetail();
		}

	}

	public String nameDepart() {
		try {
			return departmentServicePublic.findByCode("code", codeDepart).getName();
		} catch (Exception e) {
			return "";
		}
	}

	public void createOrUpdate() {
		notify = new Notify(FacesContext.getCurrentInstance());
		try {
			if (tagetDepart != null) {
				tagetDepart.setkTagetDepartCate(tagetDepartCate);
				tagetDepart.setWeightParent(weight);
				if (!"".equals(tagetDepart.getCode()) && !"".equals(tagetDepart.getYear() != 0)) {
					if (tagetDepart.getId() == null) {
						if (allowSave(null)) {
							if (codeDepart != null) {
								tagetDepart = installSave(tagetDepart);
								tagetDepart.setCodeDepart(codeDepart);
								tagetDepart = tagetDepartService.create(tagetDepart);
								tagetDeparts.add(0, tagetDepart);
								writeLogInfo("Tạo mới " + tagetDepart.toString());
								notify.success();
							} else {
								notify.warning("Tài khoản không thuộc phòng ban nào.");
							}
						} else {
							tagetDepartEdit = new TagetDepart();
							notify.warningDetail();
						}
					} else {
						if (allowUpdate(null)) {
							tagetDepart = installUpdate(tagetDepart);
							tagetDepart = tagetDepartService.update(tagetDepart);
							int index = tagetDeparts.indexOf(tagetDepart);
							tagetDeparts.set(index, tagetDepart);
							writeLogInfo("Cập nhật " + tagetDepart.toString());
							notify.success();
						} else {
							notify.warningDetail();
						}
					}
					reset();
				} else {
					notify.warning("Điền đầy đủ thông tin!");
				}
			}
		} catch (Exception e) {
			writeLogError(e.getLocalizedMessage());
			notify.warning("Mã đã tồn tại!");
		}
	}

	public void reset() {
		TagetDepartCate TagetDepartCate = tagetDepart.getkTagetDepartCate();
		int year = tagetDepart.getYear();
		tagetDepart = new TagetDepart();
		tagetDepart.setYear(year);
		tagetDepart.setkTagetDepartCate(TagetDepartCate);

	}

	public void showEdit() {
		this.tagetDepart = tagetDepartEdit;
		tagetDepartCate = new TagetDepartCate();
		tagetDepartCate = this.tagetDepart.getkTagetDepartCate();
		weight = (int) this.tagetDepart.getWeightParent();
		codeDepart = this.tagetDepart.getCodeDepart();
		// ajaxTagetDepart();
	}

	public void delete() {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (tagetDepart.getId() != null) {
			if (allowDelete(null)) {
				boolean status = tagetDepartService.delete(tagetDepart);
				if (status) {
					tagetDeparts.remove(tagetDepart);
					writeLogInfo("Xoá " + tagetDepart.toString());
					reset();
					notify.success();
				} else {
					writeLogError("Lỗi khi xoá " + tagetDepart.toString());
					notify.error();
				}
			} else {
				notify.warningDetail();
			}
		} else {
			notify.warning("Chưa chọn trong danh sách!");
		}
	}

	List<TagetDepartInfo> tagetDepartInfos;

	public void searchItem() {
		if (getAccount().isAdmin())
			tagetDeparts = tagetDepartService.findSearch(year, null);
		else if (member.getDepartment().getLevelDep().getLevel() == 3) {
			tagetDeparts = tagetDepartService.findSearch(year, member.getDepartment().getDepartment().getCode());
		}
		if (member.getDepartment().getLevelDep().getLevel() == 2) {
			tagetDeparts = tagetDepartService.findSearch(year, member.getDepartment().getCode());
		}

		Map<String, List<TagetDepart>> datagroups1 = tagetDeparts.stream()
				.collect(Collectors.groupingBy(p -> p.getCodeDepart(), Collectors.toList()));

		tagetDepartInfos = new ArrayList<TagetDepartInfo>();
		for (String key : datagroups1.keySet()) {
			List<TagetDepart> invs = datagroups1.get(key);
			try {
				TagetDepartInfo tgi = new TagetDepartInfo();
				tgi.setNameDepart(departmentServicePublic.findByCode("code", key).getName());
				tgi.setTagetDeparts(invs);
				tagetDepartInfos.add(tgi);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

	}

	public List<TagetDepart> getTagetDeparts() {
		return tagetDeparts;
	}

	public void setTagetDeparts(List<TagetDepart> TagetDeparts) {
		this.tagetDeparts = TagetDeparts;
	}

	public TagetDepart getTagetDepart() {
		return tagetDepart;
	}

	public void setTagetDepart(TagetDepart TagetDepart) {
		this.tagetDepart = TagetDepart;
	}

	public TagetDepart getTagetDepartEdit() {
		return tagetDepartEdit;
	}

	public void setTagetDepartEdit(TagetDepart TagetDepartEdit) {
		this.tagetDepartEdit = TagetDepartEdit;
	}

	public List<TagetDepartCate> getTagetDepartCates() {
		return tagetDepartCates;
	}

	public void setTagetDepartCates(List<TagetDepartCate> TagetDepartCates) {
		this.tagetDepartCates = TagetDepartCates;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public TagetDepartCate getTagetDepartCate() {
		return tagetDepartCate;
	}

	public void setTagetDepartCate(TagetDepartCate TagetDepartCate) {
		this.tagetDepartCate = TagetDepartCate;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public List<Taget> getTagets() {
		return tagets;
	}

	public void setTagets(List<Taget> tagets) {
		this.tagets = tagets;
	}

	public int getYearTaget() {
		return yearTaget;
	}

	public void setYearTaget(int yearTaget) {
		this.yearTaget = yearTaget;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public List<TagetDepartInfo> getTagetDepartInfos() {
		return tagetDepartInfos;
	}

	public void setTagetDepartInfos(List<TagetDepartInfo> tagetDepartInfos) {
		this.tagetDepartInfos = tagetDepartInfos;
	}

	public String getCodeDepart() {
		return codeDepart;
	}

	public void setCodeDepart(String codeDepart) {
		this.codeDepart = codeDepart;
	}

}
