package trong.lixco.com.beankpi;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;
import org.omnifaces.cdi.ViewScoped;

import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.ejb.servicekpi.BehaviourPersonService;
import trong.lixco.com.jpa.entitykpi.BehaviourPerson;
import trong.lixco.com.util.Notify;

@Named
@ViewScoped
// Dinh huong KPI ca nhan
public class BehaviourPersonBean extends AbstractBean<BehaviourPerson> {
	private static final long serialVersionUID = 1L;
	private Notify notify;
	private List<BehaviourPerson> behaviourPersons;
	private BehaviourPerson behaviourPerson;
	private BehaviourPerson behaviourPersonEdit;


	@Inject
	private BehaviourPersonService behaviourPersonService;
	@Inject
	private Logger logger;

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Override
	public void initItem() {
		behaviourPerson = new BehaviourPerson();
		behaviourPersons = new ArrayList<BehaviourPerson>();
		searchItem();
	}
	public void createOrUpdate() {
		notify = new Notify(FacesContext.getCurrentInstance());
		try {
			if (behaviourPerson != null) {
				if (!"".equals(behaviourPerson.getContent())) {
					if (behaviourPerson.getId() == null) {
						if (allowSave(null)) {
							behaviourPerson = installSave(behaviourPerson);
							behaviourPerson = behaviourPersonService.create(behaviourPerson);
							behaviourPersons.add(0, behaviourPerson);
							writeLogInfo("Tạo mới " + behaviourPerson.toString());
							notify.success();
							reset();
						} else {
							behaviourPersonEdit = new BehaviourPerson();
							notify.warningDetail();
						}
					} else {
						if (allowUpdate(null)) {
							behaviourPerson = installUpdate(behaviourPerson);
							behaviourPerson = behaviourPersonService.update(behaviourPerson);
							int index = behaviourPersons.indexOf(behaviourPerson);
							behaviourPersons.set(index, behaviourPerson);
							writeLogInfo("Cập nhật " + behaviourPerson.toString());
							notify.success();
						} else {
							notify.warningDetail();
						}
					}
					searchItem();
				} else {
					notify.warning("Điền đầy đủ thông tin!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			writeLogError(e.getLocalizedMessage());
			notify.warning("Xảy ra lỗi không lưu được!");
		}
	}

	public void reset() {
		behaviourPerson = new BehaviourPerson();
	}

	public void showEdit() {
		this.behaviourPerson = behaviourPersonEdit;
	}

	public void delete() {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (behaviourPerson.getId() != null) {
			if (allowDelete(null)) {
				boolean status = behaviourPersonService.delete(behaviourPerson);
				if (status) {
					behaviourPersons.remove(behaviourPerson);
					writeLogInfo("Xoá " + behaviourPerson.toString());
					reset();
					notify.success();
				} else {
					writeLogError("Lỗi khi xoá " + behaviourPerson.toString());
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
		behaviourPersons = behaviourPersonService.findAll();
	}

	public List<BehaviourPerson> getBehaviourPersons() {
		return behaviourPersons;
	}

	public void setBehaviourPersons(List<BehaviourPerson> behaviourPersons) {
		this.behaviourPersons = behaviourPersons;
	}

	public BehaviourPerson getBehaviourPerson() {
		return behaviourPerson;
	}

	public void setBehaviourPerson(BehaviourPerson behaviourPerson) {
		this.behaviourPerson = behaviourPerson;
	}

	public BehaviourPerson getBehaviourPersonEdit() {
		return behaviourPersonEdit;
	}

	public void setBehaviourPersonEdit(BehaviourPerson behaviourPersonEdit) {
		this.behaviourPersonEdit = behaviourPersonEdit;
	}

}
