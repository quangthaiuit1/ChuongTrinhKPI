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
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import trong.lixco.com.ejb.service.AbstractService;
import trong.lixco.com.jpa.entity360.ChiTietDanhGia;
import trong.lixco.com.jpa.entity360.KyDanhGia;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class ChiTietDanhGiaService extends AbstractService<ChiTietDanhGia> {
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
	protected Class<ChiTietDanhGia> getEntityClass() {
		return ChiTietDanhGia.class;
	}

	public List<ChiTietDanhGia> danhsachnhanvienduocdanhgia(KyDanhGia kyDanhGia, String codeEmp) {
		if (kyDanhGia != null && codeEmp != null) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ChiTietDanhGia> cq = cb.createQuery(ChiTietDanhGia.class);
			List<Predicate> predicates = new LinkedList<Predicate>();
			Root<ChiTietDanhGia> root = cq.from(ChiTietDanhGia.class);

			Predicate predicate0 = cb.equal(root.get("manhanvien"), codeEmp);
			predicates.add(predicate0);
			Predicate predicate1 = cb.equal(root.get("nv1"), codeEmp);
			predicates.add(predicate1);
			Predicate predicate2 = cb.equal(root.get("nv2"), codeEmp);
			predicates.add(predicate2);
			Predicate predicate3 = cb.equal(root.get("nv3"), codeEmp);
			predicates.add(predicate3);
			Predicate predicate4 = cb.equal(root.get("nv4"), codeEmp);
			predicates.add(predicate4);
			Predicate predicate5 = cb.equal(root.get("nv5"), codeEmp);
			predicates.add(predicate5);
			Predicate predicate6 = cb.equal(root.get("nv6"), codeEmp);
			predicates.add(predicate6);

			cq.select(root).where(cb.equal(root.get("kyDanhGia"), kyDanhGia),
					cb.or(predicates.toArray(new Predicate[0])));
			List<ChiTietDanhGia> results = em.createQuery(cq).getResultList();
			return results;
		} else {
			return new ArrayList<ChiTietDanhGia>();
		}
	}


	public ChiTietDanhGia findRange(KyDanhGia kyDanhGia, String codeEmp) {
		if (kyDanhGia != null && codeEmp != null) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ChiTietDanhGia> cq = cb.createQuery(ChiTietDanhGia.class);
			List<Predicate> predicates = new LinkedList<Predicate>();
			Root<ChiTietDanhGia> root = cq.from(ChiTietDanhGia.class);

			Predicate predicateStart = cb.equal(root.get("kyDanhGia"), kyDanhGia);
			predicates.add(predicateStart);
			Predicate predicateEnd = cb.equal(root.get("manhanvien"), codeEmp);
			predicates.add(predicateEnd);

			cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
			List<ChiTietDanhGia> results = em.createQuery(cq).getResultList();
			if (results.size() != 0)
				return results.get(0);
			return null;
		} else {
			return null;
		}
	}

	public void luucaidattuexcel(boolean naplai, KyDanhGia kyDanhGia, List<ChiTietDanhGia> chiTietDanhGias) {
		if (naplai) {
			List<ChiTietDanhGia> cts = findAll();
			for (int i = 0; i < cts.size(); i++) {
				em.remove(cts.get(0));
			}
		}
		for (int i = 0; i < chiTietDanhGias.size(); i++) {
			try {
				ChiTietDanhGia nl = chiTietDanhGias.get(i);
				nl.setKyDanhGia(kyDanhGia);
				em.persist(nl);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
}
