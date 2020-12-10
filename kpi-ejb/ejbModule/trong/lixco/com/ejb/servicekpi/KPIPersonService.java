package trong.lixco.com.ejb.servicekpi;

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
import trong.lixco.com.jpa.entitykpi.KPIPerson;
import trong.lixco.com.jpa.entitykpi.KPIPersonOfMonth;
import trong.lixco.com.jpa.entitykpi.PositionJob;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class KPIPersonService extends AbstractService<KPIPerson> {
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
	protected Class<KPIPerson> getEntityClass() {
		return KPIPerson.class;
	}

	public KPIPerson findByIdAll(long id) {
		try {
			KPIPerson wf = em.find(getEntityClass(), id);
			wf.setKpiPersonOfMonths(loadListDetailOrderBy(wf));
			;
			return wf;
		} catch (Exception e) {
			return null;
		}

	}

	public List<KPIPersonOfMonth> findListKPIPerson(List<KPIPerson> kpiPersons) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<KPIPersonOfMonth> cq = cb.createQuery(KPIPersonOfMonth.class);
		List<Predicate> predicates = new LinkedList<Predicate>();
		Root<KPIPersonOfMonth> root = cq.from(KPIPersonOfMonth.class);
		predicates.add(cb.in(root.get("kpiPerson")).value(kpiPersons));
		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<KPIPersonOfMonth> query = em.createQuery(cq);
		List<KPIPersonOfMonth> results = query.getResultList();
		return results;
	}

	public List<KPIPersonOfMonth> loadListDetailOrderBy(KPIPerson kpiPerson) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<KPIPersonOfMonth> cq = cb.createQuery(KPIPersonOfMonth.class);
			List<Predicate> predicates = new LinkedList<Predicate>();
			Root<KPIPersonOfMonth> root = cq.from(KPIPersonOfMonth.class);
			predicates.add(cb.equal(root.get("kpiPerson"), kpiPerson));
			cq.select(root).where(cb.and(predicates.toArray(new Predicate[0]))).orderBy(cb.asc(root.get("no")));
			TypedQuery<KPIPersonOfMonth> query = em.createQuery(cq);
			List<KPIPersonOfMonth> results = query.getResultList();
			return results;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public List<KPIPersonOfMonth> findKPIPerson(KPIPerson kpiPerson) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<KPIPersonOfMonth> cq = cb.createQuery(KPIPersonOfMonth.class);
			List<Predicate> predicates = new LinkedList<Predicate>();
			Root<KPIPersonOfMonth> root = cq.from(KPIPersonOfMonth.class);
			predicates.add(cb.equal(root.get("kpiPerson"), kpiPerson));
			cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<KPIPersonOfMonth> query = em.createQuery(cq);
			List<KPIPersonOfMonth> results = query.getResultList();
			return results;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public List<KPIPersonOfMonth> findKPIPersonBehavior(KPIPerson kpiPerson) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<KPIPersonOfMonth> cq = cb.createQuery(KPIPersonOfMonth.class);
			List<Predicate> predicates = new LinkedList<Predicate>();
			Root<KPIPersonOfMonth> root = cq.from(KPIPersonOfMonth.class);
			predicates.add(cb.equal(root.get("kpiPerson"), kpiPerson));
			cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])), cb.equal(root.get("behaviour"), true));
			TypedQuery<KPIPersonOfMonth> query = em.createQuery(cq);
			List<KPIPersonOfMonth> results = query.getResultList();
			return results;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public List<KPIPersonOfMonth> findOrientIsNull(KPIPerson kpiPerson) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<KPIPersonOfMonth> cq = cb.createQuery(KPIPersonOfMonth.class);
			List<Predicate> predicates = new LinkedList<Predicate>();
			Root<KPIPersonOfMonth> root = cq.from(KPIPersonOfMonth.class);
			predicates.add(cb.isNull(root.get("orientationPerson")));
			predicates.add(cb.equal(root.get("kpiPerson"), kpiPerson));
			cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<KPIPersonOfMonth> query = em.createQuery(cq);
			List<KPIPersonOfMonth> results = query.getResultList();
			return results;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public KPIPerson saveOrUpdate(KPIPerson kpiPerson, List<KPIPersonOfMonth> kpiPersonOfMonths) {
		try {
			if (kpiPerson.getId() == null) {
				List<KPIPersonOfMonth> detailLixs = kpiPerson.getKpiPersonOfMonths();
				kpiPerson.setKpiPersonOfMonths(null);
				em.persist(kpiPerson);
				kpiPerson = em.merge(kpiPerson);

				if (detailLixs != null) {
					for (int i = 0; i < detailLixs.size(); i++) {
						KPIPersonOfMonth odd = detailLixs.get(i);
						if (odd.getId() == null) {
							odd.setKpiPerson(kpiPerson);
							em.persist(odd);
						} else {
							odd.setKpiPerson(kpiPerson);
							em.merge(odd);
						}
					}
				}
			} else {
				List<KPIPersonOfMonth> detailLixAdds = findKPIPerson(kpiPerson);
				if (detailLixAdds != null) {
					for (int i = 0; i < detailLixAdds.size(); i++) {
						KPIPersonOfMonth odd = detailLixAdds.get(i);
						em.remove(odd);
					}
				}

				List<KPIPersonOfMonth> detailLixs = kpiPerson.getKpiPersonOfMonths();
				kpiPerson.setKpiPersonOfMonths(null);
				kpiPerson = em.merge(kpiPerson);

				if (detailLixs != null) {
					for (int i = 0; i < detailLixs.size(); i++) {
						KPIPersonOfMonth odd = detailLixs.get(i);
						if (odd.getId() == null) {
							odd.setKpiPerson(kpiPerson);
							em.persist(odd);
						} else {
							odd.setKpiPerson(kpiPerson);
							em.merge(odd);
						}
					}
				}

			}
			if (kpiPersonOfMonths != null) {
				for (int i = 0; i < kpiPersonOfMonths.size(); i++) {
					KPIPersonOfMonth odd = kpiPersonOfMonths.get(i);
					em.remove(em.contains(odd) ? odd : em.merge(odd));
				}
			}
			em.flush();
			return kpiPerson;
		} catch (Exception e) {
			e.printStackTrace();
			ct.setRollbackOnly();
			return null;
		}

	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public KPIPerson updateAssign(KPIPerson kpiPerson) {
		try {
			// Xoa cac phiếu hành vi - trước đạo đức
			List<KPIPersonOfMonth> detailLixAdds = findKPIPersonBehavior(kpiPerson);
			if (detailLixAdds != null) {
				for (int i = 0; i < detailLixAdds.size(); i++) {
					KPIPersonOfMonth odd = detailLixAdds.get(i);
					em.remove(odd);
				}
			}

			List<KPIPersonOfMonth> detailLixs = kpiPerson.getKpiPersonOfMonths();
			kpiPerson.setKpiPersonOfMonths(null);
			KPIPerson kpi = em.find(KPIPerson.class, kpiPerson.getId());
			kpi.setTotal(kpiPerson.getTotal());
			kpi.setTotalCV(kpiPerson.getTotalCV());
			kpi.setTotalHV(kpiPerson.getTotalHV());
			kpi.setDateAssignResult(kpiPerson.getDateAssignResult());

			// cai dat behavior

			if (detailLixs != null) {
				for (int i = 0; i < detailLixs.size(); i++) {
					if (detailLixs.get(i).isBehaviour()) {
						detailLixs.get(i).setId(null);
						em.persist(detailLixs.get(i));
					} else {
						KPIPersonOfMonth odd = em.find(KPIPersonOfMonth.class, detailLixs.get(i).getId());
						if (odd != null) {
							odd.setUnit(detailLixs.get(i).getUnit());
							odd.setParamA(detailLixs.get(i).getParamA());
							odd.setParamB(detailLixs.get(i).getParamB());
							odd.setPerformKPI(detailLixs.get(i).getPerformKPI());
							odd.setRatioComplete(detailLixs.get(i).getRatioComplete());
							odd.setRatioCompleteIsWeighted(detailLixs.get(i).getRatioCompleteIsWeighted());
							odd.setNameAssign(detailLixs.get(i).getNameAssign());
						}
					}
				}
			}
			em.flush();
			return kpiPerson;
		} catch (Exception e) {
			e.printStackTrace();
			ct.setRollbackOnly();
			return null;
		}

	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public KPIPerson updateSign(KPIPerson kpiPerson) {
		try {
			KPIPerson kpi = em.find(KPIPerson.class, kpiPerson.getId());
			kpi.setSignKPI(kpiPerson.isSignKPI());
			if (kpiPerson.isSignKPI()) {
				kpi.setNoteSign(kpi.getNoteSign() + "Duyet dk: " + sf.format(new Date()) + ";");
			} else {
				kpi.setNoteSign(kpi.getNoteSign() + "Bo duyet dk: " + sf.format(new Date()) + ";");
			}

			return kpiPerson;
		} catch (Exception e) {
			e.printStackTrace();
			ct.setRollbackOnly();
			return null;
		}

	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public KPIPerson updateSignResult(KPIPerson kpiPerson) {
		try {
			KPIPerson kpi = em.find(KPIPerson.class, kpiPerson.getId());
			kpi.setSignResultKPI(kpiPerson.isSignResultKPI());
			if (kpiPerson.isSignResultKPI()) {
				kpi.setNoteSign(kpi.getNoteSign() + "Duyet kq: " + sf.format(new Date()) + "/");
			} else {
				kpi.setNoteSign(kpi.getNoteSign() + "Bo duyet kq:  " + sf.format(new Date()) + "/");
			}
			return kpiPerson;
		} catch (Exception e) {
			e.printStackTrace();
			ct.setRollbackOnly();
			return null;
		}

	}

	public List<KPIPerson> findRange(String codeEmp, int year) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<KPIPerson> cq = cb.createQuery(KPIPerson.class);
			List<Predicate> predicates = new LinkedList<Predicate>();
			Root<KPIPerson> root = cq.from(KPIPerson.class);
			if (codeEmp != null) {
				predicates.add(cb.equal(root.get("codeEmp"), codeEmp));
			}
			if (year != 0) {
				predicates.add(cb.equal(root.get("kyear"), year));
			}
			cq.select(root).where(cb.and(predicates.toArray(new Predicate[0]))).orderBy(cb.desc(root.get("kyear")),
					cb.desc(root.get("kmonth")));
			List<KPIPerson> result = em.createQuery(cq).getResultList();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public List<KPIPerson> findRange(String codeEmp, int month, int year) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<KPIPerson> cq = cb.createQuery(KPIPerson.class);
			List<Predicate> predicates = new LinkedList<Predicate>();
			Root<KPIPerson> root = cq.from(KPIPerson.class);
			if (codeEmp != null) {
				predicates.add(cb.equal(root.get("codeEmp"), codeEmp));
			}
			if (month != 0) {
				predicates.add(cb.equal(root.get("kmonth"), month));
			}
			if (year != 0) {
				predicates.add(cb.equal(root.get("kyear"), year));
			}
			cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
			List<KPIPerson> result = em.createQuery(cq).getResultList();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public KPIPerson findRangeNew(String codeEmp, int month, int year) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<KPIPerson> cq = cb.createQuery(KPIPerson.class);
			List<Predicate> predicates = new LinkedList<Predicate>();
			Root<KPIPerson> root = cq.from(KPIPerson.class);
			if (codeEmp != null) {
				predicates.add(cb.equal(root.get("codeEmp"), codeEmp));
			}
			if (month != 0) {
				predicates.add(cb.equal(root.get("kmonth"), month));
			}
			if (year != 0) {
				predicates.add(cb.equal(root.get("kyear"), year));
			}
			cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
			List<KPIPerson> result = em.createQuery(cq).getResultList();
			if (!result.isEmpty()) {
				result.get(0).getKpiPersonOfMonths().size();
				return result.get(0);
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new KPIPerson();
		}
	}

	public List<KPIPerson> findRange(String codeEmp, int month, int year, List<String> codeEmps) {
		if (codeEmps != null && codeEmps.size() != 0) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<KPIPerson> cq = cb.createQuery(KPIPerson.class);
			List<Predicate> predicates = new LinkedList<Predicate>();
			Root<KPIPerson> root = cq.from(KPIPerson.class);
			if (codeEmp != null) {
				predicates.add(cb.equal(root.get("codeEmp"), codeEmp));
			}

			predicates.add(cb.in(root.get("codeEmp")).value(codeEmps));

			if (month != 0) {
				predicates.add(cb.equal(root.get("kmonth"), month));
			}
			if (year != 0) {
				predicates.add(cb.equal(root.get("kyear"), year));
			}
			cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
			List<KPIPerson> result = em.createQuery(cq).getResultList();
			return result;
		} else {
			return new ArrayList<KPIPerson>();
		}
	}

	public List<KPIPerson> findRange(int month, int year) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<KPIPerson> cq = cb.createQuery(KPIPerson.class);
			List<Predicate> predicates = new LinkedList<Predicate>();
			Root<KPIPerson> root = cq.from(KPIPerson.class);

			if (month != 0) {
				predicates.add(cb.equal(root.get("kmonth"), month));
			}
			if (year != 0) {
				predicates.add(cb.equal(root.get("kyear"), year));
			}
			cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
			List<KPIPerson> result = em.createQuery(cq).getResultList();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
}
