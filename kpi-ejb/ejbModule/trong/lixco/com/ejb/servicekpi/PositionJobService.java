package trong.lixco.com.ejb.servicekpi;

import java.util.ArrayList;
import java.util.LinkedList;
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
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jboss.logging.Logger;

import trong.lixco.com.ejb.service.AbstractService;
import trong.lixco.com.jpa.entitykpi.PositionJob;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class PositionJobService extends AbstractService<PositionJob> {
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
	protected Class<PositionJob> getEntityClass() {
		return PositionJob.class;
	}

	public List<PositionJob> findByDepartmentP(String codeDepart) {
		if (codeDepart != null) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PositionJob> cq = cb.createQuery(PositionJob.class);
			Root<PositionJob> root = cq.from(PositionJob.class);
			cq.select(root).where(cb.equal(root.get("codeDepart"), codeDepart), cb.equal(root.get("disable"), false));
			TypedQuery<PositionJob> query = em.createQuery(cq);
			List<PositionJob> results = query.getResultList();
			return results;
		}
		return new ArrayList<PositionJob>();
	}

	public PositionJob findByCode(String code) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PositionJob> cq = cb.createQuery(PositionJob.class);
		Root<PositionJob> root = cq.from(PositionJob.class);
		cq.select(root).where(cb.equal(root.get("code"), code));
		TypedQuery<PositionJob> query = em.createQuery(cq);
		List<PositionJob> results = query.getResultList();
		if (results.size() != 0)
			return results.get(0);
		return null;
	}

	public List<PositionJob> findByDepartmentP2(String codeDepart) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PositionJob> cq = cb.createQuery(PositionJob.class);
		List<Predicate> predicates = new LinkedList<Predicate>();
		Root<PositionJob> root = cq.from(PositionJob.class);
		if (codeDepart != null) {
			predicates.add(cb.equal(root.get("codeDepart"), codeDepart));
		}
		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])), cb.equal(root.get("disable"), false));
		TypedQuery<PositionJob> query = em.createQuery(cq);
		List<PositionJob> results = query.getResultList();
		return results;
	}
}
