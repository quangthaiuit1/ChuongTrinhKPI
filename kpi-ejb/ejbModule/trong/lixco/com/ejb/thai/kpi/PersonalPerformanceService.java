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
import trong.lixco.com.jpa.thai.KPIPersonalPerformance;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class PersonalPerformanceService extends AbstractService<KPIPersonalPerformance> {
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
	protected Class<KPIPersonalPerformance> getEntityClass() {
		return KPIPersonalPerformance.class;
	}

	public List<KPIPersonalPerformance> findSearch(String codePJob) {
		if (codePJob != null) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<KPIPersonalPerformance> cq = cb.createQuery(KPIPersonalPerformance.class);
			Root<KPIPersonalPerformance> root = cq.from(KPIPersonalPerformance.class);
			cq.select(root).where(cb.equal(root.get("codePJob"), codePJob)).orderBy(cb.asc(root.get("code")));
			TypedQuery<KPIPersonalPerformance> query = em.createQuery(cq);
			List<KPIPersonalPerformance> results = query.getResultList();
			return results;
		} else {
			return new ArrayList<KPIPersonalPerformance>();
		}
	}

	public List<KPIPersonalPerformance> find(List<String> codeJobs) {
		// primary
		if (codeJobs != null && !codeJobs.isEmpty()) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<KPIPersonalPerformance> cq = cb.createQuery(KPIPersonalPerformance.class);
			Root<KPIPersonalPerformance> root = cq.from(KPIPersonalPerformance.class);
			List<Predicate> queries = new ArrayList<>();
			if (codeJobs != null) {
				Predicate codePJobQuery = cb.in(root.get("codePJob")).value(codeJobs);
				queries.add(codePJobQuery);
			}
			Predicate disable = cb.equal(root.get("disable"), false);
			queries.add(disable);

			Predicate data[] = new Predicate[queries.size()];
			for (int i = 0; i < queries.size(); i++) {
				data[i] = queries.get(i);
			}
			Predicate finalPredicate = cb.and(data);
			cq.where(finalPredicate);
			TypedQuery<KPIPersonalPerformance> query = em.createQuery(cq);
			List<KPIPersonalPerformance> results = query.getResultList();
			return results;
		} else {
			return new ArrayList<KPIPersonalPerformance>();
		}
	}

	public List<KPIPersonalPerformance> findAllCustom() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<KPIPersonalPerformance> cq = cb.createQuery(KPIPersonalPerformance.class);
		Root<KPIPersonalPerformance> root = cq.from(KPIPersonalPerformance.class);
		cq.select(root);
		TypedQuery<KPIPersonalPerformance> query = em.createQuery(cq);
		List<KPIPersonalPerformance> results = query.getResultList();
		return results;
	}

}
