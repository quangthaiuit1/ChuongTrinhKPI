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
import trong.lixco.com.jpa.entitykpi.TagetCate;
import trong.lixco.com.jpa.entitykpi.TagetCateWeight;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class TagetCateService extends AbstractService<TagetCate> {
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
	protected Class<TagetCate> getEntityClass() {
		return TagetCate.class;
	}

	public TagetCateWeight findSearch(int year, TagetCate tagetCate) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TagetCateWeight> cq = cb.createQuery(TagetCateWeight.class);
		List<Predicate> predicates = new LinkedList<Predicate>();
		Root<TagetCateWeight> root = cq.from(TagetCateWeight.class);
		if (year != 0)
			predicates.add(cb.equal(root.get("year"), year));
		if (tagetCate != null)
			predicates.add(cb.equal(root.get("tagetCate"), tagetCate));
		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		;
		TypedQuery<TagetCateWeight> query = em.createQuery(cq);
		List<TagetCateWeight> results = query.getResultList();
		if (results.size() != 0)
			return results.get(0);
		return new TagetCateWeight();
	}
	
	
	public TagetCateWeight create(TagetCateWeight entity){
		getEntityManager().persist(entity);
		return getEntityManager().merge(entity);
	}
	
	public TagetCateWeight update(TagetCateWeight account){
		return getEntityManager().merge(account);

	}
}
