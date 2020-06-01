package trong.lixco.com.ejb.servicekpi;

import java.util.LinkedList;
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
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import trong.lixco.com.ejb.service.AbstractService;
import trong.lixco.com.jpa.entitykpi.KPIComp;
import trong.lixco.com.jpa.entitykpi.KPICompOfYear;
import trong.lixco.com.jpa.entitykpi.KPIPerson;
import trong.lixco.com.jpa.entitykpi.KPIPersonOfMonth;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class KPICompService extends AbstractService<KPIComp> {
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
	protected Class<KPIComp> getEntityClass() {
		return KPIComp.class;
	}
	public KPIComp findByIdAll(long id) {
		try {
			KPIComp wf = em.find(getEntityClass(), id);
			wf.getKpiCompOfYears().size();
			return wf;
		} catch (Exception e) {
			return null;
		}

	}
	
	public List<KPIComp> findKPIComp(int year) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<KPIComp> cq = cb.createQuery(KPIComp.class);
		List<Predicate> predicates = new LinkedList<Predicate>();
		Root<KPIComp> root = cq.from(KPIComp.class);
		predicates.add(cb.equal(root.get("year"),year));
		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<KPIComp> query = em.createQuery(cq);
		List<KPIComp> results = query.getResultList();
		return results;
	}
	public void removeKPICompOfyear(KPIComp kpiComp) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<KPICompOfYear> cq = cb.createQuery(KPICompOfYear.class);
		List<Predicate> predicates = new LinkedList<Predicate>();
		Root<KPICompOfYear> root = cq.from(KPICompOfYear.class);
		predicates.add(cb.equal(root.get("kpiComp"),kpiComp));
		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<KPICompOfYear> query = em.createQuery(cq);
		List<KPICompOfYear> results = query.getResultList();
		for (int i = 0; i < results.size(); i++) {
			em.remove(results.get(i));
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public KPIComp saveOrUpdate(KPIComp kpiComp, List<KPICompOfYear> kpiCompOfYears) {
		try {
			if (kpiComp.getId() == null) {
				List<KPICompOfYear> detailLixs = kpiComp.getKpiCompOfYears();
				kpiComp.setKpiCompOfYears(null);
				em.persist(kpiComp);
				kpiComp = em.merge(kpiComp);

				if (detailLixs != null) {
					for (int i = 0; i < detailLixs.size(); i++) {
						KPICompOfYear odd = detailLixs.get(i);
						odd.setWeightedParent(odd.getTaget().getkTagetCate().getWeight());
						if (odd.getId() == null) {
							odd.setKpiComp(kpiComp);
							em.persist(odd);
						} else {
							odd.setKpiComp(kpiComp);
							em.merge(odd);
						}
					}
				}
			} else {
				removeKPICompOfyear(kpiComp);
				List<KPICompOfYear> detailLixs = kpiComp.getKpiCompOfYears();
				kpiComp.setKpiCompOfYears(null);
				kpiComp = em.merge(kpiComp);
				if (detailLixs != null) {
					for (int i = 0; i < detailLixs.size(); i++) {
						KPICompOfYear odd = detailLixs.get(i);
						if (odd.getId() == null) {
							odd.setKpiComp(kpiComp);
							em.persist(odd);
						} else {
							odd.setKpiComp(kpiComp);
							em.merge(odd);
						}
					}
				}

			}
			if (kpiCompOfYears != null) {
				for (int i = 0; i < kpiCompOfYears.size(); i++) {
					KPICompOfYear odd = kpiCompOfYears.get(i);
					em.remove(em.contains(odd) ? odd : em.merge(odd));
				}
			}
			em.flush();
			return kpiComp;
		} catch (Exception e) {
			e.printStackTrace();
			ct.setRollbackOnly();
			return null;
		}

	}
	
}
