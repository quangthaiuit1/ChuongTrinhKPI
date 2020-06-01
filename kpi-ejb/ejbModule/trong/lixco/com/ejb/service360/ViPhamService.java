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
import trong.lixco.com.jpa.entity360.ViPham;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class ViPhamService extends AbstractService<ViPham> {
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
	protected Class<ViPham> getEntityClass() {
		return ViPham.class;
	}

	public ViPham findRange(KyDanhGia kyDanhGia, String codeEmp) {
		if (kyDanhGia != null && codeEmp != null) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ViPham> cq = cb.createQuery(ViPham.class);
			List<Predicate> predicates = new LinkedList<Predicate>();
			Root<ViPham> root = cq.from(ViPham.class);
			Predicate predicateStart = cb.equal(root.get("kyDanhGia"), kyDanhGia);
			predicates.add(predicateStart);
			Predicate predicateEnd = cb.equal(root.get("manhanvien"), codeEmp);
			predicates.add(predicateEnd);

			cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
			List<ViPham> results = em.createQuery(cq).getResultList();
			if (results.size() != 0)
				return results.get(0);
			return null;
		} else {
			return null;
		}
	}

	public void luuviphamtuexcel(KyDanhGia kyDanhGia, List<ViPham> viPhams) {
		for (int i = 0; i < viPhams.size(); i++) {
			try {
				ViPham nl = viPhams.get(i);
				ViPham vpold = findRange(kyDanhGia, nl.getManhanvien());
				if (vpold != null) {
					vpold.setSolanvipham(nl.getSolanvipham());
				} else {
					nl.setKyDanhGia(kyDanhGia);
					em.persist(nl);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
