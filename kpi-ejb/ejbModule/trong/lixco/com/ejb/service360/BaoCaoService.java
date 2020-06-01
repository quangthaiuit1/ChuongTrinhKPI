package trong.lixco.com.ejb.service360;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import trong.lixco.com.ejb.service.AbstractService;
import trong.lixco.com.jpa.entity360.ChiTietDanhGia;
import trong.lixco.com.jpa.entity360.KetQuaDanhGia;
import trong.lixco.com.jpa.entity360.KyDanhGia;
import trong.lixco.com.jpa.entitykpi.KPIDep;
import trong.lixco.com.jpa.entitykpi.KPIDepMonth;
import trong.lixco.com.jpa.entitykpi.KPIPerson;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class BaoCaoService extends AbstractService<KetQuaDanhGia> {
	@Inject
	private EntityManager em;

	@PersistenceContext(unitName = "kpibd")
	private EntityManager embd;

//	@PersistenceContext(unitName = "kpibn")
//	private EntityManager embn;

	@Resource
	private SessionContext ct;
	static SimpleDateFormat sf;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	@Override
	protected SessionContext getUt() {
		return ct;
	}

	@Override
	protected Class<KetQuaDanhGia> getEntityClass() {
		return KetQuaDanhGia.class;
	}

/**
 * Bao cao danh gia
 * @param kyDanhGia
 * @return
 */
	public List<ChiTietDanhGia> baocaoHCM(KyDanhGia kyDanhGia) {
		if (kyDanhGia != null) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ChiTietDanhGia> cq = cb.createQuery(ChiTietDanhGia.class);
			List<Predicate> predicates = new LinkedList<Predicate>();
			Root<ChiTietDanhGia> root = cq.from(ChiTietDanhGia.class);

			Predicate predicateStart = cb.equal(root.get("kyDanhGia").get("id"), kyDanhGia.getId());
			predicates.add(predicateStart);
			cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
			List<ChiTietDanhGia> results = em.createQuery(cq).getResultList();
			return results;
		} else {
			return new ArrayList<ChiTietDanhGia>();
		}
	}

	public List<ChiTietDanhGia> baocaoBD(KyDanhGia kyDanhGia) {
		try {
			if (kyDanhGia != null) {
				long id=kyDanhGia.getId();
				CriteriaBuilder cb = embd.getCriteriaBuilder();
				CriteriaQuery<ChiTietDanhGia> cq = cb.createQuery(ChiTietDanhGia.class);
				List<Predicate> predicates = new LinkedList<Predicate>();
				Root<ChiTietDanhGia> root = cq.from(ChiTietDanhGia.class);
				Predicate predicateStart = cb.equal(root.get("kyDanhGia").get("id"), id);
				predicates.add(predicateStart);
				cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
				List<ChiTietDanhGia> results = embd.createQuery(cq).getResultList();
				return results;
			} else {
				return new ArrayList<ChiTietDanhGia>();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<ChiTietDanhGia>();
		}
		
	}

	public List<ChiTietDanhGia> baocaoBN(KyDanhGia kyDanhGia) {
//		if (kyDanhGia != null) {
//			CriteriaBuilder cb = embn.getCriteriaBuilder();
//			CriteriaQuery<ChiTietDanhGia> cq = cb.createQuery(ChiTietDanhGia.class);
//			List<Predicate> predicates = new LinkedList<Predicate>();
//			Root<ChiTietDanhGia> root = cq.from(ChiTietDanhGia.class);
//
//			Predicate predicateStart = cb.equal(root.get("kyDanhGia").get("id"), kyDanhGia.getId());
//			predicates.add(predicateStart);
//			cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
//			List<ChiTietDanhGia> results = embn.createQuery(cq).getResultList();
//			return results;
//		} else {
			return new ArrayList<ChiTietDanhGia>();
//		}
	}

	/**
	 * KPI ca nhan
	 */
	public List<KPIPerson> findKPIPerson(int year, String codeEmp) {
		List<KPIPerson> kpiPersons = findKPIPersonHCM(year, codeEmp);
		if (kpiPersons.size() == 0)
			kpiPersons = findKPIPersonBD(year, codeEmp);
		if (kpiPersons.size() == 0)
			kpiPersons = findKPIPersonBN(year, codeEmp);
		return kpiPersons;
	}

	public List<KPIPerson> findKPIPersonHCM(int year, String codeEmp) {
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
		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])))
				.orderBy(cb.desc(root.get("kyear")), cb.desc(root.get("kmonth")));
		List<KPIPerson> result = em.createQuery(cq).getResultList();
		return result;
	}

	public List<KPIPerson> findKPIPersonBD(int year, String codeEmp) {
		CriteriaBuilder cb = embd.getCriteriaBuilder();
		CriteriaQuery<KPIPerson> cq = cb.createQuery(KPIPerson.class);
		List<Predicate> predicates = new LinkedList<Predicate>();
		Root<KPIPerson> root = cq.from(KPIPerson.class);
		if (codeEmp != null) {
			predicates.add(cb.equal(root.get("codeEmp"), codeEmp));
		}
		if (year != 0) {
			predicates.add(cb.equal(root.get("kyear"), year));
		}
		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])))
				.orderBy(cb.desc(root.get("kyear")), cb.desc(root.get("kmonth")));
		List<KPIPerson> result = embd.createQuery(cq).getResultList();
		return result;
	}

	public List<KPIPerson> findKPIPersonBN(int year, String codeEmp) {
//		CriteriaBuilder cb = embn.getCriteriaBuilder();
//		CriteriaQuery<KPIPerson> cq = cb.createQuery(KPIPerson.class);
//		List<Predicate> predicates = new LinkedList<Predicate>();
//		Root<KPIPerson> root = cq.from(KPIPerson.class);
//		if (codeEmp != null) {
//			predicates.add(cb.equal(root.get("codeEmp"), codeEmp));
//		}
//		if (year != 0) {
//			predicates.add(cb.equal(root.get("kyear"), year));
//		}
//		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])))
//				.orderBy(cb.desc(root.get("kyear")), cb.desc(root.get("kmonth")));
//		List<KPIPerson> result = embn.createQuery(cq).getResultList();
//		return result;
		return new ArrayList<KPIPerson>();
	}
