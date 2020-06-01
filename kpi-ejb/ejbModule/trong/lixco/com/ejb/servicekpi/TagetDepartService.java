package trong.lixco.com.ejb.servicekpi;

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
import trong.lixco.com.jpa.entitykpi.TagetDepart;
import trong.lixco.com.jpa.entitykpi.TagetDepartCate;
import trong.lixco.com.jpa.entitykpi.TagetDepartCateWeight;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class TagetDepartService extends AbstractService<TagetDepart> {
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
	protected Class<TagetDepart> getEntityClass() {
		return TagetDepart.class;
	}

	@Inject
	TagetDepartCateService tagetDepartCateService;

	public List<TagetDepart> findSearch(int year, String codeDepart) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TagetDepart> cq = cb.createQuery(TagetDepart.class);
		List<Predicate> predicates = new LinkedList<Predicate>();
		Root<TagetDepart> root = cq.from(TagetDepart.class);
		if (year != 0)
			predicates.add(cb.equal(root.get("year"), year));
		if (codeDepart != null)
			predicates.add(cb.equal(root.get("codeDepart"), codeDepart));
		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])))
				.orderBy(cb.asc(root.get("codeDepart")),cb.asc(root.get("kTagetDepartCate").get("code")), cb.asc(root.get("code")));
		TypedQuery<TagetDepart> query = em.createQuery(cq);
		List<TagetDepart> results = query.getResultList();
		for (int i = 0; i < results.size(); i++) {
			TagetDepartCateWeight tw = tagetDepartCateService.findSearch(year, results.get(i).getkTagetDepartCate(),codeDepart);
			try {
				results.get(i).getkTagetDepartCate().setWeight(tw.getWeigth());
			} catch (Exception e) {
			}
		}
		return results;
	}

	public TagetDepart findTagetByCater(TagetDepartCate tagetDepartCate) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TagetDepart> cq = cb.createQuery(TagetDepart.class);
		List<Predicate> predicates = new LinkedList<Predicate>();
		Root<TagetDepart> root = cq.from(TagetDepart.class);
		if (tagetDepartCate != null)
			predicates.add(cb.equal(root.get("kTagetDepartCate"), tagetDepartCate));
		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<TagetDepart> query = em.createQuery(cq);
		List<TagetDepart> results = query.getResultList();
		if(results.size()!=0)
			return results.get(0);
		return null;
		
	}
	
}
