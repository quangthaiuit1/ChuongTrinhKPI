package trong.lixco.com.ejb.servicekpi;

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
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import trong.lixco.com.ejb.service.AbstractService;
import trong.lixco.com.jpa.entitykpi.RelaDeppTaget;
import trong.lixco.com.jpa.entitykpi.Taget;
import trong.lixco.com.jpa.entitykpi.TagetCate;
import trong.lixco.com.jpa.entitykpi.TagetCateWeight;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class TagetService extends AbstractService<Taget> {
	@Inject
	private EntityManager em;
	@Resource
	private SessionContext ct;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	@Override
	protected SessionContext getUt() {
		return ct;
	}

	@Override
	protected Class<Taget> getEntityClass() {
		return Taget.class;
	}

	@Inject
	TagetCateService tagetCateService;

	public List<Taget> findSearch(int year, TagetCate tagetCate) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Taget> cq = cb.createQuery(Taget.class);
		List<Predicate> predicates = new LinkedList<Predicate>();
		Root<Taget> root = cq.from(Taget.class);
		if (year != 0)
			predicates.add(cb.equal(root.get("year"), year));
		if (tagetCate != null)
			predicates.add(cb.equal(root.get("kTagetCate"), tagetCate));
		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])))
				.orderBy(cb.asc(root.get("kTagetCate").get("code")), cb.asc(root.get("code")));
		TypedQuery<Taget> query = em.createQuery(cq);
		List<Taget> results = query.getResultList();
		for (int i = 0; i < results.size(); i++) {
			TagetCateWeight tw = tagetCateService.findSearch(year, results.get(i).getkTagetCate());
			try {
				results.get(i).getkTagetCate().setWeight(tw.getWeigth());
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return results;
	}
	public boolean checkDeppAndTaget(String codeDepart, Taget taget) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<RelaDeppTaget> cq = cb.createQuery(RelaDeppTaget.class);
		List<Predicate> predicates = new LinkedList<Predicate>();
		Root<RelaDeppTaget> root = cq.from(RelaDeppTaget.class);
		if (taget != null)
			predicates.add(cb.equal(root.get("taget"), taget));
		if (codeDepart != null)
			predicates.add(cb.equal(root.get("codeDepart"), codeDepart));
		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<RelaDeppTaget> query = em.createQuery(cq);
		List<RelaDeppTaget> results = query.getResultList();
		if (results.size() == 0)
			return false;
		return true;
	}
	public List<String> findDeppByTaget(Taget taget) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<RelaDeppTaget> cq = cb.createQuery(RelaDeppTaget.class);
		List<Predicate> predicates = new LinkedList<Predicate>();
		Root<RelaDeppTaget> root = cq.from(RelaDeppTaget.class);
		if (taget != null)
			predicates.add(cb.equal(root.get("taget"), taget));
		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<RelaDeppTaget> query = em.createQuery(cq);
		List<RelaDeppTaget> results = query.getResultList();
		List<String> codeDeparts = new ArrayList<String>();
		for (int i = 0; i < results.size(); i++) {
			codeDeparts.add(results.get(i).getCodeDepart());
		}
		return codeDeparts;
	}
	public void createRelaDeppTaget(Taget taget, List<String> codeDeparts) {
		for (int i = 0; i < codeDeparts.size(); i++) {
			RelaDeppTaget item = new RelaDeppTaget();
			item.setTaget(taget);
			item.setCodeDepart(codeDeparts.get(i));
			em.persist(item);
		}
	}
}