/**
 * KPI phong thang
 * @param year
 * @param codeDepart
 * @return
 */
	public List<KPIDepMonth> findKPIDepMonthYearHCM(int year, String codeDepart) {
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

	public List<KPIDepMonth> findKPIDepMonthYearBD(int year, String codeDepart) {
		CriteriaBuilder cb = embd.getCriteriaBuilder();
		CriteriaQuery<KPIDepMonth> cq = cb.createQuery(KPIDepMonth.class);
		List<Predicate> predicates = new LinkedList<Predicate>();
		Root<KPIDepMonth> root = cq.from(KPIDepMonth.class);
		predicates.add(cb.equal(root.get("year"), year));
		if (codeDepart != null)
			predicates.add(cb.equal(root.get("codeDepart"), codeDepart));
		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<KPIDepMonth> query = embd.createQuery(cq);
		List<KPIDepMonth> results = query.getResultList();
		return results;
	}

	public List<KPIDepMonth> findKPIDepMonthYearBN(int year, String codeDepart) {
//		CriteriaBuilder cb = embn.getCriteriaBuilder();
//		CriteriaQuery<KPIDepMonth> cq = cb.createQuery(KPIDepMonth.class);
//		List<Predicate> predicates = new LinkedList<Predicate>();
//		Root<KPIDepMonth> root = cq.from(KPIDepMonth.class);
//		predicates.add(cb.equal(root.get("year"), year));
//		if (codeDepart != null)
//			predicates.add(cb.equal(root.get("codeDepart"), codeDepart));
//		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
//		TypedQuery<KPIDepMonth> query = embn.createQuery(cq);
//		List<KPIDepMonth> results = query.getResultList();
//		return results;
		return new ArrayList<KPIDepMonth>();
	}
	/**
	 * KPI phòng nam
	 */
	public List<KPIDep> findKPIDepYearHCM(int year, String codeDepart) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<KPIDep> cq = cb.createQuery(KPIDep.class);
		List<Predicate> predicates = new LinkedList<Predicate>();
		Root<KPIDep> root = cq.from(KPIDep.class);
		predicates.add(cb.equal(root.get("year"), year));
		if (codeDepart != null)
			predicates.add(cb.equal(root.get("codeDepart"), codeDepart));
		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<KPIDep> query = em.createQuery(cq);
		List<KPIDep> results = query.getResultList();
		return results;
	}

	public List<KPIDep> findKPIDepYearBD(int year, String codeDepart) {
		CriteriaBuilder cb = embd.getCriteriaBuilder();
		CriteriaQuery<KPIDep> cq = cb.createQuery(KPIDep.class);
		List<Predicate> predicates = new LinkedList<Predicate>();
		Root<KPIDep> root = cq.from(KPIDep.class);
		predicates.add(cb.equal(root.get("year"), year));
		if (codeDepart != null)
			predicates.add(cb.equal(root.get("codeDepart"), codeDepart));
		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<KPIDep> query = embd.createQuery(cq);
		List<KPIDep> results = query.getResultList();
		return results;
	}

	public List<KPIDep> findKPIDepYearBN(int year, String codeDepart) {
//		CriteriaBuilder cb = embn.getCriteriaBuilder();
//		CriteriaQuery<KPIDep> cq = cb.createQuery(KPIDep.class);
//		List<Predicate> predicates = new LinkedList<Predicate>();
//		Root<KPIDep> root = cq.from(KPIDep.class);
//		predicates.add(cb.equal(root.get("year"), year));
//		if (codeDepart != null)
//			predicates.add(cb.equal(root.get("codeDepart"), codeDepart));
//		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
//		TypedQuery<KPIDep> query = embn.createQuery(cq);
//		List<KPIDep> results = query.getResultList();
//		return results;
		return new ArrayList<KPIDep>();
	}
	/**
	 * Chi tiet danh gia
	 */

	public List<ChiTietDanhGia> danhsachchuadanhgiaHCM(KyDanhGia kyDanhGia, String codeEmp) {
		if (kyDanhGia != null && codeEmp != null) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ChiTietDanhGia> cq = cb.createQuery(ChiTietDanhGia.class);
			List<Predicate> predicates = new LinkedList<Predicate>();
			Root<ChiTietDanhGia> root = cq.from(ChiTietDanhGia.class);

			Predicate predicate1 = cb.and(cb.equal(root.get("nv1"), codeEmp),cb.notEqual(root.get("diem1"), 0));
			predicates.add(predicate1);
			Predicate predicate2 = cb.and(cb.equal(root.get("nv2"), codeEmp),cb.notEqual(root.get("diem2"), 0));
			predicates.add(predicate2);
			Predicate predicate3 = cb.and(cb.equal(root.get("nv3"), codeEmp),cb.notEqual(root.get("diem3"), 0));
			predicates.add(predicate3);
			Predicate predicate4 = cb.and(cb.equal(root.get("nv4"), codeEmp),cb.notEqual(root.get("diem4"), 0));
			predicates.add(predicate4);
			Predicate predicate5 = cb.and(cb.equal(root.get("nv5"), codeEmp),cb.notEqual(root.get("diem5"), 0));
			predicates.add(predicate5);
			Predicate predicate6 = cb.and(cb.equal(root.get("nv6"), codeEmp),cb.notEqual(root.get("diem6"), 0));
			predicates.add(predicate6);

			cq.select(root).where(cb.equal(root.get("kyDanhGia"), kyDanhGia),
					cb.or(predicates.toArray(new Predicate[0])));
			List<ChiTietDanhGia> results = em.createQuery(cq).getResultList();
			return results;
		} else {
			return new ArrayList<ChiTietDanhGia>();
		}
	}

	public List<ChiTietDanhGia> danhsachchuadanhgiaBD(KyDanhGia kyDanhGia, String nhanviendanhgia, String nvduocdanhgia) {
		if (kyDanhGia != null && nhanviendanhgia != null) {
			CriteriaBuilder cb = embd.getCriteriaBuilder();
			CriteriaQuery<ChiTietDanhGia> cq = cb.createQuery(ChiTietDanhGia.class);
			List<Predicate> predicates = new LinkedList<Predicate>();
			Root<ChiTietDanhGia> root = cq.from(ChiTietDanhGia.class);

			Predicate predicate1 = cb.and(cb.equal(root.get("nv1"), nhanviendanhgia),cb.le(root.get("diem1"), 1));
			predicates.add(predicate1);
			Predicate predicate2 = cb.and(cb.equal(root.get("nv2"), nhanviendanhgia),cb.le(root.get("diem2"), 1));
			predicates.add(predicate2);
			Predicate predicate3 = cb.and(cb.equal(root.get("nv3"), nhanviendanhgia),cb.le(root.get("diem3"), 1));
			predicates.add(predicate3);
			Predicate predicate4 = cb.and(cb.equal(root.get("nv4"), nhanviendanhgia),cb.le(root.get("diem4"), 1));
			predicates.add(predicate4);
			Predicate predicate5 = cb.and(cb.equal(root.get("nv5"), nhanviendanhgia),cb.le(root.get("diem5"), 1));
			predicates.add(predicate5);
			Predicate predicate6 = cb.and(cb.equal(root.get("nv6"), nhanviendanhgia),cb.le(root.get("diem6"), 1));
			predicates.add(predicate6);

			cq.select(root).where(cb.equal(root.get("kyDanhGia"), kyDanhGia),cb.equal(root.get("manhanvien"), nvduocdanhgia),
					cb.or(predicates.toArray(new Predicate[0])));
			List<ChiTietDanhGia> results = embd.createQuery(cq).getResultList();
			return results;
		} else {
			return new ArrayList<ChiTietDanhGia>();
		}
	}

	public List<ChiTietDanhGia> danhsachchuadanhgiaBN(KyDanhGia kyDanhGia, String codeEmp) {
//		if (kyDanhGia != null && codeEmp != null) {
//			CriteriaBuilder cb = embn.getCriteriaBuilder();
//			CriteriaQuery<ChiTietDanhGia> cq = cb.createQuery(ChiTietDanhGia.class);
//			List<Predicate> predicates = new LinkedList<Predicate>();
//			Root<ChiTietDanhGia> root = cq.from(ChiTietDanhGia.class);
//
//			Predicate predicate1 = cb.and(cb.equal(root.get("nv1"), codeEmp),cb.notEqual(root.get("diem1"), 0));
//			predicates.add(predicate1);
//			Predicate predicate2 = cb.and(cb.equal(root.get("nv2"), codeEmp),cb.notEqual(root.get("diem2"), 0));
//			predicates.add(predicate2);
//			Predicate predicate3 = cb.and(cb.equal(root.get("nv3"), codeEmp),cb.notEqual(root.get("diem3"), 0));
//			predicates.add(predicate3);
//			Predicate predicate4 = cb.and(cb.equal(root.get("nv4"), codeEmp),cb.notEqual(root.get("diem4"), 0));
//			predicates.add(predicate4);
//			Predicate predicate5 = cb.and(cb.equal(root.get("nv5"), codeEmp),cb.notEqual(root.get("diem5"), 0));
//			predicates.add(predicate5);
//			Predicate predicate6 = cb.and(cb.equal(root.get("nv6"), codeEmp),cb.notEqual(root.get("diem6"), 0));
//			predicates.add(predicate6);
//
//			cq.select(root).where(cb.equal(root.get("kyDanhGia"), kyDanhGia),
//					cb.or(predicates.toArray(new Predicate[0])));
//			List<ChiTietDanhGia> results = embn.createQuery(cq).getResultList();
//			return results;
//		} else {
			return new ArrayList<ChiTietDanhGia>();
//		}
	}
}
