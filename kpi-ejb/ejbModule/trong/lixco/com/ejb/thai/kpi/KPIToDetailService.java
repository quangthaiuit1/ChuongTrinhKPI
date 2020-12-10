package trong.lixco.com.ejb.thai.kpi;

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
import trong.lixco.com.jpa.thai.KPITo;
import trong.lixco.com.jpa.thai.KPIToDetail;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class KPIToDetailService extends AbstractService<KPIToDetail> {
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
	protected Class<KPIToDetail> getEntityClass() {
		return KPIToDetail.class;
	}

	public KPITo findKPIDepMonth(int month, int year, String codeDepart) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<KPITo> cq = cb.createQuery(KPITo.class);
		List<Predicate> predicates = new LinkedList<Predicate>();
		Root<KPITo> root = cq.from(KPITo.class);
		predicates.add(cb.equal(root.get("month"), month));
		predicates.add(cb.equal(root.get("year"), year));
		predicates.add(cb.equal(root.get("codeDepart"), codeDepart));
		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<KPITo> query = em.createQuery(cq);
		List<KPITo> results = query.getResultList();
		if (results.size() != 0) {
			KPITo kpim = results.get(0);
			kpim.getKpi_to_chitiet().size();
			return kpim;
		}
		return null;
	}
}
