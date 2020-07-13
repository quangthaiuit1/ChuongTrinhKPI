package trong.lixco.com.ejb.servicekpi;

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
import trong.lixco.com.jpa.entitykpi.KPIDepMonth;
import trong.lixco.com.jpa.entitykpi.KPIDepOfMonth;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class KPIDepOfMonthService extends AbstractService<KPIDepOfMonth> {
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
	protected Class<KPIDepOfMonth> getEntityClass() {
		return KPIDepOfMonth.class;
	}
	public KPIDepMonth findKPIDepMonth(int month,int year,String codeDepart) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<KPIDepMonth> cq = cb.createQuery(KPIDepMonth.class);
		List<Predicate> predicates = new LinkedList<Predicate>();
		Root<KPIDepMonth> root = cq.from(KPIDepMonth.class);
		predicates.add(cb.equal(root.get("month"),month));
		predicates.add(cb.equal(root.get("year"),year));
		predicates.add(cb.equal(root.get("codeDepart"),codeDepart));
		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<KPIDepMonth> query = em.createQuery(cq);
		List<KPIDepMonth> results = query.getResultList();
		if(results.size()!=0){
			KPIDepMonth kpim=results.get(0);
			kpim.getKpiDepOfMonths().size();
			return kpim;
		}
		return null;
	}
	
}