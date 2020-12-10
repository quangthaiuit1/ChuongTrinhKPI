package trong.lixco.com.ejb.thai.kpi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import trong.lixco.com.jpa.thai.KPITo;
import trong.lixco.com.jpa.thai.KPIToDetail;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class KPIToService extends AbstractService<KPITo> {
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
	protected Class<KPITo> getEntityClass() {
		return KPITo.class;
	}

	public KPITo findByIdAll(long id) {
		try {
			KPITo wf = em.find(getEntityClass(), id);
			List<KPIToDetail> KPIDepOfMonths = loadListOrderBy(wf);
			for (int i = 0; i < KPIDepOfMonths.size(); i++) {
				KPIDepOfMonths.get(i).setIndex(i);
			}
			wf.setKpi_to_chitiet(KPIDepOfMonths);
			return wf;
		} catch (Exception e) {
			return null;
		}

	}

	public List<KPIToDetail> loadListOrderBy(KPITo kPIDepMonth) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<KPIToDetail> cq = cb.createQuery(KPIToDetail.class);
		List<Predicate> predicates = new LinkedList<Predicate>();
		Root<KPIToDetail> root = cq.from(KPIToDetail.class);
		predicates.add(cb.equal(root.get("kpi_to"), kPIDepMonth));
		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0]))).orderBy(cb.asc(root.get("no")));
		TypedQuery<KPIToDetail> query = em.createQuery(cq);
		List<KPIToDetail> results = new ArrayList<>();
		results = query.getResultList();
		return results;
	}

	public List<KPITo> findKPITo(int month, int year, String codeDepart) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<KPITo> cq = cb.createQuery(KPITo.class);
		List<Predicate> predicates = new LinkedList<Predicate>();
		Root<KPITo> root = cq.from(KPITo.class);
		predicates.add(cb.equal(root.get("month"), month));
		predicates.add(cb.equal(root.get("year"), year));
		if (codeDepart != null)
			predicates.add(cb.equal(root.get("codeDepart"), codeDepart));
		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<KPITo> query = em.createQuery(cq);
		List<KPITo> results = query.getResultList();
		return results;
	}

	// thai
	public List<KPITo> find(String codeDepart, int year) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<KPITo> cq = cb.createQuery(KPITo.class);
		Root<KPITo> root = cq.from(KPITo.class);
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
		TypedQuery<KPITo> query = em.createQuery(cq);
		return query.getResultList();
	}

	public List<KPITo> find(int month, int year) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<KPITo> cq = cb.createQuery(KPITo.class);
		Root<KPITo> root = cq.from(KPITo.class);
		List<Predicate> queries = new ArrayList<>();
		if (year != 0) {
			Predicate answerTypeQuery = cb.equal(root.get("year"), year);
			queries.add(answerTypeQuery);
		}
		if (month != 0) {
			Predicate departmentMonthQuery = cb.equal(root.get("month"), month);
			queries.add(departmentMonthQuery);
		}
		Predicate data[] = new Predicate[queries.size()];
		for (int i = 0; i < queries.size(); i++) {
			data[i] = queries.get(i);
		}
		Predicate finalPredicate = cb.and(data);
		cq.where(finalPredicate);
		TypedQuery<KPITo> query = em.createQuery(cq);
		return query.getResultList();
	}
	// end thai

	public List<KPITo> findKPIDepYear(int year, String codeDepart) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<KPITo> cq = cb.createQuery(KPITo.class);
		List<Predicate> predicates = new LinkedList<Predicate>();
		Root<KPITo> root = cq.from(KPITo.class);
		predicates.add(cb.equal(root.get("year"), year));
		if (codeDepart != null)
			predicates.add(cb.equal(root.get("codeDepart"), codeDepart));
		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<KPITo> query = em.createQuery(cq);
		List<KPITo> results = query.getResultList();
		return results;
	}

	public List<KPITo> findKPIDepMonth(List<String> codeDeparts) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<KPITo> cq = cb.createQuery(KPITo.class);
		List<Predicate> predicates = new LinkedList<Predicate>();
		Root<KPITo> root = cq.from(KPITo.class);

		if (codeDeparts != null && codeDeparts.size() != 0) {
			Predicate pd = cb.in(root.get("codeDepart")).value(codeDeparts);
			predicates.add(pd);
		}
		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0]))).orderBy(cb.desc(root.get("year")),
				cb.desc(root.get("month")));
		TypedQuery<KPITo> query = em.createQuery(cq);
		List<KPITo> results = query.getResultList();
		return results;
	}

	public void removeKPIDepOfyear(KPITo kpiDepMonth) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<KPIToDetail> cq = cb.createQuery(KPIToDetail.class);
		List<Predicate> predicates = new LinkedList<Predicate>();
		Root<KPIToDetail> root = cq.from(KPIToDetail.class);
		predicates.add(cb.equal(root.get("kpi_to"), kpiDepMonth));
		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<KPIToDetail> query = em.createQuery(cq);
		List<KPIToDetail> results = query.getResultList();
		for (int i = 0; i < results.size(); i++) {
			em.remove(results.get(i));
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public KPITo saveOrUpdate(KPITo kpiDepMonth, List<KPIToDetail> kpiDepOfMonths) {
		try {
			if (kpiDepMonth.getId() == null) {
				List<KPIToDetail> detailLixs = kpiDepMonth.getKpi_to_chitiet();
				kpiDepMonth.setKpi_to_chitiet(null);
				em.persist(kpiDepMonth);
				kpiDepMonth = em.merge(kpiDepMonth);

				if (detailLixs != null) {
					for (int i = 0; i < detailLixs.size(); i++) {
						KPIToDetail odd = detailLixs.get(i);
						if (odd.getId() == null) {
							odd.setKpi_to(kpiDepMonth);
							em.persist(odd);
						} else {
							odd.setKpi_to(kpiDepMonth);
							em.merge(odd);
						}
					}
				}
			} else {
				removeKPIDepOfyear(kpiDepMonth);
				List<KPIToDetail> detailLixs = kpiDepMonth.getKpi_to_chitiet();
				kpiDepMonth.setKpi_to_chitiet(null);
				kpiDepMonth = em.merge(kpiDepMonth);
				if (detailLixs != null) {
					for (int i = 0; i < detailLixs.size(); i++) {
						KPIToDetail odd = detailLixs.get(i);
						if (odd.getId() == null) {
							odd.setKpi_to(kpiDepMonth);
							em.persist(odd);
						} else {
							odd.setKpi_to(kpiDepMonth);
							em.merge(odd);
						}
					}
				}

			}
			if (kpiDepOfMonths != null) {
				for (int i = 0; i < kpiDepOfMonths.size(); i++) {
					KPIToDetail odd = kpiDepOfMonths.get(i);
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
	public KPITo updateAssign(KPITo kpiDepMonth) {
		try {
			List<KPIToDetail> kpiDepOfMonths = kpiDepMonth.getKpi_to_chitiet();
			kpiDepMonth.setKpi_to_chitiet(null);
			KPITo kpi = em.find(KPITo.class, kpiDepMonth.getId());
			kpi.setResult(kpiDepMonth.getResult());

			if (kpiDepOfMonths != null) {
				for (int i = 0; i < kpiDepOfMonths.size(); i++) {
					KPIToDetail odd = em.find(KPIToDetail.class, kpiDepOfMonths.get(i).getId());
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
	public KPITo updateSign(KPITo kpiDepMonth) {
		try {
			KPITo kpi = em.find(KPITo.class, kpiDepMonth.getId());
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
	public KPITo updateSignResult(KPITo kpiDepMonth) {
		try {
			KPITo kpi = em.find(KPITo.class, kpiDepMonth.getId());
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
	public KPITo updateSignBLD(KPITo kpiDepMonth) {
		try {
			KPITo kpi = em.find(KPITo.class, kpiDepMonth.getId());
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
	public KPITo updateSignBLDResult(KPITo kpiDepMonth) {
		try {
			KPITo kpi = em.find(KPITo.class, kpiDepMonth.getId());
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

	public List<KPITo> findKPIDepMonth(int month, int year, String codeDepart) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<KPITo> cq = cb.createQuery(KPITo.class);
		List<Predicate> predicates = new LinkedList<Predicate>();
		Root<KPITo> root = cq.from(KPITo.class);
		predicates.add(cb.equal(root.get("month"), month));
		predicates.add(cb.equal(root.get("year"), year));
		if (codeDepart != null)
			predicates.add(cb.equal(root.get("codeDepart"), codeDepart));
		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<KPITo> query = em.createQuery(cq);
		List<KPITo> results = query.getResultList();
		return results;
	}

	public List<KPITo> findKPIToByListDep(int month, int year, List<String> codeDeparts) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<KPITo> cq = cb.createQuery(KPITo.class);
			List<Predicate> predicates = new LinkedList<Predicate>();
			Root<KPITo> root = cq.from(KPITo.class);
			predicates.add(cb.equal(root.get("month"), month));
			predicates.add(cb.equal(root.get("year"), year));
			predicates.add(cb.in(root.get("codeDepart")).value(codeDeparts));
			cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<KPITo> query = em.createQuery(cq);
			List<KPITo> results = query.getResultList();
			return results;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
}
