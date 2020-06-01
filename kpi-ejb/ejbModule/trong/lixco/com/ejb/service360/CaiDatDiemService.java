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
import trong.lixco.com.jpa.entity360.CaiDatDiem;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class CaiDatDiemService extends AbstractService<CaiDatDiem> {
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
	protected Class<CaiDatDiem> getEntityClass() {
		return CaiDatDiem.class;
	}

	public CaiDatDiem findRange(KyDanhGia kyDanhGia) {
		if (kyDanhGia != null) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CaiDatDiem> cq = cb.createQuery(CaiDatDiem.class);
			List<Predicate> predicates = new LinkedList<Predicate>();
			Root<CaiDatDiem> root = cq.from(CaiDatDiem.class);
			Predicate predicateStart = cb.equal(root.get("kyDanhGia"), kyDanhGia);
			predicates.add(predicateStart);
			cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
			List<CaiDatDiem> results = em.createQuery(cq).getResultList();
			if (results.size() != 0)
				return results.get(0);
			return null;
		} else {
			return null;
		}
	}

}
