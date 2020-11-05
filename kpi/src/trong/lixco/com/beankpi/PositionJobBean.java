package trong.lixco.com.beankpi;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import trong.lixco.com.ejb.servicekpi.EmpPJobService;
import trong.lixco.com.ejb.servicekpi.PositionJobService;
import trong.lixco.com.jpa.entitykpi.EmpPJob;
import trong.lixco.com.jpa.entitykpi.PositionJob;
import trong.lixco.com.servicepublic.EmpPJobServicePublic;
import trong.lixco.com.servicepublic.EmpPJobServicePublicProxy;
import trong.lixco.com.servicepublic.PositionJobServicePublic;
import trong.lixco.com.servicepublic.PositionJobServicePublicProxy;
import trong.lixco.com.util.Notify;

@ManagedBean
@javax.faces.bean.ViewScoped
public class PositionJobBean {
	private static final long serialVersionUID = 1L;
	@Inject
	private Logger logger;
	@Inject
	private PositionJobService positionJobService;
	@Inject
	private EmpPJobService empPJobService;
	private Notify notify;
	private PositionJob crudPositionJob;
	private PositionJob selectPositionJob;
	private PositionJob searchPositionJob;
	private List<PositionJob> listPositionJob;

	@PostConstruct
	protected void initItem() {
		try {
			searchPositionJob = new PositionJob();
			crudPositionJob = new PositionJob();
			listPositionJob = positionJobService.findAll();
		} catch (Exception e) {
			logger.error("PositionJobBean.initItem:" + e.getMessage(), e);
		}

	}

	public void forwardDes() {
		notify = new Notify(FacesContext.getCurrentInstance());
		try {
			if (crudPositionJob.getId() != null) {
				FacesContext.getCurrentInstance().getExternalContext()
						.redirect("/kpi/pages/nhansu/danhmuc/motacongviec.jsf?idpj=" + crudPositionJob.getId());
			} else {
				notify.warning("Chưa lưu hoặc chọn vị trí/chức vụ công việc.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void syncCenter() {
		try {
			PositionJobServicePublic positionJobServicePublic = new PositionJobServicePublicProxy();
			trong.lixco.com.servicepublic.PositionJobDTO[] positionJobs = positionJobServicePublic.findAll();
			for (int i = 0; i < positionJobs.length; i++) {
				PositionJob positionJob = positionJobService.findByCode(positionJobs[i].getCode());
				// xu ly de khong bi loi

				if (positionJob != null) {
					positionJob.setId(positionJobs[i].getId());
					// positionJob.setIduser(0);
					positionJob.setName(positionJobs[i].getName());
					if (positionJobs[i].getCodeDepart() != null)
						positionJob.setCodeDepart(positionJobs[i].getCodeDepart());
					positionJob.setDisable(positionJobs[i].isDisable());
					positionJobService.update(positionJob);
				} else {
					positionJob = new PositionJob();
					positionJob.setId(positionJobs[i].getId());
					positionJob.setCode(positionJobs[i].getCode());
					// xu ly de khong bi loi
					// positionJob.setIduser(0);
					positionJob.setName(positionJobs[i].getName());
					if (positionJobs[i].getCodeDepart() != null)
						positionJob.setCodeDepart(positionJobs[i].getCodeDepart());
					positionJob.setDisable(positionJobs[i].isDisable());
					positionJobService.create(positionJob);
				}
			}

			// Cai dat vi tri cong viec, nhan vien
			List<EmpPJob> deletes = empPJobService.findAll();
			for (int i = 0; i < deletes.size(); i++) {
				empPJobService.delete(deletes.get(i));
			}
			EmpPJobServicePublic empPJobServicePublic = new EmpPJobServicePublicProxy();
			trong.lixco.com.servicepublic.EmpPJobDTO[] empPJobs = empPJobServicePublic.findAll();
			for (int i = 0; i < empPJobs.length; i++) {
				EmpPJob ep = new EmpPJob();
				ep.setCodeEmp(empPJobs[i].getCodeEmp());
				ep.setCodePJob(empPJobs[i].getCodePJob());
				empPJobService.create(ep);
			}

			notify = new Notify(FacesContext.getCurrentInstance());
			notify.success();
		} catch (Exception e) {
		}
	}

	public void reset() {
		crudPositionJob = new PositionJob();
	}

	public void showEdit() {
		this.crudPositionJob = selectPositionJob;
	}

	public PositionJobService getPositionJobService() {
		return positionJobService;
	}

	public void setPositionJobService(PositionJobService positionJobService) {
		this.positionJobService = positionJobService;
	}

	public PositionJob getCrudPositionJob() {
		return crudPositionJob;
	}

	public void setCrudPositionJob(PositionJob crudPositionJob) {
		this.crudPositionJob = crudPositionJob;
	}

	public PositionJob getSelectPositionJob() {
		return selectPositionJob;
	}

	public void setSelectPositionJob(PositionJob selectPositionJob) {
		this.selectPositionJob = selectPositionJob;
	}

	public PositionJob getSearchPositionJob() {
		return searchPositionJob;
	}

	public void setSearchPositionJob(PositionJob searchPositionJob) {
		this.searchPositionJob = searchPositionJob;
	}

	public List<PositionJob> getListPositionJob() {
		return listPositionJob;
	}

	public void setListPositionJob(List<PositionJob> listPositionJob) {
		this.listPositionJob = listPositionJob;
	}

}
