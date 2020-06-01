package trong.lixco.com.beankpi;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;
import org.omnifaces.cdi.ViewScoped;

import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.ejb.servicekpi.TagetCateService;
import trong.lixco.com.jpa.entitykpi.TagetCate;
import trong.lixco.com.util.Notify;

@Named
@ViewScoped
public class TagetCateBean extends AbstractBean<TagetCate> {
	private static final long serialVersionUID = 1L;
	private Notify notify;
	private List<TagetCate> tagetCates;
	private TagetCate tagetCate;
	private TagetCate tagetCateEdit;

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
		tagetCate = new TagetCate();

	}
	public void createOrUpdate() {
		notify = new Notify(FacesContext.getCurrentInstance());
		try {
			if (tagetCate != null) {
				if (!"".equals(tagetCate.getCode()) && !"".equals(tagetCate.getContent())) {
					if (tagetCate.getId() == null) {
						if (allowSave(null)) {
							tagetCate=installSave(tagetCate);
							tagetCate = tagetCateService.create(tagetCate);
							tagetCates.add(0, tagetCate);
							writeLogInfo("Tạo mới " + tagetCate.toString());
							notify.success();
						} else {
							tagetCateEdit = new TagetCate();
							notify.warningDetail();
						}
					} else {
						if (allowUpdate(null)) {
							tagetCate=installUpdate(tagetCate);
							tagetCate = tagetCateService.update(tagetCate);
							int index = tagetCates.indexOf(tagetCate);
							tagetCates.set(index, tagetCate);
							writeLogInfo("Cập nhật " + tagetCate.toString());
							notify.success();
						} else {
							notify.warningDetail();
						}
					}
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
		tagetCate = new TagetCate();

	}

	public void showEdit() {
		this.tagetCate = tagetCateEdit;
	}

	public void delete() {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (tagetCate.getId() != null) {
			if (allowDelete(null)) {
				boolean status = tagetCateService.delete(tagetCate);
				if (status) {
					tagetCates.remove(tagetCate);
					writeLogInfo("Xoá " + tagetCate.toString());
					reset();
					notify.success();
				} else {
					writeLogError("Lỗi khi xoá " + tagetCate.toString());
					notify.error();
				}
			} else {
				notify.warningDetail();
			}
		} else {
			notify.warning("Chưa chọn trong danh sách!");
		}
	}


	public List<TagetCate> getTagetCates() {
		return tagetCates;
	}

	public void setTagetCates(List<TagetCate> TagetCates) {
		this.tagetCates = TagetCates;
	}

	public TagetCate getTagetCate() {
		return tagetCate;
	}

	public void setTagetCate(TagetCate TagetCate) {
		this.tagetCate = TagetCate;
	}

	public TagetCate getTagetCateEdit() {
		return tagetCateEdit;
	}

	public void setTagetCateEdit(TagetCate TagetCateEdit) {
		this.tagetCateEdit = TagetCateEdit;
	}

	
}
