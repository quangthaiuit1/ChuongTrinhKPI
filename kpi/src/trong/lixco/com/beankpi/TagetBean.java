package trong.lixco.com.beankpi;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;
import org.joda.time.DateTime;
import org.omnifaces.cdi.ViewScoped;

import trong.lixco.com.account.servicepublics.Department;
import trong.lixco.com.account.servicepublics.DepartmentServicePublic;
import trong.lixco.com.account.servicepublics.DepartmentServicePublicProxy;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.ejb.servicekpi.TagetCateService;
import trong.lixco.com.ejb.servicekpi.TagetService;
import trong.lixco.com.jpa.entitykpi.Taget;
import trong.lixco.com.jpa.entitykpi.TagetCate;
import trong.lixco.com.jpa.entitykpi.TagetCateWeight;
import trong.lixco.com.util.Notify;

@Named
@ViewScoped
public class TagetBean extends AbstractBean<Taget> {
	private static final long serialVersionUID = 1L;
	private Notify notify;
	private List<Taget> tagets;
	private Taget taget;
	private Taget tagetEdit;
	private TagetCate tagetCate;
	private int weight = 0;

	private int year;
	private TagetCate tagetCateS;

	private List<TagetCate> tagetCates;
	private List<Department> departments;

	private DepartmentServicePublic departmentServicePublic;

	@Inject
	private TagetService tagetService;
	@Inject
	private TagetCateService tagetCateService;
	@Inject
	private Logger logger;

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Override
	public void initItem() {
		taget = new Taget();
		year = new DateTime().getYear();
		departments = new ArrayList<Department>();
		try {
			departmentServicePublic = new DepartmentServicePublicProxy();
			Department[] deps = departmentServicePublic.findAll();
			for (int i = 0; i < deps.length; i++) {
				if (!deps[i].isDisable()&&deps[i].getLevelDep().getLevel()==2)
					departments.add(deps[i]);
			}
		} catch (Exception e) {
		}
		ajaxTaget();
		searchItem();
	}

	public void ajaxTaget() {
		tagetCates = tagetCateService.findAll();
		for (int i = 0; i < tagetCates.size(); i++) {
			TagetCateWeight tagetCateWeight = tagetCateService.findSearch(taget.getYear(), tagetCates.get(i));
			tagetCates.get(i).setWeight(tagetCateWeight.getWeigth());
		}
		try {
			tagetCate = tagetCates.get(0);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private Taget tagetSelect;

	public void installListDepP(Taget taget) {
		this.tagetSelect = taget;
		List<String> codeDepart = tagetService.findDeppByTaget(taget);
		for (int i = 0; i < departments.size(); i++) {
			departments.get(i).setSelect(false);
			for (int j = 0; j < codeDepart.size(); j++) {
				if (codeDepart.get(j).equals(departments.get(i).getCode())) {
					departments.get(i).setSelect(true);
					break;
				}
			}
		}
	}

	public void saveDepP() {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (allowUpdate(null) && allowSave(null)) {
			List<String> codeDeparts = new ArrayList<String>();
			for (int i = 0; i < departments.size(); i++) {
				if (departments.get(i).isSelect()) {
					codeDeparts.add(departments.get(i).getCode());
				}
			}
			tagetService.createRelaDeppTaget(tagetSelect, codeDeparts);
			notify.success();
		} else {
			notify.warningDetail();
		}
	}

	public void saveWeight() {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (allowUpdate(null) && allowSave(null)) {
			for (int i = 0; i < tagetCates.size(); i++) {
				TagetCateWeight tagetCateWeight = tagetCateService.findSearch(taget.getYear(), tagetCates.get(i));
				tagetCateWeight.setTagetCate(tagetCates.get(i));
				tagetCateWeight.setYear(taget.getYear());
				int a = tagetCates.get(i).getWeight();
				tagetCateWeight.setWeigth(tagetCates.get(i).getWeight());
				if (tagetCateWeight.getId() != null) {
					tagetCateService.update(tagetCateWeight);
				} else {
					tagetCateService.create(tagetCateWeight);
				}
			}
			notify.success();
		} else {
			notify.warningDetail();
		}

	}

	public void createOrUpdate() {
		notify = new Notify(FacesContext.getCurrentInstance());
		try {
			if (taget != null) {
				taget.setkTagetCate(tagetCate);
				taget.setWeightParent(weight);
				if (!"".equals(taget.getCode()) && !"".equals(taget.getYear() != 0)) {
					if (taget.getId() == null) {
						if (allowSave(null)) {
							taget = installSave(taget);
							taget = tagetService.create(taget);
							tagets.add(0, taget);
							writeLogInfo("Tạo mới " + taget.toString());
							notify.success();
						} else {
							tagetEdit = new Taget();
							notify.warningDetail();
						}
					} else {
						if (allowUpdate(null)) {
							taget = installUpdate(taget);
							taget = tagetService.update(taget);
							int index = tagets.indexOf(taget);
							tagets.set(index, taget);
							writeLogInfo("Cập nhật " + taget.toString());
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
		TagetCate tagetCate = taget.getkTagetCate();
		int year = taget.getYear();
		taget = new Taget();
		taget.setYear(year);
		taget.setkTagetCate(tagetCate);

	}

	public void showEdit() {
		this.taget = tagetEdit;
		tagetCate = this.taget.getkTagetCate();
		weight = (int) this.taget.getWeightParent();
	}

	public void delete() {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (taget.getId() != null) {
			if (allowDelete(null)) {
				boolean status = tagetService.delete(taget);
				if (status) {
					tagets.remove(taget);
					writeLogInfo("Xoá " + taget.toString());
					reset();
					notify.success();
				} else {
					writeLogError("Lỗi khi xoá " + taget.toString());
					notify.error();
				}
			} else {
				notify.warningDetail();
			}
		} else {
			notify.warning("Chưa chọn trong danh sách!");
		}
	}

	public void searchItem() {
		tagets = tagetService.findSearch(year, tagetCateS);
	}

	public List<Taget> getTagets() {
		return tagets;
	}

	public void setTagets(List<Taget> Tagets) {
		this.tagets = Tagets;
	}

	public Taget getTaget() {
		return taget;
	}

	public void setTaget(Taget Taget) {
		this.taget = Taget;
	}

	public Taget getTagetEdit() {
		return tagetEdit;
	}

	public void setTagetEdit(Taget TagetEdit) {
		this.tagetEdit = TagetEdit;
	}

	public List<TagetCate> getTagetCates() {
		return tagetCates;
	}

	public void setTagetCates(List<TagetCate> tagetCates) {
		this.tagetCates = tagetCates;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public TagetCate getTagetCateS() {
		return tagetCateS;
	}

	public void setTagetCateS(TagetCate tagetCateS) {
		this.tagetCateS = tagetCateS;
	}

	public TagetCate getTagetCate() {
		return tagetCate;
	}

	public void setTagetCate(TagetCate tagetCate) {
		this.tagetCate = tagetCate;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public List<Department> getDepartments() {
		return departments;
	}

	public void setDepartments(List<Department> departments) {
		this.departments = departments;
	}
}
