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
import trong.lixco.com.jpa.thai.KPIPersonalOther;
import trong.lixco.com.jpa.thai.KPIPersonalOtherDetail;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class PersonalOtherDetailService extends AbstractService<KPIPersonalOtherDetail>{

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
	protected Class<KPIPersonalOtherDetail> getEntityClass() {
		return KPIPersonalOtherDetail.class;
	}
	public List<KPIPersonalOtherDetail> find(KPIPersonalOther kpiPersonalOther) {
		// primary
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<KPIPersonalOtherDetail> cq = cb.createQuery(KPIPersonalOtherDetail.class);
			Root<KPIPersonalOtherDetail> root = cq.from(KPIPersonalOtherDetail.class);
			List<Predicate> queries = new ArrayList<>();
			if (kpiPersonalOther != null) {
				Predicate nameDepartQuery = cb.equal(root.get("kpiPersonalOther"),kpiPersonalOther);
				queries.add(nameDepartQuery);
			}
			Predicate data[] = new Predicate[queries.size()];
			for (int i = 0; i < queries.size(); i++) {
				data[i] = queries.get(i);
			}
			Predicate finalPredicate = cb.and(data);
			cq.where(finalPredicate);
			TypedQuery<KPIPersonalOtherDetail> query = em.createQuery(cq);
			List<KPIPersonalOtherDetail> results = query.getResultList();
			
			return results;
	}
}