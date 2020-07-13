package trong.lixco.com.beankpi;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import trong.lixco.com.ejb.servicekpi.FormulaKPIService;
import trong.lixco.com.jpa.entitykpi.FormulaKPI;
import trong.lixco.com.util.Notify;

@ManagedBean
@ViewScoped
// Dinh huong KPI ca nhan
public class FormulaKPIBean {
	private Notify notify;
	private List<FormulaKPI> formulaKPIs;
	private FormulaKPI formulaKPIEdit;
	private FormulaKPI formulaKPI;

	@Inject
	private FormulaKPIService formulaKPIService;

	@PostConstruct
	public void initItem() {
		formulaKPI = new FormulaKPI();
		formulaKPIs = new ArrayList<FormulaKPI>();
		searchItem();

	}

	public void createOrUpdateFormula() {
		notify = new Notify(FacesContext.getCurrentInstance());
		try {
			if (formulaKPI.getId() == null) {
				if (!formulaKPI.getCode().equals("")) {
					formulaKPI = formulaKPIService.create(formulaKPI);
					formulaKPIs.add(0, formulaKPI);
					notify.success();
					reset();
				}else{
					notify.warning("Điền đầy đủ thông tin!");
				}
			} else {
				FormulaKPI fold = formulaKPIService.findById(formulaKPI.getId());
				fold.setComment(formulaKPI.getComment());
				fold.setCode(formulaKPI.getCode());
				formulaKPI = formulaKPIService.update(fold);
				formulaKPIs.set(formulaKPIs.indexOf(formulaKPI), formulaKPI);
				notify.warning("Chỉ sửa được mô tả và ghi chú!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			notify.warning("Xảy ra lỗi không lưu được!");
		}
	}

	public void reset() {
		formulaKPI = new FormulaKPI();

	}

	public void showEdit() {
		this.formulaKPI = formulaKPIEdit;
	}

	public void delete() {
		notify = new Notify(FacesContext.getCurrentInstance());
		if (formulaKPI.getId() != null) {
			boolean status = formulaKPIService.delete(formulaKPI);
			if (status) {
				formulaKPIs.remove(formulaKPI);
				reset();
				notify.success();
			} else {
				notify.error();
			}
		} else {
			notify.warning("Chưa chọn trong danh sách!");
		}
	}

	public void searchItem() {
		formulaKPIs = formulaKPIService.findAll();
	}

	public List<FormulaKPI> getFormulaKPIs() {
		return formulaKPIs;
	}

	public void setFormulaKPIs(List<FormulaKPI> FormulaKPIs) {
		this.formulaKPIs = FormulaKPIs;
	}

	public FormulaKPI getFormulaKPIEdit() {
		return formulaKPIEdit;
	}

	public void setFormulaKPIEdit(FormulaKPI formulaKPIEdit) {
		this.formulaKPIEdit = formulaKPIEdit;
	}

	public FormulaKPI getFormulaKPI() {
		return formulaKPI;
	}

	public void setFormulaKPI(FormulaKPI formulaKPI) {
		this.formulaKPI = formulaKPI;
	}

}