package trong.lixco.com.ejb.servicekpi;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import trong.lixco.com.ejb.service.AbstractService;
import trong.lixco.com.jpa.entitykpi.DescribeJob;
import trong.lixco.com.jpa.entitykpi.MissionAnother;
import trong.lixco.com.jpa.entitykpi.MissionResponsibility;
import trong.lixco.com.jpa.entitykpi.OrverviewJob;
import trong.lixco.com.jpa.entitykpi.PositionJob;
import trong.lixco.com.jpa.entitykpi.RelaExternal;
import trong.lixco.com.jpa.entitykpi.RelaInternal;
import trong.lixco.com.jpa.entitykpi.RelaLocal;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class DescribeJobService extends AbstractService<DescribeJob> {
	@Inject
	private EntityManager em;
	@Resource
	private SessionContext ct;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	@Override
	protected SessionContext getUt() {
		return ct;
	}

	@Override
	protected Class<DescribeJob> getEntityClass() {
		return DescribeJob.class;
	}

	public DescribeJob findByPositionJob(PositionJob positionJob) {
		if (positionJob != null) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<DescribeJob> cq = cb.createQuery(DescribeJob.class);
			Root<DescribeJob> root = cq.from(DescribeJob.class);
			cq.select(root).where(cb.equal(root.get("positionJob"), positionJob));
			TypedQuery<DescribeJob> query = em.createQuery(cq);
			List<DescribeJob> results = query.getResultList();
			if (results.size() != 0) {
				DescribeJob describeJob = results.get(0);
				describeJob.getOrverviewJobs().size();
				describeJob.getMissionResponsibilities().size();
				describeJob.getMissionAnothers().size();
				describeJob.getRelaExternals().size();
				describeJob.getRelaInternals().size();
				return describeJob;
			}
			return new DescribeJob();
		}
		return new DescribeJob();
	}

	public DescribeJob findByIdAll(long id) {
		DescribeJob describeJob = em.find(DescribeJob.class, id);
		describeJob.getOrverviewJobs().size();
		describeJob.getMissionResponsibilities().size();
		describeJob.getMissionAnothers().size();
		describeJob.getRelaExternals().size();
		describeJob.getRelaInternals().size();
		return describeJob;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public DescribeJob createOrUpdate(DescribeJob describeJob) {
		try {
			if (describeJob.getId() != null) {
				delete(describeJob);
			}
			describeJob.setId(null);
			List<OrverviewJob> orverviewJobs = describeJob.getOrverviewJobs();
			describeJob.setOrverviewJobs(null);
			List<MissionResponsibility> missionResponsibilities = describeJob.getMissionResponsibilities();
			describeJob.setMissionResponsibilities(null);
			List<MissionAnother> missionAnothers = describeJob.getMissionAnothers();
			describeJob.setMissionAnothers(null);
			List<RelaExternal> relaExternals = describeJob.getRelaExternals();
			describeJob.setRelaExternals(null);
			List<RelaInternal> relaInternals = describeJob.getRelaInternals();
			describeJob.setRelaInternals(null);

			RelaLocal rl = describeJob.getRelaLocal();
			if (rl.getId() == null) {
				em.persist(rl);
			}
			describeJob.setRelaLocal(em.merge(rl));

			em.persist(describeJob);
			describeJob = em.merge(describeJob);
			if (orverviewJobs != null)
				for (int i = 0; i < orverviewJobs.size(); i++) {
					orverviewJobs.get(i).setDescribeJob(describeJob);
					orverviewJobs.get(i).setId(null);
					em.persist(orverviewJobs.get(i));
				}
			if (missionResponsibilities != null)
				for (int i = 0; i < missionResponsibilities.size(); i++) {
					missionResponsibilities.get(i).setDescribeJob(describeJob);
					missionResponsibilities.get(i).setId(null);
					em.persist(missionResponsibilities.get(i));
				}
			if (missionAnothers != null)
				for (int i = 0; i < missionAnothers.size(); i++) {
					missionAnothers.get(i).setDescribeJob(describeJob);
					missionAnothers.get(i).setId(null);
					em.persist(missionAnothers.get(i));
				}
			if (relaExternals != null)
				for (int i = 0; i < relaExternals.size(); i++) {
					relaExternals.get(i).setDescribeJob(describeJob);
					relaExternals.get(i).setId(null);
					em.persist(relaExternals.get(i));
				}
			if (relaInternals != null)
				for (int i = 0; i < relaInternals.size(); i++) {
					relaInternals.get(i).setDescribeJob(describeJob);
					relaInternals.get(i).setId(null);
					em.persist(relaInternals.get(i));
				}
			em.flush();
			return describeJob;
		} catch (Exception e) {
			e.printStackTrace();
			ct.setRollbackOnly();
			return null;
		}
	}

}
