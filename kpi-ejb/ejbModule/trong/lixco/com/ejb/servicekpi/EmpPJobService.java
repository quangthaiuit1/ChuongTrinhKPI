package trong.lixco.com.ejb.servicekpi;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.jboss.logging.Logger;

import trong.lixco.com.ejb.service.AbstractService;
import trong.lixco.com.jpa.entitykpi.EmpPJob;
import trong.lixco.com.jpa.entitykpi.PositionJob;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class EmpPJobService extends AbstractService<EmpPJob> {
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
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
	protected Class<EmpPJob> getEntityClass() {
		return EmpPJob.class;
	}

	public void saveOrUpdate(String codeEmp, List<PositionJob> positionJobs) {
		// List<EmpPJob> empPJobs = findByEmployee(codeEmp);
		// if (empPJobs != null)
		// for (int i = 0; i < empPJobs.size(); i++) {
		// delete(empPJobs.get(i));
		// }
		// if (positionJobs != null)
		// for (int i = 0; i < positionJobs.size(); i++) {
		// EmpPJob em = new EmpPJob(positionJobs.get(i));
		// em.setCodeEmp(codeEmp);
		// create(em);
		// }
	}

	public List<EmpPJob> findByEmployee(String codeEmp) {
		if (codeEmp != null) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EmpPJob> cq = cb.createQuery(EmpPJob.class);
			Root<EmpPJob> root = cq.from(EmpPJob.class);
			cq.select(root).where(cb.equal(root.get("codeEmp"), codeEmp));
			TypedQuery<EmpPJob> query = em.createQuery(cq);
			List<EmpPJob> results = query.getResultList();
			return results;
		}
		return new ArrayList<EmpPJob>();
	}

	public EmpPJob findByEmpPos(String codeEmp, String codePos) {
		if (codeEmp != null && codePos != null) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EmpPJob> cq = cb.createQuery(EmpPJob.class);
			Root<EmpPJob> root = cq.from(EmpPJob.class);
			cq.select(root).where(cb.equal(root.get("codeEmp"), codeEmp), cb.equal(root.get("codePJob"), codePos));
			TypedQuery<EmpPJob> query = em.createQuery(cq);
			List<EmpPJob> results = query.getResultList();
			if (results.size() != 0)
				return results.get(0);
			return null;
		}
		return null;
	}

	public List<EmpPJob> findByPositionJob(PositionJob positionJob) {
		if (positionJob != null) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EmpPJob> cq = cb.createQuery(EmpPJob.class);
			Root<EmpPJob> root = cq.from(EmpPJob.class);
			cq.select(root).where(cb.equal(root.get("positionJob"), positionJob));
			TypedQuery<EmpPJob> query = em.createQuery(cq);
			List<EmpPJob> results = query.getResultList();
			return results;
		}
		return new ArrayList<EmpPJob>();
	}

	public List<EmpPJob> findByPositionJob(List<String> positionJobs) {
		if (positionJobs != null && positionJobs.size() != 0) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EmpPJob> cq = cb.createQuery(EmpPJob.class);
			Root<EmpPJob> root = cq.from(EmpPJob.class);
			cq.select(root).where(cb.in(root.get("codePJob")).value(positionJobs))
					.orderBy(cb.asc(root.get("codePJob")));
			TypedQuery<EmpPJob> query = em.createQuery(cq);
			List<EmpPJob> results = query.getResultList();
			return results;
		}
		return new ArrayList<EmpPJob>();
	}
}
