package trong.lixco.com.beankpi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;
import org.omnifaces.cdi.ViewScoped;

import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.ejb.servicekpi.DescribeJobService;
import trong.lixco.com.ejb.servicekpi.PositionJobService;
import trong.lixco.com.jpa.entitykpi.DescribeJob;
import trong.lixco.com.jpa.entitykpi.MissionAnother;
import trong.lixco.com.jpa.entitykpi.MissionResponsibility;
import trong.lixco.com.jpa.entitykpi.OrverviewJob;
import trong.lixco.com.jpa.entitykpi.PositionJob;
import trong.lixco.com.jpa.entitykpi.RelaExternal;
import trong.lixco.com.jpa.entitykpi.RelaInternal;
import trong.lixco.com.util.Notify;

@Named
@ViewScoped
public class DescribeJobBean extends AbstractBean<DescribeJob> {
	private static final long serialVersionUID = 1L;
	private Notify notify;
	private DescribeJob describeJob;
	private PositionJob positionJob;
	@Inject
	private Logger logger;
	@Inject
	private DescribeJobService describeJobService;
	@Inject
	private PositionJobService positionJobService;

	@Override
	protected void initItem() {

//		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
//		String parameterOne = params.get("idpj");
//		if (parameterOne != null) {
//			positionJob = positionJobService.findById(Long.parseLong(parameterOne));
//			describeJob = describeJobService.findByPositionJob(positionJob);
//			if(describeJob.getId()==null){
//				describeJob.setPositionJob(positionJob);
//			}
//			
//		}
	}

	public void addRow(String s) {
		if ("orverview".equals(s)) {
			OrverviewJob orverviewJob = new OrverviewJob();
			List<OrverviewJob> orverviewJobs = describeJob.getOrverviewJobs();
			if (orverviewJobs == null)
				orverviewJobs = new ArrayList<OrverviewJob>();
			orverviewJobs.add(orverviewJob);
			describeJob.setOrverviewJobs(orverviewJobs);
		} else if ("mission".equals(s)) {
			MissionResponsibility mission = new MissionResponsibility();
			List<MissionResponsibility> missions = describeJob.getMissionResponsibilities();
			if (missions == null)
				missions = new ArrayList<MissionResponsibility>();
			missions.add(mission);
			describeJob.setMissionResponsibilities(missions);
		} else if ("missionAno".equals(s)) {
			MissionAnother missionAno = new MissionAnother();
			List<MissionAnother> missionAnos = describeJob.getMissionAnothers();
			if (missionAnos == null)
				missionAnos = new ArrayList<MissionAnother>();
			missionAnos.add(missionAno);
			describeJob.setMissionAnothers(missionAnos);
		} else if ("relaIn".equals(s)) {
			RelaInternal reInternal = new RelaInternal();
			List<RelaInternal> relaInternals = describeJob.getRelaInternals();
			if (relaInternals == null)
				relaInternals = new ArrayList<RelaInternal>();
			relaInternals.add(reInternal);
			describeJob.setRelaInternals(relaInternals);
		} else if ("relaEx".equals(s)) {
			RelaExternal external = new RelaExternal();
			List<RelaExternal> externals = describeJob.getRelaExternals();
			if (externals == null)
				externals = new ArrayList<RelaExternal>();
			externals.add(external);
			describeJob.setRelaExternals(externals);
		}
	}

	public void createOrUpdate() {
//		notify = new Notify(FacesContext.getCurrentInstance());
//		if (positionJob == null) {
//			notify.error("Không có vị trí công việc.");
//		} else {
//			describeJob.setPositionJob(positionJob);
//			DescribeJob entity = describeJobService.createOrUpdate(describeJob);
//			if (entity != null) {
//				describeJob = describeJobService.findByIdAll(entity.getId());
//				notify.success();
//			} else {
//				notify.error();
//			}
//		}
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

	public DescribeJob getDescribeJob() {
		return describeJob;
	}

	public void setDescribeJob(DescribeJob describeJob) {
		this.describeJob = describeJob;
	}

}
