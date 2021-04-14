package trong.lixco.com.beankpi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;
import org.omnifaces.cdi.ViewScoped;

import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.ejb.servicekpi.PositionJobService;
import trong.lixco.com.ejb.thai.kpi.PositionDontKPIService;
import trong.lixco.com.jpa.entitykpi.PositionJob;
import trong.lixco.com.jpa.thai.PositionDontKPI;
import trong.lixco.com.thai.bean.staticentity.MessageView;

@Named
@ViewScoped
public class SettingPosDontKPI extends AbstractBean {
	private static final long serialVersionUID = 1L;

	private List<PositionJob> allPosition;
	private List<PositionJob> filterAllPosition;
	// danh sach vi tri chuc danh khong lam kpi
	private List<PositionDontKPI> positionDontKPIs;

	@Inject
	private PositionJobService POSITION_JOB_SERVICE;

	@Inject
	private PositionDontKPIService POSITION_DONT_KPI_SERVICE;

	@Override
	protected void initItem() {
		allPosition = new ArrayList<>();
		allPosition = POSITION_JOB_SERVICE.findAll();
		// handle tich chon vi tri chuc danh khong lam kpi
		handleSelectPositionDontKPI();
	}

	public void handleSelectPositionDontKPI() {
		positionDontKPIs = POSITION_DONT_KPI_SERVICE.findAll();
		for (int i = 0; i < positionDontKPIs.size(); i++) {
			for (int j = 0; j < allPosition.size(); j++) {
				if (allPosition.get(j).getCode().equals(positionDontKPIs.get(i).getPosition_code())) {
					allPosition.get(j).setSelect(true);
					break;
				}
			}
		}
	}

	public void saveOrUpdate() {
		List<PositionDontKPI> positionIsTrue = new ArrayList<>();
		for (int i = 0; i < allPosition.size(); i++) {
			if (allPosition.get(i).isSelect()) {
				boolean isExist = false;
				for (int j = 0; j < positionDontKPIs.size(); j++) {
					if (allPosition.get(i).getCode().equals(positionDontKPIs.get(j).getPosition_code())) {
						isExist = true;
						positionIsTrue.add(positionDontKPIs.get(j));
						break;
					}
				}
				if (!isExist) {
					PositionDontKPI p = new PositionDontKPI(allPosition.get(i).getCode());
					POSITION_DONT_KPI_SERVICE.create(p);
					positionIsTrue.add(p);
				}
			}
		}

		// xoa nhung vi tri chuc danh da luu duoi db
		for (int i = 0; i < positionDontKPIs.size(); i++) {
			boolean isExist = false;
			for (int j = 0; j < positionIsTrue.size(); j++) {
				if (positionDontKPIs.get(i).getPosition_code().equals(positionIsTrue.get(j).getPosition_code())) {
					isExist = true;
					break;
				}
			}
			if (!isExist) {
				POSITION_DONT_KPI_SERVICE.delete(positionDontKPIs.get(i));
			}
		}
		// cap nhat lai giao dien
		handleSelectPositionDontKPI();
		MessageView.INFO("Thành công");
	}

	@Override
	protected Logger getLogger() {
		return null;
	}

	public List<PositionJob> getAllPosition() {
		return allPosition;
	}

	public void setAllPosition(List<PositionJob> allPosition) {
		this.allPosition = allPosition;
	}

	public List<PositionJob> getFilterAllPosition() {
		return filterAllPosition;
	}

	public void setFilterAllPosition(List<PositionJob> filterAllPosition) {
		this.filterAllPosition = filterAllPosition;
	}

}
