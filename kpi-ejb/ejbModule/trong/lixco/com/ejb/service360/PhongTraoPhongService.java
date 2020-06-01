package trong.lixco.com.ejb.service360;

import java.text.SimpleDateFormat;
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
import trong.lixco.com.jpa.entity360.KyDanhGia;
import trong.lixco.com.jpa.entity360.PhongTraoPhong;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class PhongTraoPhongService extends AbstractService<PhongTraoPhong> {
	@Inject
	private EntityManager em;
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
	protected Class<PhongTraoPhong> getEntityClass() {
		return PhongTraoPhong.class;
	}

	public PhongTraoPhong findRange(KyDanhGia kyDanhGia, String codeEmp) {
		if (kyDanhGia != null && codeEmp != null) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PhongTraoPhong> cq = cb.createQuery(PhongTraoPhong.class);
			List<Predicate> predicates = new LinkedList<Predicate>();
			Root<PhongTraoPhong> root = cq.from(PhongTraoPhong.class);
			Predicate predicateStart = cb.equal(root.get("kyDanhGia"), kyDanhGia);
			predicates.add(predicateStart);
			Predicate predicateEnd = cb.equal(root.get("maphong"), codeEmp);
			predicates.add(predicateEnd);

			cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
			List<PhongTraoPhong> results = em.createQuery(cq).getResultList();
			if (results.size() != 0)
				return results.get(0);
			return null;
		} else {
			return null;
		}
	}

	public void luuPhongTraoPhongtuexcel(KyDanhGia kyDanhGia, List<PhongTraoPhong> PhongTraoPhongs) {
		for (int i = 0; i < PhongTraoPhongs.size(); i++) {
			try {
				PhongTraoPhong nl = PhongTraoPhongs.get(i);
				if (nl.getMaphong() != null && !"".equals(nl.getMaphong())) {
					PhongTraoPhong vpold = findRange(kyDanhGia, nl.getMaphong());
					if (vpold != null) {
						vpold.setSodiem(nl.getSodiem());
					} else {
						nl.setKyDanhGia(kyDanhGia);
						em.persist(nl);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
