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
import trong.lixco.com.jpa.entitykpi.TagetDepartCate;
import trong.lixco.com.jpa.entitykpi.TagetDepartCateWeight;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class TagetDepartCateService extends AbstractService<TagetDepartCate> {
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
	protected Class<TagetDepartCate> getEntityClass() {
		return TagetDepartCate.class;
	}

	public TagetDepartCateWeight findSearch(int year, TagetDepartCate tagetDepartCate, String codeDepart) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TagetDepartCateWeight> cq = cb.createQuery(TagetDepartCateWeight.class);
		List<Predicate> predicates = new LinkedList<Predicate>();
		Root<TagetDepartCateWeight> root = cq.from(TagetDepartCateWeight.class);
		if (year != 0)
			predicates.add(cb.equal(root.get("year"), year));
		if (tagetDepartCate != null)
			predicates.add(cb.equal(root.get("tagetDepartCate"), tagetDepartCate));
		if (codeDepart != null)
			predicates.add(cb.equal(root.get("codeDepart"), codeDepart));
		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<TagetDepartCateWeight> query = em.createQuery(cq);
		List<TagetDepartCateWeight> results = query.getResultList();
		if (results.size() != 0)
			return results.get(0);
		return new TagetDepartCateWeight();
	}

	public TagetDepartCateWeight create(TagetDepartCateWeight tagetDepartCateWeight) {
		getEntityManager().persist(tagetDepartCateWeight);
		return getEntityManager().merge(tagetDepartCateWeight);
	}

	public TagetDepartCateWeight update(TagetDepartCateWeight tagetDepartCateWeight) {
		return getEntityManager().merge(tagetDepartCateWeight);

	}
}