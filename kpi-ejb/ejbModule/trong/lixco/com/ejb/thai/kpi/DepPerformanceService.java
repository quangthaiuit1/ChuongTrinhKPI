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
import trong.lixco.com.jpa.thai.KPIDepPerformanceJPA;
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class DepPerformanceService extends AbstractService<KPIDepPerformanceJPA> {
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
	protected Class<KPIDepPerformanceJPA> getEntityClass() {
		return KPIDepPerformanceJPA.class;
	}
	
	public List<KPIDepPerformanceJPA> find(int year, String codeDepart) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<KPIDepPerformanceJPA> cq = cb.createQuery(KPIDepPerformanceJPA.class);
		Root<KPIDepPerformanceJPA> root = cq.from(KPIDepPerformanceJPA.class);
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
		TypedQuery<KPIDepPerformanceJPA> query = em.createQuery(cq);
		return query.getResultList();
	}
	
}
