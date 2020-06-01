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
import trong.lixco.com.jpa.entity360.CauHoi;
import trong.lixco.com.jpa.entity360.KyDanhGia;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class CauHoiService extends AbstractService<CauHoi> {
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
	protected Class<CauHoi> getEntityClass() {
		return CauHoi.class;
	}

	public CauHoi findByCode(int ma,int nhom) {
		if (ma != 0&&nhom!=0) {
			try {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<CauHoi> cq = cb.createQuery(CauHoi.class);
				List<Predicate> predicates = new LinkedList<Predicate>();
				Root<CauHoi> root = cq.from(CauHoi.class);
				predicates.add(cb.equal(root.get("macauhoi"), ma));
				predicates.add(cb.equal(root.get("nhomcauhoi"), nhom));
				cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
				return em.createQuery(cq).getSingleResult();
			} catch (Exception e) {
				return null;
			}
		} else {
			return null;
		}
	}

	public List<CauHoi> findRange(int nhomcauhoi) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CauHoi> cq = cb.createQuery(CauHoi.class);
		List<Predicate> predicates = new LinkedList<Predicate>();
		Root<CauHoi> root = cq.from(CauHoi.class);
		Predicate predicateStart = cb.equal(root.get("nhomcauhoi"), nhomcauhoi);
		predicates.add(predicateStart);
		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		List<CauHoi> results = em.createQuery(cq).getResultList();
		return results;
	}

	public void luucauhoituexcel(List<CauHoi> cauHois) {
		for (int i = 0; i < cauHois.size(); i++) {
			try {
				CauHoi nl = cauHois.get(i);
				CauHoi nlDataBase = findByCode(cauHois.get(i).getMacauhoi(),nl.getNhomcauhoi());
				if (nlDataBase == null) {
					em.persist(nl);
				}else{
					nlDataBase.setNoidung(nl.getNoidung());
					em.merge(nlDataBase);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
}
