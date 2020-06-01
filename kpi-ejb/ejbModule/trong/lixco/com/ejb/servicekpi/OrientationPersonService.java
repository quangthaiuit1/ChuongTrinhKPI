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

import trong.lixco.com.ejb.service.AbstractService;
import trong.lixco.com.jpa.entitykpi.OrientationPerson;
import trong.lixco.com.jpa.entitykpi.PositionJob;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class OrientationPersonService extends AbstractService<OrientationPerson> {
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
	protected Class<OrientationPerson> getEntityClass() {
		return OrientationPerson.class;
	}

	public List<OrientationPerson> findSearch(String codePJob) {
		if (codePJob != null) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<OrientationPerson> cq = cb.createQuery(OrientationPerson.class);
			Root<OrientationPerson> root = cq.from(OrientationPerson.class);
			cq.select(root).where(cb.equal(root.get("codePJob"), codePJob)).orderBy(cb.asc(root.get("code")));
			TypedQuery<OrientationPerson> query = em.createQuery(cq);
			List<OrientationPerson> results = query.getResultList();
			return results;
		} else {
			return new ArrayList<OrientationPerson>();
		}
	}

}
