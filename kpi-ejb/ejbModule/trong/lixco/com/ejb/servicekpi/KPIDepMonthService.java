package trong.lixco.com.ejb.servicekpi;

import java.text.SimpleDateFormat;
import java.util.Date;
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
import trong.lixco.com.jpa.entitykpi.KPIDepMonth;
import trong.lixco.com.jpa.entitykpi.KPIDepOfMonth;
import trong.lixco.com.jpa.entitykpi.KPIPerson;
import trong.lixco.com.jpa.entitykpi.KPIPersonOfMonth;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class KPIDepMonthService extends AbstractService<KPIDepMonth> {
	@Inject
	private EntityManager em;
	@Resource
	private SessionContext ct;
	static SimpleDateFormat sf;
	static {
		sf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	@Override
	protected SessionContext getUt() {
		return ct;
	}

	@Override
	protected Class<KPIDepMonth> getEntityClass() {
		return KPIDepMonth.class;
	}

	public KPIDepMonth findByIdAll(long id) {
		try {
			KPIDepMonth wf = em.find(getEntityClass(), id);
			List<KPIDepOfMonth> KPIDepOfMonths = loadListOrderBy(wf);
			for (int i = 0; i < KPIDepOfMonths.size(); i++) {
				KPIDepOfMonths.get(i).setIndex(i);
			}
			wf.setKpiDepOfMonths(KPIDepOfMonths);
			return wf;
		} catch (Exception e) {
			return null;
		}

	}

	public List<KPIDepOfMonth> loadListOrderBy(KPIDepMonth kPIDepMonth) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<KPIDepOfMonth> cq = cb.createQuery(KPIDepOfMonth.class);
		List<Predicate> predicates = new LinkedList<Predicate>();
		Root<KPIDepOfMonth> root = cq.from(KPIDepOfMonth.class);
		predicates.add(cb.equal(root.get("kpiDepMonth"), kPIDepMonth));
		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0]))).orderBy(cb.asc(root.get("no")));
		TypedQuery<KPIDepOfMonth> query = em.createQuery(cq);
		List<KPIDepOfMonth> results = query.getResultList();
		return results;
	}

	public List<KPIDepMonth> findKPIDepMonth(int month, int year, String codeDepart) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<KPIDepMonth> cq = cb.createQuery(KPIDepMonth.class);
		List<Predicate> predicates = new LinkedList<Predicate>();
		Root<KPIDepMonth> root = cq.from(KPIDepMonth.class);
		predicates.add(cb.equal(root.get("month"), month));
		predicates.add(cb.equal(root.get("year"), year));
		if (codeDepart != null)
			predicates.add(cb.equal(root.get("codeDepart"), codeDepart));
		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<KPIDepMonth> query = em.createQuery(cq);
		List<KPIDepMonth> results = query.getResultList();
		return results;
	}
	public List<KPIDepMonth> findKPIDepYear(int year, String codeDepart) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<KPIDepMonth> cq = cb.createQuery(KPIDepMonth.class);
		List<Predicate> predicates = new LinkedList<Predicate>();
		Root<KPIDepMonth> root = cq.from(KPIDepMonth.class);
		predicates.add(cb.equal(root.get("year"), year));
		if (codeDepart != null)
			predicates.add(cb.equal(root.get("codeDepart"), codeDepart));
		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<KPIDepMonth> query = em.createQuery(cq);
		List<KPIDepMonth> results = query.getResultList();
		return results;
	}

	public List<KPIDepMonth> findKPIDepMonth(List<String> codeDeparts) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<KPIDepMonth> cq = cb.createQuery(KPIDepMonth.class);
		List<Predicate> predicates = new LinkedList<Predicate>();
		Root<KPIDepMonth> root = cq.from(KPIDepMonth.class);

		if (codeDeparts != null && codeDeparts.size() != 0) {
			Predicate pd = cb.in(root.get("codeDepart")).value(codeDeparts);
			predicates.add(pd);
		}
		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])))
				.orderBy(cb.desc(root.get("year")), cb.desc(root.get("month")));
		TypedQuery<KPIDepMonth> query = em.createQuery(cq);
		List<KPIDepMonth> results = query.getResultList();
		return results;
	}

	public void removeKPIDepOfyear(KPIDepMonth kpiDepMonth) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<KPIDepOfMonth> cq = cb.createQuery(KPIDepOfMonth.class);
		List<Predicate> predicates = new LinkedList<Predicate>();
		Root<KPIDepOfMonth> root = cq.from(KPIDepOfMonth.class);
		predicates.add(cb.equal(root.get("kpiDepMonth"), kpiDepMonth));
		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<KPIDepOfMonth> query = em.createQuery(cq);
		List<KPIDepOfMonth> results = query.getResultList();
		for (int i = 0; i < results.size(); i++) {
			em.remove(results.get(i));
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public KPIDepMonth saveOrUpdate(KPIDepMonth kpiDepMonth, List<KPIDepOfMonth> kpiDepOfMonths) {
		try {
			if (kpiDepMonth.getId() == null) {
				List<KPIDepOfMonth> detailLixs = kpiDepMonth.getKpiDepOfMonths();
				kpiDepMonth.setKpiDepOfMonths(null);
				em.persist(kpiDepMonth);
				kpiDepMonth = em.merge(kpiDepMonth);

				if (detailLixs != null) {
					for (int i = 0; i < detailLixs.size(); i++) {
						KPIDepOfMonth odd = detailLixs.get(i);
						if (odd.getId() == null) {
							odd.setKpiDepMonth(kpiDepMonth);
							em.persist(odd);
						} else {
							odd.setKpiDepMonth(kpiDepMonth);
							em.merge(odd);
						}
					}
				}
			} else {
				removeKPIDepOfyear(kpiDepMonth);
				List<KPIDepOfMonth> detailLixs = kpiDepMonth.getKpiDepOfMonths();
				kpiDepMonth.setKpiDepOfMonths(null);
				kpiDepMonth = em.merge(kpiDepMonth);
				if (detailLixs != null) {
					for (int i = 0; i < detailLixs.size(); i++) {
						KPIDepOfMonth odd = detailLixs.get(i);
						if (odd.getId() == null) {
							odd.setKpiDepMonth(kpiDepMonth);
							em.persist(odd);
						} else {
							odd.setKpiDepMonth(kpiDepMonth);
							em.merge(odd);
						}
					}
				}

			}
			if (kpiDepOfMonths != null) {
				for (int i = 0; i < kpiDepOfMonths.size(); i++) {
					KPIDepOfMonth odd = kpiDepOfMonths.get(i);
					em.remove(em.contains(odd) ? odd : em.merge(odd));
				}
			}
			em.flush();
			return kpiDepMonth;
		} catch (Exception e) {
			e.printStackTrace();
			ct.setRollbackOnly();
			return null;
		}

	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public KPIDepMonth updateAssign(KPIDepMonth kpiDepMonth) {
		try {
			List<KPIDepOfMonth> kpiDepOfMonths = kpiDepMonth.getKpiDepOfMonths();
			kpiDepMonth.setKpiDepOfMonths(null);
			KPIDepMonth kpi = em.find(KPIDepMonth.class, kpiDepMonth.getId());
			kpi.setResult(kpiDepMonth.getResult());

			if (kpiDepOfMonths != null) {
				for (int i = 0; i < kpiDepOfMonths.size(); i++) {
					KPIDepOfMonth odd = em.find(KPIDepOfMonth.class, kpiDepOfMonths.get(i).getId());
					if (odd != null) {
						odd.setPerformKPI(kpiDepOfMonths.get(i).getPerformKPI());
						odd.setRatioComplete(kpiDepOfMonths.get(i).getRatioComplete());
						odd.setRatioCompleteIsWeighted(kpiDepOfMonths.get(i).getRatioCompleteIsWeighted());
						odd.setNameAssign(kpiDepOfMonths.get(i).getNameAssign());
					}
				}
			}
			em.flush();
			return kpiDepMonth;
		} catch (Exception e) {
			e.printStackTrace();
			ct.setRollbackOnly();
			return null;
		}

	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public KPIDepMonth updateSign(KPIDepMonth kpiDepMonth) {
		try {
			KPIDepMonth kpi = em.find(KPIDepMonth.class, kpiDepMonth.getId());
			kpi.setSignKPI(kpiDepMonth.isSignKPI());
			if (kpiDepMonth.isSignKPI()) {
				kpi.setNoteSign(kpi.getNoteSign() + "Duyet dk: " + sf.format(new Date()) + ";");
			} else {
				kpi.setNoteSign(kpi.getNoteSign() + "Bo duyet dk: " + sf.format(new Date()) + ";");
			}
			return kpiDepMonth;
		} catch (Exception e) {
			e.printStackTrace();
			ct.setRollbackOnly();
			return null;
		}

	}
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public KPIDepMonth updateSignResult(KPIDepMonth kpiDepMonth) {
		try {
			KPIDepMonth kpi = em.find(KPIDepMonth.class, kpiDepMonth.getId());
			kpi.setSignResultKPI(kpiDepMonth.isSignResultKPI());
			if (kpiDepMonth.isSignResultKPI()) {
				kpi.setNoteSign(kpi.getNoteSign() + "Duyet kq: " + sf.format(new Date()) + ";");
			} else {
				kpi.setNoteSign(kpi.getNoteSign() + "Bo duyet kq: " + sf.format(new Date()) + ";");
			}
			return kpiDepMonth;
		} catch (Exception e) {
			e.printStackTrace();
			ct.setRollbackOnly();
			return null;
		}

	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public KPIDepMonth updateSignBLD(KPIDepMonth kpiDepMonth) {
		try {
			KPIDepMonth kpi = em.find(KPIDepMonth.class, kpiDepMonth.getId());
			kpi.setSignKPIBLD(kpiDepMonth.isSignKPIBLD());
			if (kpiDepMonth.isSignKPIBLD()) {
				kpi.setNoteSignBLD(kpi.getNoteSignBLD() + "Duyet dk: " + sf.format(new Date()) + ";");
			} else {
				kpi.setNoteSignBLD(kpi.getNoteSignBLD() + "Bo duyet dk: " + sf.format(new Date()) + ";");
			}
			return kpiDepMonth;
		} catch (Exception e) {
			e.printStackTrace();
			ct.setRollbackOnly();
			return null;
		}

	}
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public KPIDepMonth updateSignBLDResult(KPIDepMonth kpiDepMonth) {
		try {
			KPIDepMonth kpi = em.find(KPIDepMonth.class, kpiDepMonth.getId());
			kpi.setSignResultKPIBLD(kpiDepMonth.isSignResultKPIBLD());
			if (kpiDepMonth.isSignResultKPIBLD()) {
				kpi.setNoteSignBLD(kpi.getNoteSignBLD() + "Duyet kq: " + sf.format(new Date()) + ";");
			} else {
				kpi.setNoteSignBLD(kpi.getNoteSignBLD() + "Bo duyet kq: " + sf.format(new Date()) + ";");
			}
			return kpiDepMonth;
		} catch (Exception e) {
			e.printStackTrace();
			ct.setRollbackOnly();
			return null;
		}

	}

}
