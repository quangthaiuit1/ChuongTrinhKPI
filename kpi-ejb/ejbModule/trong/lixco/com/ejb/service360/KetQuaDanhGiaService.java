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
import trong.lixco.com.jpa.entity360.CauHoi;
import trong.lixco.com.jpa.entity360.KetQuaDanhGia;
import trong.lixco.com.jpa.entity360.KyDanhGia;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class KetQuaDanhGiaService extends AbstractService<KetQuaDanhGia> {
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
	protected Class<KetQuaDanhGia> getEntityClass() {
		return KetQuaDanhGia.class;
	}

	public List<KetQuaDanhGia> findRange(String nvdanhgia,String nvduocdanhgia, KyDanhGia kyDanhGia) {
		if (nvduocdanhgia != null && kyDanhGia != null) {
			try {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<KetQuaDanhGia> cq = cb.createQuery(KetQuaDanhGia.class);
				List<Predicate> predicates = new LinkedList<Predicate>();
				Root<KetQuaDanhGia> root = cq.from(KetQuaDanhGia.class);
				predicates.add(cb.equal(root.get("nvdanhgia"), nvdanhgia));
				predicates.add(cb.equal(root.get("nvduocdanhgia"), nvduocdanhgia));
				predicates.add(cb.equal(root.get("kyDanhGia"), kyDanhGia));
				cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
				return em.createQuery(cq).getResultList();
			} catch (Exception e) {
				return new ArrayList<KetQuaDanhGia>();
			}
		} else {
			return new ArrayList<KetQuaDanhGia>();
		}
	}

	public void luuketquadanhgia(List<KetQuaDanhGia> ketQuaDanhGias) {
		for (int i = 0; i < ketQuaDanhGias.size(); i++) {
			try {
				KetQuaDanhGia nl = ketQuaDanhGias.get(i);
				if (nl.getId() == null) {
					em.persist(nl);
				} else {
					em.merge(nl);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
