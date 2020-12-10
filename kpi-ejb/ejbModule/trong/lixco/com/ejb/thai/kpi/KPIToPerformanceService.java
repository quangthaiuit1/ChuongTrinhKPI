package trong.lixco.com.ejb.thai.kpi;

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
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import trong.lixco.com.ejb.service.AbstractService;
import trong.lixco.com.jpa.thai.KPIToPerformance;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class KPIToPerformanceService extends AbstractService<KPIToPerformance> {
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
	protected Class<KPIToPerformance> getEntityClass() {
		return KPIToPerformance.class;
	}

	public List<KPIToPerformance> findSubDisable(int year, String codeDepart) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<KPIToPerformance> cq = cb.createQuery(KPIToPerformance.class);
		Root<KPIToPerformance> root = cq.from(KPIToPerformance.class);
		List<Predicate> queries = new ArrayList<>();
		if (year != 0) {
			Predicate answerTypeQuery = cb.equal(root.get("year"), year);
			queries.add(answerTypeQuery);
		}
		if (codeDepart != null) {
			Predicate departmentNameQuery = cb.equal(root.get("codeDepart"), codeDepart);
			queries.add(departmentNameQuery);
		}
		// loai nhung row disable
		Predicate disable = cb.equal(root.get("disable"), false);
		queries.add(disable);
		Predicate data[] = new Predicate[queries.size()];
		for (int i = 0; i < queries.size(); i++) {
			data[i] = queries.get(i);
		}
		Predicate finalPredicate = cb.and(data);
		cq.where(finalPredicate);
		TypedQuery<KPIToPerformance> query = em.createQuery(cq);
		return query.getResultList();
	}

	public List<KPIToPerformance> find(int year, String codeDepart) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<KPIToPerformance> cq = cb.createQuery(KPIToPerformance.class);
		Root<KPIToPerformance> root = cq.from(KPIToPerformance.class);
		List<Predicate> queries = new ArrayList<>();
		if (year != 0) {
			Predicate answerTypeQuery = cb.equal(root.get("year"), year);
			queries.add(answerTypeQuery);
		}
		if (codeDepart != null) {
			Predicate departmentNameQuery = cb.equal(root.get("codeDepart"), codeDepart);
			queries.add(departmentNameQuery);
		}
		Predicate data[] = new Predicate[queries.size()];
		for (int i = 0; i < queries.size(); i++) {
			data[i] = queries.get(i);
		}
		Predicate finalPredicate = cb.and(data);
		cq.where(finalPredicate);
		TypedQuery<KPIToPerformance> query = em.createQuery(cq);
		return query.getResultList();
	}
}
