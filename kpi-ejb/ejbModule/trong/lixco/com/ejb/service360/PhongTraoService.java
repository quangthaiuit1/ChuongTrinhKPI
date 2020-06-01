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
import trong.lixco.com.jpa.entity360.PhongTrao;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class PhongTraoService extends AbstractService<PhongTrao> {
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
	protected Class<PhongTrao> getEntityClass() {
		return PhongTrao.class;
	}

	public PhongTrao findRange(KyDanhGia kyDanhGia, String codeEmp) {
		if (kyDanhGia != null && codeEmp != null) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PhongTrao> cq = cb.createQuery(PhongTrao.class);
			List<Predicate> predicates = new LinkedList<Predicate>();
			Root<PhongTrao> root = cq.from(PhongTrao.class);
			Predicate predicateStart = cb.equal(root.get("kyDanhGia"), kyDanhGia);
			predicates.add(predicateStart);
			Predicate predicateEnd = cb.equal(root.get("manhanvien"), codeEmp);
			predicates.add(predicateEnd);

			cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
			List<PhongTrao> results = em.createQuery(cq).getResultList();
			if (results.size() != 0)
				return results.get(0);
			return null;
		} else {
			return null;
		}
	}

	public void luuphongtraotuexcel(KyDanhGia kyDanhGia, List<PhongTrao> PhongTraos) {
		for (int i = 0; i < PhongTraos.size(); i++) {
			try {
				PhongTrao nl = PhongTraos.get(i);
				System.out.println("nl.getSolanthamgia(): "+nl.getSolanthamgia());
				PhongTrao vpold = findRange(kyDanhGia, nl.getManhanvien());
				if (vpold != null) {
					vpold.setSolanthamgia(nl.getSolanthamgia());
					em.merge(nl);
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
