package trong.lixco.com.ejb.service360;

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
import trong.lixco.com.jpa.entity360.CauHoi;
import trong.lixco.com.jpa.entity360.ChiTietDanhGia;
import trong.lixco.com.jpa.entity360.KyDanhGia;
import trong.lixco.com.jpa.entitykpi.KPIPerson;
import trong.lixco.com.jpa.entitykpi.KPIPersonOfMonth;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class KyDanhGiaService extends AbstractService<KyDanhGia> {
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
	protected Class<KyDanhGia> getEntityClass() {
		return KyDanhGia.class;
	}

	public KyDanhGia findRange(Date ngay) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<KyDanhGia> cq = cb.createQuery(KyDanhGia.class);
		List<Predicate> predicates = new LinkedList<Predicate>();
		Root<KyDanhGia> root = cq.from(KyDanhGia.class);
		if (ngay != null) {
			Predicate predicateStart = cb.lessThanOrEqualTo(root.get("ngaybatdau"), ngay);
			predicates.add(predicateStart);
			Predicate predicateEnd = cb.greaterThanOrEqualTo(root.get("ngayketthuc"), ngay);
			predicates.add(predicateEnd);
		}
		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		List<KyDanhGia> results = em.createQuery(cq).getResultList();
		if (results.size() != 0)
			return results.get(0);
		return null;
	}

}
