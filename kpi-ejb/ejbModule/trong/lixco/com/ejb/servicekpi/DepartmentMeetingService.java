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
import trong.lixco.com.jpa.entitykpi.DepartmentMeeting;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class DepartmentMeetingService extends AbstractService<DepartmentMeeting> {
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
	protected Class<DepartmentMeeting> getEntityClass() {
		return DepartmentMeeting.class;
	}

	public List<DepartmentMeeting> findDepartmentMeeting(int week, int year, String codeDepart) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DepartmentMeeting> cq = cb.createQuery(DepartmentMeeting.class);
		List<Predicate> predicates = new LinkedList<Predicate>();
		Root<DepartmentMeeting> root = cq.from(DepartmentMeeting.class);
		predicates.add(cb.equal(root.get("week"), week));
		predicates.add(cb.equal(root.get("year"), year));
		if (codeDepart != null)
			predicates.add(cb.equal(root.get("codeDepart"), codeDepart));
		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<DepartmentMeeting> query = em.createQuery(cq);
		List<DepartmentMeeting> results = query.getResultList();
		return results;
	}

	public List<DepartmentMeeting> findDepartmentMeeting(List<Integer> weeks, int year,
			String codeDepart, boolean view) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DepartmentMeeting> cq = cb.createQuery(DepartmentMeeting.class);
		List<Predicate> predicates = new LinkedList<Predicate>();
		Root<DepartmentMeeting> root = cq.from(DepartmentMeeting.class);
		if (weeks.size() != 0) {
			predicates.add(cb.in(root.get("week")).value(weeks));
		}
		predicates.add(cb.equal(root.get("year"), year));
		if (codeDepart != null)
			predicates.add(cb.equal(root.get("codeDepart"), codeDepart));
		if (view)
			predicates.add(cb.equal(root.get("isfinal"), view));
		cq.select(root)
				.where(cb.and(predicates.toArray(new Predicate[0])))
				.orderBy(cb.desc(root.get("year")),
						cb.desc(root.get("week")));
		TypedQuery<DepartmentMeeting> query = em.createQuery(cq);
		List<DepartmentMeeting> results = query.getResultList();
		return results;
	}

	

	public List<DepartmentMeeting> findDepartmentMeeting(int year, String  codeDepart, boolean view) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DepartmentMeeting> cq = cb.createQuery(DepartmentMeeting.class);
		List<Predicate> predicates = new LinkedList<Predicate>();
		Root<DepartmentMeeting> root = cq.from(DepartmentMeeting.class);
		predicates.add(cb.equal(root.get("year"), year));
		if (codeDepart != null)
			predicates.add(cb.equal(root.get("codeDepart"), codeDepart));
		if (view)
			predicates.add(cb.equal(root.get("isfinal"), view));
		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])))
				.orderBy(cb.desc(root.get("year")), cb.desc(root.get("week")));
		TypedQuery<DepartmentMeeting> query = em.createQuery(cq);
		List<DepartmentMeeting> results = query.getResultList();
		return results;
	}

}
